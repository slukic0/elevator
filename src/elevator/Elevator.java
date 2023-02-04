package elevator;

import java.util.Queue;

import elevator.Message.Sender;

public class Elevator implements Runnable {
	private final int ELEVATOR_NUMBER;
	private int currentFloor;
	private Queue<Message> recieveQueue;
	private Queue<Message> schedulerQueue;

	public Elevator(Queue<Message> recieveQueue, Queue<Message> schedulerQueue, int elevatorNumber, int currentFloor) {
		this.recieveQueue = recieveQueue;
		this.schedulerQueue = schedulerQueue;
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.currentFloor = currentFloor;
	}

	public int getCurrentFloor() {
		return this.currentFloor;
	}

	public void setCurrentFloor(int floor) {
		this.currentFloor = floor;
	}

	public void run() {
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
			Message recievedMessage = recieveQueue.poll();
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
			Message newMessage = new Message(Sender.ELEVATOR, currentFloor, false);
			schedulerQueue.add(newMessage);
			System.out.println("Elevator sent message: " + newMessage.toString());
			schedulerQueue.notifyAll();
		}
	}
}
