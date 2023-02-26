package elevator;

import java.util.HashMap;
import java.util.Queue;

import util.SendReceiveUtil;

public class ElevatorSubsystem implements Runnable {

	private Elevator elevator;
	private Queue<FloorData> receiveQueue;
	private Queue<Object> schedulerReceiveQueue;

	public ElevatorSubsystem(Queue<FloorData> receiveQueue, Queue<Object> schedulerReceiveQueue, int elevatorNumber,
			int currentFloor) {
		this.elevator = new Elevator(null, elevatorNumber, currentFloor);
		this.receiveQueue = receiveQueue;
		this.schedulerReceiveQueue = schedulerReceiveQueue;
	}

	public Elevator getElevator() {
		return elevator;
	}

	public void sendSchedulerMessage(ElevatorData message) {
		new Thread(() -> {
			SendReceiveUtil.sendData(schedulerReceiveQueue, message);
		}).start();
	}

	@Override
	public void run() {
		while (true) {
			synchronized (receiveQueue) {
				while (receiveQueue.isEmpty()) {
					try {
						receiveQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < receiveQueue.size(); i++) {
					FloorData data = receiveQueue.poll();
					System.out.println("Elevator SubSystem received " + data);
					elevator.processPacket(data);
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
