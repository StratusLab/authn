package eu.stratuslab.authn;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ShortMsgFormatter extends Formatter {

	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		Date date = new Date(record.getMillis());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		sb.append(dateFormat.format(date));
		sb.append(":");

		sb.append(record.getLevel().getName());
		sb.append("::");

		sb.append(record.getMessage());
		sb.append("\n");

		return sb.toString();
	}
}
