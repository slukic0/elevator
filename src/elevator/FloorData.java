package elevator;

import java.time.LocalTime;

import elevator.Message.Sender;

public class FloorData {
	private LocalTime time;
	private int floor;
	private boolean goingUp;

	/**
	 * 
	 * @param startingFloor floor to start at
	 * @param goingUp true if going up, false if down
	 * @param time time of request
	 */
	public FloorData(int startingFloor, int destFloor, boolean goingUp, LocalTime time) {
		this.floor = startingFloor;
		this.goingUp = goingUp;
		this.time = time;
	}

	/** 
	 * Second Class constructor 
	**/
	public FloorData(int startingFloor, int destFloor, boolean goingUp) {
		this(startingFloor, destFloor, goingUp, LocalTime.now());
	}

	/**
	 * Returns the time of the message
	 * 
	 * @return time  the local time
	 */
	public LocalTime getTime() {
		return time;
	}

	/** 
	 * Returns the attribute representing the floor number 
	 * 
	 * @return floor  the floor number
	 * 
	 */
	public int getFloor() {
		return floor;
	}
	
	/**
	 * Returns a boolean value indicating whether the elevator is going up or not
	 * 
	 * @return goingUp  
	 */
	public boolean getGoingUp() {
		return goingUp;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "Floor: " + floor + ", Time: " + time.toString() + ", GoingUp: " + goingUp;
	}
}
