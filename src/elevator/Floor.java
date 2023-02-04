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
		synchronized (recieveQueue) {
			// press the button
			System.out.println(": Floor is sending message to Scheduler");
			schedulerQueue.add(new Message(Sender.FLOOR, FLOOR_NUMBER, true));

			// wait for elevator response
			while (recieveQueue.isEmpty()) {
				try {
					System.out.println("floor wait");
					recieveQueue.wait();
				} catch (InterruptedException e) {
					System.out.println("Error in Elavtor Thread");
					e.printStackTrace();
				}
			}

			Message message = recieveQueue.poll();
			System.out.println(": Floor recieved message: " + message.toString());

			notifyAll();
		}
	}
}
