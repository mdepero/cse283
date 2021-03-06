import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

/**
 * Matt DePero CSE283 B Dr. Jianhui Yue
 * 
 * @author deperomm
 * 
 *         This class acts as a TCP server with multi-threading for each
 *         connection made to it. Upon connection, it generates a random range
 *         of number and an random number that the has to try and guess
 * 
 * 
 */
public class Question_03_Server {

	// Socket variables
	ServerSocket serverSocket;
	boolean serverIsOn;
	int port;

	// Thread variables
	int threadID;

	// used for random number generation
	Random rand = new Random();

	/**
	 * Constructor that initializes and runs the server
	 */
	public Question_03_Server() {

		getServerInfo();

		setUpServer();

		runServer();

	}// end constructor

	/**
	 * prompts user for info for the server
	 */
	private void getServerInfo() {
		Scanner input = new Scanner(System.in);

		System.out.print("What port would you like to use? ");
		port = Integer.parseInt(input.nextLine());

		input.close();

	}// end getServerInfo

	/**
	 * sets up the server with the info
	 */
	private void setUpServer() {

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException ioe) {
			System.out.println("Could not create server socket on port " + port
					+ ". " + ioe.getMessage());

			System.exit(-1);
		}

		serverIsOn = true;

	}// end setupserver

	/**
	 * Runs the server. Creates a new thread for each subsequent connection
	 */
	private void runServer() {

		while (serverIsOn) {

			try {

				Socket socketToClient = serverSocket.accept();

				threadID++;

				ClientThread singleClientThread = new ClientThread(
						socketToClient, threadID);
				singleClientThread.start();

			} catch (IOException ioe) {
				System.out
						.println("Exception encountered on accept. Ignoring. Stack Trace :");
				ioe.printStackTrace();
			}

		}

		try {
			serverSocket.close();
			System.out.println("Server Stopped");
		} catch (Exception ioe) {
			System.out.println("Problem stopping server");
			System.exit(-1);
		}

	}// end runserver

	/**
	 * An inner class for running each thread for each connection
	 * 
	 * @author matt
	 * 
	 */
	public class ClientThread extends Thread {

		// thread variables
		Socket clientSocket;
		int threadID;
		boolean threadRunning;

		// game variables
		int min, max, num, numGuesses;
		int maxGuessesAllowed = 5;

		/**
		 * Constructor for the socket thread.
		 * 
		 * @param clientSocket
		 *            The socket that connects this thread to the client
		 * @param threadID
		 *            The unique ID for this thread on the server
		 */
		public ClientThread(Socket clientSocket, int threadID) {

			this.clientSocket = clientSocket;

			this.threadID = threadID;

			threadRunning = false;

		}// end constructor

		/**
		 * starts the thread process and loops until completed
		 */
		public void run() {

			threadRunning = true;

			System.out.println("Starting Thread.\n  Session: " + threadID
					+ "\n  Client Address: "
					+ clientSocket.getInetAddress().getHostName());

			BufferedReader inputFromClient = null;
			PrintWriter outputToClient = null;

			try {

				// set up input output buffers for the client
				inputFromClient = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));

				outputToClient = new PrintWriter(new OutputStreamWriter(
						clientSocket.getOutputStream()));

				// run the thread
				while (threadRunning) {

					// read in message from client
					String clientMessage = inputFromClient.readLine();
					System.out.println("Thread " + threadID + " Said: "
							+ clientMessage);

					// check that the upper level server wasn't stopped while
					// running the thread
					if (!serverIsOn) {
						// The server was stopped. Quit this thread
						System.out.print("Ending thread " + threadID
								+ ", server was stopped.");
						outputToClient.println("Sorry, the server was stopped");
						outputToClient.flush();
						threadRunning = false;

					}

					// based on the message received, create the message to
					// return

					// messageToSend will be sent to the client and printed on
					// the server console
					String messageToSend;

					if (clientMessage.equalsIgnoreCase("quit")) {

						threadRunning = false;

						messageToSend = "Quitting process due to stop message: "
								+ clientMessage;

					} else if (clientMessage
							.equalsIgnoreCase("Hi Server, let's play a game.")) {

						// creates a random range of numbers
						min = rand.nextInt(100);

						max = rand.nextInt(500 - min) + min;

						num = rand.nextInt(max - min) + min;

						System.out.println("Thread " + threadID
								+ " Number to Guess is: " + num);

						messageToSend = "Ok, let's play. Guess a number between "
								+ min + " and " + max + " (inclusive)";

					} else if (clientMessage.matches("^\\d+$")) {
						// client sent a number, check guess

						if (Integer.parseInt(clientMessage) == num) {

							messageToSend = "You guessed it! The game is done.";
							threadRunning = false;

						} else {

							if (numGuesses + 1 >= maxGuessesAllowed) {

								messageToSend = "Sorry, that was your last guess. You lose, the number was "
										+ num + ". The game is done.";
								threadRunning = false;

							} else {
								numGuesses++;
								int guess = Integer.parseInt(clientMessage);
								messageToSend = String
										.format("That's not it,  the number is %s than that.",
												(num > guess) ? "greater"
														: "less");
							}
						}

					} else {

						// all other input mark as invalid
						messageToSend = "Invalid input, try again.";

					}

					// send the actual reply to the server and console
					outputToClient.println(messageToSend);
					outputToClient.flush();
					System.out.println("Thread " + threadID + " Sent: "
							+ messageToSend);

				}

				// handle exceptions
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				try {
					inputFromClient.close();
					outputToClient.close();
					clientSocket.close();
					System.out.println("Stopped thread " + threadID);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

		}// end run()

	}// end inner class ClientThread

	/**
	 * main method initializes the server
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		new Question_03_Server();

	}// end main

}// end class
