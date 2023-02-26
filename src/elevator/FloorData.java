package elevator;

import java.time.LocalTime;

import elevator.Message.Sender;

public class FloorData {
	private LocalTime time;
	private int startingFloor;
	private int destFloor;
	private boolean goingUp;

	/**
	 * 
	 * @param startingFloor floor to start at
	 * @param destFloor destination of the elevator
	 * @param goingUp true if going up, false if down
	 * @param time time of request
	 */
	public FloorData(int startingFloor, int destFloor, boolean goingUp, LocalTime time) {
		this.startingFloor = startingFloor;
		this.destFloor = destFloor;
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
	 * Returns the attribute representing the starting floor number 
	 * 
	 * @return floor  the floor number
	 * 
	 */
	public int getStartingFloor() {
		return startingFloor;
	}
	
	/** 
	 * Returns the attribute representing the destination floor number 
	 * 
	 * @return floor  the floor number
	 * 
	 */
	public int getDestFloor() {
		return destFloor;
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
		return "StartFloor: " + startingFloor +", DestFloor: " + destFloor+ ", Time: " + time.toString() + ", GoingUp: " + goingUp;
	}
}
