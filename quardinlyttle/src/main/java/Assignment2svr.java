/*
Quardin Lyttle Assignment 2
Server Portion
 */
import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Assignment2svr {
	private static int portNum;
	private static DatagramSocket socket;
	private static int bufferSize = 512;
	private static HashMap<String, FileOutputStream> clientFiles = new HashMap<>();

	public static void main(String[] args) {

		// Error handling for args
		if (args.length != 1) {
			System.out.println("Please provide the port number!");
			System.exit(1);
		}

		// Check port number
		try {
			portNum = Integer.parseInt(args[0]); 
			if (portNum < 1024 || portNum > 65535) {
				System.err.println("Error: Port number must be between 1024 and 65535.");
				System.exit(1);
			}
			socket = new DatagramSocket(portNum);
			System.out.println("Server listening on port " + portNum + "...");
		} catch (Exception e) {
			System.err.println("Error: Invalid port number. Must be between 1024–65535.");
			e.printStackTrace(); 
			System.exit(1);
		}

		// Packet reception loop
		try {
			while (true) {
				byte[] buffer = new byte[bufferSize]; // 
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				String clientKey = packet.getAddress().getHostAddress() + ":" + packet.getPort();
				byte[] data = packet.getData();
				int length = packet.getLength();

				if (!clientFiles.containsKey(clientKey)) {
					// First packet is filename
					String originalFilename = new String(data, 0, length).trim(); // trim whitespace
					
					// prevent overwriting
					String filename = "received_" + clientKey.replace(":", "_") + "_" + originalFilename;

					FileOutputStream out = new FileOutputStream(filename);
					clientFiles.put(clientKey, out);
					System.out.println("Started receiving from " + clientKey + " → " + originalFilename);
				} else {
					// Check for EOF
					String msg = new String(data, 0, length);
					if (msg.equals("EOF")) {
						clientFiles.get(clientKey).close();
						clientFiles.remove(clientKey);
						System.out.println("Completed transfer from " + clientKey);
					} else {
						// Write file chunk
						clientFiles.get(clientKey).write(data, 0, length);
						System.out.println("Received " + length + " bytes from " + clientKey);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
}
