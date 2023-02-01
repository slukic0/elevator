package elevator;

public class Scheduler implements Runnable{
	private Floor[] floors;
	private Elevator[] elevators;
	
	public Scheduler(Floor[] floors, Elevator[] elevators) {
		this.floors = floors;
		this.elevators = elevators;
	}
	
	public void run() {
		return;
	}
}