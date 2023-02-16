package elevator;
import java.util.HashMap;
import java.util.Queue;

public class ElevatorSubsystem implements Runnable{
	
	private Elevator[] elevators;
	private HashMap<Elevator, Integer> tasks; // Elevator, Floor
	private Queue<Message> elevatorQueue;
	
	public ElevatorSubsystem(Elevator[] elevators){
		this.elevators = elevators;
		HashMap<Elevator, Integer> tasks = new HashMap<Elevator, Integer>();
	}

	@Override
	public void run() {
		while(true) {
			synchronized(tasks) {
//				while(// condtion) {
//					tasks.wait();
//				}
			}
			
		}
		
	}
	
	public void sendTaskToElevator() {  // Send floor number to elevator queue
		
	}
	
	public void sendArrivalToScheduler() { //send elevator's floor arrival to scheduler
		
	}
	
	public void receiveTaskFromScheduler() { // receive Task for elevator and destination from scheduler
		
	}
	
	public void receiveStatusFromElevator() { // receive Status from elevator 
		
	}
	
}
