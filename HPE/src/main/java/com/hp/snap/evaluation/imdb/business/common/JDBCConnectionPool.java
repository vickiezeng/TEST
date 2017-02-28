/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business.common
** Date: 5/30/12				Time: 10:47 AM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * TODO description of this Class
 */
public class JDBCConnectionPool
{
	String _url, _user, _password;
	boolean _autoCommit;

    public JDBCConnectionPool(boolean autoCommit) throws ClassNotFoundException, SQLException {
        this(autoCommit, "");
    }

	public JDBCConnectionPool(boolean autoCommit, String prefix) throws ClassNotFoundException, SQLException
	{
        Class.forName(CallService.getInstance().getApplicationProperty(prefix + "jdbc.driver"));
        _url = CallService.getInstance().getApplicationProperty(prefix + "jdbc.url");
        _user = CallService.getInstance().getApplicationProperty(prefix + "jdbc.user");
        _password = CallService.getInstance().getApplicationProperty(prefix + "jdbc.password");
		int threadCount = Integer.parseInt(CallService.getInstance().getApplicationProperty("ThreadPoolSize"));
		//String str = CallService.getInstance().getApplicationProperty("ThreadPoolSizeForImmediateTask");
		//threadCount = threadCount + ((str != null) ? Integer.parseInt(str) : threadCount);
		int connCount = threadCount;
		for (int i = 0; i < connCount; i++)
		{
			Connection conn = DriverManager.getConnection(_url, _user, _password);
			conn.setAutoCommit(autoCommit);
			_connPoolIdle.add(conn);
		}
		this._autoCommit =autoCommit;
		_logger.info("Created " + connCount + " JDBC connections to " + _url);
	}

	public Connection getJDBCConnection()
	{
		Connection conn = _threadConnection.get();
		if (conn != null) return conn;
		synchronized (_connPoolIdle)
		{
			conn = _connPoolIdle.pollFirst();
			if(conn==null)
			{
				try
				{
					_logger.info("Create a new connection to "+ _url);
					conn = DriverManager.getConnection(_url, _user, _password);
					conn.setAutoCommit(_autoCommit);
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
			_connPoolActive.add(conn);
		}
		_threadConnection.set(conn);
		return conn;
	}

	public void shutdown(boolean commit)
	{
		//Close connections
		for (Connection conn : _connPoolActive)
		{
			try
			{
				if (commit) conn.commit();
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		_connPoolActive.clear();
		for (Connection conn : _connPoolIdle)
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		_connPoolIdle.clear();
		_logger.info("Shutdown JDBC connections.");

	}

	private LinkedList<Connection> _connPoolActive = new LinkedList<Connection>();
	private LinkedList<Connection> _connPoolIdle = new LinkedList<Connection>();
	private ThreadLocal<Connection> _threadConnection = new ThreadLocal<Connection>();
	private Logger _logger = Logger.getLogger(getClass().getName());

}
