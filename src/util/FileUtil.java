package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;

import messages.FloorData;

public class FileUtil {
	/**
	 * method to read each line in events file
	 * @param myClass
	 * @param filePath relative path to events
	 * @return array with each line
	 * @throws IOException
	 */
	public static String[] readFile(Class<?> myClass, String filePath) throws IOException {
		if (myClass == null)
			throw new IllegalArgumentException();

		InputStream stream = myClass.getResourceAsStream(filePath);

		ArrayList<String> lines = new ArrayList<String>();
		try (InputStreamReader isr = new InputStreamReader(stream); BufferedReader reader = new BufferedReader(isr)) {

			String line;
			while (reader.ready()) {
				line = reader.readLine();
				if (!line.isBlank() && !line.startsWith("/") && !line.startsWith("#")) {
					lines.add(line);
				}
			}
		}
		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * Method to parse each string into variables
	 * @param lines array of strings
	 * @return floor data of with floor data variables
	 */
	public static FloorData[] parseStringInput(String[] lines) {
		ArrayList<FloorData> data = new ArrayList<>();
		for (String s : lines) {
			// time|startingFloor|destinationFloor|Hard|Transient
			// Example: 12:43:47.0|1|3
			String[] event = s.split("\\|");

			int startingFloor = Integer.parseInt(event[1]);
			int destinationFloor = Integer.parseInt(event[2]);
			LocalTime time = LocalTime.parse(event[0]);
			int hardFault = Integer.parseInt(event[3]);
			int transientFault = Integer.parseInt(event[4]);

			data.add(new FloorData(startingFloor, destinationFloor, time, hardFault, transientFault));
		}

		return data.toArray(new FloorData[data.size()]);
	}
}
