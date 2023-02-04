package elevator;

import java.util.ArrayList;
import java.util.Queue;

import elevator.Message.Sender;

public class Floor implements Runnable {
	private final int FLOOR_NUMBER;
	private Queue<Message> recieveQueue;
	private Queue<Message> schedulerQueue;
	private ArrayList<Message> messages; // Iteration 1: file inputed messages

	public Floor(Queue<Message> recieveQueue, Queue<Message> schedulerQueue, int floorNumber) {
		this.recieveQueue = recieveQueue;
		this.schedulerQueue = schedulerQueue;
		this.FLOOR_NUMBER = floorNumber;
		this.messages = new ArrayList<>();
	}

	public void addMessage(Message message) {
		messages.add(message);
	}

	public void run() {
		for (Message m : messages) {
			synchronized (schedulerQueue) {
				while (!schedulerQueue.isEmpty()) {
					try {
						schedulerQueue.wait();
					} catch (InterruptedException e) {
						System.out.println("Error in Floor Thread");
						e.printStackTrace();
					}
				}
				System.out.println("Floor is sending message to Scheduler: " + m.toString());
				schedulerQueue.add(m);
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
}
