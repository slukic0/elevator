package elevator;

import java.util.HashMap;
import java.util.Queue;

import elevator.Message.Sender;

/** 
 * Class to represent the elevator subsystem
 * 
 * @author Group G5
 * 
 */
public class Elevator implements Runnable {
	private final int ELEVATOR_NUMBER;
	private int currentFloor;
	private Queue<Integer> receiveQueue; // Elevator now receives floor number from task
	private Queue<Message> schedulerQueue;
	HashMap<Elevator, Integer>Tasks = new HashMap<Elevator, Integer>();

	/** 
	 * Creates an elevator with shared synchronized message queues, the elevator number and its current floor
	 * 
	 * @param receiveQueue   the synchronized message queue to receive information from the Scheduler
	 * @param schedulerQueue the synchronized message queue to send information to the Scheduler
	 * @param elevatorNumber the elevator number
	 * @param currentFloor   the elevator's current floor
	 */
	public Elevator(Queue<Integer> receiveQueue, Queue<Message> schedulerQueue, int elevatorNumber, int currentFloor) {
		this.receiveQueue = receiveQueue;
		this.schedulerQueue = schedulerQueue;
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.currentFloor = currentFloor;
	}
	
	/** Gets the elevator's current floor
	 * 
	 * @return currentFloor  the elevator's current floor
	 */
	public int getCurrentFloor() {
		return this.currentFloor;
	}

	/** Sets the elevator's current floor
	 * 
	 * @param floor  an integer containing a floor number
	 */
	public void setCurrentFloor(int floor) {
		this.currentFloor = floor;
	}
	
	/**
	 * Gets the elevator's receive queue
	 * 
	 * @return receiveQueue  elevator's receive queue
	 */
	public Queue<Integer> getreceiveQueue() {
		return this.receiveQueue;
	}
	
	/**
	 * Gets the elevator's scheduler queue
	 * 
	 * @return schedulerQueue  elevator's scheduler queue
	 */
	public Queue<Message> getSchedulerQueue() {
		return this.schedulerQueue;
	}

	/** 
	 *  Runs the elevator's thread
	 */
	public void run() {
		while (true) {
			int receivedMessage;
			synchronized (receiveQueue) {
				while (receiveQueue.isEmpty()) {
					// wait for a message
					try {
						receiveQueue.wait();
					} catch (InterruptedException e) {
						System.out.println("Error in Elevator thread");
						e.printStackTrace();
					}
				}
				receivedMessage = receiveQueue.poll();
				System.out.println("Elevator received message: " + receivedMessage);
			}
			//Change to send arrival floor number -------------------------
			// Send a message back to the floor
//			synchronized (schedulerQueue) {
//				while (!schedulerQueue.isEmpty()) {
//					try {
//						schedulerQueue.wait();
//					} catch (InterruptedException e) {
//						System.out.println("Error in Floor Thread");
//						e.printStackTrace();
//					}
//				}
//				// create a new message with the same info but sent from the elevator
//				Message newMessage = new Message(Sender.ELEVATOR, receivedMessage);
//				schedulerQueue.add(newMessage);
//				System.out.println("Elevator sent message: " + newMessage.toString());
//				schedulerQueue.notifyAll();
//			}
//
		}
	}
}
