package elevator;

import java.util.Queue;

import elevator.Message.Sender;

public class Scheduler implements Runnable {
	private Floor[] floors;
	private Elevator[] elevators;
	private Queue<Message> recieveQueue;
	private Queue<Message> floorQueue;
	private Queue<Message> elevatorQueue;

	public Scheduler(Queue<Message> recieveQueue, Queue<Message> floorQueue, Queue<Message> elevatorQueue,
			Floor[] floors, Elevator[] elevators) {
		this.recieveQueue = recieveQueue;
		this.floorQueue = floorQueue;
		this.elevatorQueue = elevatorQueue;
		this.floors = floors;
		this.elevators = elevators;
	}

	private void sendFloorMessage(Message message) {
		synchronized (floorQueue) {
			floorQueue.add(message);
			System.out.println("Scheduler forwarded message to floor");
			floorQueue.notifyAll();
		}
	}

	private void sendElevatorMessage(Message message) {
		synchronized (elevatorQueue) {
			elevatorQueue.add(message);
			System.out.println("Scheduler forwarded message to elevator");
			elevatorQueue.notifyAll();
		}
	}

	public void run() {
		while (true) {
			synchronized (recieveQueue) {
				while (recieveQueue.isEmpty()) {
					try {
						recieveQueue.wait();
					} catch (InterruptedException e) {
						System.out.println("Error in Scheduler Thread");
						e.printStackTrace();
					}
				}
				// get the message
				Message message = recieveQueue.poll();
				System.out.println("Scheduler got message: " + message.toString());
				recieveQueue.notifyAll();

				if (message.getSender() == Sender.FLOOR) {
					sendElevatorMessage(message);
				} else {
					sendFloorMessage(message);

				}
			}
		}
	}
}