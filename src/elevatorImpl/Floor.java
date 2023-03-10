package elevatorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import messages.ElevatorData;
import messages.FloorData;
import util.NetworkUtils;

/**
 * Class to represent the floor subsystem
 * 
 * @author Group G5
 *
 */
public class Floor implements Runnable {
//	private Queue<ElevatorData> receiveQueue;
//	private Queue<Object> schedulerQueue;

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
		this.floorReceiveSocket = new DatagramSocket();
	}

//	public void sendMessage(FloorData data) {
//		System.out.println("Floor is sending message to Scheduler: " + data.toString());
//		new Thread(() -> {
//			SendReceiveUtil.sendData(schedulerQueue, data);
//		}).start();
//	}
	public void sendMessageToScheduler(FloorData data) {
		
		new Thread(() -> {
			System.out.println("Floor is sending message to Scheduler: " + data.toString());
			try {
				//DatagramSocket socket = new DatagramSocket(null);
				//socket.connect(floorReceiveSocket.getInetAddress(), floorReceiveSocket.getPort());
				byte[] byteData = NetworkUtils.serializeObject(data);
				
				NetworkUtils.sendPacket(byteData, floorReceiveSocket, Constants.SCHEDULER_FLOOR_RECEIVE_PORT);
				// TODO Scheduler Address
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
				if (message.getCurrentFloor() == message.getMovingToFloor()
						&& message.getState() == ElevatorStates.IDLE) {
					System.out.println("Floor: Elevator has arrived at floor " + message.getCurrentFloor());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
