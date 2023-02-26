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
	
	private int NUMBER_OF_FLOORS;
	private int STARTING_FLOOR;

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
	public Scheduler(int NUMBER_OF_FLOORS, int STARTING_FLOOR, Queue<Object> receiveQueue, Queue<ElevatorData> floorRecieveQueue,
			Queue<FloorData> elevatorRecieveQuque, Floor[] floors, Elevator[] elevators) {
		this.NUMBER_OF_FLOORS = NUMBER_OF_FLOORS;
		this.STARTING_FLOOR = STARTING_FLOOR;
		this.receiveQueue = receiveQueue;
		this.floorRecieveQueue = floorRecieveQueue;
		this.elevatorRecieveQuque = elevatorRecieveQuque;
		this.floors = floors;
		this.floorUpButtonsMap = new HashMap<>();
		this.floorDownButtonsMap = new HashMap<>();
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
			System.out.println("Scheduler forwarded message to elevator");
			elevatorRecieveQuque.notifyAll();
		}
	}
	
	private int findClosestUp(int currFloor) {
		for (int i = currFloor + 1; i <= NUMBER_OF_FLOORS; i++) {
			if (floorUpButtonsMap.get(i)) {
				return i;
			}
		}
		return Integer.MAX_VALUE;
	}
	
	private int findClosestDown(int currFloor) {
		for (int i = currFloor-1; i >= STARTING_FLOOR; i--) {
			if (floorDownButtonsMap.get(i)) {
				return i;
			}
		}
		return Integer.MAX_VALUE;
	}
	
	private boolean checkIfIdle() {
		return floorDownButtonsMap.isEmpty() && floorUpButtonsMap.isEmpty();
	}
	
	private int findClosest(int currFloor) {
		int closest =  Math.min(
			findClosestDown(currFloor) - currFloor,
			findClosestUp(currFloor) - currFloor
		);
		
		return (closest > NUMBER_OF_FLOORS) ? Integer.MAX_VALUE : closest;
		
	}
	

	public void handleFloorRequest(FloorData message) {
		// elevator sent the message

		// TODO: for now we only have 1 elevator
		// in the future we should choose the most efficient elevator
		// and tell the elevator subsystem which elevator to move
		// for (Elevator e : elevatorSystems) {
		// elevatorNumber = e.getELEVATOR_NUMBER();
		// }
		// for now for will select the only subsystem that exists

		ElevatorSubsystem eSubsystem = elevatorSystems.get(0);

		boolean goingUp = message.getGoingUp();
		int destFloor = message.getFloor();
		LocalTime time = message.getTime();

		int elevatorCurrFloor = eSubsystem.getElevator().getCurrentFloor();
		int elevatorDestFloor;
		
		if (goingUp) {
			floorUpButtonsMap.put(destFloor, true);
		} else {
			floorDownButtonsMap.put(destFloor, true);
		}
		
		switch (eSubsystem.getElevator().getState()) {
		case IDLE: {
			elevatorCurrFloor = checkIfIdle() ? destFloor : findClosest(elevatorCurrFloor);
			break;
		}
		case GOING_UP: {
			elevatorCurrFloor = findClosestUp(elevatorCurrFloor);
			break;
		}
		case GOING_DOWN: {
	
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + eSubsystem.getElevator().getState();
		}

		
		sendElevatorSystemMessage(new FloorData(elevatorDestFloor, goingUp, time));
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
						// TODO
						sendFloorSystemMessage((ElevatorData) message);
					}
				}

				receiveQueue.notifyAll();
			}
		}
	}
}