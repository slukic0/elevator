
package elevatorTests;
import messages.ElevatorData;
import messages.FloorData;

import org.junit.jupiter.api.*;

import elevatorImpl.*;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Class for testing the methods in the Elevator Class
 *
 * @author Group G5
 * @version 1.0
 */
public class ElevatorTest {

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
        elevatorSubsystem = new ElevatorSubsystem(floorQueue, schedulerQueue, 0, 0);
        floorData = new FloorData(0, false);
        elevatorData = new ElevatorData(ElevatorStates.GOING_UP, ElevatorStates.GOING_DOWN, 0, 1, LocalTime.now());
    }
	
	/**
     * Method to test sending a message in Elevator class
     */
	@Test
    public void testSendMessage(){
        
        elevatorSubsystem.sendSchedulerMessage(elevatorData);
        
        boolean value = elevatorData.getArrivalTime().compareTo(LocalTime.now()) < 2;
        assertTrue(value);
        
    }
	
	public void testElevatorState() {
		elevatorSubsystem.sendSchedulerMessage(elevatorData);
		assertEquals(elevatorData.getState(), ElevatorStates.GOING_UP);
	}

}
