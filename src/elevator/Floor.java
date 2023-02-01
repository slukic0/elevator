package elevator;

import java.util.ArrayList;
import java.time.LocalTime;

public class Floor implements Runnable{
	private Scheduler server;
	private final int floorNumber;
	private final ArrayList<Integer> elevatorNumbers;
	
	public Floor(Scheduler server, int floorNumber, ArrayList<Integer> elevatorNumbers) {
		this.server = server;
		this.floorNumber = floorNumber;
		this.elevatorNumbers = elevatorNumbers;
	}
	
	private void sendMessage(boolean goingUp) {
		server.handleMessage(new Message(LocalTime.now(), this.floorNumber, goingUp));
	}
	
	public void run() {
		return;
	}
}
