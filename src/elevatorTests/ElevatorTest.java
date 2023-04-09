
package elevatorTests;
import messages.ElevatorCommandData;
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
    public static ElevatorCommandData floorData;
	public static ElevatorCommandData transFaultData;
    public static ElevatorData elevatorData;
	public static DatagramSocket socket;
	public static ElevatorCommandData processData;
	

    /**
     * Initializes the variables that will be used to test the methods in the Elevator Class
     * @throws SocketException
     */
	@BeforeAll
	public static void init() throws SocketException{
		elevatorSubsystem = new ElevatorSubsystem(1, 1, 1027);
        floorData = new ElevatorCommandData(3,1,0);
		processData = new ElevatorCommandData(2 , 1, 1);
		transFaultData = new ElevatorCommandData(3,0,1);
        elevatorData = new ElevatorData(ElevatorStates.GOING_UP, 1, 2, LocalTime.now(), 1);
	}
	
	/**
     * Tests the sendSchedulerMessage method in the Elevator class
	 * @throws SocketException
     */
	@Test
    public void testSendMessage(){
        
        elevatorSubsystem.sendSchedulerMessage(elevatorData);
		boolean value = elevatorData.getArrivalTime().compareTo(LocalTime.now()) < 2;
		assertTrue(value);
    }

	/**
	 * Test to check if received packet data is correct
	 * @throws SocketException
	 */
	@Test
	public void testProcessPacketDataHardFault() throws SocketException {
		
		elevatorSubsystem.getElevator().processPacketData(processData);
		assertEquals(elevatorSubsystem.getElevator().getState(), ElevatorStates.GOING_UP);
		assertEquals(1, elevatorSubsystem.getElevator().getHardFaultQueue().peek());
		assertEquals(1, elevatorSubsystem.getElevator().getTransientFaultQueue().peek());
	}

	/**
	 * Test hard fault handling
	 * @throws IOException
	 */
	@Test 
	public void testHardFault() throws IOException {
		socket = new DatagramSocket();

		ElevatorSubsystem elevatorSubsystem1 = new ElevatorSubsystem(1, Constants.STARTING_FLOOR,
				1028);

		Thread eThread1 = new Thread(elevatorSubsystem1);
		eThread1.setName("1");

		eThread1.start();

		System.out.println(elevatorSubsystem1.getElevator().getHardFaultQueue());

		byte[] byteData = NetworkUtils.serializeObject(floorData);
		NetworkUtils.sendPacket(byteData, socket, 1028, InetAddress.getLocalHost());

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(true, elevatorSubsystem1.getElevator().getFlag());
	}

	/**
	 * Test transient fault handling
	 * @throws IOException
	 */
	@Test
	public void testTransientFault() throws IOException {
		socket = new DatagramSocket();

		ElevatorSubsystem elevatorSubsystem2 = new ElevatorSubsystem(1, Constants.STARTING_FLOOR,
				1029);

		Thread eThread2 = new Thread(elevatorSubsystem2);
		eThread2.setName("1");
		eThread2.start();

		byte[] byteData = NetworkUtils.serializeObject(transFaultData);
		NetworkUtils.sendPacket(byteData, socket, 1029, InetAddress.getLocalHost());

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(true, elevatorSubsystem2.getElevator().getTransFlag());
	}

}
