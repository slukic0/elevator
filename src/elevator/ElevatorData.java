package elevator;


public class ElevatorData {
	private ElevatorStates state;
	private int currentFloor;
	private int movingToFloor;
	
	public ElevatorData(ElevatorStates state, int currentFloor, int movingToFloor) {
		this.state=state;
		this.currentFloor=currentFloor;
		this.movingToFloor=movingToFloor;
	}
	
	public ElevatorStates getState() {
		return state;
	}
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	public int getMovingToFloor() {
		return movingToFloor;
	}


	@Override
	public String toString() {
		return "State: " + state +", CurrentFloor: " + currentFloor +", MovingToFloor: " + movingToFloor;
	}
}
