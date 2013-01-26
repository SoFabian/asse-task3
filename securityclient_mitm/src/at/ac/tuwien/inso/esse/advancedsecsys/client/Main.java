package at.ac.tuwien.inso.esse.advancedsecsys.client;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import at.ac.tuwien.inso.esse.advancedsecsys.client.mitm.FakeServer;
import at.ac.tuwien.inso.esse.advancedsecsys.client.mitm.NumbersToReplace;

public class Main implements Runnable {
//	private static final String REAL_SERVER_ADDRESS_IN_LAB = "10.0.2.2";
//	private static final String REAL_SERVER_PORT_IN_LAB = "8080";
	
	public static final int REAL_PORT = 8080;
	public static final int MITM_PORT = 8081;
	
	private static Main main;

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final FakeServer fakeServer = new FakeServer();
	private final String realServerAddress;
	private final int realServerPort;
	private final NumbersToReplace numbersToReplace;
	
	private Main(String realServerAddress, int realServerPort,
			NumbersToReplace numbersToReplace) {
		this.realServerAddress = realServerAddress;
		this.realServerPort = realServerPort;
		this.numbersToReplace = numbersToReplace;
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("console");
		final Scanner scanner = new Scanner(System.in);
		System.out.println("Press Enter to Exit");
		while (true) {
			try {
				scanner.nextLine();
			} catch (NoSuchElementException e) { //Strg + C
			}
			break;
		}
		System.out.println("exit");
		scanner.close();
		try {
			fakeServer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		executor.shutdownNow();
	}

	public static String getRealServerAddress() {
		return main.realServerAddress;
	}

	public static int getRealServerPort() {
		return main.realServerPort;
	}

	public static NumbersToReplace getNumbersToReplace() {
		return main.numbersToReplace;
	}

	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		main = new Main("127.0.0.1", MITM_PORT, new NumbersToReplace(new File("addressbook.txt")));
		main.fakeServer.start();
		System.out.println("FakeServer started");
		main.executor.execute(main);
	}


}
