/*
*****************************************************************************
** Module	:	com.hp.ocs.dccmeter
** Date: 12-3-18				Time: 上午9:54
** Author: Wang Bo (Brain Wang)  2012
*****************************************************************************
********************* CVS Change History ************************************
* $Id: DCCMeterLoggingFormatter.java,v 1.1 2012/06/04 08:31:26 WangBo Exp $
* $Log: DCCMeterLoggingFormatter.java,v $
* Revision 1.1  2012/06/04 08:31:26  WangBo
* 5.0.4 Draft
*
*****************************************************************************
*/
package com.hp.snap.evaluation.imdb.business.cases;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Logger Formatter
 */
public class DCCMeterLoggingFormatter extends Formatter {
    private Calendar _calendar = Calendar.getInstance();
    private DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public synchronized String format(LogRecord record) {

        StringBuilder sb = new StringBuilder();
        // Time
        _calendar.setTimeInMillis(record.getMillis());
        sb.append(_dateFormat.format(_calendar.getTime())).append(' ');
        sb.append("T[").append(record.getThreadID()).append(']');

        Level loggerLevel = record.getLevel();// Logger.getLogger(record.getLoggerName()).getLevel();
        if (loggerLevel != null) {
            sb.append(loggerLevel.getName()).append(' ');// Level
            // if (loggerLevel.intValue() <= Level.FINE.intValue()) {// Debug
            if (record.getSourceClassName() != null) {
                sb.append(record.getSourceClassName()).append('.');
            }
            if (record.getSourceMethodName() != null) {
                sb.append(record.getSourceMethodName()).append(' ');
            }
            // }
        }
        // message
        String message = formatMessage(record);
        sb.append(message);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append('\n').append(sw.toString());
            } catch (Exception ex) {
            }
        }
        sb.append('\n');
        return sb.toString();
    }
}
