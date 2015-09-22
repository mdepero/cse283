import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Matt DePero CSE283 B Dr. Jianhui Yue
 * 
 * @author deperomm
 * 
 *         This class sends the current system time to a server, which is
 *         configured to return the difference in time between this client and
 *         that server
 * 
 * 
 */
public class Question_02_Client {

	// variables for client
	static DatagramSocket myUDPSocket;
	static int port;
	static String hostString;
	static byte[] byteData;
	static InetAddress host;

	// handle getting the date and time
	static Date currentDate;
	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * This class gets the data for the client and info to send
	 */
	public static void getInfo() {

		hostString = "localhost";

		port = 99;

		currentDate = new Date();

	}

	/**
	 * This class takes the current time and sends it to a server. It also then
	 * listens for that server to reply the difference between the client time
	 * and the server time and then prints the result
	 * 
	 * @throws IOException
	 */
	public static void sendPacket() throws IOException {

		// set up socket
		myUDPSocket = new DatagramSocket();

		// convert current time value into a byte array
		byteData = ByteBuffer.allocate(8).putLong(currentDate.getTime())
				.array();

		// set up and send packet
		host = InetAddress.getByName(hostString);
		DatagramPacket outgoingMessage = new DatagramPacket(byteData,
				byteData.length, host, port);
		myUDPSocket.send(outgoingMessage);

		// set up and receive reply
		byte[] buffer = new byte[1000];
		DatagramPacket incomingMessage = new DatagramPacket(buffer,
				buffer.length);
		myUDPSocket.receive(incomingMessage);

		// print reply
		System.out.println("Time Difference: "
				+ new String(incomingMessage.getData()) + " milliseconds");

	}// end sendPacket

	/**
	 * This main method calls the methods above and handles errors
	 * @param args
	 */
	public static void main(String[] args) {

		getInfo();

		try {
			sendPacket();

		} catch (IOException e) {

			System.err.println(e.getMessage());

		} finally {

			myUDPSocket.close();
		}

	}// end main

}// end class
