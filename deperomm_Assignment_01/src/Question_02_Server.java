import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Matt DePero CSE283 B Dr. Jianhui Yue
 * 
 * @author deperomm
 * 
 *         This class is a simple java server that utilized UDP and takes a long
 *         value representing the time of the client. If the correct type of
 *         value is sent by the client, it returns the difference in time (in
 *         milliseconds) between the client and the server. Otherwise it returns
 *         a string.
 * 
 * 
 */
public class Question_02_Server {

	// the string to return if the date received from the client is not properly
	// formatted
	static String ERROR_TEXT = "Incorrect data sent, unable to retrieve time data from packet.";

	// socket variables
	static DatagramSocket myUDPSocket;
	static int port, bufferSize;
	static byte[] buffer;

	// handle getting the date and time
	static Date currentDate;
	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * This method gets the information for the server
	 */
	public static void getInfo() {

		port = 99;

		bufferSize = 1000;

	}// end getInfo

	/**
	 * this method starts the server and actively listens for packets. If it
	 * receives a correctly formatted packet, it returns the difference in time
	 * between the client and the server in milliseconds. If it is not properly
	 * formatted, it returns a string.
	 * 
	 * @throws IOException
	 */
	public static void startServer() throws IOException {

		myUDPSocket = new DatagramSocket(port);

		buffer = new byte[bufferSize];

		while (true) {

			// wait for incoming message
			DatagramPacket incomingMessage = new DatagramPacket(buffer,
					bufferSize);
			myUDPSocket.receive(incomingMessage);

			byte[] replyData;

			try {

				// converts the byte code into the long value representing time
				long clientTime = java.nio.ByteBuffer.wrap(
						incomingMessage.getData()).getLong();

				// get most up to date time
				currentDate = new Date();

				long timeDifference = currentDate.getTime() - clientTime;

				if (timeDifference < 0 || timeDifference > 1000000) {

					// received incorrect data, as it is not possible for the
					// packet transfer to take negative time, or if difference
					// is too great likely incorrect data also
					throw new Exception();
				}

				String timeDifferenceString = Long.toString(timeDifference);

				replyData = timeDifferenceString.getBytes();// Long.toString(timeDifference).getBytes();

			} catch (Exception e) {

				// unable to parse client data to a number value, return error
				// message
				replyData = ERROR_TEXT.getBytes();

			}

			// send reply message
			DatagramPacket outgoingMessage = new DatagramPacket(replyData,
					replyData.length, incomingMessage.getAddress(),
					incomingMessage.getPort());

			myUDPSocket.send(outgoingMessage);

		}

	}// end startServer

	/**
	 * This main method calls the methods above and handles errors
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
