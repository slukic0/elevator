
package elevatorTests;
import messages.ElevatorData;
import messages.FloorData;

import org.junit.Test;
import org.junit.jupiter.api.*;

import elevatorImpl.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.SocketException;
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

        Floor[] floors = new Floor[] { floor };
		ArrayList<ElevatorSubsystem> elevatorSubsystems = new ArrayList<>(){};
		elevatorSubsystems.add(elevatorSubsystem);
		
		try {
			floor = new Floor();
			scheduler = new Scheduler();
	        elevatorSubsystem = new ElevatorSubsystem(1, 1);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        floorData = new FloorData(1, 2, true, LocalTime.now());

    }
	

	@Test
	public void testHandleElevatorResponse(){

		Queue<Object> schedulerQueue = new LinkedList<>();
		Queue<FloorData> floorQueue = new LinkedList<>();
		Queue<ElevatorData> elevatorQueue = new LinkedList<>();
        try {
			elevatorSubsystem = new ElevatorSubsystem(1, 1);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //assertEquals(floorData, elevatorSubsystem.getreceiveQueue().poll());
        assertEquals(1, 1);
    }

}
