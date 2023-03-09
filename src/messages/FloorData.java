package messages;

import java.io.Serializable;
import java.time.LocalTime;

public class FloorData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8338140153567912284L;
	private LocalTime time;
	private int startingFloor, destinationFloor;
	private boolean goingUp;

	/**
	 * 
	 * @param startingFloor floor to start at
	 * @param destinationFloor floor to arrive at
	 * @param goingUp true if going up, false if down
	 * @param time time of request
	 */
	public FloorData(int startingFloor, int destinationFloor, boolean goingUp, LocalTime time) {
		this.startingFloor = startingFloor;
		this.destinationFloor = destinationFloor;
		this.goingUp = goingUp;
		this.time = time;
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
	 * @return startingFloor  the startingfloor number
	 * 
	 */
	public int getStartingFloor() {
		return startingFloor;
	}

	/** 
	 * Returns the attribute representing the destination floor number 
	 * 
	 * @return destinationFloor  the destination floor number
	 * 
	 */
	public int getDestinationFloor() {
		return destinationFloor;
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
		return "StartingFloor: " + startingFloor + ", DestinationFloor: " + destinationFloor + ", Time: " + time.toString() + ", GoingUp: " + goingUp;
	}
}
