package messages;

import java.io.Serializable;

/**
 * Data structure class used to hold elevator command data
 * 
 * @author Group G5
 *
 */
public class ElevatorCommandData implements Serializable {

	private static final long serialVersionUID = 8338140153567112284L;
	private int destinationFloor, hardFault, transientFault;

	/**
	 * Creates a ElevatorCommandData object with passed in parameters
	 * 
	 * @param destinationFloor,		int, the elevator's destination floor
	 * @param hardFault, 			int, 1 if there is a hard fault, else 0
	 * @param transientFault, 		int, 1 if there is a transient fault, else 0
	 * 
	 */
	public ElevatorCommandData(int destinationFloor, int hardFault, int transientFault) {
		this.destinationFloor = destinationFloor;
		this.hardFault = hardFault;
		this.transientFault = transientFault;
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
	 * Returns the attribute representing the hard fault status
	 * 
	 * @return hardFault,  			1 if there is a hard fault, else 0
	 * 
	 */
	public int getHardFault(){
		return hardFault;
	}

	/** 
	 * Returns the attribute representing the transient fault status
	 * 
	 * @return transientFault,  	1 if there is a transient fault, else 0
	 * 
	 */
	public int getTransientFault(){
		return transientFault;
	}

	/**
	 * To string method to neatly display the information contained in the elevator command data
	 * 
	 * @return String, 		A string containing all the stored information
	 */
	@Override
	public String toString() {
		return "DestinationFloor: " + destinationFloor + ", HardFault: " + hardFault + ", transientFault: " + transientFault;
	}
}
