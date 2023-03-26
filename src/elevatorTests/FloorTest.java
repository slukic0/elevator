
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
	 * @throws SocketException
	 */
	@BeforeAll
	public static void init() throws SocketException{
		
		floor = new Floor(1028);
        elevatorData = new ElevatorData(ElevatorStates.GOING_UP, ElevatorStates.GOING_DOWN, 1, 2, LocalTime.now(),1);
	}
	
	@Test
	public void testInvalidMessage() throws SocketException {
		boolean result = floor.checkMessage(null);
		assertFalse(result);
	}
	
	@Test
	public void testValidMessage() {
		boolean result = floor.checkMessage(elevatorData);
		assertTrue(result);
	}

	// Tests UDP - redundant
	// /**
    //  * Method to test sending a message in Floor class
	//  * @throws IOException
    //  */
	// @Test
    // public void testSendMessage() throws IOException{
	// 	floor.sendMessageToScheduler(floorData);
	// 	DatagramPacket packet = NetworkUtils.receivePacket(scheduler.getFloorSocket());
	// 	FloorData floorMessage = (FloorData) NetworkUtils.deserializeObject(packet);
	// 	assertEquals(floorData.getDestinationFloor(), floorMessage.getDestinationFloor());
	// 	assertEquals(floorData.getGoingUp(), floorMessage.getGoingUp());
	// 	assertEquals(floorData.getStartingFloor(), floorMessage.getStartingFloor());
	// 	assertTrue(floorData.getTime().compareTo(floorMessage.getTime()) < 2);
    // }
	

}
