package elevator;

import java.util.ArrayList;
import java.util.Queue;

import elevator.Message.Sender;

/** 
 * Class to represent the floor subsystem
 * 
 * @author Group G5
 *
 */
public class Floor implements Runnable {
	private final int FLOOR_NUMBER;
	private Queue<Message> recieveQueue;
	private Queue<Message> schedulerQueue;
	private ArrayList<Message> messages; // Iteration 1: file inputed messages

	/** 
	 * Creates a floor with shared synchronized message queues and the floor number
	 *  
	 * @param recieveQueue the synchronized message queue to recieve information from the Scheduler
	 * @param schedulerQueue the synchronized message queue to send information to the Scheduler
	 * @param floorNumber the floor's number
	 */
	public Floor(Queue<Message> recieveQueue, Queue<Message> schedulerQueue, int floorNumber) {
		this.recieveQueue = recieveQueue;
		this.schedulerQueue = schedulerQueue;
		this.FLOOR_NUMBER = floorNumber;
		this.messages = new ArrayList<>();
	}
	
	/**
	 * Gets the floor's recieve queue
	 * 
	 * @return		Queue <Message>, floor's recieve queue
	 */
	public Queue<Message> getRecieveQueue() {
		return this.recieveQueue;
	}
	
	/**
	 * Gets the floor's scheduler queue
	 * 
	 * @return		Queue <Message>, floor's scheduler queue
	 */
	public Queue<Message> getSchedulerQueue() {
		return this.schedulerQueue;
	}

	/**
	 * Gets the floor's message list
	 * 
	 * @return		ArrayList <Message>, floor's message list
	 */
	public ArrayList<Message> getMessages() {
		return this.messages;
	}
	
	
	public void addMessage(Message message) {
		messages.add(message);
	}
	
	
	/** 
	 *  Runs the floor's thread
	 */
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
