package elevator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

/**
 * Represents the Scheduler in the system
 * 
 * @author Group G5
 *
 */
public class Scheduler implements Runnable {
	private ArrayList<ElevatorSubsystem> elevatorSystems;

	private Queue<Object> receiveQueue;
	private Queue<FloorData> elevatorRecieveQuque;
	private Queue<ElevatorData> floorRecieveQueue;

	public HashMap<Integer, Boolean> floorUpButtonsMap;
	public HashMap<Integer, Boolean> floorDownButtonsMap;

	private final int NUMBER_OF_FLOORS = Constants.NUMBER_OF_FLOORS;
	private final int STARTING_FLOOR = Constants.STARTING_FLOOR;

	private SchedulerStates state;

	/**
	 * Creates scheduler objects
	 * @param receiveQueue			receive Queue from floor
	 * @param floorRecieveQueue		Queue for floor to receive
	 * @param elevatorRecieveQuque  Queue for elevator to receive
	 * @param floors				list of floors
	 * @param elevatorSubsystems	list of Elevator Subsystems
	 */
	public Scheduler(Queue<Object> receiveQueue, Queue<ElevatorData> floorRecieveQueue,
			Queue<FloorData> elevatorRecieveQuque, Floor[] floors, ArrayList<ElevatorSubsystem> elevatorSubsystems) {
		this.receiveQueue = receiveQueue;
		this.floorRecieveQueue = floorRecieveQueue;
		this.elevatorRecieveQuque = elevatorRecieveQuque;
		this.elevatorSystems = elevatorSubsystems;

		this.floorUpButtonsMap = new HashMap<>();
		this.floorDownButtonsMap = new HashMap<>();
		this.state = SchedulerStates.IDLE;
	}

	/**
	 * Gets the scheduler's receive queue
	 * 
	 * @return receiveQueue scheduler's receive queue
	 */
	public Queue<Object> getreceiveQueue() {
		return this.receiveQueue;
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

	/*
	 * Returns the floor above current
	 * @return int, the closest floor above currFloor or 2*NUMBER_OF_FLOORS if no floors exist.
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
		return 2*NUMBER_OF_FLOORS;
	}

	/*
	 * Returns the floor below current
	 * @return int, the closest floor below currFloor or 2*NUMBER_OF_FLOORS if no floors exist.
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
		return 2*NUMBER_OF_FLOORS;
	}

	/**
	 * A very hacky function to find the closest floor that needs an elevator
	 * @param currFloor int, floor the elevator is on
	 * @return the closest floor or 2*NUMBER_OF_FLOORS if no floors exist.
	 */
	private int findClosest(int currFloor) {
		int closestDown = findClosestDown(currFloor); // TODO only checks for down buttons below it
		int closestUp = findClosestUp(currFloor); // TODO only checks for up buttons above it
		// TODO 
		// what if we are going down, and the only floor left is a 
		// a floor below us that is going up ???
		if (closestDown == 2*NUMBER_OF_FLOORS &&  closestUp == 2*NUMBER_OF_FLOORS) {
			return 2*NUMBER_OF_FLOORS;
		} else if (closestUp == 2*NUMBER_OF_FLOORS){
			return closestDown == 2*NUMBER_OF_FLOORS ? 2*NUMBER_OF_FLOORS : closestDown;
		} else if (closestDown == 2*NUMBER_OF_FLOORS) {
			return closestUp == 2*NUMBER_OF_FLOORS ? 2*NUMBER_OF_FLOORS : closestUp;
		} else {
			if (currFloor - closestDown < closestUp - currFloor){
				return closestDown;
			} else {
				return closestUp;
			}
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
		}
	}

	/**
	 * Tell the elevator to move if needed
	 */
	public void sendElevatorCommand() {
		ElevatorSubsystem eSubsystem = elevatorSystems.get(0);

		int elevatorCurrFloor = eSubsystem.getElevator().getCurrentFloor();
		int elevatorDestFloor;

		System.out.println("Scheduler determining next floor for Elevator with elevator state "
				+ eSubsystem.getElevator().getState());

		// figure out where to move
		switch (eSubsystem.getElevator().getState()) {
		case IDLE: {
			elevatorDestFloor = findClosest(elevatorCurrFloor);
			break;
		}
		case GOING_UP: {
			elevatorDestFloor = !floorUpButtonsMap.isEmpty() ? findClosestUp(elevatorCurrFloor)
					: findClosestDown(elevatorCurrFloor);
			break;
		}
		case GOING_DOWN: {
			elevatorDestFloor = !floorDownButtonsMap.isEmpty() ? findClosestDown(elevatorCurrFloor)
					: findClosestUp(elevatorCurrFloor);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + eSubsystem.getElevator().getState());
		}

		if (elevatorDestFloor == 2*NUMBER_OF_FLOORS) {
			System.out.println("No work left to do!");
			// no more work
			state = SchedulerStates.IDLE;
			floorUpButtonsMap.remove(elevatorCurrFloor);
			floorDownButtonsMap.remove(elevatorCurrFloor);
		} else {
			// tell the elevator where to go
			
			//clear the button the elevator is at
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
			System.out
					.println("Scheduler sending elevator to floor " + elevatorDestFloor + ", isGoingUp: " + isFutureStateGoingUp);
			FloorData message = new FloorData(elevatorDestFloor, isFutureStateGoingUp);
			sendElevatorSystemMessage(message);
		}
	}

	public void handleElevatorResponse(ElevatorData message) {
		if (message.getState() == ElevatorStates.IDLE) {
			System.out.println("Scheduler got reply: Elevator looking for work");
			sendElevatorCommand();
		} else {
			System.out.println("Scheduler got reply: Elevator moving, arrival at" + message.getArrivalTime());
		}
	}

	/**
	 * Runs the scheduler's thread
	 */
	public void run() {
		while (true) {
			synchronized (receiveQueue) {
				while (receiveQueue.isEmpty()) {
					try {
						receiveQueue.wait();
					} catch (InterruptedException e) {
						System.out.println("Error in Scheduler Thread");
						e.printStackTrace();
					}
				}
				// get the message(s)
				for (int i = 0; i < receiveQueue.size(); i++) {

					Object message = receiveQueue.poll();

					if (message instanceof FloorData) {
						handleFloorRequest((FloorData) message);

					} else if (message instanceof ElevatorData) {
						// one of the elevators sent us this message
						handleElevatorResponse((ElevatorData) message);
					}
				}

				receiveQueue.notifyAll();
			}
		}
	}
}