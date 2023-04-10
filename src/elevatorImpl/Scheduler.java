package elevatorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
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

	/**
	 * Constructor created for test
	 * 
	 * @param elevatorPort
	 * @param floorPort
	 * @throws SocketException
	 */
	public Scheduler(int elevatorPort, int floorPort) throws SocketException {
		this.schedulerElevatorSendReceiveSocket = new DatagramSocket(elevatorPort);
		this.schedulerFloorSendReceiveSocket = new DatagramSocket(floorPort);

		this.elevatorMap = new HashMap<>();

		this.elevatorQueueMap = new HashMap<>();

		this.state = SchedulerStates.IDLE;
	}

	public DatagramSocket getFloorSocket() {
		return this.schedulerFloorSendReceiveSocket;
	}

	public void setElevatorMap(HashMap<Integer, ElevatorStatus> elevatorMap) {
		this.elevatorMap = elevatorMap;
	}

	public void setElevatorQueueMap(HashMap<Integer, Deque<Integer>> elevatorQueueMap) {
		this.elevatorQueueMap = elevatorQueueMap;
	}

	public HashMap<Integer, ElevatorStatus> getElevatorMap() {
		return this.elevatorMap;
	}

	public void checkHardFault(ElevatorData elevatorMessage) {
		if (elevatorMessage.getHardFault()) {
			// Remove elevator from system if unrecoverable fault occurs
			System.out.println(
					"Removing elevator " + elevatorMessage.getELEVATOR_NUMBER() + " from system due to timing fault");
			elevatorQueueMap.remove(elevatorMessage.getELEVATOR_NUMBER());
			elevatorMap.remove(elevatorMessage.getELEVATOR_NUMBER());
		}
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
		int minDiff = Integer.MAX_VALUE; // Track distance for elevators in motion, lowest is best
		int elevatorId = -1;
		ElevatorData elevatorMessage;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			// Iterate to find closest elevator, record distance and elevator number
			elevatorMessage = elevator.getValue().getLatestMessage();
			if (elevatorMessage.getState() == ElevatorStates.GOING_UP
					&& elevatorMessage.getCurrentFloor() <= requestFloor) {
				if (Math.abs(requestFloor - elevatorMessage.getCurrentFloor()) < minDiff) {
					minDiff = requestFloor - elevatorMessage.getCurrentFloor();
					elevatorId = elevator.getValue().getLatestMessage().getELEVATOR_NUMBER();
				}
			}
		}
		return new int[] { elevatorId, minDiff };
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
		int minDiff = Integer.MAX_VALUE; // Track distance for elevators in motion, lowest is best
		int elevatorId = -1;
		ElevatorData elevatorMessage;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			elevatorMessage = elevator.getValue().getLatestMessage();
			if (elevatorMessage.getState() == ElevatorStates.GOING_DOWN
					&& elevatorMessage.getCurrentFloor() >= requestFloor) {
				// Iterate to find closest elevator, record distance and elevator number
				if (Math.abs(elevatorMessage.getCurrentFloor() - requestFloor) < minDiff) {
					minDiff = Math.abs(elevatorMessage.getCurrentFloor() - requestFloor);
					elevatorId = elevatorMessage.getELEVATOR_NUMBER();
				}
			}
		}
		return new int[] { elevatorId, minDiff };
	}

	/**
	 * Check all elevators to find closest to the request floor
	 * 
	 * @param requestFloor
	 * @return int[], [id of elevator, distance in floors of closet elevator found]
	 */
	public int[] checkAllElevators(int requestFloor) {
		int minDiff = Integer.MAX_VALUE;
		int elevatorId = -1;
		ElevatorData elevatorMessage;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			elevatorMessage = elevator.getValue().getLatestMessage();
			if (elevatorMessage.getState() == ElevatorStates.ARRIVED) {
				// Iterate to find closest elevator, record distance and elevator number
				if (Math.abs(requestFloor - elevatorMessage.getCurrentFloor()) < minDiff) {
					minDiff = Math.abs(requestFloor - elevatorMessage.getCurrentFloor());
					elevatorId = elevatorMessage.getELEVATOR_NUMBER();
				}
			} else if (elevatorMessage.getState() == ElevatorStates.GOING_DOWN
					|| elevatorMessage.getState() == ElevatorStates.GOING_UP) {

				int elevatorNumber = elevator.getKey();
				int currentFloorDifference = Math
						// Distance from current floor to final destination
						.abs(elevatorQueueMap.get(elevatorNumber).peekLast() - elevatorMessage.getCurrentFloor())
						// Trying to take the total number of destinations into account 
						+ elevatorQueueMap.get(elevatorNumber).size()
						// Distance from final destination to new request
						+ Math.abs(requestFloor - elevatorQueueMap.get(elevatorNumber).peekLast());
				if (currentFloorDifference < minDiff) { 
					minDiff = currentFloorDifference;
					elevatorId = elevatorMessage.getELEVATOR_NUMBER();
				}
			}
		}
		return new int[] { elevatorId, minDiff };
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
	 * Place the new destination ahead of the old destination
	 * 
	 * @param elevatorNumber int, the elevator being used
	 * @param startFloor     int, starting floor
	 * @param destFloor      int, new destination floor
	 * @return int, the new moving to floor after reaching the start floor
	 */
	public int moveNewDestInfrontOfOld(int elevatorNumber, int startFloor, int destFloor) {
		// Insert new dest infront of old dest
		elevatorQueueMap.get(elevatorNumber).offerFirst(destFloor);
		// Insert starting floor to stop at as first dest
		elevatorQueueMap.get(elevatorNumber).offerFirst(startFloor);
		return destFloor;
	}

	/**
	 * Place the new destination after the old destination
	 * 
	 * @param elevatorNumber int, the elevator being used
	 * @param startFloor     int, starting floor
	 * @param destFloor      int, new destination floor
	 * @return int, the new moving to floor after reaching the start floor
	 */
	public int moveNewDestAfterfOld(int elevatorNumber, int startFloor, int destFloor) {
		// Remove old dest to be able to put new dest after it
		int oldDest = elevatorQueueMap.get(elevatorNumber).removeFirst();
		// Insert new dest infront
		elevatorQueueMap.get(elevatorNumber).offerFirst(destFloor);
		// Insert old dest infront of new dest
		elevatorQueueMap.get(elevatorNumber).offerFirst(oldDest);
		// Insert starting floor to stop at as first dest
		elevatorQueueMap.get(elevatorNumber).offerFirst(startFloor);
		return oldDest;
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
			int elevatorNumber = elevatorMessage.getELEVATOR_NUMBER();

			// Update the status of the received elevator data
			synchronized (this) {
				// System.out.println("Got E Message: Num " +
				// elevatorMessage.getELEVATOR_NUMBER() + ", Cur "
				// + elevatorMessage.getCurrentFloor() + ", Dest " +
				// elevatorMessage.getMovingToFloor()
				// + ", State "
				// + elevatorMessage.getState());
				elevatorMap.put(elevatorNumber, new ElevatorStatus(senderAddress, senderPort, elevatorMessage));

				elevatorMap.put(elevatorMessage.getELEVATOR_NUMBER(),
						new ElevatorStatus(senderAddress, senderPort, elevatorMessage));

				// if (elevatorMessage.getHardFault()) {
				// // Remove elevator from system if unrecoverable fault occurs
				// System.out.println("Removing elevator " +
				// elevatorMessage.getELEVATOR_NUMBER() + " from system due to timing fault");
				// elevatorQueueMap.remove(elevatorMessage.getELEVATOR_NUMBER());
				// elevatorMap.remove(elevatorMessage.getELEVATOR_NUMBER());
				// }

				checkHardFault(elevatorMessage);

				if (elevatorMessage.getState() == ElevatorStates.ARRIVED) {
					System.out.println(elevatorNumber + " arrived at " + elevatorMessage.getCurrentFloor());

					// Remove the destination now from the queue
					if (elevatorQueueMap.get(elevatorNumber) != null
							&& elevatorQueueMap.get(elevatorNumber).peekFirst() != null) {
						int floor = elevatorQueueMap.get(elevatorNumber).peek();
						System.out.println("Removing floor " + floor + " from elevator " + elevatorNumber + " queue");
						elevatorQueueMap.get(elevatorNumber).removeFirst();
					}

					// inform the floor of the arrival
					NetworkUtils.sendPacket(elevatorPacket.getData(), schedulerFloorSendReceiveSocket,
							Constants.FLOOR_RECEIVE_PORT, InetAddress.getByName(Constants.FLOOR_ADDRESS));

					System.out.println(elevatorNumber + " IDLE, getting next floor");

					if (elevatorQueueMap.get(elevatorNumber) != null
							&& elevatorQueueMap.get(elevatorNumber).peekFirst() != null) {

						ElevatorCommandData message = getElevatorMoveCommand(
								elevatorQueueMap.get(elevatorNumber).peekFirst(), 0, 0);

						byte[] data = NetworkUtils.serializeObject(message);
						System.out.println("Send message to Elevator " + elevatorNumber + " "
								+ message.toString());

						NetworkUtils.sendPacket(data, schedulerElevatorSendReceiveSocket,
								elevatorMap.get(elevatorNumber).getPort(),
								elevatorMap.get(elevatorNumber).getAddress());
					}
				}
			}
			System.out.println(elevatorNumber + ": " + this.elevatorQueueMap.get(elevatorNumber));
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

			state = SchedulerStates.DISPTACHING_ELEVATOR;

			synchronized (this) {
				System.out.println(
						"Got Message: Floor " + floorMessage.getStartingFloor() + " GoingUp: "
								+ floorMessage.getGoingUp());
				int elevatorNumber = findClosestElevator(startFloor, goingUp);

				// check if we need to interupt the elevator
				ElevatorData latestData = elevatorMap.get(elevatorNumber).getLatestMessage();

				if (latestData.getState() == ElevatorStates.ARRIVED) { // free elevator
					Deque<Integer> queue = elevatorQueueMap.get(elevatorNumber);
					if (queue == null) {
						// create new queue
						Deque<Integer> newDeque = new ArrayDeque<>();
						newDeque.offerLast(startFloor);
						newDeque.offerLast(destFloor);
						elevatorQueueMap.put(elevatorNumber, newDeque);
					} else {
						queue.offerLast(startFloor);
						queue.offerLast(destFloor);
					}
					// set elevator to NOT ARRIVED
					ElevatorStates newState = startFloor > elevatorMap.get(elevatorNumber).getLatestMessage()
							.getCurrentFloor() ? ElevatorStates.GOING_UP : ElevatorStates.GOING_DOWN;
					elevatorMap.get(elevatorNumber).getLatestMessage().setState(newState);

				} else {
					latestData = elevatorMap.get(elevatorNumber).getLatestMessage();
					boolean elevatorGoingUp = latestData.getCurrentFloor() < latestData.getMovingToFloor();

					if (elevatorGoingUp == goingUp) { // both going up or both going down
						int newMovingToFloor = latestData.getMovingToFloor();
						if (startFloor < latestData.getMovingToFloor()
								&& goingUp && latestData.getCurrentFloor() <= startFloor) { // Interrupt needed going
																							// down
							// BOTH UP, new start is before old dest
							if (destFloor < latestData.getMovingToFloor()) {
								// New destination is before old destination
								newMovingToFloor = moveNewDestInfrontOfOld(elevatorNumber, startFloor, destFloor);
							} else {
								// BOTH UP, new Dest is after old dest
								newMovingToFloor = moveNewDestAfterfOld(elevatorNumber, startFloor, destFloor);
							}

						} else if (startFloor > latestData.getMovingToFloor()
								&& !goingUp && latestData.getCurrentFloor() >= startFloor) { // Interrupt needed going
																								// down
							// BOTH DOWN, new start is before old dest
							if (destFloor > latestData.getMovingToFloor()) {
								// New destination is before old destination
								newMovingToFloor = moveNewDestInfrontOfOld(elevatorNumber, startFloor, destFloor);
							} else {
								// BOTH DOWN, new Dest is after old dest
								newMovingToFloor = moveNewDestAfterfOld(elevatorNumber, startFloor, destFloor);
							}
						}
						latestData.setMovingToFloor(newMovingToFloor);
					} else { // Elevator is going one way, floor is going the other way
						// Not on the way, so add at end
						elevatorQueueMap.get(elevatorNumber).addLast(startFloor);
						elevatorQueueMap.get(elevatorNumber).addLast(destFloor);
					}
				}
				ElevatorCommandData message = getElevatorMoveCommand(elevatorQueueMap.get(elevatorNumber).peekFirst(),
						hardFault, transientFault);
				byte[] data = NetworkUtils.serializeObject(message);
				System.out.println("Send message to Elevator " + elevatorNumber + " " + message.toString());
				NetworkUtils.sendPacket(data, schedulerElevatorSendReceiveSocket,
						elevatorMap.get(elevatorNumber).getPort(),
						elevatorMap.get(elevatorNumber).getAddress());
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