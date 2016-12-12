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


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Command other = (Command) obj;
		if (endTime != other.endTime)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (requestType == null) {
			if (other.requestType != null)
				return false;
		} else if (!requestType.equals(other.requestType))
			return false;
		if (startTime != other.startTime)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}


}
