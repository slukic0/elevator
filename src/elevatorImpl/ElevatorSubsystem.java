package elevatorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import messages.ElevatorCommandData;
import messages.ElevatorData;
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
	public ElevatorSubsystem(int elevatorNumber, int currentFloor, int receivePort) throws SocketException {
		this.elevator = new Elevator(this, elevatorNumber, currentFloor);
		this.elevatorSendReceiveSocket = new DatagramSocket(receivePort);

		// Start the elevator
		Thread eThread = new Thread(this.elevator);
		eThread.setName(Thread.currentThread().getName());
		eThread.start();
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
		new Thread(() -> {
			try {
				byte[] byteData = NetworkUtils.serializeObject(message);
				NetworkUtils.sendPacket(byteData, elevatorSendReceiveSocket, Constants.SCHEDULER_ELEVATOR_RECEIVE_PORT,
						InetAddress.getByName(Constants.SCHEDULER_ADDRESS));
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
				DatagramPacket elevatorCommandMessage = NetworkUtils.receivePacket(elevatorSendReceiveSocket);
				ElevatorCommandData message = (ElevatorCommandData) NetworkUtils.deserializeObject(elevatorCommandMessage);
				System.out.println("Elevator "+ Thread.currentThread().getName() +" Got Message: " + message.toString());
				elevator.processPacketData(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Main method to run Elevator Subsystem
	 */
	public static void main(String[] args) throws SocketException {
		ElevatorSubsystem elevatorSubsystem1 = new ElevatorSubsystem(1, Constants.STARTING_FLOOR,
				Constants.ELEVATOR_SYS_RECEIVE_PORT1);
		ElevatorSubsystem elevatorSubsystem2 = new ElevatorSubsystem(2, Constants.STARTING_FLOOR,
				Constants.ELEVATOR_SYS_RECEIVE_PORT2);

		Thread eThread1 = new Thread(elevatorSubsystem1);
		eThread1.setName("1");
		Thread eThread2 = new Thread(elevatorSubsystem2);
		eThread2.setName("2");

		eThread1.start();
		eThread2.start();
	}
}
