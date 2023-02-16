package elevator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import util.FileUtil;

public class Main {

	public static void main(String[] args) throws IOException {
		System.out.println("Hello Elevator");

		Queue<Message> schedulerQueue = new LinkedList<>();
		Queue<Message> floorQueue = new LinkedList<>();
		Queue<Integer> elevatorQueue = new LinkedList<>();

		// only 1 floor and 1 elevator for now...
		Elevator elevator = new Elevator(elevatorQueue, schedulerQueue, 0, 0);
		Floor floor = new Floor(floorQueue, schedulerQueue, 0);

		Floor[] floors = new Floor[] { floor };
		Elevator[] elevators = new Elevator[] { elevator };

		Scheduler scheduler = new Scheduler(schedulerQueue, floorQueue,floors, elevators);

		// Read input file and create floor messages
		String[] input = FileUtil.readFile(floor.getClass(), "events.txt");
		Message[] messages = FileUtil.parseStringInput(input);
		for (Message m : messages) {
			System.out.println("Adding message: " + m.toString());
			floor.addMessage(m);
		}

		// Create & start the threads
		Thread eThread = new Thread(elevator);
		Thread fThread = new Thread(floor);
		Thread sThread = new Thread(scheduler);

		eThread.start();
		fThread.start();
		sThread.start();

	}

}
