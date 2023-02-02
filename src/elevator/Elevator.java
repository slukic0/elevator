package elevator;

import elevator.*;
import java.time.LocalTime;
import java.util.ArrayList;

public class Elevator implements Runnable {
	private final ArrayList<Integer> FLOOR_NUMBERS;
	private final int ELEVATOR_NUMBER;
	private int currentFloor;
	
	public Elevator(int elevatorNumber, ArrayList<Integer> floorNums, int currentFloor) {
		this.ELEVATOR_NUMBER = elevatorNumber;
		this.FLOOR_NUMBERS = floorNums;
		this.currentFloor = currentFloor;
	}
	
	public void sendMessage(boolean goingUp) {
		synchronized (Scheduler.getMessages()) {
			try {
				Scheduler.enqueueMessage(new Message(LocalTime.now(), this.currentFloor, goingUp));
				Scheduler.getMessages().wait();
			} catch (InterruptedException e) {
				System.out.println("Error in Elavtor thread");
			}
		}
	}
	
	public int getCurrentFloor() {
		return this.currentFloor;
	}
	
	public void setCurrentFloor(int floor) {
		this.currentFloor = floor;
	}
	
	public void run() {
		return;
	}
}
