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

	int k;

	public MyThread(int i) {
		k = i;
	}

	@Override
	public void run() {
		// Your Code
		System.out.println("Client no. " + k +" is running.");

	}

}
