/**
 * 
 */
package App;

import java.util.ArrayList;
import java.util.Random;

import org.apache.thrift.TException;

/**
 * This file will act like one client. Several clients will run all together to
 * test the consistency of any distributed server( Key-value server in this
 * case).
 *
 */
public class MyThread extends Thread {
	TimeServer timeObject;
	int number;
	KVStore.Client client;
	ArrayList<Command> list;
	String HOST;
	int PORT; // default

	public MyThread(TimeServer timeObject, KVStore.Client client, ArrayList<Command> list, int x) {
		this.timeObject = timeObject;
		this.number = x;
		this.client = client;
		this.list = list;
	}

	@Override
	public void run() {
		// Your Code
		Thread.currentThread().setName(String.valueOf(number));
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
				list.add(cmd);
			} catch (TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// execute set command.

		}

		System.out.println("Client no. " + Thread.currentThread().getName() + " is running.");

	}

}
