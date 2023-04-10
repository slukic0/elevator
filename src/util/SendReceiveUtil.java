package util;

import java.util.Queue;

/**
 * Utility class used to send and receive data
 * 
 * @author Group G5
 *
 */
public class SendReceiveUtil {
	/**
	 * Adds data to the object queue and notifies all waiting threads
	 *
	 * @param queue		Queue<Object>, the queue to which the data must be added
	 * @param data		Object, the message to be added to the queue
	 *
	 */
	public static void sendData(Queue<Object> queue, Object data) {
		synchronized (queue) {
			queue.add(data);
			queue.notifyAll();
		}
	}
	
	/**
	 * Waits until queue is not empty then retrieves data from the queue
	 *
	 * @param queue		Queue<Object>, the queue from which data must be received
	 * 
	 * @return data		Object, the message received from the queue
	 * 
	 */
	public static Object receiveData(Queue<?> queue) {
		synchronized (queue) {
			while (queue.isEmpty()) {
				try {
					queue.wait();
				} catch (InterruptedException e) {
					System.out.println("Error receiving data");
					e.printStackTrace();
				}
			}
			
			Object data = queue.poll();
			queue.notifyAll();
			return data;
		}
	}
}
