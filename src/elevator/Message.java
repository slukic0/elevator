package elevator;

import java.time.LocalTime;

/** Represents a message passed within the system
 * 
 * @author Group G5
 *
 */
public class Message {
	public enum Sender {
		ELEVATOR, FLOOR
	}

	private Sender sender;
	private LocalTime time;
	private int floor;
	private boolean goingUp;

	/** Creates a message with the specified parameters 
	 * 
	 * @param sender  the message sender
	 * @param floor   the floor number
	 * @param goingUp checks if the elevator is going up
	 * @param time    the local time
	 */
	public Message(Sender sender, int floor, boolean goingUp, LocalTime time) {
		this.sender = sender;
		this.time = time;
		this.floor = floor;
		this.goingUp = goingUp;
	}

	/** 
	 * Second Class constructor 
	 * 
	 * @param sender  the message sender
	 * @param floor   the floor number
	 * @param goingUp checks if the elevator is going up
	 */
	public Message(Sender sender, int floor, boolean goingUp) {
		this(sender, floor, goingUp, LocalTime.now());
	}

	/** 
	 * Returns the thread subsystem that sent the message
	 * 
	 * @return sender  the sender of the message
	 */
	public Sender getSender() {
		return sender;
	}

	/**
	 * Returns the time of the message
	 * 
	 * @return time  the local time
	 */
	public LocalTime getTime() {
		return time;
	}

	/** 
	 * Returns the attribute representing the floor number 
	 * 
	 * @return floor  the floor number
	 * 
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * Returns a boolean value indicating whether the elevator is going up or not
	 * 
	 * @return goingUp  
	 */
	public boolean getGoingUp() {
		return goingUp;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "Sender: " + sender + ", Time: " + time.toString() + ", Floor: " + floor + ", GoingUp: " + goingUp;
	}
}
