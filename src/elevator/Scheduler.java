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

	public Queue<Message> getMessages() {
		return recieveQueue;
	}

	public void enqueueMessage(Message mesage) {
		recieveQueue.add(mesage);
	}

	public void run() {
		synchronized (recieveQueue) {
			while (recieveQueue.isEmpty()) {
				try {
					System.out.println(recieveQueue.toString());
					System.out.println("sched wait");
					recieveQueue.wait();
				} catch (InterruptedException e) {
					System.out.println("Error in Scheduler Thread");
					e.printStackTrace();
				}
			}
			// get the message
			Message message = recieveQueue.poll();
			System.out.println("Scheduler got message: " + message.toString());

			if (message.getSender() == Sender.FLOOR) {
				floorQueue.add(message);
			} else {
				elevatorQueue.add(message);
			}
			System.out.println("Scheduler forwarded message tp" + message.getSender());

			notifyAll();
		}
	}
}