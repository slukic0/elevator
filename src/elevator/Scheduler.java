package elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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
	private ArrayList<ElevatorSubsystem> elevatorSystems;

//	private Queue<Object> receiveQueue;
//	private Queue<FloorData> elevatorRecieveQuque;
//	private Queue<ElevatorData> floorRecieveQueue;
	// replace queues with one socket for FloorData, one socket for ElevatorData
	// don't need to know floor/elevator port, just respond using the packet port
	// and address

	private DatagramSocket schedulerFloorReceiveSocket;
	private DatagramSocket schedulerElevatorReceiveSocket;


	public HashMap<Integer, Boolean> floorUpButtonsMap;
	public HashMap<Integer, Boolean> floorDownButtonsMap;

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
		this.schedulerElevatorReceiveSocket = new DatagramSocket(Constants.SCHEDULER_ELEVATOR_RECEIVE_PORT);
		this.schedulerFloorReceiveSocket = new DatagramSocket(Constants.SCHEDULER_FLOOR_RECEIVE_PORT);
		this.elevatorSystems = elevatorSubsystems;

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
	 * Tell the elevator to move if needed
	 */
	public void sendElevatorCommand() {
		ElevatorSubsystem eSubsystem = elevatorSystems.get(0);

		int elevatorCurrFloor = eSubsystem.getElevator().getCurrentFloor();
		int elevatorDestFloor = 2 * NUMBER_OF_FLOORS;

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
			if (elevatorDestFloor == 2*NUMBER_OF_FLOORS) {
				elevatorDestFloor = findClosestDown(elevatorCurrFloor);
			}
		} else if (downButtonSet) {
			System.out.println("Current floor button is DOWN - find closest down or up if empty");
			elevatorDestFloor = findClosestDown(elevatorCurrFloor);
			if (elevatorDestFloor == 2*NUMBER_OF_FLOORS) {
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
			FloorData message = new FloorData(elevatorDestFloor, isFutureStateGoingUp);
			sendElevatorSystemMessage(message);
		}
	}

	/**
	 * Sends a message to the floor subsystem
	 * 
	 * @param message the message to send to the floor
	 */
	public void sendFloorSystemMessage(ElevatorData message) {
		synchronized (floorRecieveQueue) {
			floorRecieveQueue.add(message);
			System.out.println("Scheduler forwarded message to floor");
			floorRecieveQueue.notifyAll();
		}
	}

	/**
	 * Set the floor button as pressed in the HashMaps
	 * 
	 * @param message FloorData, message received from floor
	 */
	public void handleFloorRequest(FloorData message) {
		System.out.println("Scheduler got message " + message);
		System.out.println("Scheduler marking floor " + message.getFloor() + " as GoingUp: " + message.getGoingUp());
		boolean goingUp = message.getGoingUp();
		int destFloor = message.getFloor();

		if (goingUp) {
			floorUpButtonsMap.put(destFloor, true);
		} else {
			floorDownButtonsMap.put(destFloor, true);
		}
		System.out.println("Scheduler marked floor " + message.getFloor() + " as GoingUp: " + message.getGoingUp());
		state = SchedulerStates.WOKRING;
		if (elevatorSystems.get(0).getElevator().getState() == ElevatorStates.IDLE) {
			sendElevatorCommand();
			elevatorSystems.get(0).getElevator().setState(ElevatorStates.PROCESSING);
		}
	}

	/**
	 * Sends a message to the elevator subsystem
	 * 
	 * @param message the message to send to the elevator
	 */
	public void sendElevatorSystemMessage(FloorData message) {
		synchronized (elevatorRecieveQuque) {
			elevatorRecieveQuque.add(message);
			System.out.println("Scheduler sending message to Elevator subsys : " + message);
			elevatorRecieveQuque.notifyAll();
		}
	}

	public void handleElevatorResponse(ElevatorData message) {
		if (message.getState() == ElevatorStates.IDLE) {
			System.out.println("Scheduler forwarding floor elevator arrival");
			sendFloorSystemMessage(message);

			System.out.println("Scheduler got reply: Elevator looking for work");
			sendElevatorCommand();
		} else {
			System.out.println("Scheduler got reply: Elevator moving, arrival at" + message.getArrivalTime());
		}
	}

	public void receiveElevator() throws IOException {
		while(true) {
			DatagramPacket elevatorPacket = NetworkUtils.receivePacket(schedulerElevatorReceiveSocket);
			ElevatorData elevatorMessage = (ElevatorData) NetworkUtils.deserializeObject(elevatorPacket);
			int senderPort = elevatorPacket.getPort();
			InetAddress senderAddress = elevatorPacket.getAddress();
		}
	}

	public void receiveFloor() throws IOException {
		while (true) {
			DatagramPacket floorPacket = NetworkUtils.receivePacket(schedulerFloorReceiveSocket);
			FloorData floorMessage = (FloorData) NetworkUtils.deserializeObject(floorPacket);
			int senderPort = floorPacket.getPort();
			InetAddress senderAddress = floorPacket.getAddress();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			try {
				this.receiveFloor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}
}