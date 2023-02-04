package elevator;

import java.util.Queue;

import elevator.Message.Sender;

public class Floor implements Runnable {
	private final int FLOOR_NUMBER;
	private Queue<Message> recieveQueue;
	private Queue<Message> schedulerQueue;

	public Floor(Queue<Message> recieveQueue, Queue<Message> schedulerQueue, int floorNumber) {
		this.recieveQueue = recieveQueue;
		this.schedulerQueue = schedulerQueue;
		this.FLOOR_NUMBER = floorNumber;
	}

	public void run() {
		synchronized (schedulerQueue) {
			while(!schedulerQueue.isEmpty()) {
				try {
					schedulerQueue.wait();
				} catch (InterruptedException e) {
					System.out.println("Error in Floor Thread");
					e.printStackTrace();
				}
			}
			Message message = new Message(Sender.FLOOR, FLOOR_NUMBER, true);
			System.out.println("Floor is sending message to Scheduler: "+message.toString());
			schedulerQueue.add(message);
			schedulerQueue.notifyAll();
		}
		
		synchronized (recieveQueue) {
			// wait for elevator response
			while (recieveQueue.isEmpty()) {
				try {
					recieveQueue.wait();
				} catch (InterruptedException e) {
					System.out.println("Error in Floor Thread");
					e.printStackTrace();
				}
			}

			Message message = recieveQueue.poll();
			System.out.println("Floor recieved message: " + message.toString());

			recieveQueue.notifyAll();
		}
	}
}
