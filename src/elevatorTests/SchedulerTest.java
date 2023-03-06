
package elevatorTests;
import elevator.*;
import messages.ElevatorData;
import messages.FloorData;

import org.junit.Test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


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
	@BeforeEach
	public static void init(){

        Queue<Object> schedulerQueue = new LinkedList<>();
		Queue<FloorData> floorQueue = new LinkedList<>();
		Queue<ElevatorData> elevatorQueue = new LinkedList<>();
        elevatorSubsystem = new ElevatorSubsystem(floorQueue, schedulerQueue, 0, 0);

        Floor[] floors = new Floor[] { floor };
		ArrayList<ElevatorSubsystem> elevatorSubsystems = new ArrayList<>(){};
		elevatorSubsystems.add(elevatorSubsystem);
		
		floor = new Floor(elevatorQueue, schedulerQueue, 0);
		
        scheduler = new Scheduler(schedulerQueue, elevatorQueue, floorQueue, floors, elevatorSubsystems);
        elevatorSubsystem = new ElevatorSubsystem(floorQueue, schedulerQueue, 0, 0);
        floorData = new FloorData(0, true);

    }
	

	@Test
	public void testHandleElevatorResponse(){

		Queue<Object> schedulerQueue = new LinkedList<>();
		Queue<FloorData> floorQueue = new LinkedList<>();
		Queue<ElevatorData> elevatorQueue = new LinkedList<>();
        elevatorSubsystem = new ElevatorSubsystem(floorQueue, schedulerQueue, 0, 0);
        
        assertEquals(floorData, elevatorSubsystem.getreceiveQueue().poll());
    }

}
