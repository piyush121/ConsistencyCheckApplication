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

import App.ErrorCode;
import App.KVStore;
import App.Result;

/**
 * This file will invoke all the clients and check the given server for the
 * Safety, Regularity and Atomicity of the stored values.
 *
 */
public class CheckServer {

	/**
	 * @param args
	 */
	
	public static class kvclient {
		private static String HOST = "localhost";
		private static int PORT = 9091; // default
		private static TTransport transport;
		private static TProtocol protocol;
		private static KVStore.Client client;
		
		public static String produceOutput(String[] command) {
			
			try {
				HOST = command[1].split(":")[0];
				PORT = Integer.parseInt(command[1].split(":")[1]);
				transport = new TSocket(HOST, PORT, 5000);// Will timeout after 5 secs.
				transport.open();
				protocol = new TBinaryProtocol(transport);
				client = new KVStore.Client(protocol);

				int length = command.length;
				Result result = null;
				switch (length) {
				
				// either get or delete
				case 4:
					if (command[2].equals("-get")) {
						result = client.kvget(command[3]);
						if (result.getError() == ErrorCode.kSuccess) {
							return result.toString();
						} else if (result.getError() == ErrorCode.kKeyNotFound) {
							return result.errortext;
						} else {
							return result.errortext;
						}
					} 
					break;
				case 5: // set command sent.
					if (command[2].equals("-set")) {
						result = client.kvset(command[3], command[4]);
						if (result.getError() == ErrorCode.kSuccess) {
							return result.toString();
						} else {
							return result.errortext;
						}
					}
					break;
				default:
					return "Command does not exist or Wrong arguments passed.";
				}

			} catch (TTransportException ex) {
				ex.printStackTrace();
				return ex.toString();
			} catch (TException tx) {
				return tx.toString();
			}
			
			return "";
			
		}
		
	}
	
	public static void main(String[] args) {

		// create 255 Thread using for loop
		for (int x = 0; x < 256; x++) {
			// Create Thread class
			TimeServer timeObject = new TimeServer();
			kvclient kvc_obj = new kvclient();
			MyThread temp = new MyThread(timeObject, kvc_obj.produceOutput(args), x);
			temp.start();
			try {
				temp.join(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
