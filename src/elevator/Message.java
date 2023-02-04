package elevator;

import java.time.LocalTime;

public class Message {
	public enum Sender {
		ELEVATOR, FLOOR
	}

	private Sender sender;
	private LocalTime time;
	private int floor;
	private boolean goingUp;

	public Message(Sender sender, int floor, boolean goingUp, LocalTime time) {
		this.sender = sender;
		this.time = time;
		this.floor = floor;
		this.goingUp = goingUp;
	}

	public Message(Sender sender, int floor, boolean goingUp) {
		this(sender, floor, goingUp, LocalTime.now());
	}

	public Sender getSender() {
		return sender;
	}

	public LocalTime getTime() {
		return time;
	}

	public int getFloor() {
		return floor;
	}

	public boolean getGoingUp() {
		return goingUp;
	}

	@Override
	public String toString() {
		return "Sender: " + sender + ", Time: " + time.toString() + ", Floor: " + floor + ", GoingUp: " + goingUp;
	}
}
