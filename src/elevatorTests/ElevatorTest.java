
package elevatorTests;
import messages.ElevatorData;
import messages.FloorData;
import util.NetworkUtils;

import org.junit.jupiter.api.*;

import elevatorImpl.*;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.print.attribute.standard.DateTimeAtCompleted;


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
	public static DatagramSocket socket;
	

    /**
     * Initializes the variables that will be used to test the methods in the Elevator Class
     * @throws SocketException
     */
	@BeforeAll
	public static void init() throws SocketException{
		scheduler = new Scheduler(1025, 1026);
		elevatorSubsystem = new ElevatorSubsystem(1, 1, 1027);
        floorData = new FloorData(2, 3, LocalTime.now(), 1, 0);
        elevatorData = new ElevatorData(ElevatorStates.GOING_UP, 1, 2, LocalTime.now(), 1);
	}
	
	/**
     * Method to test sending a message in Elevator class
	 * @throws SocketException
     */
	@Test
    public void testSendMessage(){
        
        elevatorSubsystem.sendSchedulerMessage(elevatorData);
		boolean value = elevatorData.getArrivalTime().compareTo(LocalTime.now()) < 2;
		assertTrue(value);
    }

	@Test
	public void testProcessPacketData() throws SocketException { // TODO Elevator Command Data
		
		elevatorSubsystem.getElevator().processPacketData(floorData);
		assertEquals(elevatorSubsystem.getElevator().getState(), ElevatorStates.PROCESSING);
		System.out.println(elevatorSubsystem.getElevator().getDestQueue());
		assertEquals(elevatorSubsystem.getElevator().getDestQueue().peek(), floorData.getDestinationFloor());
	}

	@Test
	public void testHardFault() throws UnknownHostException, IOException {
		socket = new DatagramSocket();

		ElevatorSubsystem elevatorSubsystem1 = new ElevatorSubsystem(1, Constants.STARTING_FLOOR,
				1028);

		Thread eThread1 = new Thread(elevatorSubsystem1);
		eThread1.setName("1");

		eThread1.start();

		byte[] byteData = NetworkUtils.serializeObject(floorData);
		NetworkUtils.sendPacket(byteData, socket, 1028, InetAddress.getLocalHost());

		assertEquals(true, elevatorSubsystem1.getElevator().getFlag());
	}

	// public void testTransientFault() throws SocketException {
		
	// }
	
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
