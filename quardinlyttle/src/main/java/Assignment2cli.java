/*
Quardin Lyttle Assignment 2
Client Portion
 */
import java.io.*;
import java.net.*;

public class Assignment2cli {
	private static int portNum;
	private static InetAddress ipaddress;
	private static DatagramSocket socket;
	private static File file;
	private static byte[] buffer;
	private static int bufferSize= 512;

    public static void main(String[] args){
    	
    	//Error handling and validation of args
    	if(args.length!= 3) {
    		System.out.println("Please provide the IP address, port number, and filename!");
    		System.exit(0);
    	}
    	
    	//Check IP
    	try {
    		ipaddress = InetAddress.getByName(args[0]);
    	}
    	catch(Exception e) {
    		  System.err.println("Error: Invalid IP address provided.");
    	}
    	//Check Port number
    	 
    	try {
    		portNum = Integer.parseInt(args[1]);
    		if (portNum < 1024 || portNum > 65535){
    		        System.err.println("Error: Port number must be between 1024 and 65535.");
    		        System.exit(1);
    		}
    		socket = new DatagramSocket();
    	}
    	catch (Exception e) {
    		System.err.println("Port number not valid!");
    		}
    	//Check File
    	file = new File(args[2]);
    	if (!file.exists() || !file.isFile() || !file.canRead()) {
    	    System.err.println("File is invalid or cannot be read.");
    	    System.exit(1);
    	}
    	
    	
    	// https://www.baeldung.com/udp-in-java
    	//Read file and send over packets
    	try{
    		//Send Filename first
    		byte[] filenameBytes = args[2].getBytes();
    		DatagramPacket filenamePacket = new DatagramPacket(filenameBytes,filenameBytes.length,ipaddress,portNum);
			socket.send(filenamePacket);

    		InputStream in = new BufferedInputStream(new FileInputStream(file));
    		buffer = new byte[bufferSize];
    		int bytesRead;
    		while ((bytesRead = in.read(buffer)) != -1) {
    			DatagramPacket chunks = new DatagramPacket(buffer,bytesRead,ipaddress,portNum);
    			socket.send(chunks);
    		}
    		byte[] eof= "EOF".getBytes();
    		DatagramPacket finalPacket = new DatagramPacket(eof,eof.length,ipaddress,portNum);
    		socket.send(finalPacket);
    		socket.close();
    		in.close();
    		
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		
    	}

    	
    }
    
}
