package App;

import org.apache.thrift.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KVStoreServiceHandler implements KVStore.Iface {

	private static HashMap<String, String> kvmap = new HashMap<String, String>();

	public Result kvset(String key, String value) throws TException {
		kvmap.put(key, value);
		return new Result(value, ErrorCode.kSuccess, "Pair inserted Successfully");
	}

	public Result kvget(String key) throws TException {
		if (kvmap.containsKey(key)) {
			return new Result(kvmap.get(key), ErrorCode.kSuccess, "Pair fetched successfully.");
		} else {
			return new Result(kvmap.get(key), ErrorCode.kKeyNotFound, "Key could not be found.");
		}
	}

	public Result kvdelete(String key) throws TException {
		if (kvmap.containsKey(key)) {
			return new Result(kvmap.remove(key), ErrorCode.kSuccess, "Pair deleted successfully.");
		} else {
			return new Result(null, ErrorCode.kKeyNotFound, "Pair could not be found.");
		}
	}

}
