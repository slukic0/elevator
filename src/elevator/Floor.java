package elevator;

public class Floor implements Runnable{
	private Scheduler server;
	private int floorNumber;
	
	public Floor(Scheduler server, int floorNumber) {
		this.server = server;
		this.floorNumber = floorNumber;
	}
	
	private void sendMessage() {
		
	}
	
	public void run() {
		return;
	}
}
