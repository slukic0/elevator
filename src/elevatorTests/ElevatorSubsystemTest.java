package elevatorTests;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import elevatorImpl.*;
import messages.ElevatorData;
import messages.FloorData;

/**
 * Class for testing the methods in the ElevatorSubsystem Class
 *
 * @author Group G5
 * @version 1.0
 */
public class ElevatorSubsystemTest {
	
    public static Floor floor;
    public static Elevator elevator;
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
		try {
			elevatorSubsystem = new ElevatorSubsystem(1,1);
			floor = new Floor();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//elevatorSubsystem.getSchedulerReceiveQueue().add(elevatorData); 

        Floor[] floors = new Floor[] { floor };
		ArrayList<ElevatorSubsystem> elevatorSubsystems = new ArrayList<>(){};
		
		elevatorSubsystems.add(elevatorSubsystem);
        
        floorData = new FloorData(1, 2, true, LocalTime.now());
        elevatorData = new ElevatorData(ElevatorStates.GOING_UP, ElevatorStates.GOING_DOWN, 1, 2, LocalTime.now(), 1);
    }
	
	
	@Test
	public void testSendSchedulerMessage() {
		
		Queue<Object> schedulerQueue = new LinkedList<>();
		Queue<FloorData> floorQueue = new LinkedList<>();
		Queue<ElevatorData> elevatorQueue = new LinkedList<>();
		
		//elevatorSubsystem.getSchedulerReceiveQueue().add(elevatorData); 
		
		Floor[] floors = new Floor[] { floor };
		ArrayList<ElevatorSubsystem> elevatorSubsystems = new ArrayList<>(){};
		elevatorSubsystems.add(elevatorSubsystem);
		
		try {
			elevatorSubsystem = new ElevatorSubsystem(2, 2);
			Scheduler scheduler = new Scheduler();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		elevatorData = new ElevatorData(ElevatorStates.GOING_UP, ElevatorStates.GOING_DOWN, 1, 2, LocalTime.now(),1);
	
		//scheduler.getFloorReceiveQueue().add(elevatorData);
		
		assertEquals(elevatorData.getState(), ElevatorStates.GOING_UP);

        //assertEquals(elevatorData, scheduler.getFloorReceiveQueue().poll(), "Message was not sent/received properly");
	}
	
}
	
	
	
	
