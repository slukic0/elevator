package messages;

import java.io.Serializable;
import java.time.LocalTime;

import elevatorImpl.ElevatorStates;

/**
 * Data structure class used to hold elevator data
 * 
 * @author Group G5
 *
 */
public class ElevatorData implements Serializable {

	private static final long serialVersionUID = 7724275007090309156L;
	private ElevatorStates state;
	private int currentFloor;
	private int movingToFloor;
	private LocalTime arrivalTime;
	private int ELEVATOR_NUMBER;
	private boolean hardFault;

	/**
	 * Creates a ElevatorData object with passed in parameters
	 * 
	 * @param state, 			ElevatorStates, the elevator's current state
	 * @param currentFloor,		int, the elevator's current floor
	 * @param motingToFloor, 	int, the elevator's destination floor
	 * @param arrivalTime, 		LocalTime, the time of arrival
	 * @param elevatorNumber,	int, the elevator number
	 * 
	 */
	public ElevatorData(ElevatorStates state, int currentFloor, int movingToFloor,
			LocalTime arrivalTime, int elevatorNumber) {
		this.state = state;
		this.currentFloor = currentFloor;
		this.movingToFloor = movingToFloor;
		this.arrivalTime = arrivalTime;
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.hardFault = false;
	}

	/**
	 * Creates a ElevatorData object with passed in parameters
	 * 
	 * @param state, 			ElevatorStates, the elevator's current state
	 * @param currentFloor,		int, the elevator's current floor
	 * @param motingToFloor, 	int, the elevator's destination floor
	 * @param arrivalTime, 		LocalTime, the time of arrival
	 * @param elevatorNumber,	int, the elevator number
	 * @param hardFault,		boolean, true if there is a hard fault, else false
	 */
	public ElevatorData(ElevatorStates state, int currentFloor, int movingToFloor,
			LocalTime arrivalTime, int elevatorNumber, boolean hardFault) {
		this.state = state;
		this.currentFloor = currentFloor;
		this.movingToFloor = movingToFloor;
		this.arrivalTime = arrivalTime;
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.hardFault = hardFault;
	}

	/** 
	 * Returns the attribute representing the elevator's current state 
	 * 
	 * @return state,  		the elevator's current state
	 * 
	 */
	public ElevatorStates getState() {
		return state;
	}

	/** 
	 * Sets the attribute representing the elevator's current state 
	 * 
	 * @param state,  		ElevatorStates, the elevator's new state
	 * 
	 */
	public void setState(ElevatorStates state) {
		this.state = state;
	}

	/** 
	 * Returns the attribute representing the current floor number 
	 * 
	 * @return currentFloor,  		the current floor number
	 * 
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	/** 
	 * Returns the attribute representing the destination floor number 
	 * 
	 * @return movingToFloor,  		the floor number the elevator is moving to
	 * 
	 */
	public int getMovingToFloor() {
		return movingToFloor;
	}

	/** 
	 * Sets the attribute representing the elevator's destination floor 
	 * 
	 * @param movingToFloor,  		int, the elevator's new destination
	 * 
	 */
	public void setMovingToFloor(int movingToFloor) {
		this.movingToFloor = movingToFloor;
	}

	/** 
	 * Returns the attribute representing the time of arrival
	 * 
	 * @return arrivalTime,  		the time of arrival
	 * 
	 */
	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	/** 
	 * Returns the attribute representing the elevator's unique number 
	 * 
	 * @return ELEVATOR_NUMBER,  	the elevator's number
	 * 
	 */
	public int getELEVATOR_NUMBER() {
		return ELEVATOR_NUMBER;
	}

	/**
	 * Returns the status of a potential hard fault
	 * 
	 * @return hardFault, 			1 if there is a hard fault, else 0
	 */
	public boolean getHardFault() {
		return hardFault;
	}

	/**
	 * To string method to neatly display the information contained in the elevator data
	 * 
	 * @return String, 		A string containing all the stored information
	 */
	@Override
	public String toString() {
		return "Number: " + ELEVATOR_NUMBER + ", State: " + state + ", CurrentFloor: " + currentFloor
				+ ", MovingToFloor: " + movingToFloor
				+ ", ArrivalTime: " + arrivalTime.toString();
	}
}
