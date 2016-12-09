/**
 * 
 */
package App;

/**
 * This file will act like one client. Several clients will run all together to test
 * the consistency of any distributed server( Key-value server in this case).
 *
 */
public class MyThread extends Thread{

	int time;
	int number;
	String output;
	public MyThread(TimeServer timeObject, String output, int x) {
		time = timeObject.getNextTime();
		this.output = output;
		number = x;
	}

	@Override
	public void run() {
		// Your Code
		Thread.currentThread().setName(String.valueOf(number));
		System.out.println("Client no. " + Thread.currentThread().getName() +" is running.");
		
	}

}
