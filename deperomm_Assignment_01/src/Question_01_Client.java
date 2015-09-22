import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;


/**
 * Matt DePero CSE283 B Dr. Jianhui Yue
 * 
 * @author deperomm
 * 
 *         This class is a simple java client that reads in a
 *         host, port, and message from the console, sends a 
 *         packet, and prints the server's response.
 * 
 */
public class Question_01_Client {

	static DatagramSocket socket;

	static String messageString, hostString, portString;

	static Scanner keyboardInput = new Scanner(System.in);

	/**
	 * Gets the information for the packet from the console
	 */
	public static void getInfo() {

		System.out.print("Enter Host Name: ");
		hostString = keyboardInput.nextLine();

		System.out.print("Enter Port Number: ");
		portString = keyboardInput.nextLine();

		System.out.print("Enter Message to Send: ");
		messageString = keyboardInput.nextLine();
	
	}// end getInfo

	
	/**
	 * Sends the packet and prints the server's response
	 */
	public static void sendMessage() {

		try {
			
			// set up socket connection
			socket = new DatagramSocket();
			InetAddress host = InetAddress.getByName(hostString);
			int serverPort = Integer.valueOf(portString).intValue();
			
			// set up packet and send it
			DatagramPacket packet = new DatagramPacket(
					messageString.getBytes(), messageString.length(), host,
					serverPort);	
			socket.send(packet);
			
			// set up reply packet
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			
			// retrieve and print reply
			socket.receive(reply);
			System.out.println("Server Replied: " + new String(reply.getData()));
			
		} catch (Exception e) {
			
			System.out.println("An Error Occured - " + e.getMessage());
			
		}  finally {
			if (socket != null)
				socket.close();
		}
	
	}// end sendMessage

	/**
	 * A main class for running the static methods above
	 * @param args
	 */
	public static void main(String args[]) {

		getInfo();

		sendMessage();

	}// end main
}// end class