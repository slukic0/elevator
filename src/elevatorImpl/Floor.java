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
	 * Creates a floor with shared synchronized message queues and the floor number
	 * 
	 * @param receiveQueue   the synchronized message queue to receive information
	 *                       from the Scheduler
	 * @param schedulerQueue the synchronized message queue to send information to
	 *                       the Scheduler
	 * @param floorNumber    the floor's number
	 * @throws SocketException
	 */
	public Floor() throws SocketException {
		this.floorReceiveSocket = new DatagramSocket(Constants.FLOOR_RECEIVE_PORT);
	}

	/**
	 * Sends Floor data message to Scheduler
	 *
	 * @param data		FloorData, the data to send to scheduler
	 *
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
	 * @param elevatorData		ElevatorData, the data receieved from the scheduler
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
