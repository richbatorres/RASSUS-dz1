package sensorProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable{
	private final Socket clientSocket;
	private final AtomicBoolean isRunning;
	private final AtomicInteger activeConnections;

	public Worker(Socket clientSocket, AtomicBoolean isRunning, AtomicInteger activeConnections) {
		this.clientSocket = clientSocket;
		this.isRunning = isRunning;
		this.activeConnections = activeConnections;
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
				if (receivedString.contains("shutdown")) {
					outToClient.println("Initiating server shutdown!");//WRITE
					System.out.println("Server sent: Initiating server shutdown!");
					isRunning.set(false);
					activeConnections.getAndDecrement();
					return;
				}

				String stringToSend = receivedString.toUpperCase();

				// send a String then terminate the line and flush
				outToClient.println(stringToSend);//WRITE
				System.out.println("Server sent: " + stringToSend);
			}
			activeConnections.getAndDecrement();
		} catch (IOException ex) {
			System.err.println("Exception caught when trying to read or send data: " + ex);
		}
	}

}
