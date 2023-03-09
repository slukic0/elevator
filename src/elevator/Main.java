package elevator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import messages.ElevatorData;
import messages.FloorData;
import util.FileUtil;

public class Main {

	public static void main(String[] args) throws IOException {
		System.out.println("Hello Elevator");

		Queue<Object> schedulerQueue = new LinkedList<>();
		Queue<ElevatorData> floorQueue = new LinkedList<>();
		Queue<FloorData> elevatorSystemQueue = new LinkedList<>();

		// only 1 floor and 1 elevator for now...
		
		
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(1, Constants.STARTING_FLOOR);
		Floor floor = new Floor();

		Floor[] floors = new Floor[] { floor };
		ArrayList<ElevatorSubsystem> elevatorSubsystems = new ArrayList<ElevatorSubsystem>();
		elevatorSubsystems.add(elevatorSubsystem);

		Scheduler scheduler = new Scheduler(floors, elevatorSubsystems);

		// Create & start the threads
		Thread eThread = new Thread(elevatorSubsystem);
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
			floor.sendMessageToScheduler(d);
		}

	}

}
