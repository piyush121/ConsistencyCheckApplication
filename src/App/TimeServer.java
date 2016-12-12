package App;

public class TimeServer {
	static int count = 2;

	public static int getNextTime() {
		return ++count;
	}
}
