package sensorProject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Stopwatch;

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
				
				// execute a new request handler in a new thread
				Runnable worker = new Worker(clientSocket, runningFlag, activeConnections, mjerenja, stopwatch);
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

}
