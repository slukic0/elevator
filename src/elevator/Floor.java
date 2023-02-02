package elevator;

import java.util.ArrayList;
import java.time.LocalTime;

public class Floor implements Runnable{
	private final int FLOOR_NUMBER;
	private final ArrayList<Integer> elevatorNumbers;
	
	public Floor(int floorNumber, ArrayList<Integer> elevatorNumbers) {
		this.FLOOR_NUMBER = floorNumber;
		this.elevatorNumbers = elevatorNumbers;
	}
	
	private void sendMessage(boolean goingUp) {
		synchronized (Scheduler.getMessages()) {
			try {
				Scheduler.enqueueMessage(new Message(LocalTime.now(), this.FLOOR_NUMBER, goingUp));
				Scheduler.getMessages().wait();
			} catch (InterruptedException e) {
				System.out.println("Error in Elavtor thread");
			}
		}
	}
	
	public void run() {
		return;
	}
}
