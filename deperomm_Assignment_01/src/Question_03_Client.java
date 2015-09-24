import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Matt DePero CSE283 B Dr. Jianhui Yue
 * 
 * @author deperomm
 * 
 *         This class connects to a TCP server and exchanges messages provided
 *         by the user to attempts to guess a number generated by the server
 * 
 * 
 */
public class Question_03_Client {

	private static String hostName;
	private static int port = 11111;

	// a scanner for reading in user input
	private static Scanner input = new Scanner(System.in);

	/**
	 * This static class uses the main method to do all of its work The main
	 * method runs, loops during the game, and then ends
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// get info for the client
		System.out.print("Enter the port you'd like to use: ");

		port = Integer.parseInt(input.nextLine());

		System.out.print("\nEnter the host you'd like to connect to: ");

		hostName = input.nextLine();

		Socket s = null;

		// Create the socket connection
		try {
			s = new Socket(hostName, port);

		} catch (UnknownHostException uhe) {

			// Server Host unreachable
			System.out.println("Unknown Host :" + hostName);
			s = null;

		} catch (IOException ioe) {

			// Cannot connect to port on given server host
			System.out
					.println("Cant connect to server. Make sure it is running and port is correct.");
			s = null;
		}

		if (s == null)
			System.exit(-1);

		BufferedReader in = null;
		PrintWriter out = null;

		try {
			// Create the streams to send and receive information
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

			// begin handshake with server
			out.println("Hi Server, let's play a game.");
			out.flush();
			System.out.println("\nHi Server, let's play a game.");

			boolean stillPlayingGame;

			// Receive the reply.
			String handShake = in.readLine();

			if (handShake.contains("Ok, let's play.")) {
				stillPlayingGame = true;
				System.out.println("Server replied: " + handShake);
			} else {
				stillPlayingGame = false;
			}

			String serverMessage;

			// begin playing the game
			while (stillPlayingGame) {

				String messageToSend = input.nextLine();

				out.println(messageToSend);
				out.flush();

				serverMessage = in.readLine();

				// interpret the result from the server. End on certain key
				// words.
				if (serverMessage == null
						|| serverMessage.contains("You guessed it!")
						|| serverMessage.contains("that was your last guess")
						|| serverMessage.contains("quit")) {
					// game ended
					stillPlayingGame = false;
				}

				// if the server returned results, print them
				if (serverMessage != null) {
					System.out.println("Server replied: " + serverMessage);
				}

			}

		} catch (IOException ioe) {
			System.out
					.println("Exception during communication. Server probably closed connection.");
		} finally {
			try {
				// Close the input and output streams
				out.close();
				in.close();

				// close the Scanner
				input.close();

				// Close the socket before quitting
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}// end main

}// end class