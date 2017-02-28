/*
*****************************************************************************
** Module	:	com.hp.snap.evaluation.imdb.business.common
** Date: 5/22/12				Time: 8:36 AM
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id$
* $Log$
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.common;

/**
 * Call Interface.
 * IMDB venders must implement this interface.
 * The implementation class name is specified in configuration file with key: CallImplClass
 */
public interface CallIF
{
	/**
	 * Prepare call data before begining.
	 * E.g. generating call data in memory.
	 * @param executor
	 * @return
	 */
	public CallIF prepare(CallExecutor executor);

	/**
	 * call begin
	 * the begin logic should be executed, the data should be persistent to IMDB.
	 * @return
	 */
	public CallIF begin();

	/**
	 * call update
	 * the update logic should be executed, the data should be persistent to IMDB.
	 * @return
	 */
	public CallIF update();

	/**
	 * call end
	 * the end logic should be executed, the data should be persistent to IMDB.
	 * @return
	 */
	public CallIF end();

	/**
	 * Get call session ID, which identify this call.
	 * @return
	 */
	public String getSessionID();
}
