package util;

import java.util.Queue;

public class SendReceiveUtil {
	public static void sendData(Queue<Object> queue, Object data) {
		synchronized (queue) {
			queue.add(data);
			queue.notifyAll();
		}
	}
	
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
