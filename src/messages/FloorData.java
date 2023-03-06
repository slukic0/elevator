package messages;

import java.io.Serializable;
import java.time.LocalTime;

public class FloorData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8338140153567912284L;
	private LocalTime time;
	private int floor;
	private boolean goingUp;

	/**
	 * 
	 * @param floor floor to start at
	 * @param goingUp true if going up, false if down
	 * @param time time of request
	 */
	public FloorData(int floor, boolean goingUp, LocalTime time) {
		this.floor = floor;
		this.goingUp = goingUp;
		this.time = time;
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
		return "Floor: " + floor + ", Time: " + time.toString() + ", GoingUp: " + goingUp;
	}
}
