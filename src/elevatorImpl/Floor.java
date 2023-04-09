package elevatorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import messages.ElevatorData;
import messages.FloorData;
import util.NetworkUtils;

import util.FileUtil;

/**
 * Class to represent the floor subsystem
 * 
 * @author Group G5
 *
 */
public class Floor implements Runnable {

	private DatagramSocket floorReceiveSocket;

	/**
	 * Creates a floor with a receive socket
	 * 
	 * @throws SocketException
	 */
	public Floor() throws SocketException {
		this.floorReceiveSocket = new DatagramSocket(Constants.FLOOR_RECEIVE_PORT);
	}

	/**
	 * Creates a floor with a receive socket using a desired port number
	 * 
	 * @param port,			int, the port number for the Datagram Socket
	 * 
	 * @throws SocketException
	 */
	public Floor(int port) throws SocketException {
		this.floorReceiveSocket = new DatagramSocket(port);
	}

	/**
	 * Sends a FloorData message to the scheduler
	 * 
	 * @param data,			FloorData, the FloorData to send to the scheduler
	 */
	public void sendMessageToScheduler(FloorData data) {

		new Thread(() -> {
			try {
				// wait before sending
				Thread.sleep(data.getTime().toSecondOfDay() * 1000);

				byte[] byteData = NetworkUtils.serializeObject(data);
				System.out.println("Floor sending message " + data.toString());
				NetworkUtils.sendPacket(byteData, floorReceiveSocket, Constants.SCHEDULER_FLOOR_RECEIVE_PORT,
						InetAddress.getByName(Constants.SCHEDULER_ADDRESS));
			} catch (Exception e) {
				System.err.println("FLOOR ERROR: sendMessageToScheduler()");
				e.printStackTrace();
			}

		}).start();
	}
	
	/**
	 * Checks the message received from the scheduler
	 *
	 * @param elevatorData,		ElevatorData, the data received from the scheduler
	 *
	 * @return  false if evelatorData is null, else true
	 */
	public boolean checkMessage(ElevatorData elevatorData) {
		if (elevatorData == null) {
			return false;
		}
		else {
			if (elevatorData.getCurrentFloor() == elevatorData.getMovingToFloor()) {
				System.out.println("Floor: Elevator " + elevatorData.getELEVATOR_NUMBER() + " has arrived at floor " + elevatorData.getCurrentFloor());
			}
			return true;
		}
		
	}

	/**
	 * Runs the floor's thread
	 */
	public void run() {
		while (true) {
			try {
				DatagramPacket elevatorMessage = NetworkUtils.receivePacket(floorReceiveSocket);
				ElevatorData message = (ElevatorData) NetworkUtils.deserializeObject(elevatorMessage);
				System.out.println("Got message: " + message.toString());
				this.checkMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Main method to run Floor
	 */
	public static void main(String[] args) throws SocketException, IOException {
		Floor floor = new Floor();
		Thread fThread = new Thread(floor);
		fThread.start();

		String[] input = FileUtil.readFile(floor.getClass(), "events.txt");
		FloorData[] data = FileUtil.parseStringInput(input);
		for (FloorData d : data) {
			floor.sendMessageToScheduler(d);
		}
	}

}
