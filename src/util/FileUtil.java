package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;

import elevator.Message;
import elevator.Message.Sender;


public class FileUtil {
	public static String[] readFile(Class<?> myClass, String filePath) throws IOException {		
		if (myClass == null ) throw new IllegalArgumentException();
				
		ArrayList<String> lines = new ArrayList<String>();
		try (InputStreamReader isr = new InputStreamReader(myClass.getResourceAsStream(filePath));
				BufferedReader reader = new BufferedReader(isr)){
			
			String line;
			while(reader.ready()) {
				line = reader.readLine();
				if(!line.isBlank() || !line.startsWith("/") && !line.startsWith("#")) {
					lines.add(line);
				}
			}
		}
		return lines.toArray(new String[lines.size()]);
	}

	public static Message[] parseStringInput(String[] lines) {
		ArrayList<Message> messages = new ArrayList<>();
		for (String s : lines) {
			// time|floor|button
			// Example: 12:43:47.0|0|UP
			String[] event = s.split("\\|");
			
			int floor = Integer.parseInt(event[1]);
			boolean up = event[2].equals("UP") ? true : false;
			LocalTime time = LocalTime.parse(event[0]);
			
			messages.add(new Message(Sender.FLOOR, floor, up, time));
		}
		
		return messages.toArray(new Message[messages.size()]);
	}
}
