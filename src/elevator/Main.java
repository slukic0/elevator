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
		Queue<Integer> elevatorSystemQueue = new LinkedList<>();

		// only 1 floor and 1 elevator for now...
		
		
	// Elevator will create subsystem
		Elevator elevator = new Elevator(elevatorQueue, schedulerQueue, 0, 0);
		Floor floor = new Floor(floorQueue, schedulerQueue, 0);

		Floor[] floors = new Floor[] { floor };
		Elevator[] elevators = new Elevator[] { elevator };

		Scheduler scheduler = new Scheduler(schedulerQueue, floorQueue, floors);

		// Create & start the threads
		Thread eThread = new Thread(elevator);
		Thread fThread = new Thread(floor);
		Thread sThread = new Thread(scheduler);

		eThread.start();
		fThread.start();
		sThread.start();

		// Read input file and create floor messages
		String[] input = FileUtil.readFile(floor.getClass(), "events.txt");
		FloorData[] data = FileUtil.parseStringInput(input);
		for (FloorData d : data) {
			System.out.println("Adding FloorData: " + d.toString());
			floor.sendMessage(d);
		}

	}

}
