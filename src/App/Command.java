/**
 * 
 */
package App;

/**
 * This class creates information about the request.
 *
 */
public class Command {
	int startTime;
	int endTime;
	String requestType;
	String key;
	String value;

	public Command(int startTime, int endTime, String requestType, String key, String value) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.requestType = requestType;
		this.key = key;
		this.value = value;

	}


}
