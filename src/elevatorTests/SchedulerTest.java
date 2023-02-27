
package elevatorTests;
import elevator.*;

import org.junit.Test;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.ArrayList;
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
        floorData = new FloorData(0, false);
        elevatorData = new ElevatorData(0, false, LocalTime.now().plusSeconds(2)+);
    }
	
	@Test
	public void handleFloorRequest(){
        
		assertEquals(, floor.getreceiveQueue().poll(), "Message was not sent/received properly");
    }

	@Test
	public void handleElevatorResponse(){
        
    }
	
	
//	/**
//     * Method to test the sendFloorMessage method in Scheduler class
//     */
//	@Test
//    public void testSendFloorMessage(){
//        
//        scheduler.sendFloorMessage(message_f);
//
//        assertEquals(message_f, floor.getreceiveQueue().poll(), "Message was not sent/received properly");
//    }
//
//	/**
//     * Method to test the sendElevatorMessage method in Scheduler class
//     */
//	@Test
//    public void testSendElevatorMessage(){
//        
//        scheduler.sendElevatorSystemMessage(message_e);
//
//        assertEquals(message_e, elevator.getreceiveQueue().poll(), "Message was not sent/received properly");
//    }
	

}
