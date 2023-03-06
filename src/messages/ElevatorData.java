package messages;

import java.io.Serializable;
import java.time.LocalTime;

import elevator.ElevatorStates;

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

	public ElevatorData(ElevatorStates state, ElevatorStates prevDirection, int currentFloor, int movingToFloor,
			LocalTime arrivalTime) {
		this.state = state;
		this.prevDirection = prevDirection;
		this.currentFloor = currentFloor;
		this.movingToFloor = movingToFloor;
		this.arrivalTime = arrivalTime;
	}

	public ElevatorStates getState() {
		return state;
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

	@Override
	public String toString() {
		return "State: " + state + ", CurrentFloor: " + currentFloor + ", MovingToFloor: " + movingToFloor
				+ ", ArrivalTime: " + arrivalTime.toString();
	}
}
