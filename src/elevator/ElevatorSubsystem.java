package elevator;
import java.util.HashMap;
import java.util.Queue;

import util.SendReceiveUtil;

public class ElevatorSubsystem implements Runnable{
	
	private Elevator[] elevators;
	private Queue<Object> receiveQueue;
	private Queue<Object> schedulerReceiveQueue;
	
	public ElevatorSubsystem(Elevator[] elevators, Queue<Object> receiveQueue, Queue<Object>schedulerReceiveQueue){
		this.elevators = elevators;
		this.receiveQueue = receiveQueue;
		this.schedulerReceiveQueue = schedulerReceiveQueue;
	}

	@Override
	public void run() {
		while(true) {
			synchronized(receiveQueue) {
				while (receiveQueue.isEmpty()) {
					try {
						receiveQueue.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				for (int i=0; i<receiveQueue.size(); i++) {
					Object data = receiveQueue.poll();
					if (data instanceof FloorData) {
						// tell the elevator to process the data (just 1 elevator for now)
						elevators[0].processPacket((FloorData) data);
					} else if (data instanceof ElevatorData) {
						// elevator sending data to scheduler
						new Thread(() -> {
							SendReceiveUtil.sendData(schedulerReceiveQueue, data);
						}).start();
						
					}
				}
				
			}
			
		}
		
	}
//	
//	public void sendTaskToElevator() {  // Send floor number to elevator queue
//		
//	}
//	
//	public void sendArrivalToScheduler() { //send elevator's floor arrival to scheduler
//		
//	}
//	
//	public void receiveTaskFromScheduler() { // receive Task for elevator and destination from scheduler
//		
//	}
//	
//	public void receiveStatusFromElevator() { // receive Status from elevator 
//		
//	}
//	
}
