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

/**
 * This file will act like one client. Several clients will run all together to test
 * the consistency of any distributed server( Key-value server in this case).
 *
 */
public class MyThread extends Thread{

	int time;
	int number;
	public MyThread(TimeServer timeObject, int x) {
		time = timeObject.getNextTime();
		number = x;
	}

	@Override
	public void run() {
		// Your Code
		Thread.currentThread().setName(String.valueOf(number));
		System.out.println("Client no. " + Thread.currentThread().getName() +" is running.");
		
	}

}
