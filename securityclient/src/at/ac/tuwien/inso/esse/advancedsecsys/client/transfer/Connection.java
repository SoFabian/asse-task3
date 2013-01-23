package at.ac.tuwien.inso.esse.advancedsecsys.client.transfer;

import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;

public class Connection {
	private final PrintWriter pWriter;
	private final BufferedReader reader;
	private final SSLSocket s;
	
	public Connection(PrintWriter pWriter, BufferedReader reader,
			SSLSocket s) {
		this.pWriter = pWriter;
		this.reader = reader;
		this.s = s;
	}

	public PrintWriter getpWriter() {
		return pWriter;
	}

	public BufferedReader getReader() {
		return reader;
	}
	
	public SSLSocket getSocket() {
		return s;
	}
}
