package elevator;

import java.time.LocalTime;

public class Message {
	private LocalTime time;
	private int floor;
	private boolean upButton;
	
	public Message(LocalTime time, int floor, boolean up) {
		this.time = time;
		this.floor = floor;
		this.upButton = up;
	}
	
	public LocalTime getTime() {
		return time;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public boolean getUp() {
		return upButton;
	}
}
