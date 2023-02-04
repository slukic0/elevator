
package elevatorTests;
import elevator.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public static Elevator elevator;
    public static Scheduler scheduler;
    public static Message message_f;
    public static Message message_e;

    /**
     * Initializes the variables that will be used to test the methods in the Scheduler Class
     */
	@BeforeAll
	public static void init(){

        Queue<Message> schedulerQueue = new LinkedList<>();
		Queue<Message> floorQueue = new LinkedList<>();
		Queue<Message> elevatorQueue = new LinkedList<>();

        Floor[] floors = new Floor[] { floor };
		Elevator[] elevators = new Elevator[] { elevator };

		floor = new Floor(floorQueue, schedulerQueue, 0);
        elevator = new Elevator(elevatorQueue, schedulerQueue, 0, 0);
        scheduler = new Scheduler(schedulerQueue, floorQueue, elevatorQueue, floors, elevators);
        message_f = new Message(Sender.FLOOR, 0, true);
        message_e = new Message(Sender.ELEVATOR, 0, true);
    }
	
	/**
     * Method to test the sendFloorMessage method in Scheduler class
     */
	@Test
    public void testSendFloorMessage(){
        
        scheduler.sendFloorMessage(message_f);

        assertEquals(message_f, floor.getRecieveQueue().poll(), "Message was not sent/received properly");
    }

	/**
     * Method to test the sendElevatorMessage method in Scheduler class
     */
	@Test
    public void testSendElevatorMessage(){
        
        scheduler.sendElevatorMessage(message_e);

        assertEquals(message_e, elevator.getRecieveQueue().poll(), "Message was not sent/received properly");
    }
}
