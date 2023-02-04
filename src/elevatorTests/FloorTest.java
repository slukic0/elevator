
package elevatorTests;
import elevator.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.LinkedList;
import java.util.Queue;
import elevator.Message.Sender;
import util.FileUtil;


/**
 * Class for testing the methods in the Floor Class
 *
 * @author Group G5
 * @version 1.0
 */
public class FloorTest {

    public static Floor floor;
    public static Elevator elevator;
    public static Scheduler scheduler;
    public static Message message;

    /**
     * Initializes the variables that will be used to test the methods in the Floor Class
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
        message = new Message(Sender.FLOOR, 0, true);
    }

    /**
     * Method to test reading from an input file in Floor class
     */
	@Test
    public void testReadInput(){
		
		// Read input file and create floor message
		try {
			String[] input = FileUtil.readFile(floor.getClass(), "events.txt");
			Message[] messages = FileUtil.parseStringInput(input);
			floor.addMessage(messages[0]);
		} catch (Exception e) {
		}

		assertEquals(Sender.FLOOR, floor.getMessages().get(0).getSender(), "Reading message from input failed");
        assertEquals(0, floor.getMessages().get(0).getFloor(), "Reading message from input failed");
        assertTrue(floor.getMessages().get(0).getGoingUp(),"Reading message from input failed");
        assertEquals("13:34:46.438994900", floor.getMessages().get(0).getTime().toString(), "Reading message from input failed");
        
    }
	
	/**
     * Method to test sending a message in Floor class
     */
	@Test
    public void testSendMessage(){
        
        floor.getSchedulerQueue().add(message);

        assertEquals(message, scheduler.getRecieveQueue().poll(), "Message was not sent/received properly");
    }

}
