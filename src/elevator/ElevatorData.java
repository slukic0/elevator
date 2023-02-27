package elevator;

import java.time.LocalTime;

public class ElevatorData {
	private ElevatorStates state;
	private ElevatorStates prevState;
	private int currentFloor;
	private int movingToFloor;
	private LocalTime arrivalTime;

	public ElevatorData(ElevatorStates state, ElevatorStates prevState, int currentFloor, int movingToFloor, LocalTime arrivalTime) {
		this.state = state;
		this.prevState = prevState;
		this.currentFloor = currentFloor;
		this.movingToFloor = movingToFloor;
		this.arrivalTime = arrivalTime;
	}

	public ElevatorStates getState() {
		return state;
	}
	
	public ElevatorStates getPrevState() {
		return prevState;
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
