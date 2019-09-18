package hr.fer.tel.communicationTest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import sensorProject.ServerIf;

public class ServerTest implements ServerIf, Runnable{

	private static final int PORT = 10003; // server port
	private static final int NUMBER_OF_THREADS = 4;
	private static final int BACKLOG = 10;

	private final AtomicInteger activeConnections;
	private ServerSocket serverSocket;
	private final ExecutorService executor;
	private final AtomicBoolean runningFlag;

	public ServerTest() {
		activeConnections = new AtomicInteger(0);
		executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
		runningFlag = new AtomicBoolean(false);
	}

	// Starts all required server services.
	@Override
	public void startup() {
		// create a server socket, bind it to the specified port on the local host
		// and set the max backlog for client requests
		try {
			this.serverSocket = new ServerSocket(PORT, BACKLOG);/*SOCKET->BIND->LISTEN*/;

			// set socket timeout to avoid blocking when there are no new incoming connection requests
			serverSocket.setSoTimeout(500);
			runningFlag.set(true);
			System.out.println("Server is ready!");
			System.out.println("Server sluša na portu: " + serverSocket.getLocalPort());
		
		} catch (SocketException e1) {
			System.err.println("Exception caught when setting server socket timeout: " + e1);
		} catch (IOException ex) {
			System.err.println("Exception caught when opening or setting the server socket: " + ex);
		}
	}

	//Main loop which accepts all client requests 
	@Override
	public void loop() {
		
		while (runningFlag.get()) {
			try {
				// listen for a connection to be made to server socket from a client
				// accept connection and create a new active socket which communicates with the client
				Socket clientSocket = serverSocket.accept();/*ACCEPT*/

				// execute a new request handler in a new thread
				Runnable worker = new Worker(clientSocket, runningFlag, activeConnections);
				executor.execute(worker);
				//increment the number of active connections 
				activeConnections.getAndIncrement();
			} catch (SocketTimeoutException ste) {
				// do nothing, check the runningFlag flag
			} catch (IOException e) {
				System.err.println("Exception caught when waiting for a connection: " + e);
			} 
		}
	}

	
	public void shutdown() {
		while (activeConnections.get() > 0) {
			System.out.println("WARNING: There are still active connections"); //Need to wait!
			try {
				Thread.sleep(5000);
			} catch (java.lang.InterruptedException e) {
				// Do nothing, check again whether there are still active connections to the server.
			}
		}
		if (activeConnections.get() == 0) {
			System.out.println("Starting server shutdown.");
			try {
				serverSocket.close(); /*CLOSE the main server socket*/ 
			} catch (IOException e) {
				System.err.println("Exception caught when closing the server socket: " + e);
			} finally {
				executor.shutdown();
			} 

			System.out.println("Server has been shutdown.");
		}
	}

	@Override
	public boolean getRunningFlag() {
		return runningFlag.get();
	}

	public void run() {
		//start all required services
		this.startup();
		//run the main loop for accepting client requests
		this.loop();
		//initiate shutdown when startup is finished
		((ExecutorService) this).shutdown();

	}
}
