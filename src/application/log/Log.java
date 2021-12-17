package application.log;

public class Log {
	public static StringBuilder sbLog = new StringBuilder();
	
	public static String showLog() {
		return sbLog.toString();
	}
}
