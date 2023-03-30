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
	private Queue<Integer> hardFaultQueue;
	private Queue<Integer> transientFaultQueue;
	private boolean isStuck;
	private boolean exitFlag;
	private boolean sleepFlag;

	/**
	 * Creates an elevator ties to an Elevator Subsystem with the elevator number and its current floor
	 * 
	 * @param elevatorSubsystem   the synchronized message queue to receive information
	 *                       from the Scheduler
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
		this.hardFaultQueue = new LinkedList<Integer>();
		this.transientFaultQueue = new LinkedList<Integer>();
		this.isStuck = false;
		this.exitFlag = false;
		this.sleepFlag = false;
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

	public void setHardFaultFlag(){
		this.exitFlag = true;
	}

	public boolean getHardFaultFlag(){
		return this.exitFlag;
	}

	public void setTransientFaultFlag(){
		this.sleepFlag = true;
	}

	public boolean getTransientFaultFlag(){
		return this.sleepFlag;
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
	 * Gets the elevator's destination floor queue
	 * 
	 * @return destFloorQueue the elevator's destination floor queue
	 */
	public Queue<Integer> getDestQueue(){
		return this.destFloorQueue;
	}

	/**
	 * Gets the elevator's hard fault queue
	 * 
	 * @return hardFaultQueue the elevator's hard fault queue
	 */
	public Queue<Integer> getHardFaultQueue(){
		return this.hardFaultQueue;
	}

	/**
	 * Gets the elevator's transient fault queue
	 * 
	 * @return transientFaultQueue the elevator's transient fault queue
	 */
	public Queue<Integer> getTransientFaultQueue(){
		return this.transientFaultQueue;
	}
	
	/**
	 * Process information from floor related to elevator, including 
	 * starting and destination floors, and faults
	 * @param data FloorData, message from floor
	 */
	public void processPacketData(FloorData data) {
		this.destFloorQueue.offer(data.getStartingFloor());
		this.destFloorQueue.offer(data.getDestinationFloor());
		if (this.state == ElevatorStates.IDLE) {
			this.state = ElevatorStates.PROCESSING;
			this.wake();
		}

		this.hardFaultQueue.offer(0);
		this.hardFaultQueue.offer(data.getHardFault());
		this.transientFaultQueue.offer(0);
		this.transientFaultQueue.offer(data.getTransientFault());
	}
	
	public void setIsStuck(){
		this.isStuck = true;
	}

	public boolean getIsStuck(){
		return this.isStuck;
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
					this.destinationFloor = destFloorQueue.poll();
					ElevatorStates newState = destinationFloor > this.currentFloor ? ElevatorStates.GOING_UP : ElevatorStates.GOING_DOWN;
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
				System.out.println("Elevator " + this.ELEVATOR_NUMBER + " Moving to floor " + destinationFloor);
				int diff = destinationFloor - currentFloor;
				elevatorSubsystem.sendSchedulerMessage(new ElevatorData(state, prevDirection, currentFloor,
						destinationFloor, LocalTime.now().plusSeconds(2 * diff), ELEVATOR_NUMBER));

				System.out.println("Diff:" + diff);
				for (int i=1; i <=  Math.abs(diff); i++){
					try {
						Thread.sleep(2000);

						System.out.println("HELLO");

						if(diff > 0){
							currentFloor++;
						} else if (diff < 0){
							currentFloor--;
						}
						
						elevatorSubsystem.sendSchedulerMessage(new ElevatorData(state, prevDirection, currentFloor,
								destinationFloor, LocalTime.now().plusSeconds(2 * (diff-i)), ELEVATOR_NUMBER));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				System.out.println(
						"Elevator " + ELEVATOR_NUMBER + " has arrived at floor " + currentFloor);

				//Check for Timer fault
				if (this.hardFaultQueue.poll() == 1) {
					System.out.println("\nTiming event fault\n");
					setIsStuck();
				} else {
					System.out.println("\nElse hit\n");
					this.state = ElevatorStates.ARRIVED;
				}
				break;
			
			case ARRIVED:
				// wait for a bit
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if(this.transientFaultQueue.poll() == 1){
					this.setTransientFaultFlag();
					System.out.println("\nElevator " + ELEVATOR_NUMBER+": Door stuck fault\n");
					//Handle transient fault
					try {
						Thread.sleep(2000);
						System.out.println("\nElevator " + ELEVATOR_NUMBER +": Door has been fixed\n");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// wait for a bit
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currState = state;

				// Tell the floor we have arrived
				elevatorSubsystem.sendSchedulerMessage(
						new ElevatorData(state, prevDirection, currentFloor, destinationFloor, LocalTime.now(),
								ELEVATOR_NUMBER));

				this.state = ElevatorStates.PROCESSING;
				
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + state);
			}
		}
		setHardFaultFlag();
		System.err.println("Elevator " + ELEVATOR_NUMBER + " shutdown");
	}
}
