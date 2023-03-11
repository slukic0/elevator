package elevatorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import messages.ElevatorData;
import messages.FloorData;
import util.NetworkUtils;

public class ElevatorSubsystem implements Runnable {

	private Elevator elevator;
	private DatagramSocket elevatorSendReceiveSocket;;

	/**
	 * Creates Elevator Subsystem object
	 * 
	 * @param receiveQueue          Queue for information received from floor
	 * @param schedulerReceiveQueue Queue for message send to scheduler
	 * @param elevatorNumber        Associated elevator number
	 * @param currentFloor          Associated elevator's current floor
	 * @throws SocketException thrown if socket cannot be created
	 */
	public ElevatorSubsystem(int elevatorNumber, int currentFloor) throws SocketException {
		this.elevator = new Elevator(this, elevatorNumber, currentFloor);
		this.elevatorSendReceiveSocket = new DatagramSocket(Constants.ELEVATOR_SYS_RECEIVE_PORT);
		
		// Start the elevator
		new Thread(this.elevator).start();
	}

	/**
	 * Getter to return elevator object
	 * 
	 * @return returns elevator of subsystem
	 */
	public Elevator getElevator() {
		return elevator;
	}

	/**
	 * Sends message of elevator data to scheduler
	 * 
	 * @param message ElevatorData, message to send to scheduler
	 *
	 */
	public void sendSchedulerMessage(ElevatorData message) {
		System.out.println("Elevator subsystem is sending message to Scheduler: " + message.toString());
		new Thread(() -> {
			try {
				byte[] byteData = NetworkUtils.serializeObject(message);
				NetworkUtils.sendPacket(byteData, elevatorSendReceiveSocket, Constants.SCHEDULER_ELEVATOR_RECEIVE_PORT);
				// TODO Scheduler Address
			} catch (Exception e) {
				System.err.println("FLOOR ERROR: sendSchedulerMessage()");
				e.printStackTrace();
			}

		}).start();
	}

	/**
	 * Runs subsystem's thread
	 */
	@Override
	public void run() {
		while (true) {
			try {
				DatagramPacket floorMessage = NetworkUtils.receivePacket(elevatorSendReceiveSocket);
				FloorData message = (FloorData) NetworkUtils.deserializeObject(floorMessage);
				System.out.println("Elevator SubSystem received " + message);
				elevator.processPacketData(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
