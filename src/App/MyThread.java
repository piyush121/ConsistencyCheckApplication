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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This file will act like one client. Several clients will run all together to
 * test the consistency of any distributed server( Key-value server in this
 * case).
 *
 */
public class MyThread extends Thread {
	int number;
	KVStore.Client client;
	List<Command> list;
	String HOST;
	int PORT; // default

	public MyThread(List<Command> list,String host, int port, int x) {
		this.number = x;
		this.list = list;
		this.HOST = host;
		this.PORT = port;
	}

	@Override
	public void run() {
		// Your Code
		System.out.println("Client no. " + Thread.currentThread().getName() + " is running.");
		Thread.currentThread().setName(String.valueOf(number));
		HOST = "localhost";
		PORT = 5000;
		TTransport transport = new TSocket(HOST, PORT, 150000);
		try {
			transport.open();
		} catch (TTransportException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TProtocol protocol = new TBinaryProtocol(transport);
		KVStore.Client client = new KVStore.Client(protocol);
		
		Random rand = new Random();
		String callType = "";
		Command cmd = null;
		String val = "";
		String randomValue = "";
		int startTime = 0;
		int endTime = 0;
		for (int i = 0; i < 20; i++) {
			// execute command randomly.
			
			int choose = rand.nextInt(2);
			try {
				startTime = TimeServer.getNextTime();
				callType = choose == 0 ? "get" : "set";
				if (callType.equals("get")) {
					val = client.kvget("1").value;
					endTime = TimeServer.getNextTime();
					cmd = new Command(startTime, endTime, "get", "1", val);
				} else {
					randomValue = String.valueOf(rand.nextInt(500));
					val = client.kvset("1", randomValue).value;
					endTime = TimeServer.getNextTime();
					cmd = new Command(startTime, endTime, "set", "1", randomValue);
				}			
				System.out.println("Loop " + i + " Thread: " + Thread.currentThread().getName());
				list.add(cmd);
			} catch (TException e) {
				// TODO Auto-generated catch block
				System.out.println("TException found.");
				e.printStackTrace();
			}

		}
		transport.close();
		System.out.println(list.get(1).endTime);
	}

}
