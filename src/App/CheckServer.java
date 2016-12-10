/**
 * 
 */
package App;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import App.ErrorCode;
import App.KVStore;
import App.Result;

/**
 * This file will invoke all the clients and check the given server for the
 * Safety, Regularity and Atomicity of the stored values.
 *
 */
public class CheckServer {

	private static String HOST = "localhost";
	private static int PORT = 9091; // default
	private static TTransport transport;
	private static TProtocol protocol;
	private static KVStore.Client client;

	/*
	 * HOST = command[1].split(":")[0]; PORT =
	 * Integer.parseInt(command[1].split(":")[1]); transport = new TSocket(HOST,
	 * PORT, 5000);// Will timeout after // 5 secs. transport.open(); protocol =
	 * new TBinaryProtocol(transport); client = new KVStore.Client(protocol);
	 */

	/**
	 * Class kvclient is used for creating an object for a client. Using this
	 * object, command posted by the buggy server is read and it's result is
	 * then used to create a thread for the client.
	 */

	public static void main(String[] args) {

		try {
			HOST = args[1].split(":")[0];
			PORT = Integer.parseInt(args[1].split(":")[1]);
			transport = new TSocket(HOST, PORT, 5000);// Will timeout after
														// 5 secs.
			transport.open();
			protocol = new TBinaryProtocol(transport);
			client = new KVStore.Client(protocol);
			ArrayList<Command> list = new ArrayList<>();
			// create 255 Thread using for loop
			for (int x = 0; x < 256; x++) {
				// Create Thread class and start accumulating logs in the list.
				TimeServer timeObject = new TimeServer();
				KVStore.Client kvc_obj = new KVStore.Client(protocol);
				MyThread temp = new MyThread(timeObject, kvc_obj, list, x);
				temp.start();
				temp.join(10);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}
}
