package sensorProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Stopwatch;

public class Worker implements Runnable{
	private static final String NOT_FOUND = "404";
	
	private final Socket clientSocket;
	private final AtomicBoolean isRunning;
	private final AtomicInteger activeConnections;
	private List<String> mjerenja;
	private Stopwatch stopwatch;

	/**
	 * Creates new Worker instance with given values:
	 * @param clientSocket socket for communication with client
	 * @param isRunning 
	 * @param activeConnections number of active connections on MutithreadedServer
	 * @param mjerenja list of generated measurments
	 * @param stopwatch measures time since start of the procces
	 */
	public Worker(Socket clientSocket, AtomicBoolean isRunning, AtomicInteger activeConnections, List<String> mjerenja, Stopwatch stopwatch) {
		this.clientSocket = clientSocket;
		this.isRunning = isRunning;
		this.activeConnections = activeConnections;
		this.mjerenja = mjerenja;
		this.stopwatch = stopwatch;
	}

	@Override
	public void run() {
		try (// create a new BufferedReader from an existing InputStream
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				// create a PrintWriter from an existing OutputStream
				PrintWriter outToClient = new PrintWriter(new OutputStreamWriter(
						clientSocket.getOutputStream()), true);) {

			String receivedString;

			// read a few lines of text
			while ((receivedString = inFromClient.readLine()) != null/*READ*/) {
				System.out.println("Server received: " + receivedString);

				//shutdown the server if requested
				if (receivedString.contains(Commands.SHUTDOWN.toString())) {
					outToClient.println("Initiating server shutdown!");//WRITE
					System.out.println("Server sent: Initiating server shutdown!");
					isRunning.set(false);
					activeConnections.getAndDecrement();
					break;
				}else if (receivedString.contains(Commands.REQ.toString())) {
					int redniBroj = (int) ((stopwatch.elapsed(TimeUnit.SECONDS) % 100) + 2);
					String stringToSend = mjerenja.get(redniBroj);
					outToClient.println(stringToSend);
					System.out.println("Server sent: " + stringToSend);
				}else {
					outToClient.println(NOT_FOUND);
					System.out.println("Server sent: " + NOT_FOUND);
				}
			}
			activeConnections.getAndDecrement();
		} catch (IOException ex) {
			System.err.println("Exception caught when trying to read or send data: " + ex);
		}
	}

}
