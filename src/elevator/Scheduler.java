package elevator;

import java.util.ArrayList;
import java.util.HashMap;
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
	private ArrayList<Elevator> idleElevators;
	
	private Queue<Object> receiveQueue;
	private Queue<FloorData> elevatorRecieveQuque;
	private Queue<ElevatorData> floorRecieveQueue;

	/**
	 * Creates a scheduler with shared synchronized message queues, floors and elevators in the system
	 * 
	 * @param receiveQueue 
	 * @param floorQueue
	 * @param elevatorQueue
	 * @param floors
	 * @param elevators
	 */
	public Scheduler(Queue<Object> receiveQueue, Queue<ElevatorData> floorRecieveQueue, Queue<FloorData> elevatorRecieveQuque,
			Floor[] floors, Elevator[] elevators) {
		this.receiveQueue = receiveQueue;
		this.floorRecieveQueue = floorRecieveQueue;
		this.elevatorRecieveQuque = elevatorRecieveQuque;
		this.floors = floors;
		this.elevators = elevators;
	}

	/**
	 * Gets the scheduler's receive queue
	 * 
	 * @return receiveQueue  scheduler's receive queue
	 */
	public Queue<Object> getreceiveQueue() {
		return this.receiveQueue;
	}

	/**
	 * Sends a message to the floor subsystem
	 * 
	 * @param message  the message to send to the floor
	 */
	public void sendFloorSystemMessage(ElevatorData message) {
		synchronized (floorRecieveQueue) {
			floorRecieveQueue.add(message);
			System.out.println("Scheduler forwarded message to floor");
			floorRecieveQueue.notifyAll();
		}
	}
	
	/**
	 * Sends a message to the elevator subsystem
	 * 
	 * @param message  the message to send to the elevator
	 */
	public void sendElevatorSystemMessage(FloorData message) {
		synchronized (elevatorRecieveQuque) {
			elevatorRecieveQuque.add(message);
			System.out.println("Scheduler forwarded message to elevator");
			elevatorRecieveQuque.notifyAll();
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
				// get the message(s)
				for (int i=0; i<receiveQueue.size(); i++) {
					
					Object message = receiveQueue.poll();
					System.out.println("Scheduler got message: " + message.toString());
					
					if (message instanceof FloorData) {
						// elevator sent the message
						// check if any elevators are ready
						if (idleElevators.isEmpty()) {
							// TODO wait
						} else {
							// TODO: for now we only have 1 elevator
//							// in the future we should choose the most efficient elevator
//							// and tell the elevator subsystem which elevator to move
//							for (Elevator e : idleElevators) {
								// elevatorNumber = e.getELEVATOR_NUMBER();
//							}
							// for now we will simply tell the system just where to move
							sendElevatorSystemMessage((FloorData)message);
						}
						
					} else if (message instanceof ElevatorData) {
						// one of the elevators sent us this message
						sendFloorSystemMessage((ElevatorData) message);
					}
				}
				
				receiveQueue.notifyAll();
			}
		}
	}
}