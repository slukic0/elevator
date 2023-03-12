package elevatorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;
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

	/*
	 * Returns the floor above current
	 * 
	 * @return int, the closest floor above currFloor or 2*NUMBER_OF_FLOORS if no
	 * floors exist.
	 */
	private int findClosestUp(int currFloor) {
		for (int i = currFloor + 1; i <= NUMBER_OF_FLOORS; i++) {
			if (floorUpButtonsMap.containsKey(i)) {
				return i;
			}
		}
		for (int i = currFloor - 1; i >= STARTING_FLOOR; i--) {
			if (floorUpButtonsMap.containsKey(i)) {
				return i;
			}
		}
		return 2 * NUMBER_OF_FLOORS;
	}

	/*
	 * Returns the floor below current
	 * 
	 * @return int, the closest floor below currFloor or 2*NUMBER_OF_FLOORS if no
	 * floors exist.
	 */
	private int findClosestDown(int currFloor) {
		for (int i = currFloor - 1; i >= STARTING_FLOOR; i--) {
			if (floorDownButtonsMap.containsKey(i)) {
				return i;
			}
		}
		for (int i = currFloor + 1; i <= NUMBER_OF_FLOORS; i++) {
			if (floorDownButtonsMap.containsKey(i)) {
				return i;
			}
		}
		return 2 * NUMBER_OF_FLOORS;
	}

	/**
	 * A very hacky function to find the closest floor that needs an elevator
	 * 
	 * @param currFloor int, floor the elevator is on
	 * @return the closest floor or 2*NUMBER_OF_FLOORS if no floors exist.
	 */
	private int findClosest(int currFloor) {
		int closestDown = findClosestDown(currFloor);
		int closestUp = findClosestUp(currFloor);

		if (closestDown == 2 * NUMBER_OF_FLOORS && closestUp == 2 * NUMBER_OF_FLOORS) {
			return 2 * NUMBER_OF_FLOORS;
		} else if (closestUp == 2 * NUMBER_OF_FLOORS) {
			return closestDown == 2 * NUMBER_OF_FLOORS ? 2 * NUMBER_OF_FLOORS : closestDown;
		} else if (closestDown == 2 * NUMBER_OF_FLOORS) {
			return closestUp == 2 * NUMBER_OF_FLOORS ? 2 * NUMBER_OF_FLOORS : closestUp;
		} else {
			if (currFloor - closestDown < closestUp - currFloor) {
				return closestDown;
			} else {
				return closestUp;
			}
		}
	}

	public FloorData getElevatorMoveCommand(int startFloor, int destFloor, boolean goingUp) {
		return new FloorData(startFloor, destFloor, goingUp, LocalTime.now());
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
			int currFloor = elevator.getValue().getLatestMessage().getCurrentFloor();
			if (Math.abs(currFloor - floor) < diff) {
				elevatorNum = elevator.getKey();
			}
		}

		// Get destinations of moving elevators
		int movingElevatorNum = -1;
		for (Map.Entry<Integer, ElevatorStatus> elevator : elevatorMap.entrySet()) {
			if (elevator.getValue().getLatestMessage().getState() != ElevatorStates.IDLE) {
				int currFloor = elevator.getValue().getLatestMessage().getMovingToFloor();
				if (Math.abs(currFloor - floor) * 2 < diff) { // Priority to free elevators
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
			return movingElevatorNum;
		} else {
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
				System.out.println("Scheduler forwarding floor elevator arrival");
				NetworkUtils.sendPacket(elevatorPacket.getData(), schedulerFloorSendReceiveSocket,
						Constants.FLOOR_RECEIVE_PORT);
			} else {
				System.out
						.println("Scheduler got reply: Elevator moving, arrival at" + elevatorMessage.getArrivalTime());
			}
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

			if (goingUp) {
				floorUpButtonsMap.put(startFloor, destFloor);
			} else {
				floorDownButtonsMap.put(startFloor, destFloor);
			}
			System.out.println(
					"Scheduler marked floor " + floorMessage.getStartingFloor() + " as GoingUp: "
							+ floorMessage.getGoingUp()); /// Should this be starting or destination floor?
			// TODO what elevator to send this to?
			int elevatorNumber = findElevatorForMove(startFloor, goingUp);
			// getElevatorMoveCommand should probably tell us this

			state = SchedulerStates.DISPTACHING_ELEVATOR;

			FloorData message = getElevatorMoveCommand(startFloor, destFloor, goingUp);
			byte[] data = NetworkUtils.serializeObject(message);
			System.out.println("Scheduler send message to Elevator " + message.toString());
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
}