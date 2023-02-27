
package elevatorTests;
import elevator.*;

import org.junit.Test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import elevator.Message.Sender;


/**
 * Class for testing the methods in the Scheduler Class
 *
 * @author Group G5
 * @version 1.0
 */
public class SchedulerTest {

    public static Floor floor;
    public static Scheduler scheduler;
    public static Elevator elevator;
    public static FloorData floorData;
    public static ElevatorData elevatorData;
    public static ElevatorSubsystem elevatorSubsystem;
	public static HashMap<Integer, Boolean> floorUpButtonsMap;
	public static HashMap<Integer, Boolean> floorDownButtonsMap;

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
        elevatorSubsystem = new ElevatorSubsystem(floorQueue, schedulerQueue, 1, 1);
        floorData = new FloorData(0, true);

    }
	
	@Test
	public void testHandleFloorRequest(){

		floorData = new FloorData(0, true);
		floorUpButtonsMap = new HashMap<>();
		floorDownButtonsMap = new HashMap<>();		
		
		if (floorData.getGoingUp()) {
			floorUpButtonsMap.put(0, true);
		} else {
			floorDownButtonsMap.put(0, true);
		}
		
		assertEquals(true, scheduler.floorUpButtonsMap.get(0), "Message was not sent/received properly");
    }

	@Test
	public void testHandleElevatorResponse(){

        assertEquals(floorData, elevatorSubsystem.getreceiveQueue().poll());
    }

}
