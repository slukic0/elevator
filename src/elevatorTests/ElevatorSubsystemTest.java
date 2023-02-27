package elevatorTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import elevator.Elevator;
import elevator.ElevatorData;
import elevator.ElevatorSubsystem;
import elevator.Floor;
import elevator.FloorData;
import elevator.Message;
import elevator.Scheduler;
import elevator.Message.Sender;

/**
 * Class for testing the methods in the ElevatorSubsystem Class
 *
 * @author Group G5
 * @version 1.0
 */
public class ElevatorSubsystemTest {
	
	public static Floor floor;
    public static Scheduler scheduler;
    public static FloorData floorData;
    public static ElevatorData elevatorData;
    public static ElevatorSubsystem elevatorSubsystem;

    /**
     * Initializes the variables that will be used to test the methods in the Scheduler Class
     */
	@BeforeAll
	public static void init(){

        Queue<Object> schedulerQueue = new LinkedList<>();
		Queue<FloorData> floorQueue = new LinkedList<>();
		Queue<ElevatorData> elevatorQueue = new LinkedList<>();

        Floor[] floors = new Floor[] { floor };
		ArrayList<ElevatorSubsystem> elevatorSubsystems = new ArrayList<>(){};
		elevatorSubsystems.add(elevatorSubsystem);
		
		floor = new Floor(elevatorQueue, schedulerQueue, 0);
        scheduler = new Scheduler(schedulerQueue, elevatorQueue, floorQueue, floors, elevatorSubsystems);
        elevatorSubsystem = new ElevatorSubsystem(floorQueue, schedulerQueue, 0, 0);
        elevatorData = new ElevatorData(0, false);
	}
	
	@Test
	public static void testSendSchedulerMessage() {
		
		elevatorSubsystem.getSchedulerReceiveQueue().add(elevatorData);

        assertEquals(floorData, scheduler.getreceiveQueue().poll(), "Message was not sent/received properly");
	}
	
	
	
}
	
	
	
	
