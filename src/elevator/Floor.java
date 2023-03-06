package elevator;

import java.util.Queue;

import util.SendReceiveUtil;

/**
 * Class to represent the floor subsystem
 * 
 * @author Group G5
 *
 */
public class Floor implements Runnable {
	private final int FLOOR_NUMBER;
	private Queue<ElevatorData> receiveQueue;
	private Queue<Object> schedulerQueue;

	/**
	 * Creates a floor with shared synchronized message queues and the floor number
	 * 
	 * @param receiveQueue   the synchronized message queue to receive information
	 *                       from the Scheduler
	 * @param schedulerQueue the synchronized message queue to send information to
	 *                       the Scheduler
	 * @param floorNumber    the floor's number
	 */
	public Floor(Queue<ElevatorData> receiveQueue, Queue<Object> schedulerQueue, int floorNumber) {
		this.receiveQueue = receiveQueue;
		this.schedulerQueue = schedulerQueue;
		this.FLOOR_NUMBER = floorNumber;
	}

	/**
	 * Gets the floor's receive queue
	 * 
	 * @return Queue <Message>, floor's receive queue
	 */
	public Queue<ElevatorData> getreceiveQueue() {
		return this.receiveQueue;
	}

	/**
	 * Gets the floor's scheduler queue
	 * 
	 * @return Object <Message>, floor's scheduler queue
	 */
	public Queue<Object> getSchedulerQueue() {
		return this.schedulerQueue;
	}

	public void sendMessage(FloorData data) {
		System.out.println("Floor is sending message to Scheduler: " + data.toString());
		new Thread(() -> {
			SendReceiveUtil.sendData(schedulerQueue, data);
		}).start();
	}

	/**
	 * Runs the floor's thread
	 */
	public void run() {
		while (true) {
			synchronized (receiveQueue) {
				// wait for elevator response
				while (receiveQueue.isEmpty()) {
					try {
						receiveQueue.wait();
					} catch (InterruptedException e) {
						System.out.println("Error in Floor Thread");
						e.printStackTrace();
					}
				}

				for (int i = 0; i < receiveQueue.size(); i++) {
					ElevatorData message = receiveQueue.poll();
					System.out.println("Floor received message: " + message.toString());
					if (message.getCurrentFloor() == message.getMovingToFloor()
							&& message.getState() == ElevatorStates.IDLE) {
						System.out.println("Floor: Elevator has arrived at floor " + message.getCurrentFloor());
					}

					receiveQueue.notifyAll();
				}
			}
		}
	}

}
