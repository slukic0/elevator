package elevator;

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
	private Queue<Message> recieveQueue;
	private Queue<Message> schedulerQueue;

	/** 
	 * Creates an elevator with shared synchronized message queues, the elevator number and its current floor
	 * 
	 * @param recieveQueue   the synchronized message queue to recieve information from the Scheduler
	 * @param schedulerQueue the synchronized message queue to send information to the Scheduler
	 * @param elevatorNumber the elevator number
	 * @param currentFloor   the elevator's current floor
	 */
	public Elevator(Queue<Message> recieveQueue, Queue<Message> schedulerQueue, int elevatorNumber, int currentFloor) {
		this.recieveQueue = recieveQueue;
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
	
	public Queue<Message> getRecieveQueue() {
		return this.recieveQueue;
	}
	
	public Queue<Message> getSchedulerQueue() {
		return this.schedulerQueue;
	}

	/** 
	 *  Runs the elevator's thread
	 */
	public void run() {
		while (true) {
			Message recievedMessage;
			synchronized (recieveQueue) {
				while (recieveQueue.isEmpty()) {
					// wait for a message
					try {
						recieveQueue.wait();
					} catch (InterruptedException e) {
						System.out.println("Error in Elevator thread");
						e.printStackTrace();
					}
				}
				recievedMessage = recieveQueue.poll();
				System.out.println("Elevator recieved message: " + recievedMessage.toString());
			}
			// Send a message back to the floor
			synchronized (schedulerQueue) {
				while (!schedulerQueue.isEmpty()) {
					try {
						schedulerQueue.wait();
					} catch (InterruptedException e) {
						System.out.println("Error in Floor Thread");
						e.printStackTrace();
					}
				}
				// create a new message with the same info but sent from the elevator
				Message newMessage = new Message(Sender.ELEVATOR, recievedMessage.getFloor(),
						recievedMessage.getGoingUp(), recievedMessage.getTime());
				schedulerQueue.add(newMessage);
				System.out.println("Elevator sent message: " + newMessage.toString());
				schedulerQueue.notifyAll();
			}

		}
	}
}
