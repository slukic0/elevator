
package elevatorTests;
import messages.ElevatorData;
import messages.FloorData;

import org.junit.jupiter.api.*;

import elevatorImpl.*;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.crypto.Data;

import util.FileUtil;
import util.NetworkUtils;

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
	    public static DatagramSocket datagramSocket;

	    /**
	     * Initializes the variables that will be used to test the methods in the Scheduler Class
	     */
	@BeforeAll
	public static void init(){

        Queue<Object> schedulerQueue = new LinkedList<>();
		Queue<FloorData> floorQueue = new LinkedList<>();
		Queue<ElevatorData> elevatorQueue = new LinkedList<>();
		
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        Floor[] floors = new Floor[] { floor };
		ArrayList<ElevatorSubsystem> elevatorSubsystems = new ArrayList<>(){};
		elevatorSubsystems.add(elevatorSubsystem);
		
		floor = null;
		
		try {
			floor = new Floor();
			scheduler = new Scheduler();
	        elevatorSubsystem = new ElevatorSubsystem(1, 1, Constants.ELEVATOR_SYS_RECEIVE_PORT1);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        floorData = new FloorData(1, 2, LocalTime.now());
        elevatorData = new ElevatorData(ElevatorStates.GOING_UP, ElevatorStates.GOING_DOWN, 1, 2, LocalTime.now(),1);
        
	}
	
	/**
     * Method to test sending a message in Floor class
     */
	@Test
    public void testSendMessage(){
        try {
        	floor = new Floor();
        	scheduler = new Scheduler();
        	floor.sendMessageToScheduler(floorData);
			DatagramPacket packet = NetworkUtils.receivePacket(scheduler.getFloorSocket());
			FloorData floorMessage = (FloorData) NetworkUtils.deserializeObject(packet);
			assertEquals(floorData.getDestinationFloor(), floorMessage.getDestinationFloor());
			assertEquals(floorData.getGoingUp(), floorMessage.getGoingUp());
			assertEquals(floorData.getStartingFloor(), floorMessage.getStartingFloor());
			assertTrue(floorData.getTime().compareTo(floorMessage.getTime()) < 2);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@Test
	public void testInvalidMessage() {
		
		try {
			floor = new Floor();
			boolean result = floor.checkMessage(null);
			assertFalse(result);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testValidMessage() {
		
		try {
			floor = new Floor();
			boolean result = floor.checkMessage(elevatorData);
			assertTrue(result);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}

}
