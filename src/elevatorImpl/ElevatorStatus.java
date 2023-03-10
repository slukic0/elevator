package elevatorImpl;

import java.net.InetAddress;

import messages.ElevatorData;

public class ElevatorStatus {
	private InetAddress address;
	private int port;
	private ElevatorData latestMessage;

	public ElevatorStatus(InetAddress address, int port, ElevatorData latestMessage) {
		this.address = address;
		this.port = port;
		this.latestMessage = latestMessage;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public ElevatorData getLatestMessage() {
		return latestMessage;
	}

	public void setLatestMessage(ElevatorData latestMessage) {
		this.latestMessage = latestMessage;
	}
}
