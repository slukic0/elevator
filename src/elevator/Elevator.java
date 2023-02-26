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
	private int destFloor;
	private Queue<FloorData> receiveQueue; // Elevator now receives floor number from task
	private Queue<Object> subsystemQueue;
	private ElevatorStates state;

	/** 
	 * Creates an elevator with shared synchronized message queues, the elevator number and its current floor
	 * 
	 * @param receiveQueue   the synchronized message queue to receive information from the Scheduler
	 * @param subsystemQueue the synchronized message queue to send information to the Scheduler
	 * @param elevatorNumber the elevator number
	 * @param currentFloor   the elevator's current floor
	 */
	public Elevator(int elevatorNumber, Queue<FloorData> receiveQueue, Queue<Object> subsystemQueue, int currentFloor) {
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.receiveQueue = receiveQueue;
		this.subsystemQueue = subsystemQueue;
		this.currentFloor = currentFloor;
	}
	
	public int getELEVATOR_NUMBER() {
		return ELEVATOR_NUMBER;
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
	public Queue<FloorData> getreceiveQueue() {
		return this.receiveQueue;
	}
	
	/**
	 * Gets the elevator's scheduler queue
	 * 
	 * @return schedulerQueue  elevator's scheduler queue
	 */
	public Queue<Object> getSubsystemQueue() {
		return subsystemQueue;
	}
	
	
	public void processPacket(FloorData data) {
		// going up is not relevant at the moment since we only have 1 elevator
		
		switch (state) {
		case IDLE: {
			destFloor = data.getFloor();
			// TODO tell the scheduler something???	
			this.wake();
			break;
		}
		case GOING_DOWN: {
			
			break;
		}
		
		case GOING_UP: {
			
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + state);
		}
				
	}
	
	private synchronized void pause() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private synchronized void wake() {
		this.notify();
	}

	/** 
	 *  Runs the elevator's thread
	 */
	public void run() {
		switch (state) {
		case IDLE: {
			pause();
			break;
		}
		case GOING_DOWN:
		case GOING_UP:
			// TODO move the elevator
			
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + state);
		}
	}
}
