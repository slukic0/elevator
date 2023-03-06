package elevator;

public class Constants {

	private Constants() {}
	
	public static final int NUMBER_OF_FLOORS = 10;
	public static final int STARTING_FLOOR = 0;

	// Only Scheduler ports need to be known, Scheduler can just reply to messages
	// from Floor and Elevator
	public static final int SCHEDULER_FLOOR_RECEIVE_PORT = 1;
	public static final int SCHEDULER_ELEVATOR_RECEIVE_PORT = 2;
	public static final int FLOOR_SYS_RECEIVE_PORT = 3;

	// TODO Scheduler address
}
