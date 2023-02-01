package elevator;

import java.util.ArrayList;
import java.util.Queue;

public class Scheduler implements Runnable{
	private Floor[] floors;
	private Elevator[] elevators;
	private static Queue<Message> messageQueue;
	
	public Scheduler(Floor[] floors, Elevator[] elevators) {
		this.floors = floors;
		this.elevators = elevators;
	}
	
	public static Queue<Message> getMessages() {
		return messageQueue;
	}
	
	public static void enqueueMessage(Message mesage) {
		messageQueue.add(mesage);
	}
	
	public void handleFloorMessage(Message message) {
		// TODO: implement handleMessage
	}
	
	public void run() {
		return;
	}
}