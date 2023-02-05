package elevator;

import java.util.Queue;

import elevator.Message.Sender;

/** 
 * Represents the Scheduler in the system 
 * 
 * @author Group G5
 *
 */
public class Scheduler implements Runnable {
	private Floor[] floors;
	private Elevator[] elevators;
	private Queue<Message> receiveQueue;
	private Queue<Message> floorQueue;
	private Queue<Message> elevatorQueue;

	/**
	 * Creates a scheduler with shared synchronized message queues, floors and elevators in the system
	 * 
	 * @param receiveQueue 
	 * @param floorQueue
	 * @param elevatorQueue
	 * @param floors
	 * @param elevators
	 */
	public Scheduler(Queue<Message> receiveQueue, Queue<Message> floorQueue, Queue<Message> elevatorQueue,
			Floor[] floors, Elevator[] elevators) {
		this.receiveQueue = receiveQueue;
		this.floorQueue = floorQueue;
		this.elevatorQueue = elevatorQueue;
		this.floors = floors;
		this.elevators = elevators;
	}

	/**
	 * Gets the scheduler's receive queue
	 * 
	 * @return receiveQueue  scheduler's receive queue
	 */
	public Queue<Message> getreceiveQueue() {
		return this.receiveQueue;
	}

	/**
	 * Sends a message to the floor subsystem
	 * 
	 * @param message  the message to send to the floor
	 */
	public void sendFloorMessage(Message message) {
		synchronized (floorQueue) {
			floorQueue.add(message);
			System.out.println("Scheduler forwarded message to floor");
			floorQueue.notifyAll();
		}
	}
	
	/**
	 * Sends a message to the elevator subsystem
	 * 
	 * @param message  the message to send to the elevator
	 */
	public void sendElevatorMessage(Message message) {
		synchronized (elevatorQueue) {
			elevatorQueue.add(message);
			System.out.println("Scheduler forwarded message to elevator");
			elevatorQueue.notifyAll();
		}
	}

	/** 
	 *  Runs the scheduler's thread
	 */
	public void run() {
		while (true) {
			synchronized (receiveQueue) {
				while (receiveQueue.isEmpty()) {
					try {
						receiveQueue.wait();
					} catch (InterruptedException e) {
						System.out.println("Error in Scheduler Thread");
						e.printStackTrace();
					}
				}
				// get the message
				Message message = receiveQueue.poll();
				System.out.println("Scheduler got message: " + message.toString());
				receiveQueue.notifyAll();

				if (message.getSender() == Sender.FLOOR) {
					sendElevatorMessage(message);
				} else {
					sendFloorMessage(message);

				}
			}
		}
	}
}