package elevatorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;

import messages.ElevatorData;
import messages.FloorData;
import messages.ElevatorCommandData;
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

	public HashMap<Integer, Deque<Integer>> elevatorQueueMap;

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

		this.elevatorQueueMap = new HashMap<>();

		this.state = SchedulerStates.IDLE;
	}

	public DatagramSocket getFloorSocket() {
		return this.schedulerFloorSendReceiveSocket;
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

	public ElevatorCommandData getElevatorMoveCommand(int destFloor, int hardFault, int transientFault) {
		return new ElevatorCommandData(destFloor, hardFault, transientFault);
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

			if (elevatorMessage.getState() == ElevatorStates.ARRIVED) { // Remove the destination now that the elevator
																		// had arrived
				elevatorQueueMap.get(elevatorMessage.getELEVATOR_NUMBER()).removeFirst();
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

			boolean goingUp = floorMessage.getGoingUp();
			int startFloor = floorMessage.getStartingFloor();
			int destFloor = floorMessage.getDestinationFloor();
			int hardFault = floorMessage.getHardFault();
			int transientFault = floorMessage.getTransientFault();

			System.out.println(
					"Marked floor " + floorMessage.getStartingFloor() + " as GoingUp: "
							+ floorMessage.getGoingUp());
			int elevatorNumber = findClosestElevator(startFloor, goingUp);

			state = SchedulerStates.DISPTACHING_ELEVATOR;

			// check if we need to interupt the elevator
			ElevatorData latestData = elevatorMap.get(elevatorNumber).getLatestMessage();

			if (latestData.getState() == ElevatorStates.IDLE) { // free elevator
				Queue<Integer> queue = elevatorQueueMap.get(elevatorNumber);
				int prevDestFloor = latestData.getMovingToFloor();
				if (queue == null) {
					Deque<Integer> newDeque = new ArrayDeque<>();
					newDeque.offerLast(prevDestFloor);
					elevatorQueueMap.put(elevatorNumber, newDeque);
				} else {
					queue.offer(prevDestFloor);
				}
				ElevatorCommandData message = getElevatorMoveCommand(destFloor, hardFault, transientFault);
				byte[] data = NetworkUtils.serializeObject(message);
				System.out.println("Send message to Elevator " + elevatorNumber + " " + message.toString());
				NetworkUtils.sendPacket(data, schedulerElevatorSendReceiveSocket,
						elevatorMap.get(elevatorNumber).getPort(),
						elevatorMap.get(elevatorNumber).getAddress());

			} else {
				// Check If Interupt needed

				// CASES
				//
				// BOTH UP, new Dest is after old dest
				// BOTH DOWN, new Dest is after old dest

				boolean elevatorGoingUp = latestData.getCurrentFloor() < latestData.getMovingToFloor();
				boolean newGoingUp = startFloor < destFloor;

				if (elevatorGoingUp == newGoingUp) { // both going up or both going down
					if (startFloor < elevatorMap.get(elevatorNumber).getLatestMessage().getMovingToFloor()
							&& newGoingUp) { // Interupt needed going down
						// BOTH UP, new start is before old dest
						if (destFloor < elevatorMap.get(elevatorNumber).getLatestMessage().getMovingToFloor()) {
							// New destination is before old destination
							// Insert new dest infront of old dest
							elevatorQueueMap.get(elevatorNumber).offerFirst(destFloor);
							// Insert starting floor to stop at as first dest
							elevatorQueueMap.get(elevatorNumber).offerFirst(startFloor);
						} else {
							// New destination is after old destination
							// Remove old dest to be able to put new dest after it
							int oldDest = elevatorQueueMap.get(elevatorNumber).removeFirst();
							// Insert new dest infront
							elevatorQueueMap.get(elevatorNumber).offerFirst(destFloor);
							// Insert old dest infront of new dest
							elevatorQueueMap.get(elevatorNumber).offerFirst(oldDest);
							// Insert starting floor to stop at as first dest
							elevatorQueueMap.get(elevatorNumber).offerFirst(startFloor);
						}

					} else if (startFloor > elevatorMap.get(elevatorNumber).getLatestMessage().getMovingToFloor()
							&& !newGoingUp) { // Interupt needed going down
						// BOTH DOWN, new start is before old dest
						if (destFloor > elevatorMap.get(elevatorNumber).getLatestMessage().getMovingToFloor()) {
							// New destination is before old destination
							// Insert new dest infront of old dest
							elevatorQueueMap.get(elevatorNumber).offerFirst(destFloor);
							// Insert starting floor to stop at as first dest
							elevatorQueueMap.get(elevatorNumber).offerFirst(startFloor);
						} else {
							// New destination is after old destination
							// Remove old dest to be able to put new dest after it
							int oldDest = elevatorQueueMap.get(elevatorNumber).removeFirst();
							// Insert new dest infront
							elevatorQueueMap.get(elevatorNumber).offerFirst(destFloor);
							// Insert old dest infront of new dest
							elevatorQueueMap.get(elevatorNumber).offerFirst(oldDest);
							// Insert starting floor to stop at as first dest
							elevatorQueueMap.get(elevatorNumber).offerFirst(startFloor);
						}
					}

				} else {
					// Not on the way, so add at end
					elevatorQueueMap.get(elevatorNumber).addLast(destFloor);
					elevatorQueueMap.get(elevatorNumber).addLast(startFloor);
				}
			}
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
		System.out.println("Scheduler starting...");
	}
}