package elevatorTests;
import messages.ElevatorData;
import messages.FloorData;
import util.NetworkUtils;

import org.junit.Test;
import org.junit.jupiter.api.*;

import elevatorImpl.*;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.HashMap;

import javax.xml.crypto.Data;


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
	public static ElevatorSubsystem elevatorSubsystem;
    public static FloorData floorData;
    public static ElevatorData elevatorData;
	public static ElevatorData elevatorData2;
	public static HashMap<Integer, Boolean> floorUpButtonsMap;
	public static HashMap<Integer, Boolean> floorDownButtonsMap;
	public static HashMap<Integer, ElevatorStatus> elevatorMap;
	public static ElevatorStatus status;
	public static ElevatorStatus status2;
	public int[] elevatorArray;
	public DatagramSocket socket;


    /**
     * Initializes the variables that will be used to test the methods in the Scheduler Class
     * @throws SocketException
     */
	@BeforeAll
	public static void init() throws SocketException{
		
		floor = new Floor(1025);
		// elevatorSubsystem = new ElevatorSubsystem(1, 1, 1028);
        // floorData = new FloorData(1, 2, true, LocalTime.now(), 1, 1);
		elevatorData = new ElevatorData(ElevatorStates.IDLE, 0, 0, null, 0);
        elevatorData2 = new ElevatorData(ElevatorStates.GOING_DOWN, 2, 0, null, 0);
        status = new ElevatorStatus(null, 0, elevatorData);
        status2 = new ElevatorStatus(null, 0, elevatorData2);

    }	

	/**
	 * Test for the algorithm - findClosestElevator method
	 * @throws SocketException
	 */
	@Test
	public void testFindClosestElevator() throws SocketException{
		scheduler = new Scheduler(1026, 1027);
		elevatorMap = new HashMap<Integer, ElevatorStatus>(){};
		int closestElevator = 0;

		// Check if function grabs the closest IDLE elevator
		elevatorMap.put(1, new ElevatorStatus(null, 0, new ElevatorData(ElevatorStates.IDLE, 5, 5, null, 1)));
		elevatorMap.put(2, new ElevatorStatus(null, 0, new ElevatorData(ElevatorStates.GOING_UP, 2, 5, null, 2)));
		scheduler.setElevatorMap(elevatorMap);

		closestElevator = scheduler.findClosestElevator(7, false);
		assertEquals(1, closestElevator);

		elevatorMap.put(3, new ElevatorStatus(null, 0, new ElevatorData(ElevatorStates.GOING_DOWN, 8, 3, null, 3)));

		closestElevator = scheduler.findClosestElevator(4, true);
		assertEquals(2, closestElevator);

		closestElevator = scheduler.findClosestElevator(4, false);
		assertEquals(3, closestElevator);
	}
	
	/*
	 * Test if the scheduler receives a hard fault 
	 */
	@Test
	public void testHardFault() throws IOException {
		Scheduler scheduler = new Scheduler(1028, 1029);
		elevatorMap = new HashMap<Integer, ElevatorStatus>(){};
		elevatorMap.put(1, new ElevatorStatus(null, 0, new ElevatorData(ElevatorStates.IDLE, 5, 5, LocalTime.now(), 1, true)));
		scheduler.setElevatorMap(elevatorMap);

		elevatorData = new ElevatorData(ElevatorStates.IDLE, 5, 5, LocalTime.now(), 1, true);

		scheduler.checkHardFault(elevatorData);

		assertEquals(null, scheduler.getElevatorMap().get(1));
	}
}