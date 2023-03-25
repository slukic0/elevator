package messages;

import java.io.Serializable;
import java.time.LocalTime;

public class FloorData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8338140153567912284L;
	private LocalTime time;
	private int startingFloor, destinationFloor, hardFault, transientFault;
	private boolean goingUp;

	/**
	 * 
	 * @param startingFloor floor to start at
	 * @param destinationFloor floor to arrive at
	 * @param time time of request
	 */
	public FloorData(int startingFloor, int destinationFloor, LocalTime time, int hardFault, int transientFault) {
		this.startingFloor = startingFloor;
		this.destinationFloor = destinationFloor;
		this.time = time;
		this.goingUp = destinationFloor - startingFloor > 0;
		this.hardFault = hardFault;
		this.transientFault = transientFault;
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

	public int getHardFault(){
		return hardFault;
	}

	public int getTransientFault(){
		return transientFault;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "StartingFloor: " + startingFloor + ", DestinationFloor: " + destinationFloor + ", Time: " + time.toString() + ", GoingUp: " + goingUp +  ", HardFault: " + hardFault + ", transientFault: " + transientFault;
	}
}
