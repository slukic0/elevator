package elevator;

import java.time.LocalTime;
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
	private int destinationFloor;
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
		this.destinationFloor = currentFloor;
		this.state = ElevatorStates.IDLE;
	}
	
	public int getELEVATOR_NUMBER() {
		return ELEVATOR_NUMBER;
	}
	
	
	public ElevatorStates getState() {
		return state;
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
		int destFloor = data.getFloor();
		ElevatorStates newState = destFloor > this.currentFloor ? ElevatorStates.GOING_UP : ElevatorStates.GOING_DOWN;
		System.out.println("Elevator SubSystem setting state to " + newState + " and destFloor to " + destFloor);
		
		this.destinationFloor = destFloor;
		this.state = newState;
		this.wake();		
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
			System.out.println("Elevator IDLE, going to nap...");
			pause();
			break;
		}
		case GOING_DOWN:
		case GOING_UP:
			// TODO move the elevator
			System.out.println("Moving to floor "+destinationFloor);
			int diff = Math.abs(destinationFloor - currentFloor);
			elevatorSubsystem.sendSchedulerMessage(new ElevatorData(state, currentFloor, destinationFloor, LocalTime.now().plusSeconds(1*diff)));
			
			// wait for a bit
			try {
				Thread.sleep(1000* diff);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			currentFloor=destinationFloor;
			state = ElevatorStates.IDLE;
			// tell the scheduler we have arrived
			elevatorSubsystem.sendSchedulerMessage(new ElevatorData(state, currentFloor, destinationFloor, LocalTime.now()));
			
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + state);
		}
	}
}
