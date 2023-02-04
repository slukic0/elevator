
package elevatorTests;
import elevator.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.LinkedList;
import java.util.Queue;
import elevator.Message.Sender;


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
    public static Message message;

    /**
     * Initializes the variables that will be used to test the methods in the Elevator Class
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
        message = new Message(Sender.ELEVATOR, 0, true);
    }
	
	/**
     * Method to test sending a message in Elevator class
     */
	@Test
    public void testSendMessage(){
        
        elevator.getSchedulerQueue().add(message);

        assertEquals(message, scheduler.getreceiveQueue().poll(), "Message was not sent/received properly");
    }

}
