package App;

public class TimeServer {
	static int count = 0;

	public static int getNextTime() {
		return ++count;
	}
}
