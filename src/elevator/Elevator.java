package elevator;

import elevator.*;
import java.time.LocalTime;
import java.util.ArrayList;

public class Elevator implements Runnable {
	private Scheduler server;
	private ArrayList<Integer> floorNums;
	private int currentFloor;
	
	public Elevator(Scheduler server, ArrayList<Integer> floorNums, int currentFloor) {
		this.server = server;
		this.floorNums = floorNums;
		this.currentFloor = currentFloor;
	}
	
	public void run() {
		return;
	}
	
	public void sendMessage(boolean goingUp) {
		
		synchronized (server) {
			Scheduler.enqueueMessage(new Message(LocalTime.now(), this.currentFloor, goingUp));
		}
		
	}
	
	public void setCurrentFloor(int floor) {
		this.currentFloor = floor;
	}
}
