package hr.fer.tel.communicationTest;


public class Main {

	public static void main(String[] args) {
		ServerTest server = new ServerTest();
		Thread t1 = new Thread(server, "t1");
		t1.start();
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ClientTest client = new ClientTest();
		Thread t2 = new Thread(client, "t2");
		t2.start();
	}

}
