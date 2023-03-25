package elevatorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import messages.ElevatorData;
import messages.FloorData;
import util.NetworkUtils;

/**
 * Represents the Scheduler in the system
 * 
 * @author Group G5
 *
 */
public class Scheduler implements Runnable {
	private DatagramSocket schedulerFloorSendReceiveSocket;
	private DatagramSocket schedulerElevatorSendReceiveSocket;

	public HashMap<Integer, Integer> floorUpButtonsMap;
	public HashMap<Integer, Integer> floorDownButtonsMap;

	public HashMap<Integer, ElevatorStatus> elevatorMap; // all the elevators in our system

	private final int NUMBER_OF_FLOORS = Constants.NUMBER_OF_FLOORS;
	private final int STARTING_FLOOR = Constants.STARTING_FLOOR;

	private SchedulerStates state;

	/**
	 * Creates scheduler objects
	 * 
	 * @param receiveQueue         receive Queue from floor
	 * @param floorRecieveQueue    Queue for floor to receive
	 * @param elevatorRecieveQuque Queue for elevator to receive
	 * @throws SocketException thrown if sockets cannot be created
	 */
	public Scheduler() throws SocketException {
		this.schedulerElevatorSendReceiveSocket = new DatagramSocket(Constants.SCHEDULER_ELEVATOR_RECEIVE_PORT);
		this.schedulerFloorSendReceiveSocket = new DatagramSocket(Constants.SCHEDULER_FLOOR_RECEIVE_PORT);

		this.elevatorMap = new HashMap<>();

		this.floorUpButtonsMap = new HashMap<>();
		this.floorDownButtonsMap = new HashMap<>();
		this.state = SchedulerStates.IDLE;
	}
	
	public DatagramSocket getFloorSocket() {
		return this.schedulerFloorSendReceiveSocket;
	}

	/*
	 * Checks all up-moving elevators if a floor request is on its way
	 * e.g. If elevator x is moving from floor 2 to floor 5 and a request to go up at at floor 4 comes in,
	 * elevator x should stop at floor 4 since it's on its way
	 * 
	 * @return int[], [id of elevator, distance in floors of closet elevator found]
	 */
	public int[] checkUpElevators(int requestFloor) {
		state = SchedulerStates.FINDING_CLOSEST_ELEVATOR;
		int diff = Integer.MAX_VALUE; // Track distance for elevators in motion, lowest is best
		int elevatorId = -1;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			// Iterate to find closest elevator, record distance and elevaor number
			if (elevator.getValue().getLatestMessage().getState() == ElevatorStates.GOING_UP){
				if(Math.abs(requestFloor - elevator.getValue().getLatestMessage().getCurrentFloor()) < diff) {
					diff = requestFloor - elevator.getValue().getLatestMessage().getCurrentFloor();
					elevatorId = elevator.getValue().getLatestMessage().getELEVATOR_NUMBER();
				}
			}
		}
		return new int[]{elevatorId, diff};
	}

	/*
	 * Checks all down-moving elevators if a floor request is on its way
	 * e.g. If elevator x is moving from floor 8 to floor 3 and a request to go dwon at at floor 4 comes in,
	 * elevator x should stop at floor 4 since it's on its way
	 * 
	 * @return int[], [id of elevator, distance in floors of closet elevator found]
	 */
	public int[] checkDownElevators(int requestFloor) {
		state = SchedulerStates.FINDING_CLOSEST_ELEVATOR;
		int diff = Integer.MAX_VALUE; // Track distance for elevators in motion, lowest is best
		int elevatorId = -1;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			if (elevator.getValue().getLatestMessage().getState() == ElevatorStates.GOING_DOWN){
				// Iterate to find closest elevator, record distance and elevaor number
				if(Math.abs(elevator.getValue().getLatestMessage().getCurrentFloor() - requestFloor) < diff) {
					diff = elevator.getValue().getLatestMessage().getCurrentFloor() - requestFloor;
					elevatorId = elevator.getValue().getLatestMessage().getELEVATOR_NUMBER();
				}
			}
		}
		return new int[]{elevatorId, diff};
	}

	public FloorData getElevatorMoveCommand(int startFloor, int destFloor, boolean goingUp, int hardFault, int transientFault) {
		return new FloorData(startFloor, destFloor, goingUp, LocalTime.now(), hardFault, transientFault);
	}

	/**
	 * Scheduling Algorithm
	 */
	public int findElevatorForMove(int floor, boolean goingUp) {
		state = SchedulerStates.FINDING_CLOSEST_ELEVATOR;
		// Get all free elevators (idle)
		int diff = Integer.MAX_VALUE;
		int elevatorNum = -1;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			if (elevator.getValue().getLatestMessage().getState() == ElevatorStates.IDLE) {
				int currFloor = elevator.getValue().getLatestMessage().getCurrentFloor();
				System.out.println(
						"Elevator " + elevator.getKey() + "(free) is " + Math.abs(currFloor - floor) + " floors away");
				if (Math.abs(currFloor - floor) < diff) {
					diff = Math.abs(currFloor - floor);
					elevatorNum = elevator.getKey();
				}
			}
		}

		// Get destinations of moving elevators
		int movingElevatorNum = -1;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			if (elevator.getValue().getLatestMessage().getState() != ElevatorStates.IDLE) {
				int destFloor = elevator.getValue().getLatestMessage().getMovingToFloor();
				System.out.println(
						"Elevator " + elevator.getKey() + "(in use) is " + Math.abs(destFloor - floor) + " floors away");
				if ((Math.abs(destFloor - floor)) < diff) { // Priority to free elevators
					diff = Math.abs(destFloor - floor);
					movingElevatorNum = elevator.getKey();
				}
			}
		}

		// Floor has been tasked to an elevator, remove button flag
		if (goingUp) {
			floorUpButtonsMap.remove(floor);
		} else {
			floorDownButtonsMap.remove(floor);
		}

		// If a moving elevator was closer
		if (movingElevatorNum != -1) {
			System.out.println("Elevator " + movingElevatorNum + " has been selected");
			return movingElevatorNum;
		} else {
			System.out.println("Elevator " + elevatorNum + " has been selected");
			return elevatorNum;
		}
	}

	/**
	 * Receives packets from an elevator
	 * 
	 * @throws IOException
	 */
	public void receiveElevator() throws IOException {
		while (true) {
			DatagramPacket elevatorPacket = NetworkUtils.receivePacket(schedulerElevatorSendReceiveSocket);
			ElevatorData elevatorMessage = (ElevatorData) NetworkUtils.deserializeObject(elevatorPacket);
			int senderPort = elevatorPacket.getPort();
			InetAddress senderAddress = elevatorPacket.getAddress();

			// Update the status of the received elevator data
			elevatorMap.put(elevatorMessage.getELEVATOR_NUMBER(),
					new ElevatorStatus(senderAddress, senderPort, elevatorMessage));

			if (elevatorMessage.getState() == ElevatorStates.IDLE) {
				// tell the floor elevator has arrived
				System.out.println(
						"Scheduler forwarding floor elevator " + elevatorMessage.getELEVATOR_NUMBER() + " arrival");
			} else {
				System.out
						.println("Scheduler got reply: Elevator " + elevatorMessage.getELEVATOR_NUMBER()
								+ " moving from floor " + elevatorMessage.getCurrentFloor() + " to floor "
								+ elevatorMessage.getMovingToFloor() + ", arrival at "
								+ elevatorMessage.getArrivalTime());
			}
			NetworkUtils.sendPacket(elevatorPacket.getData(), schedulerFloorSendReceiveSocket,
						Constants.FLOOR_RECEIVE_PORT, InetAddress.getByName(Constants.FLOOR_ADDRESS));
		}
	}

	/**
	 * Receives data from the floor
	 * 
	 * @throws IOException
	 */
	public void receiveFloor() throws IOException {
		while (true) {
			DatagramPacket floorPacket = NetworkUtils.receivePacket(schedulerFloorSendReceiveSocket);
			FloorData floorMessage = (FloorData) NetworkUtils.deserializeObject(floorPacket);

			System.out.println("Scheduler got message " + floorMessage);

			boolean goingUp = floorMessage.getGoingUp();
			int startFloor = floorMessage.getStartingFloor();
			int destFloor = floorMessage.getDestinationFloor();
			int hardFault = floorMessage.getHardFault();
			int transientFault = floorMessage.getTransientFault();

			if (goingUp) {
				floorUpButtonsMap.put(startFloor, destFloor);
			} else {
				floorDownButtonsMap.put(startFloor, destFloor);
			}
			System.out.println(
					"Scheduler marked floor " + floorMessage.getStartingFloor() + " as GoingUp: "
							+ floorMessage.getGoingUp());
			// TODO what elevator to send this to?
			int elevatorNumber = findElevatorForMove(startFloor, goingUp);
			// getElevatorMoveCommand should probably tell us this

			state = SchedulerStates.DISPTACHING_ELEVATOR;

			FloorData message = getElevatorMoveCommand(startFloor, destFloor, goingUp, hardFault, transientFault);
			byte[] data = NetworkUtils.serializeObject(message);
			System.out.println("Scheduler send message to Elevator " + elevatorNumber + " " + message.toString());
			NetworkUtils.sendPacket(data, schedulerElevatorSendReceiveSocket, elevatorMap.get(elevatorNumber).getPort(),
					elevatorMap.get(elevatorNumber).getAddress());
		}
	}

	/**
	 * Runs the scheduler's thread
	 */
	public void run() {
		new Thread(() -> {
			try {
				this.receiveElevator();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			try {
				this.receiveFloor();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public static void main(String[] args) throws SocketException {
		Scheduler scheduler = new Scheduler();
		Thread sThread = new Thread(scheduler);
		sThread.start();

	}
}