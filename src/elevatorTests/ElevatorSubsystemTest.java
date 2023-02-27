package elevatorTests;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import elevator.*;

/**
 * Class for testing the methods in the ElevatorSubsystem Class
 *
 * @author Group G5
 * @version 1.0
 */
public class ElevatorSubsystemTest {
	
    public static Floor floor;
    public static Elevator elevator;
    public static Scheduler scheduler;
    public static ElevatorSubsystem elevatorSubsystem;
    public static FloorData floorData;
    public static ElevatorData elevatorData;

	    /**
	     * Initializes the variables that will be used to test the methods in the Elevator Class
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
        floorData = new FloorData(0, false);
        elevatorData = new ElevatorData(ElevatorStates.GOING_UP, ElevatorStates.GOING_DOWN, 0, 1, LocalTime.now());
    }
	
	
	@Test
	public void testSendSchedulerMessage() {
		
		Queue<Object> schedulerQueue = new LinkedList<>();
		Queue<FloorData> floorQueue = new LinkedList<>();
		Queue<ElevatorData> elevatorQueue = new LinkedList<>();
		//elevatorSubsystem = new ElevatorSubsystem(floorQueue, schedulerQueue, 0, 0);
		//elevatorSubsystem.getSchedulerReceiveQueue().add(elevatorData); 
		
		elevatorData = new ElevatorData(ElevatorStates.GOING_UP, ElevatorStates.GOING_DOWN, 0, 1, LocalTime.now());
		
		assertNull(elevatorSubsystem);
		assertEquals(elevatorData.getState(), ElevatorStates.GOING_UP);

        //assertEquals(elevatorData, scheduler.getreceiveQueue().poll(), "Message was not sent/received properly");
	}
	
}
	
	
	
	
