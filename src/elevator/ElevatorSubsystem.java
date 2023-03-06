package elevator;

import java.util.Queue;

import util.SendReceiveUtil;

public class ElevatorSubsystem implements Runnable {

	private Elevator elevator;
	private Queue<FloorData> receiveQueue;
	private Queue<Object> schedulerReceiveQueue;

	/**
	 * Creates Elevator Subsystem object
	 * @param receiveQueue			Queue for information received from floor
	 * @param schedulerReceiveQueue	Queue for message send to scheduler
	 * @param elevatorNumber		Associated elevator number
	 * @param currentFloor			Associated elevator's current floor
	 */
	public ElevatorSubsystem(Queue<FloorData> receiveQueue, Queue<Object> schedulerReceiveQueue, int elevatorNumber,
			int currentFloor) {
		this.elevator = new Elevator(this, elevatorNumber, currentFloor);
		this.receiveQueue = receiveQueue;
		this.schedulerReceiveQueue = schedulerReceiveQueue;
		
		// Start the elevator
		new Thread(this.elevator).start();
	}
	
	/**
	 * Getter to return elevator object
	 * @return returns elevator of subsystem
	 */
	public Elevator getElevator() {
		return elevator;
	}
	
	/***
	 * Gets the scheduler's receive queue
	 * 
	 * @return receiveQueue scheduler's receive queue
	 */
	public Queue<FloorData> getreceiveQueue() {
		return this.receiveQueue;
	}
	
	/***
	 * Gets the scheduler's schedulerReceive queue
	 * 
	 * @return receiveQueue scheduler's receive queue
	 */
	public Queue<Object> getSchedulerReceiveQueue() {
		return this.schedulerReceiveQueue;
	}

	/**
	 * Sends message of elevator data to scheduler
	 * @param message ElevatorData, message to send to scheduler
	 *
	 */
	public void sendSchedulerMessage(ElevatorData message) {
		System.out.println("Elevator subsystem sending message " + message);
		new Thread(() -> {
			SendReceiveUtil.sendData(schedulerReceiveQueue, message);
		}).start();
	}

	/**
	 * Runs subsystem's thread
	 */
	@Override
	public void run() {
		while (true) {
			synchronized (receiveQueue) {
				while (receiveQueue.isEmpty()) {
					try {
						receiveQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < receiveQueue.size(); i++) {
					FloorData data = receiveQueue.poll();
					System.out.println("Elevator SubSystem received " + data);
					elevator.processPacket(data);
				}
			}
		}
	}
//	
//	public void sendTaskToElevator() {  // Send floor number to elevator queue
//		
//	}
//	
//	public void sendArrivalToScheduler() { //send elevator's floor arrival to scheduler
//		
//	}
//	
//	public void receiveTaskFromScheduler() { // receive Task for elevator and destination from scheduler
//		
//	}
//	
//	public void receiveStatusFromElevator() { // receive Status from elevator 
//		
//	}
//	
}
