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
	private ElevatorSubsystem elevatorSubsystem;
	private ElevatorStates state;

	/** 
	 * Creates an elevator with shared synchronized message queues, the elevator number and its current floor
	 * 
	 * @param receiveQueue   the synchronized message queue to receive information from the Scheduler
	 * @param subsystemQueue the synchronized message queue to send information to the Scheduler
	 * @param elevatorNumber the elevator number
	 * @param currentFloor   the elevator's current floor
	 */
	public Elevator(ElevatorSubsystem elevatorSubsystem, int elevatorNumber,  int currentFloor) {
		this.elevatorSubsystem = elevatorSubsystem;
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.currentFloor = currentFloor;
		this.destFloor = -1; // -1 When at IDLE
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
