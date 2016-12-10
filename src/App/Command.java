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

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
