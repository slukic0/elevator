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

		this.state = SchedulerStates.IDLE;
	}

	/**
	 * Constructor created for test
	 * @param elevatorPort
	 * @param floorPort
	 * @throws SocketException
	 */
	public Scheduler(int elevatorPort, int floorPort) throws SocketException{
		this.schedulerElevatorSendReceiveSocket = new DatagramSocket(elevatorPort);
		this.schedulerFloorSendReceiveSocket = new DatagramSocket(floorPort);

		this.elevatorMap = new HashMap<>();

		this.state = SchedulerStates.IDLE;
	}
	
	/**
	 * Gets the scheduler's floor socket
	 * 
	 * @return schedulerFloorSendReceiveSocket	 the scheduler's floor socket
	 */
	public DatagramSocket getFloorSocket() {
		return this.schedulerFloorSendReceiveSocket;
	}

	public void setElevatorMap(HashMap<Integer, ElevatorStatus> elevatorMap) {
		this.elevatorMap = elevatorMap;
	}

	/*
	 * Checks all up-moving elevators if a floor request is on its way
	 * e.g. If elevator x is moving from floor 2 to floor 5 and a request to go up
	 * at at floor 4 comes in,
	 * elevator x should stop at floor 4 since it's on its way
	 * 
	 * @return int[], [id of elevator, distance in floors of closet elevator found]
	 */
	public int[] checkUpElevatorsEnRoute(int requestFloor) {
		int diff = Integer.MAX_VALUE; // Track distance for elevators in motion, lowest is best
		int elevatorId = -1;
		ElevatorData elevatorMessage;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			// Iterate to find closest elevator, record distance and elevator number
			elevatorMessage = elevator.getValue().getLatestMessage();
			if (elevatorMessage.getState() == ElevatorStates.GOING_UP
					&& elevatorMessage.getCurrentFloor() < requestFloor) {
				if (Math.abs(requestFloor - elevatorMessage.getCurrentFloor()) < diff) {
					diff = requestFloor - elevatorMessage.getCurrentFloor();
					elevatorId = elevator.getValue().getLatestMessage().getELEVATOR_NUMBER();
				}
			}
		}
		return new int[] { elevatorId, diff };
	}

	/*
	 * Checks all down-moving elevators if a floor request is on its way
	 * e.g. If elevator x is moving from floor 8 to floor 3 and a request to go dwon
	 * at at floor 4 comes in,
	 * elevator x should stop at floor 4 since it's on its way
	 * 
	 * @return int[], [id of elevator, distance in floors of closet elevator found]
	 */
	public int[] checkDownElevatorsEnRoute(int requestFloor) {
		int diff = Integer.MAX_VALUE; // Track distance for elevators in motion, lowest is best
		int elevatorId = -1;
		ElevatorData elevatorMessage;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			elevatorMessage = elevator.getValue().getLatestMessage();
			if (elevatorMessage.getState() == ElevatorStates.GOING_DOWN
					&& elevatorMessage.getCurrentFloor() > requestFloor) {
				// Iterate to find closest elevator, record distance and elevator number
				if (Math.abs(elevatorMessage.getCurrentFloor() - requestFloor) < diff) {
					diff = Math.abs(elevatorMessage.getCurrentFloor() - requestFloor);
					elevatorId = elevatorMessage.getELEVATOR_NUMBER();
				}
			}
		}
		return new int[] { elevatorId, diff };
	}

	/**
	 * Check free elevators to find closest to the request floor
	 * 
	 * @param requestFloor
	 * @return int[], [id of elevator, distance in floors of closet elevator found]
	 */
	public int[] checkAllElevators(int requestFloor) {
		int diff = Integer.MAX_VALUE;
		int elevatorId = -1;
		ElevatorData elevatorMessage;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			elevatorMessage = elevator.getValue().getLatestMessage();
			if (elevatorMessage.getState() == ElevatorStates.IDLE) {
				// Iterate to find closest elevator, record distance and elevator number
				if (Math.abs(requestFloor - elevatorMessage.getCurrentFloor()) < diff) {
					diff = Math.abs(requestFloor - elevatorMessage.getCurrentFloor());
					elevatorId = elevatorMessage.getELEVATOR_NUMBER();
				}
			}
			if (elevatorMessage.getState() == ElevatorStates.GOING_DOWN
					|| elevatorMessage.getState() == ElevatorStates.GOING_UP) {
				if (Math.abs(elevatorMessage.getMovingToFloor() - elevatorMessage.getCurrentFloor())
						+ Math.abs(requestFloor - elevatorMessage.getCurrentFloor()) < diff) {
					diff = Math.abs(elevatorMessage.getMovingToFloor() - elevatorMessage.getCurrentFloor())
							+ Math.abs(requestFloor - elevatorMessage.getCurrentFloor());
					elevatorId = elevatorMessage.getELEVATOR_NUMBER();
				}
			}
		}
		return new int[] { elevatorId, diff };
	}

	/**
	 * Gets the elevator move command 
	 *
	 * @param startFloor		the elevator's starting floor
	 * @param destFloor			the elevator's destination floor
	 * @param goingUp			true if the evelator going up, else false
	 * @param hardFault			1 if there is a hard fault, else 0
	 * @param transientFault	1 if there is a transient fault, else 0
	 *
	 * @return FloorData		FloorData containing the elevator's next instructions
	 */
	public FloorData getElevatorMoveCommand(int startFloor, int destFloor, boolean goingUp, int hardFault,
			int transientFault) {
		return new FloorData(startFloor, destFloor, LocalTime.now(), hardFault, transientFault);
	}

	/**
	 * Find the closest elevator to a floor. Check free and moving elevators
	 * 
	 * @param requestFloor int, the floor sending a request to go up or down
	 * @param goingUp      boolean, direction of button on floor
	 */
	public int findClosestElevator(int requestFloor, boolean goingUp) {
		state = SchedulerStates.FINDING_CLOSEST_ELEVATOR;
		int[] closestEnRoute;
		int[] closestElevators;
		// Check if request floor is on the way of a moving elevator
		if (goingUp) {
			closestEnRoute = checkUpElevatorsEnRoute(requestFloor);
		} else {
			closestEnRoute = checkDownElevatorsEnRoute(requestFloor);
		}
		// If the floor is on the way of a moving elevator, then we can just task that
		// elevator to stop
		// at the request floor to pickup and continue
		if (closestEnRoute[1] != Integer.MAX_VALUE) {
			return closestEnRoute[0];
		}

		closestElevators = checkAllElevators(requestFloor);
		return closestElevators[0];
	}

	// /**
	//  * Scheduling Algorithm
	//  */
	// public int findElevatorForMove(int floor, boolean goingUp) {
	// 	// TODO update algo
	// 	state = SchedulerStates.FINDING_CLOSEST_ELEVATOR;
	// 	// Get all free elevators (idle)
	// 	int diff = Integer.MAX_VALUE;
	// 	int elevatorNum = -1;
	// 	for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
	// 		if (elevator.getValue().getLatestMessage().getState() == ElevatorStates.IDLE) {
	// 			int currFloor = elevator.getValue().getLatestMessage().getCurrentFloor();
	// 			System.out.println(
	// 					"Elevator " + elevator.getKey() + "(free) is " + Math.abs(currFloor - floor) + " floors away");
	// 			if (Math.abs(currFloor - floor) < diff) {
	// 				diff = Math.abs(currFloor - floor);
	// 				elevatorNum = elevator.getKey();
	// 			}
	// 		}
	// 	}

	// 	// Get destinations of moving elevators
	// 	int movingElevatorNum = -1;
	// 	for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
	// 		if (elevator.getValue().getLatestMessage().getState() != ElevatorStates.IDLE) {
	// 			int destFloor = elevator.getValue().getLatestMessage().getMovingToFloor();
	// 			System.out.println(
	// 					"Elevator " + elevator.getKey() + "(in use) is " + Math.abs(destFloor - floor)
	// 							+ " floors away");
	// 			if ((Math.abs(destFloor - floor)) < diff) { // Priority to free elevators
	// 				diff = Math.abs(destFloor - floor);
	// 				movingElevatorNum = elevator.getKey();
	// 			}
	// 		}
	// 	}

	// 	// If a moving elevator was closer
	// 	if (movingElevatorNum != -1) {
	// 		System.out.println("Elevator " + movingElevatorNum + " has been selected");
	// 		return movingElevatorNum;
	// 	} else {
	// 		System.out.println("Elevator " + elevatorNum + " has been selected");
	// 		return elevatorNum;
	// 	}
	// }

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

			System.out
					.println("Got Message: Elevator " + elevatorMessage.getELEVATOR_NUMBER()
							+ " moving from floor " + elevatorMessage.getCurrentFloor() + " to floor "
							+ elevatorMessage.getMovingToFloor() + ", arrival at "
							+ elevatorMessage.getArrivalTime());

			// TODO determine when to forward messages to floor

			// if (elevatorMessage.getState() == ElevatorStates.ARRIVED || ){

			// }
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

			boolean goingUp = floorMessage.getGoingUp();
			int startFloor = floorMessage.getStartingFloor();
			int destFloor = floorMessage.getDestinationFloor();
			int hardFault = floorMessage.getHardFault();
			int transientFault = floorMessage.getTransientFault();

			System.out.println(
					"Marked floor " + floorMessage.getStartingFloor() + " as GoingUp: "
							+ floorMessage.getGoingUp());
			// TODO what elevator to send this to?
			int elevatorNumber = findClosestElevator(startFloor, goingUp);
			// getElevatorMoveCommand should probably tell us this

			state = SchedulerStates.DISPTACHING_ELEVATOR;

			FloorData message = getElevatorMoveCommand(startFloor, destFloor, goingUp, hardFault, transientFault);
			byte[] data = NetworkUtils.serializeObject(message);
			System.out.println("Send message to Elevator " + elevatorNumber + " " + message.toString());
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

	/**
	 * Main method to run Scheduler
	 */
	public static void main(String[] args) throws SocketException {
		Scheduler scheduler = new Scheduler();
		Thread sThread = new Thread(scheduler);
		sThread.start();
		System.out.println("Scheduler starting...");
	}


}