package sensorProject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.base.Stopwatch;

import sensorProject.Main;

/**
*
* @author Tin Barić
*/
public class MultithreadedServer implements ServerIf, Runnable {

	//private static final int PORT = 10002; // server port
	private static final int NUMBER_OF_THREADS = 4;
	private static final int BACKLOG = 10;
	
	private String username;
	private int port;

	private final AtomicInteger activeConnections;
	private ServerSocket serverSocket;
	private final ExecutorService executor;
	private final AtomicBoolean runningFlag;
	private List<String> mjerenja;

	public MultithreadedServer(String username, int port, List<String> mjerenja) {
		activeConnections = new AtomicInteger(0);
		executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
		runningFlag = new AtomicBoolean(false);
		this.username = username;
		this.port = port;
		this.mjerenja = mjerenja;
	}
	
	public MultithreadedServer() {
		activeConnections = new AtomicInteger(0);
		executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
		runningFlag = new AtomicBoolean(false);
	}

	// Starts all required server services.
	public void startup() {
		System.out.println(this.username + ", " + this.port);
		// create a server socket, bind it to the specified port on the local host
		// and set the max backlog for client requests
		try {
			
			this.serverSocket = new ServerSocket(this.port, BACKLOG);/*SOCKET->BIND->LISTEN*/;
			
			// set socket timeout to avoid blocking when there are no new incoming connection requests
			serverSocket.setSoTimeout(500);
			runningFlag.set(true);
			//System.out.println("Server is ready!");
		
		} catch (SocketException e1) {
			System.err.println("Exception caught when setting server socket timeout: " + e1);
		} catch (IOException ex) {
			System.err.println("Exception caught when opening or setting the server socket: " + ex);
		} 
	}

	//Main loop which accepts all client requests 
	public void loop() {
		
		while (runningFlag.get()) {
			try {

				Stopwatch stopwatch = Stopwatch.createStarted();
				// listen for a connection to be made to server socket from a client
				// accept connection and create a new active socket which communicates with the client
				Socket clientSocket = serverSocket.accept();/*ACCEPT*/

				//increment the number of active connections 
				activeConnections.getAndIncrement();
				try (// create a new BufferedReader from an existing InputStream
						DataInputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
						// create a PrintWriter from an existing OutputStream
						DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());) {
					
					String receivedString;

					// read a few lines of text
					while ((receivedString = inFromClient.readLine()) != null/*READ*/) {
						//System.out.println("Server received: " + receivedString);

						//shutdown the server if requested
						if (receivedString.contains(Commands.SHUTDOWN.toString())) {
							outToClient.writeUTF("Connection closing...");//WRITE
							outToClient.flush();
							//System.out.println("Server sent: Initiating server shutdown!");
							inFromClient.close();
							outToClient.close();
							clientSocket.close();
							activeConnections.getAndDecrement();
							return;
						}else if (receivedString.contains(Commands.REQ.toString())) {

							int redniBroj = (int) ((stopwatch.elapsed(TimeUnit.SECONDS) % 100) + 2);
							String stringToSend = mjerenja.get(redniBroj);

							// send a String then terminate the line and flush
							outToClient.writeUTF(stringToSend);//WRITE
							outToClient.flush();
							System.out.println("Server sent: " + stringToSend);
						}else {
							outToClient.writeUTF("404");
							outToClient.flush();
						}
					}
					activeConnections.getAndDecrement();
				} catch (IOException ex) {
					System.err.println("Exception caught when trying to read or send data: " + ex);
				}
			} catch (SocketTimeoutException ste) {
				// do nothing, check the runningFlag flag
			} catch (IOException e) {
				System.err.println("Exception caught when waiting for a connection: " + e);
			} 
		}
	}

//	public void shutdown() {
//		while (activeConnections.get() > 0) {
//			System.out.println("WARNING: There are still active connections"); //Need to wait!
//			try {
//				Thread.sleep(5000);
//			} catch (java.lang.InterruptedException e) {
//				// Do nothing, check again whether there are still active connections to the server.
//			}
//		}
//		if (activeConnections.get() == 0) {
//			System.out.println("Starting server shutdown.");
//			try {
//				serverSocket.close(); /*CLOSE the main server socket*/ 
//			} catch (IOException e) {
//				System.err.println("Exception caught when closing the server socket: " + e);
//			} finally {
//				executor.shutdown();
//			} 
//
//			System.out.println("Server has been shutdown.");
//		}
//	}

	public boolean getRunningFlag() {
		return runningFlag.get();
	}

	public void run() {
		//ServerIf server = new MultithreadedServer();
		//start all required services
		this.startup();
		//run the main loop for accepting client requests
		this.loop();
		//initiate shutdown when startup is finished
//		server.shutdown();
		
	}

//	public static void main(String[] args) {
//		ServerIf server = new MultithreadedServer();
//		//start all required services
//		server.startup();
//		//run the main loop for accepting client requests
//		server.loop();
//		//initiate shutdown when startup is finished
//		server.shutdown();
//
//	}

}
