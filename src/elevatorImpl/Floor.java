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
	 * Runs the floor's thread
	 */
	public void run() {
		while (true) {
			try {
				DatagramPacket elevatorMessage = NetworkUtils.receivePacket(floorReceiveSocket);
				ElevatorData message = (ElevatorData) NetworkUtils.deserializeObject(elevatorMessage);
				System.out.println("Floor received message: " + message.toString());
				if (message.getCurrentFloor() == message.getMovingToFloor()) {
					System.out.println("Floor: Elevator has arrived at floor " + message.getCurrentFloor());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws SocketException, IOException {
		Floor floor = new Floor();
		Thread fThread = new Thread(floor);
		fThread.start();

		String[] input = FileUtil.readFile(floor.getClass(), "events.txt");
		FloorData[] data = FileUtil.parseStringInput(input);
		for (FloorData d : data) {
			System.out.println("Adding FloorData: " + d.toString());
			floor.sendMessageToScheduler(d);
		}
	}

}
