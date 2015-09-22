import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Question_03_Server {

	int SERVER_PORT = 11111;

	// Socket variables
	ServerSocket serverSocket;
	boolean serverIsOn;
	int port;

	// Thread variables
	int threadID;

	public Question_03_Server() {

		getServerInfo();

		setUpServer();

		runServer();

	}

	private void getServerInfo() {

		port = SERVER_PORT;
	}

	private void setUpServer() {

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException ioe) {
			System.out.println("Could not create server socket on port " + port
					+ ". Quitting.");

			System.exit(-1);
		}

		serverIsOn = true;

	}

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
			System.out.println("Problem stopping server socket");
			System.exit(-1);
		}

	}

	public class ClientThread extends Thread {

		Socket clientSocket;
		int threadID;
		boolean threadRunning;

		public ClientThread(Socket clientSocket, int threadID) {

			this.clientSocket = clientSocket;

			this.threadID = threadID;

			threadRunning = false;

		}

		public void run() {

			threadRunning = true;

			System.out.println("Starting Thread.\n  Session: " + threadID
					+ "\n  Client Address: "
					+ clientSocket.getInetAddress().getHostName());

			BufferedReader inputFromClient = null;
			PrintWriter outputToClient = null;

			try {

				inputFromClient = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));

				outputToClient = new PrintWriter(new OutputStreamWriter(
						clientSocket.getOutputStream()));

				while (threadRunning) {

					String clientMessage = inputFromClient.readLine();
					System.out.println("threadID " + threadID
							+ "  Client Says :" + clientMessage);

					if (!serverIsOn) {
						// The server was stopped. Quit this thread
						System.out.print("Ending thread " + threadID
								+ ", server was stopped.");
						outputToClient.println("Server was stopped");
						outputToClient.flush();
						threadRunning = false;

					}

					// if(clientMessage.equalsIgnoreCase("quit")) {
					//
					//
					// }
					if (clientMessage.equalsIgnoreCase("quit")) {
						threadRunning = false;
						System.out.print("sessionID " + threadID
								+ "  Stopping client thread for client : ");
						outputToClient.println("Quitting Process : " + clientMessage);
						outputToClient.flush();
					}
					outputToClient.println("Server Says : " + clientMessage);
					outputToClient.flush();

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				try {
					inputFromClient.close();
					outputToClient.close();
					clientSocket.close();
					System.out.println("Stopped client");
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

		}// end run()

	}// end inner class ClientThread

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new Question_03_Server();

	}

}