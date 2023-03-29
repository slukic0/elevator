package messages;

import java.io.Serializable;
import java.time.LocalTime;

import elevatorImpl.ElevatorStates;

public class ElevatorData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7724275007090309156L;
	private ElevatorStates state;
	private ElevatorStates prevDirection;
	private int currentFloor;
	private int movingToFloor;
	private LocalTime arrivalTime;
	private int ELEVATOR_NUMBER;
	private boolean hardFault;

	public ElevatorData(ElevatorStates state, ElevatorStates prevDirection, int currentFloor, int movingToFloor,
			LocalTime arrivalTime, int elevatorNumber) {
		this.state = state;
		this.prevDirection = prevDirection;
		this.currentFloor = currentFloor;
		this.movingToFloor = movingToFloor;
		this.arrivalTime = arrivalTime;
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.hardFault = false;
	}

	public ElevatorData(ElevatorStates state, ElevatorStates prevDirection, int currentFloor, int movingToFloor,
			LocalTime arrivalTime, int elevatorNumber, boolean hardFault) {
		this.state = state;
		this.prevDirection = prevDirection;
		this.currentFloor = currentFloor;
		this.movingToFloor = movingToFloor;
		this.arrivalTime = arrivalTime;
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.hardFault = hardFault;
	}

	public ElevatorStates getState() {
		return state;
	}

	public void setState(ElevatorStates state) {
		this.state = state;
	}

	public ElevatorStates getPrevDirection() {
		return prevDirection;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public int getMovingToFloor() {
		return movingToFloor;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public int getELEVATOR_NUMBER() {
		return ELEVATOR_NUMBER;
	}

	public boolean getHardFault() {
		return hardFault;
	}

	@Override
	public String toString() {
		return "Number: " + ELEVATOR_NUMBER + ", State: " + state + ", CurrentFloor: " + currentFloor
				+ ", MovingToFloor: " + movingToFloor
				+ ", ArrivalTime: " + arrivalTime.toString();
	}
}
