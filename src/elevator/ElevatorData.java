package elevator;

import java.time.LocalTime;

public class ElevatorData {
	private ElevatorStates state;
	private ElevatorStates prevDirection;
	private int currentFloor;
	private int movingToFloor;
	private LocalTime arrivalTime;

	public ElevatorData(ElevatorStates state, ElevatorStates prevDirection, int currentFloor, int movingToFloor, LocalTime arrivalTime) {
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
