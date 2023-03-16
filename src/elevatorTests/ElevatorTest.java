
package elevatorTests;
import messages.ElevatorData;
import messages.FloorData;

import org.junit.jupiter.api.*;

import elevatorImpl.*;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;


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
        elevatorData = new ElevatorData(ElevatorStates.GOING_UP, ElevatorStates.GOING_DOWN, 1, 2, LocalTime.now(), 1);
    }
	
	/**
     * Method to test sending a message in Elevator class
     */
	@Test
    public void testSendMessage(){
        
        try {
			elevatorSubsystem = new ElevatorSubsystem(1, 1, Constants.ELEVATOR_SYS_RECEIVE_PORT1);
	        elevatorSubsystem.sendSchedulerMessage(elevatorData);
	        boolean value = elevatorData.getArrivalTime().compareTo(LocalTime.now()) < 2;
	        assertTrue(value);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Test
	public void testProcessPacketData() {
		
		 try {
				elevatorSubsystem = new ElevatorSubsystem(1, 1, Constants.ELEVATOR_SYS_RECEIVE_PORT1);
				elevatorSubsystem.getElevator().processPacketData(floorData);
				assertEquals(elevatorSubsystem.getElevator().getState(), ElevatorStates.PROCESSING);
				assertEquals(elevatorSubsystem.getElevator().getDestQueue(), floorData.getDestinationFloor());
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	// @Test
	// public void testElevatorDestinationFloor() {
	//   try {
	// 		elevatorSubsystem = new ElevatorSubsystem(1, 1, Constants.ELEVATOR_SYS_RECEIVE_PORT1);
	// 		elevatorSubsystem.sendSchedulerMessage(elevatorData);
	// 		assertEquals(elevatorData.getMovingToFloor(), elevatorSubsystem.getElevator().getDestinationFloor());
	// 	} catch (SocketException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
	// }
	
	// @Test
	// public void testElevatorStartingFloor() {
	//   try {
	// 		elevatorSubsystem = new ElevatorSubsystem(1, 1, Constants.ELEVATOR_SYS_RECEIVE_PORT1);
	// 		elevatorSubsystem.sendSchedulerMessage(elevatorData);
	// 		assertEquals(elevatorData.getCurrentFloor(), elevatorSubsystem.getElevator().getCurrentFloor());
	// 	} catch (SocketException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
	// }
	
	// @Test
	// public void testElevatorPrevDirectionFloor() {
	//   try {
	// 		elevatorSubsystem = new ElevatorSubsystem(1, 1, Constants.ELEVATOR_SYS_RECEIVE_PORT1);
	// 		elevatorSubsystem.sendSchedulerMessage(elevatorData);
	// 		assertEquals(elevatorData.getPrevDirection(), elevatorSubsystem.getElevator().getPrevDirection());
	// 	} catch (SocketException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
	// }
	



}
