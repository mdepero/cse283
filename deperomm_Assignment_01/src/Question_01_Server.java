import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Matt DePero CSE283 B Dr. Jianhui Yue
 * 
 * @author deperomm
 * 
 *         This class is a simple java server that utilized UDP and returns the
 *         date and time at the moment the server receives the packet.
 *         
 *         This class gets its port by entering it in the console when prompted
 * 
 */
public class Question_01_Server {

	// variables for socket
	static DatagramSocket myUDPSocket;
	static int port, bufferSize;
	static byte[] buffer;

	// handle getting the date and time
	static Date currentDate;
	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// be able to enter the server info from the terminal
	static Scanner keyboardInput = new Scanner(System.in);

	/**
	 * This method collects necessary information from the user before starting
	 * the server
	 */
	public static void getInfo() {

		// get the port
		do {

			System.out.print("What port do you want to use? ");
			port = keyboardInput.nextInt();

		} while (port <= 0);

		// get the byte buffer size
		do {

			System.out.print("How big of a buffer do you want? ");
			bufferSize = keyboardInput.nextInt();

		} while (bufferSize <= 0);

	}// end getInfo

	/**
	 * this method starts and runs the server. any packets it receives will be
	 * returned with the date and time of the server when the packet is
	 * received.
	 * 
	 * @throws IOException
	 */
	public static void startServer() throws IOException {

		myUDPSocket = new DatagramSocket(port);

		buffer = new byte[bufferSize];

		while (true) {

			// set up socket for receiving data
			DatagramPacket incomingMessage = new DatagramPacket(buffer,
					bufferSize);
			myUDPSocket.receive(incomingMessage);

			// get most current date and time
			currentDate = new Date();

			// alert the terminal that a packet was received
			System.out.println("Received packet: "
					+ new String(incomingMessage.getData()));
			System.out.println("Returning current system date and time...");

			// set up the outgoing message
			byte[] replyData = dateFormat.format(currentDate).getBytes();
			DatagramPacket outgoingMessage = new DatagramPacket(replyData,
					replyData.length, incomingMessage.getAddress(),
					incomingMessage.getPort());

			// send the packet
			myUDPSocket.send(outgoingMessage);

		}
	}// end startServer

	/**
	 * This is the main method that runs the methods above and also handles any
	 * errors
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		getInfo();

		try {
			
			startServer();

		} catch (IOException e) {

			System.err.println(e.getMessage());

		} finally {

			myUDPSocket.close();
		}

	}// end main

}// end class
