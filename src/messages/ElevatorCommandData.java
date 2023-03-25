package messages;

import java.io.Serializable;

public class ElevatorCommandData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8338140153567112284L;
	private int destinationFloor, hardFault, transientFault;

	/**
	 * 
	 * @param destinationFloor floor to arrive at
	 */
	public ElevatorCommandData(int destinationFloor, int hardFault, int transientFault) {
		this.destinationFloor = destinationFloor;
		this.hardFault = hardFault;
		this.transientFault = transientFault;
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
		return "DestinationFloor: " + destinationFloor + ", HardFault: " + hardFault + ", transientFault: " + transientFault;
	}
}
