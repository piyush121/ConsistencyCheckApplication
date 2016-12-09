/**
 * 
 */
package App;

/**
 * This file will invoke all the clients and check the given server for the
 * Safety, Regularity and Atomicity of the stored values.
 *
 */
public class CheckServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// create 255 Thread using for loop
		for (int x = 0; x < 256; x++) {
			// Create Thread class
			TimeServer timeObject = new TimeServer();
			MyThread temp = new MyThread(timeObject, x);
			temp.start();
			try {
				temp.join(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
