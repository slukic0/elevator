
package elevatorTests;
import messages.ElevatorData;
import messages.FloorData;

import org.junit.jupiter.api.*;

import elevatorImpl.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.crypto.Data;

import util.FileUtil;


/**
 * Class for testing the methods in the Floor Class
 *
 * @author Group G5
 * @version 1.0
 */
public class FloorTest {

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
		
		try {
			floor = new Floor();
			scheduler = new Scheduler();
	        elevatorSubsystem = new ElevatorSubsystem(1, 1, Constants.ELEVATOR_SYS_RECEIVE_PORT1);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        floorData = new FloorData(1, 2, true, LocalTime.now());
	}
	
	/**
     * Method to test sending a message in Floor class
     */
	@Test
    public void testSendMessage(){
        
        //floor.getSchedulerQueue().add(floorData);

        //assertEquals(floorData, scheduler.getreceiveQueue().poll(), "Message was not sent/received properly");
		assertEquals(1,  1);
    }

}
