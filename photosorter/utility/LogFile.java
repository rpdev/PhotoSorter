package photosorter.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile {
	private static final File LOG_FILE = new File("Log file.txt");
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static final void writeExceptionToLog(Exception ex){
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
			writer.append(df.format(new Date()) + " Exception: "+ex.getClass().getName());
			writer.newLine();
			writer.append(stackTraceToString(ex));
			writer.newLine();
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	private static final String stackTraceToString(Throwable e) {
		String retValue = null;
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			retValue = sw.toString();
		} finally {
			try {
				if (pw != null)
					pw.close();
				if (sw != null)
					sw.close();
			} catch (IOException ignore) {
			}
		}
		return retValue;
	}
}
