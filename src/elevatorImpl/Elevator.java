package elevatorImpl;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

import messages.ElevatorCommandData;
import messages.ElevatorData;

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
	private Queue<Integer> hardFaultQueue;
	private Queue<Integer> transientFaultQueue;
	private boolean isStuck;
	private boolean exitFlag;
	private Thread elevatorThread;
	private boolean transFlag;
	private String textBufferString = "                                  ";

	/**
	 * Creates an elevator ties to an Elevator Subsystem with the elevator number
	 * and its current floor
	 * 
	 * @param elevatorSubsystem the subsystem associated with this elevator
	 * @param elevatorNumber    the elevator number, must be unique
	 * @param currentFloor      the elevator's current floor
	 */
	public Elevator(ElevatorSubsystem elevatorSubsystem, int elevatorNumber, int currentFloor) {
		this.elevatorThread = Thread.currentThread();
		this.elevatorSubsystem = elevatorSubsystem;
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.currentFloor = currentFloor;
		this.destinationFloor = currentFloor;
		this.state = ElevatorStates.ARRIVED;
		this.hardFaultQueue = new LinkedList<Integer>();
		this.transientFaultQueue = new LinkedList<Integer>();
		this.isStuck = false;
		this.exitFlag = false;
		this.transFlag = false;
	}

	/**
	 * Returns elevator's own number
	 * 
	 * @return int, associated elevator number
	 */
	public int getELEVATOR_NUMBER() {
		return ELEVATOR_NUMBER;
	}

	/**
	 * Getter Returns current state
	 * 
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
	 * Gets the elevator's current floor
	 * 
	 * @return currentFloor the elevator's current floor
	 */
	public int getCurrentFloor() {
		return this.currentFloor;
	}

	public void setFlag() {
		this.exitFlag = true;
	}

	public boolean getFlag() {
		return this.exitFlag;
	}

	public void setTransFlag() {
		this.exitFlag = true;
	}

	public boolean getTransFlag() {
		return this.exitFlag;
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

	public Queue<Integer> getHardFaultQueue() {
		return this.hardFaultQueue;
	}

	/**
	 * Gets the elevator's transient fault queue
	 * 
	 * @return transientFaultQueue the elevator's transient fault queue
	 */
	public Queue<Integer> getTransientFaultQueue() {
		return this.transientFaultQueue;
	}

	/**
	 * Process information from floor related to elevator, including
	 * starting and destination floors, and faults
	 * 
	 * @param data FloorData, message from floor
	 */
	public void processPacketData(ElevatorCommandData data) {
		// this.destFloorQueue.offer(data.getDestinationFloor());
		System.out.print(textBufferString.repeat(ELEVATOR_NUMBER - 1));
		System.out.println("Set new destination to " + data.getDestinationFloor());
		synchronized (this) {
			this.destinationFloor = data.getDestinationFloor();

			// this.hardFaultQueue.offer(0);
			this.hardFaultQueue.offer(data.getHardFault());
			// this.transientFaultQueue.offer(0);
			this.transientFaultQueue.offer(data.getTransientFault());

			if (this.state == ElevatorStates.ARRIVED) {
				this.state = destinationFloor > this.currentFloor ? ElevatorStates.GOING_UP : ElevatorStates.GOING_DOWN;
				this.wake();
			} else {
				//System.out.println("processPacketData interrupt");
				elevatorThread.interrupt();
			}
		}
	}

	public void setIsStuck() {
		this.isStuck = true;
	}

	public boolean getIsStuck() {
		return this.isStuck;
	}

	/**
	 * Tell the elevator thread to wait
	 */
	private synchronized void pause() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Notify the elevator thread
	 */
	private synchronized void wake() {
		//System.out.print(textBufferString.repeat(ELEVATOR_NUMBER - 1));
		//System.out.println("Waking elevator " + this.ELEVATOR_NUMBER);
		this.notify();
	}

	/**
	 * Runs the elevator's thread
	 */
	public void run() {
		while (!isStuck) {
			switch (state) {
				case GOING_DOWN:
				case GOING_UP:
					// Move the elevator
					System.out.print(textBufferString.repeat(ELEVATOR_NUMBER - 1));
					System.out.println("Elevator " + this.ELEVATOR_NUMBER + " moving to floor " + destinationFloor);
					elevatorSubsystem.sendSchedulerMessage(new ElevatorData(state, currentFloor,
							destinationFloor, LocalTime.now().plusSeconds(2 * destinationFloor - currentFloor),
							ELEVATOR_NUMBER));

					while (currentFloor != destinationFloor) {
						try {
							Thread.sleep(2000);

							synchronized (this) {
								if (Thread.interrupted()) {
									throw new InterruptedException();
								}
								if (destinationFloor - currentFloor > 0) {
									currentFloor++;
								} else if (destinationFloor - currentFloor < 0) {
									currentFloor--;
								}

							}
							elevatorSubsystem.sendSchedulerMessage(new ElevatorData(state, currentFloor,
									destinationFloor,
									LocalTime.now().plusSeconds(2 * (destinationFloor - currentFloor)),
									ELEVATOR_NUMBER));
						} catch (InterruptedException e) {
							System.out.println("Interrupted");
							elevatorSubsystem.sendSchedulerMessage(new ElevatorData(state, currentFloor,
									destinationFloor,
									LocalTime.now().plusSeconds(2 * (destinationFloor - currentFloor)),
									ELEVATOR_NUMBER));
						}
					}
					if (this.hardFaultQueue.poll() == 1) {
						System.out.print(textBufferString.repeat(ELEVATOR_NUMBER - 1));
						System.out.println("Elevator " + ELEVATOR_NUMBER + ": Timing event fault\n");
						setIsStuck();
						break;
					} else {
						this.state = ElevatorStates.ARRIVED;
					}

					System.out.print(textBufferString.repeat(ELEVATOR_NUMBER - 1));
					System.out.println(
							"Elevator " + ELEVATOR_NUMBER + " has arrived at floor " + currentFloor);

					// Check for Timer fault

					break;

				case ARRIVED:
					//System.out.println("Elevator " + this.ELEVATOR_NUMBER + " ARRIVED...");

					// wait for a bit
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (this.transientFaultQueue.poll() == 1) {
						System.out.print(textBufferString.repeat(ELEVATOR_NUMBER - 1));
						System.out.println("Elevator " + ELEVATOR_NUMBER + ": Door stuck fault\n");
						this.setTransFlag();
						// Handle transient fault
						try {
							Thread.sleep(2000);
							System.out.print(textBufferString.repeat(ELEVATOR_NUMBER - 1));
							System.out.println("Elevator " + ELEVATOR_NUMBER + ": Door has been fixed\n");
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

					// Tell the floor we have arrived
					elevatorSubsystem.sendSchedulerMessage(
							new ElevatorData(state, currentFloor, destinationFloor, LocalTime.now(),
									ELEVATOR_NUMBER));

					pause();
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + state);
			}
		}
		elevatorSubsystem.sendSchedulerMessage(
				new ElevatorData(state, currentFloor, destinationFloor, LocalTime.now(),
						ELEVATOR_NUMBER, true));
		setFlag();
		System.out.print(textBufferString.repeat(ELEVATOR_NUMBER - 1));
		System.err.println("Elevator " + ELEVATOR_NUMBER + " shutdown");
	}
}
