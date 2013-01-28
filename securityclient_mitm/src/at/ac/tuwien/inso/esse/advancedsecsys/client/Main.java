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
	public static final String REAL_ADDRESS = "10.0.2.2";
			
	public static final int MITM_PORT = 8081;
	//public static final String MITM_ADDRESS = "0.0.0.0";
	
	private static Main main;

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final FakeServer fakeServer = new FakeServer();
	private final NumbersToReplace numbersToReplace;
	
	private Main(NumbersToReplace numbersToReplace) {
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

	public static NumbersToReplace getNumbersToReplace() {
		return main.numbersToReplace;
	}

	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		main = new Main(new NumbersToReplace(new File("addressbook.txt")));
		main.fakeServer.start();
		System.out.println("FakeServer started, MITM Port " + MITM_PORT);
		main.executor.execute(main);
	}


}
