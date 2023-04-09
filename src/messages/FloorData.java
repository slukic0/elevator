package messages;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Data structure class used to hold floor data
 * 
 * @author Group G5
 *
 */
public class FloorData implements Serializable {
	private static final long serialVersionUID = 8338140153567912284L;
	private LocalTime time;
	private int startingFloor, destinationFloor, hardFault, transientFault;
	private boolean goingUp;

	/**
	 * Creates a FloorData object with passed in parameters
	 * 
	 * @param startingFloor,		int, the floor to start at
	 * @param destinationFloor, 	int, the floor to arrive at
	 * @param time, 				LocalTime, the time of the request
	 * @param hardFault,			int, 1 if there is a hard fault, else 0
	 * @param transientFault,		int, 1 if there is a transient fault, else 0
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
	 * @return startingFloor,  		the starting floor number
	 * 
	 */
	public int getStartingFloor() {
		return startingFloor;
	}

	/** 
	 * Returns the attribute representing the destination floor number 
	 * 
	 * @return destinationFloor,	the destination floor number
	 * 
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	/**
	 * Returns a boolean value indicating whether the elevator is going up or not
	 * 
	 * @return goingUp, 			true if elevator is going up, else false
	 */
	public boolean getGoingUp() {
		return goingUp;
	}

	/**
	 * Returns the status of a potential hard fault
	 * 
	 * @return hardFault, 			1 if there is a hard fault, else 0
	 */
	public int getHardFault(){
		return hardFault;
	}

	/**
	 * Returns the status of a potential transient fault
	 * 
	 * @return transientFault, 		1 if there is a transient fault, else 0
	 */
	public int getTransientFault(){
		return transientFault;
	}

	/**
	 * To string method to neatly display the information contained in the floor data
	 * 
	 * @return String, 		A string containing all the stored information
	 */
	@Override
	public String toString() {
		return "StartingFloor: " + startingFloor + ", DestinationFloor: " + destinationFloor + ", Time: " + time.toString() + ", GoingUp: " + goingUp +  ", HardFault: " + hardFault + ", transientFault: " + transientFault;
	}
}
