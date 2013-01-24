package at.ac.tuwien.inso.esse.advancedsecsys.client.mitm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import at.ac.tuwien.inso.esse.advancedsecsys.client.Main;
import at.ac.tuwien.inso.esse.advancedsecsys.client.transfer.ServerBackend;

public class FakeServer {
	private static SecureRandom secureRandom;
	private SSLContext sslContext;
	private SSLServerSocket serverSocket;
	private final ExecutorService executor = Executors.newCachedThreadPool();

	public void close() throws IOException {
		serverSocket.close();
		executor.shutdownNow();
	}

	public void start() throws Exception {
		X509TrustManager trustManager = null;
		KeyStore localKeyStore = null;
		InputStream localInputStream = null;
		if (sslContext == null) {
			sslContext = SSLContext.getInstance("TLS");
		}
		trustManager = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate,
					String paramAnonymousString) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate,
					String paramAnonymousString) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		localKeyStore = KeyStore.getInstance("BKS", BouncyCastleProvider.PROVIDER_NAME);
		localInputStream = new FileInputStream("androidplatform.bks");
		localKeyStore.load(localInputStream, "123456".toCharArray());
		localInputStream.close();
		KeyManagerFactory localKeyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		localKeyManagerFactory.init(localKeyStore, "password".toCharArray());
		if (secureRandom == null)
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
		sslContext.init(localKeyManagerFactory.getKeyManagers(), new TrustManager[] { trustManager }, secureRandom);

		SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
		serverSocket = (SSLServerSocket) factory.createServerSocket(8080);

		executor.submit(new ClientRequestAcceptor());
	}

	private class ClientRequestAcceptor implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					SSLSocket socket = (SSLSocket) FakeServer.this.serverSocket.accept();
					System.out.println("Accepted!");
					FakeServer.this.executor.submit(new ClientRequestProcessor(socket));
				} catch (SocketException e) {
					// interrupted
					break;
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}

	}

	private class ClientRequestProcessor implements Runnable {
		private final SSLSocket socket;

		public ClientRequestProcessor(SSLSocket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				String query = null;
				if ((query = reader.readLine()) != null) {
					System.out.println("Received query from client: " + query);
					ServerBackend realServer = new ServerBackend();
					realServer.connect(Main.getRealServerAddress(), 8022); //Main.getRealServerPort() - replaced due to local port forwarding to real server
					realServer.forwardQuery(query, writer);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
