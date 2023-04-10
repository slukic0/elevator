package elevatorImpl;

import java.net.InetAddress;

import messages.ElevatorData;

/**
 * Class to represent the elevator's status
 * 
 * @author Group G5
 *
 */
public class ElevatorStatus {
	private InetAddress address;
	private int port;
	private ElevatorData latestMessage;

	/**
	 * Creates elevator status
	 * 
	 * @param address	   	 the address of the message
	 * @param port	 		 the port of the message
	 * @param latestMessage  the latest received message
	 */
	public ElevatorStatus(InetAddress address, int port, ElevatorData latestMessage) {
		this.address = address;
		this.port = port;
		this.latestMessage = latestMessage;
	}

	/**
	 * Gets the elevatorStatus's address
	 * 
	 * @return address	 the elevatorStatus's address
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * Gets the elevatorStatus's address
	 * 
	 * @return port	 	the elevatorStatus's port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the elevatorStatus's latest message
	 * 
	 * @return latestMessage	 the elevatorStatus's latest message
	 */
	public ElevatorData getLatestMessage() {
		return latestMessage;
	}

	/**
	 * Sets the elevatorStatus's latest message
	 * 
	 * @param latestMessage	  	Message to be set
	 */
	public void setLatestMessage(ElevatorData latestMessage) {
		this.latestMessage = latestMessage;
	}
}
