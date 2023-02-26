package elevator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import elevator.Message.Sender;

/**
 * Represents the Scheduler in the system
 * 
 * @author Group G5
 *
 */
public class Scheduler implements Runnable {
	private Floor[] floors;
	private ArrayList<ElevatorSubsystem> elevatorSystems;

	private Queue<Object> receiveQueue;
	private Queue<FloorData> elevatorRecieveQuque;
	private Queue<ElevatorData> floorRecieveQueue;

	private HashMap<Integer, Boolean> floorUpButtonsMap;
	private HashMap<Integer, Boolean> floorDownButtonsMap;

	private final int NUMBER_OF_FLOORS = Constants.NUMBER_OF_FLOORS;
	private final int STARTING_FLOOR = Constants.STARTING_FLOOR;

	private SchedulerStates state;

	/**
	 * Creates a scheduler with shared synchronized message queues, floors and
	 * elevators in the system
	 * 
	 * @param receiveQueue
	 * @param floorQueue
	 * @param elevatorQueue
	 * @param floors
	 * @param elevators
	 */
	public Scheduler(Queue<Object> receiveQueue, Queue<ElevatorData> floorRecieveQueue,
			Queue<FloorData> elevatorRecieveQuque, Floor[] floors, ArrayList<ElevatorSubsystem> elevatorSubsystems) {
		this.receiveQueue = receiveQueue;
		this.floorRecieveQueue = floorRecieveQueue;
		this.elevatorRecieveQuque = elevatorRecieveQuque;
		this.floors = floors;
		this.elevatorSystems = elevatorSubsystems;
		
		this.floorUpButtonsMap = new HashMap<>();
		this.floorDownButtonsMap = new HashMap<>();
		this.state=SchedulerStates.IDLE;
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
			System.out.println("Scheduler sending message to Elevator subsys : "+ message);
			elevatorRecieveQuque.notifyAll();
		}
	}

	private int findClosestUp(int currFloor) {
		for (int i = currFloor + 1; i <= NUMBER_OF_FLOORS; i++) {
			if (floorUpButtonsMap.containsKey(i)) {
				return i;
			}
		}
		return Integer.MAX_VALUE;
	}

	private int findClosestDown(int currFloor) {
		for (int i = currFloor - 1; i >= STARTING_FLOOR; i--) {
			if (floorDownButtonsMap.containsKey(i)) {
				return i;
			}
		}
		return Integer.MAX_VALUE;
	}

	private boolean checkIfButtonsPressed() {
		return floorDownButtonsMap.isEmpty() && floorUpButtonsMap.isEmpty();
	}

	private int findClosest(int currFloor) {
		int closest = Math.min(findClosestDown(currFloor) - currFloor, findClosestUp(currFloor) - currFloor);

		return (closest > NUMBER_OF_FLOORS) ? Integer.MAX_VALUE : closest;

	}

	/**
	 * Set the floor button as pressed in the HashMaps
	 * 
	 * @param message
	 */
	public void handleFloorRequest(FloorData message) {
		System.out.println("Scheduler marking floor " + message.getFloor() + " as " + message.getGoingUp());
		boolean goingUp = message.getGoingUp();
		int destFloor = message.getFloor();

		if (goingUp) {
			floorUpButtonsMap.put(destFloor, true);
		} else {
			floorDownButtonsMap.put(destFloor, true);
		}
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

		if (checkIfButtonsPressed()) {
			// no more work
			state = SchedulerStates.IDLE;
			floorUpButtonsMap.remove(elevatorCurrFloor);
			floorDownButtonsMap.remove(elevatorCurrFloor);
		} else {
			System.out.println("Scheduler determining next floor for Elevator with state " + eSubsystem.getElevator().getState());

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
			boolean isGoingUp = elevatorDestFloor > elevatorCurrFloor;
			if (isGoingUp) {
				floorUpButtonsMap.remove(elevatorCurrFloor);
			} else {
				floorDownButtonsMap.remove(elevatorCurrFloor);
			}
			FloorData message = new FloorData(elevatorDestFloor, isGoingUp);
			sendElevatorSystemMessage(message);
		}
	}

	public void handleElevatorResponse(ElevatorData message) {
		if (message.getState() == ElevatorStates.IDLE) {
			// looking for work
			sendElevatorCommand();
		} else {
			System.out.println("Scedhuler: Elevator moving ");
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
					System.out.println("Scheduler got message: " + message.toString());

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