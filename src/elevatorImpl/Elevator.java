package elevatorImpl;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

import messages.ElevatorData;
import messages.FloorData;

/**
 * Class to represent the elevator subsystem
 * 
 * @author Group G5
 * 
 */
public class Elevator implements Runnable {
	private final int ELEVATOR_NUMBER;
	private int currentFloor;
	private int destinationFloor;
	private ElevatorSubsystem elevatorSubsystem;
	private ElevatorStates state;
	private ElevatorStates prevDirection;
	private Queue<Integer> destFloorQueue;
	private int hardFaultStatus;
	private int transientFaultStatus;
	private boolean isStuck;

	/**
	 * Creates an elevator with shared synchronized message queues, the elevator
	 * number and its current floor
	 * 
	 * @param receiveQueue   the synchronized message queue to receive information
	 *                       from the Scheduler
	 * @param subsystemQueue the synchronized message queue to send information to
	 *                       the Scheduler
	 * @param elevatorNumber the elevator number
	 * @param currentFloor   the elevator's current floor
	 */
	public Elevator(ElevatorSubsystem elevatorSubsystem, int elevatorNumber, int currentFloor) {
		this.elevatorSubsystem = elevatorSubsystem;
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.currentFloor = currentFloor;
		this.destinationFloor = currentFloor;
		this.state = ElevatorStates.IDLE;
		this.prevDirection = ElevatorStates.IDLE;
		this.destFloorQueue = new LinkedList<Integer>();
		this.hardFaultStatus = 0;
		this.transientFaultStatus = 0;
		this.isStuck = false;
	}

	/**
	 * Returns elevator's own number
	 * @return int, associated elevator number
	 */
	public int getELEVATOR_NUMBER() {
		return ELEVATOR_NUMBER;
	}
	
	/**
	 * Getter Returns current state
	 * @return ElevatorState, associated elevator state
	 */
	public ElevatorStates getState() {
		return state;
	}
	
	/**
	 * Set the internal state
	 * 
	 * @param state ElevatorState, the state to set
	 */
	public void setState(ElevatorStates state) {
		this.state = state;
	}

	/**
	 * Getter returns previous direction
	 * 
	 * @return ElevatorStates, previous direction of elevator
	 */
	public ElevatorStates getPrevDirection() {
		return prevDirection;
	}

	/**
	 * Gets the elevator's current floor
	 * 
	 * @return currentFloor the elevator's current floor
	 */
	public int getCurrentFloor() {
		return this.currentFloor;
	}

	/**
	 * Sets the elevator's current floor
	 * 
	 * @param floor an integer containing a floor number
	 */
	public void setCurrentFloor(int floor) {
		this.currentFloor = floor;
	}

	/**
	 * Gets the elevator's destination floor
	 * 
	 * @return destinationFloor the elevator's destination floor
	 */
	public int getDestinationFloor() {
		return this.destinationFloor;
	}
	
	/**
	 * Process information from floor related to elevator
	 * @param data FloorData, message from floor
	 */
	public void processPacketData(FloorData data) {
		this.destFloorQueue.offer(data.getStartingFloor());
		this.destFloorQueue.offer(data.getDestinationFloor());
		if (this.state == ElevatorStates.IDLE) {
			this.state = ElevatorStates.PROCESSING;
			this.wake();
		}
		this.hardFaultStatus = data.getHardFault();
		this.transientFaultStatus = data.getTransientFault();
	}
	
	public Queue<Integer> getDestQueue(){
		return this.destFloorQueue;
	}
	
	/**
	 * Triggers synchronized wait function
	 */
	private synchronized void pause() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Triggers notify thread
	 */
	private synchronized void wake() {
		System.out.println("Waking elevator");
		this.notify();
	}

	/**
	 * Runs the elevator's thread
	 */
	public void run() {
		ElevatorStates currState = ElevatorStates.IDLE; // Use to store state before sending data
		while (!isStuck) {
			switch (state) {
			case PROCESSING: {
				if (destFloorQueue.isEmpty()) {
					this.state = ElevatorStates.IDLE;
				} else {
					elevatorSubsystem.sendSchedulerMessage(
						new ElevatorData(state, prevDirection, currentFloor, destinationFloor, LocalTime.now(),
								ELEVATOR_NUMBER)); // Tell the floor we have arrived before moving again
					this.destinationFloor = destFloorQueue.poll();
					ElevatorStates newState = destinationFloor > this.currentFloor ? ElevatorStates.GOING_UP : ElevatorStates.GOING_DOWN;
					System.out.println("Elevator" + this.ELEVATOR_NUMBER + " setting state to " + newState + " and destFloor to " + this.destinationFloor);
					this.state = newState;
				}
				
				break;
			}
			
			case IDLE: {
				System.out.println("Elevator " + this.ELEVATOR_NUMBER + " has no work, asking for work...");

				// tell the scheduler we have arrived and are IDLE (looking for work)
				elevatorSubsystem.sendSchedulerMessage(
						new ElevatorData(state, prevDirection, currentFloor, destinationFloor, LocalTime.now(),
								ELEVATOR_NUMBER));
				prevDirection = currState;

				pause();
				break;
			}
			case GOING_DOWN:
			case GOING_UP:
				// Move the elevator
				System.out.println("Elevator " + this.ELEVATOR_NUMBER + " current state: " + this.state + ", prevDirection: " + prevDirection);
				System.out.println("Elevator " + this.ELEVATOR_NUMBER + " Moving to floor " + destinationFloor);
				int diff = Math.abs(destinationFloor - currentFloor);
				elevatorSubsystem.sendSchedulerMessage(new ElevatorData(state, prevDirection, currentFloor,
						destinationFloor, LocalTime.now().plusSeconds(2 * diff), ELEVATOR_NUMBER));

				if(this.hardFaultStatus == 1){
					System.out.println("Timing event fault");
					// wait for a bit
					try {
						Thread.sleep(2500 * diff);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					isStuck = true;
				}
				else{
					// wait for a bit
					try {
						Thread.sleep(2000 * diff);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("Elevator " + this.ELEVATOR_NUMBER + " has arrived at floor " + destinationFloor);
					currentFloor = destinationFloor;
					currState = state;
					this.state = ElevatorStates.PROCESSING;
				}
				
				
				break;
			
			case DOOR_CLOSE: 
			case DOOR_OPEN:
			default:
				throw new IllegalArgumentException("Unexpected value: " + state);
			}
		}
	}
}
