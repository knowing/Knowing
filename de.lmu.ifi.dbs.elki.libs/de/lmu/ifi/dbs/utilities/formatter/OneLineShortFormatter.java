package de.lmu.ifi.dbs.utilities.formatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter that produced logging output like:
 * 
 * LogLevel: message
 *
 * No Date/Time info is printed - also no new line before the loglevel.
 * Usage in logging.properties:
 * java.util.logging.ConsoleHandler.formatter = ir.utils.OneLineFormatter
 * 
 * @author graf
 */
public class OneLineShortFormatter extends Formatter {

    Date dat = new Date();
    private final static String format = "{0,date} {0,time}";
    private MessageFormat formatter;
    private Object args[] = new Object[1];    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    private String lineSeparator = System.getProperty("line.separator");

    /**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        // Minimize memory allocations here.
//        dat.setTime(record.getMillis());
//        args[0] = dat;
        StringBuffer text = new StringBuffer();
//        if (formatter == null) {
//            formatter = new MessageFormat(format);
//        }
//        formatter.format(args, text, null);
//        sb.append(text);
//        sb.append(" ");
//        if (record.getSourceClassName() != null) {
//            sb.append(record.getSourceClassName());
//        } else {
//            sb.append(record.getLoggerName());
//        }
//        if (record.getSourceMethodName() != null) {
//            sb.append(" ");
//            sb.append(record.getSourceMethodName());
//        }
//        sb.append(lineSeparator);
//        sb.append(" ");
        String message = formatMessage(record);
        sb.append(record.getLevel().getLocalizedName());
        sb.append(": ");
        sb.append(message);
        sb.append(lineSeparator);
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
            }
        }
        return sb.toString();
    }
}
