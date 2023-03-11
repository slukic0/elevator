package elevatorImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

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

	public HashMap<Integer, Boolean> floorUpButtonsMap;
	public HashMap<Integer, Boolean> floorDownButtonsMap;
	
	
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
	 * @param floors               list of floors
	 * @param elevatorSubsystems   list of Elevator Subsystems
	 * @throws SocketException thrown if sockets cannot be created
	 */
	public Scheduler(Floor[] floors, ArrayList<ElevatorSubsystem> elevatorSubsystems) throws SocketException {
		this.schedulerElevatorSendReceiveSocket = new DatagramSocket(Constants.SCHEDULER_ELEVATOR_RECEIVE_PORT);
		this.schedulerFloorSendReceiveSocket = new DatagramSocket(Constants.SCHEDULER_FLOOR_RECEIVE_PORT);

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

	/**
	 * Determine where an elevator should move
	 * 
	 * @return FloorData - the floor to move to or null if no work to do
	 */
	/*
	public FloorData getElevatorMoveCommand() {
		// ElevatorSubsystem eSubsystem = elevatorSystems.get(0);
		// TODO pick the BEST elevator
		// TODO no longer using the subsystem to get elevator data, use internal
		// hashmaps
		ElevatorSubsystem eSubsystem = elevatorMap.get(1);

		int elevatorCurrFloor = eSubsystem.getElevator().getCurrentFloor();
		int elevatorDestFloor = eSubsystem.getElevator().getDestinationFloor();

		System.out.println("Scheduler determining next floor for Elevator with elevator state "
				+ eSubsystem.getElevator().getState());

		// figure out where to move
		boolean upButtonSet = floorUpButtonsMap.get(eSubsystem.getElevator().getCurrentFloor()) != null;
		boolean downButtonSet = floorDownButtonsMap.get(eSubsystem.getElevator().getCurrentFloor()) != null;

		if (upButtonSet && downButtonSet) {
			System.out.println("Current floor button is UP&DOWN - find closest");
			elevatorDestFloor = findClosest(elevatorCurrFloor);
		} else if (upButtonSet) {
			System.out.println("Current floor button is UP - find closest up or down if empty");
			elevatorDestFloor = findClosestUp(elevatorCurrFloor);
			if (elevatorDestFloor == 2 * NUMBER_OF_FLOORS) {
				elevatorDestFloor = findClosestDown(elevatorCurrFloor);
			}
		} else if (downButtonSet) {
			System.out.println("Current floor button is DOWN - find closest down or up if empty");
			elevatorDestFloor = findClosestDown(elevatorCurrFloor);
			if (elevatorDestFloor == 2 * NUMBER_OF_FLOORS) {
				elevatorDestFloor = findClosestUp(elevatorCurrFloor);
			}
		} else {
			// no buttons set
			elevatorDestFloor = findClosest(elevatorCurrFloor);
		}

		if (elevatorDestFloor == 2 * NUMBER_OF_FLOORS) {
			System.out.println("No work left to do!");
			// no more work
			this.state = SchedulerStates.IDLE;
			floorUpButtonsMap.remove(elevatorCurrFloor);
			floorDownButtonsMap.remove(elevatorCurrFloor);
			return null;
		} else {
			// tell the elevator where to go

			// clear the button the elevator is at
			boolean isFutureStateGoingUp = elevatorDestFloor > elevatorCurrFloor;
			if (isFutureStateGoingUp) {
				if (eSubsystem.getElevator().getPrevDirection() == ElevatorStates.GOING_DOWN) {
					System.out.println("Clearing floor " + elevatorCurrFloor + " DOWN");
					floorDownButtonsMap.remove(elevatorCurrFloor);
				}
				System.out.println("Clearing floor " + elevatorCurrFloor + " UP");
				floorUpButtonsMap.remove(elevatorCurrFloor);
			} else {
				if (eSubsystem.getElevator().getPrevDirection() == ElevatorStates.GOING_UP) {
					System.out.println("Clearing floor " + elevatorCurrFloor + " UP");
					floorUpButtonsMap.remove(elevatorCurrFloor);
				}
				System.out.println("Clearing floor " + elevatorCurrFloor + " DOWN");
				floorDownButtonsMap.remove(elevatorCurrFloor);
			}
			System.out.println("Scheduler sending elevator to floor " + elevatorDestFloor + ", isGoingUp: "
					+ isFutureStateGoingUp);
			return new FloorData(elevatorDestFloor, isFutureStateGoingUp, LocalTime.now());
		}
	}

	*/
	// TOOD UNCOMMENT
	public FloorData getElevatorMoveCommand() {
		return new FloorData(1, 5, true, LocalTime.now());
	}
	
	public void receiveElevator() throws IOException {
		while (true) {
			DatagramPacket elevatorPacket = NetworkUtils.receivePacket(schedulerElevatorSendReceiveSocket);
			ElevatorData elevatorMessage = (ElevatorData) NetworkUtils.deserializeObject(elevatorPacket);
			int senderPort = elevatorPacket.getPort();
			InetAddress senderAddress = elevatorPacket.getAddress();
			
			
			///FIX: Error because this.elevatorMap Is null

//			if (!elevatorMap.containsKey(elevatorMessage.getELEVATOR_NUMBER())) {
//				// add elevator to map if not already present
//				elevatorMap.put(elevatorMessage.getELEVATOR_NUMBER(),
//						new ElevatorStatus(senderAddress, senderPort, elevatorMessage));
//			} else {
//				// update elevator state in map
//				elevatorMap.get(elevatorMessage.getELEVATOR_NUMBER()).setLatestMessage(elevatorMessage);
//			}


			if (elevatorMessage.getState() == ElevatorStates.IDLE) {
				// tell the floor elevator has arrived
				System.out.println("Scheduler forwarding floor elevator arrival");
				NetworkUtils.sendPacket(elevatorPacket.getData(), schedulerFloorSendReceiveSocket,
						Constants.FLOOR_RECEIVE_PORT); // TODO floor port?

				// determine next floor to go to
				System.out.println("Scheduler got reply: Elevator looking for work");
				FloorData message = getElevatorMoveCommand();
				if (message != null) { // null if no work to do
					byte[] data = NetworkUtils.serializeObject(message);
					NetworkUtils.sendPacket(data, schedulerElevatorSendReceiveSocket, senderPort, senderAddress);
				}
			} else {
				System.out
						.println("Scheduler got reply: Elevator moving, arrival at" + elevatorMessage.getArrivalTime());
			}
		}
	}

	public void receiveFloor() throws IOException {
		while (true) {
			DatagramPacket floorPacket = NetworkUtils.receivePacket(schedulerFloorSendReceiveSocket);
			FloorData floorMessage = (FloorData) NetworkUtils.deserializeObject(floorPacket);
			int senderPort = floorPacket.getPort();
			InetAddress senderAddress = floorPacket.getAddress();

			System.out.println("Scheduler got message " + floorMessage);

			boolean goingUp = floorMessage.getGoingUp();
			int destFloor = floorMessage.getDestinationFloor();

			if (goingUp) {
				floorUpButtonsMap.put(destFloor, true);
			} else {
				floorDownButtonsMap.put(destFloor, true);
			}
			System.out.println(
					"Scheduler marked floor " + floorMessage.getStartingFloor() + " as GoingUp: " + floorMessage.getGoingUp());			///Should this be starting or destination floor?
			state = SchedulerStates.WOKRING;
			FloorData message = getElevatorMoveCommand();
			// TODO what elevator to send this to?
			// getElevatorMoveCommand should probably tell us this
			byte[] data = NetworkUtils.serializeObject(message);
			System.out.println("Scheduler send message to Elevator " +message.toString());
			NetworkUtils.sendPacket(data, schedulerElevatorSendReceiveSocket, Constants.ELEVATOR_SYS_RECEIVE_PORT, senderAddress);
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