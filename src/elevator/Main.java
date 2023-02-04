package elevator;

import java.util.LinkedList;
import java.util.Queue;

public class Main {

	public static void main(String[] args) {
		System.out.println("Hello Elevator");

		Queue<Message> schedulerQueue = new LinkedList<>();
		Queue<Message> floorQueue = new LinkedList<>();
		Queue<Message> elevatorQueue = new LinkedList<>();

		// only 1 floor and 1 elevator for now...
		Elevator elevator = new Elevator(elevatorQueue, schedulerQueue, 0, 0);
		Floor floor = new Floor(floorQueue, schedulerQueue, 0);

		Floor[] floors = new Floor[] { floor };
		Elevator[] elevators = new Elevator[] { elevator };

		Scheduler scheduler = new Scheduler(schedulerQueue, floorQueue, elevatorQueue, floors, elevators);

		// Create & start the threads
		Thread eThread = new Thread(elevator);
		Thread fThread = new Thread(floor);
		Thread sThread = new Thread(scheduler);

		eThread.start();
		fThread.start();
		sThread.start();

	}

}
