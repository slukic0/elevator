package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class NetworkUtils {
	private NetworkUtils() {
	}

	/**
	 * Send a UDP packet to localhost containing a byte array and print logging
	 * information.
	 *
	 * @param msg    byte array message
	 * @param socket socket to use to send the packet
	 * @param destPort   what port to send the packet to
	 * @throws IOException thrown if packet cannot be sent
	 */
	public static void sendPacket(byte[] msg, DatagramSocket socket, int destPort) throws IOException {
		try {
			sendPacket(msg, socket, destPort, InetAddress.getLocalHost());
		} catch (UnknownHostException e1) {
			System.err.println("ERROR: unable to get local host address!");
			throw e1;
		}
	}

	/**
	 * Send a UDP packet containing a byte array and print logging information.
	 *
	 * @param msg      byte array message
	 * @param socket   socket to use to send the packet
	 * @param destPort what port to send the packet to
	 * @param address  the destination IP address
	 * @throws IOException thrown if packet cannot be sent
	 */
	public static void sendPacket(byte[] msg, DatagramSocket socket, int destPort, InetAddress address)
			throws IOException {
		DatagramPacket packet;
		packet = new DatagramPacket(msg, msg.length, address, destPort);

		try {
			socket.send(packet);
		} catch (Exception e) {
			System.err.println("ERROR: Unable to send packet");
			throw e;
		}
	}

	/**
	 * Receive a UDP packet using the given socket and print logging information
	 *
	 * @param socket socket to receive packet
	 * @return the received packet
	 * @throws IOException thrown if the socket cannot receive a packet
	 */
	public static DatagramPacket receivePacket(DatagramSocket socket) throws IOException {
		byte[] data = new byte[2084];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		try {
			socket.receive(receivePacket);
		} catch (Exception e) {
			System.err.println("ERROR: Unable to receive packet!");
			throw e;
		}
		return receivePacket;
	}

	/**
	 * Send a packet and wait for a response
	 * 
	 * @param receieveSocket socket used for sending
	 * @param sendSocket     socket used for receiving
	 * @param msg            message to send
	 * @param sendPort       port to send to
	 * @param address        address to send to
	 * @return the response
	 * @throws IOException thrown if we cannot send or receive the packet
	 */
	public static DatagramPacket sendAndReceiveRepy(DatagramSocket receieveSocket, DatagramSocket sendSocket,
			byte[] msg, int sendPort, InetAddress address) throws IOException {
		sendPacket(msg, sendSocket, sendPort, address);
		return receivePacket(receieveSocket);
	}

	/**
	 * Send a packet and wait for a response
	 * 
	 * @param receieveSocket socket used for sending
	 * @param sendSocket     socket used for receiving
	 * @param msg            message to send
	 * @param sendPort       port to send to
	 * @return the response
	 * @throws IOException thrown if we cannot send or receive the packet
	 */
	public static DatagramPacket sendAndReceiveRepy(DatagramSocket receieveSocket, DatagramSocket sendSocket,
			byte[] msg, int sendPort) throws IOException {
		sendPacket(msg, sendSocket, sendPort);
		return receivePacket(receieveSocket);
	}

	public static byte[] serializeObject(Object obj) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(outputStream);
			os.writeObject(obj);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Arrays.copyOf(outputStream.toByteArray(), outputStream.toByteArray().length);
	}

	public static Object deserializeObject(DatagramPacket packet) {
		byte[] data = packet.getData();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		
		try {
			ObjectInputStream is = new ObjectInputStream(in);
			return (Object) is.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("ERROR: unable to deserialize packet " + packet.toString());
			throw new Error("Failed to Deserialize packet " + packet.toString());
		}
	}

	/**
	 * Print the string and byte array representation of an array of bytes
	 * 
	 * @param message byte array to print
	 */
	public static void printMessage(byte[] message) {
		System.out.println("Bytes: " + Arrays.toString(message));
		System.out.println("String: " + new String(message));
	}

	/**
	 * Print the string and byte array representation of a DatagramPacket
	 * 
	 * @param message DatagramPacket to print
	 */
	public static void printMessage(DatagramPacket packet) {
		printMessage(packet.getData());
	}

}
