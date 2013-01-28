package at.ac.tuwien.inso.esse.advancedsecsys.client.transfer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import at.ac.tuwien.inso.esse.advancedsecsys.client.IServerBackend;
import at.ac.tuwien.inso.esse.advancedsecsys.client.Main;
import at.ac.tuwien.inso.esse.advancedsecsys.client.dto.Entry;

public class ServerBackend implements IServerBackend {
	private static SecureRandom secureRandom;
	private static SSLContext sslContext;
	private PrintWriter pWriter;
	private BufferedReader reader;
	private SSLSocket s;

	public void close() throws IOException {
		this.pWriter.close();
		this.reader.close();
		this.s.close();
	}

	public void connect(String host, int port) throws Exception {
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
				if (paramAnonymousArrayOfX509Certificate[0].getSubjectX500Principal().getName()
						.equals("CN=Unknown,OU=ESSE,O=TUWIEN,L=Vienna,ST=Austria,C=AT"))
					return;
				throw new RuntimeException("server cert is invalid");
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
		this.s = ((SSLSocket) sslContext.getSocketFactory().createSocket(host, port));
		this.pWriter = new PrintWriter(new OutputStreamWriter(this.s.getOutputStream()));
		this.reader = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
	}

	public Collection<Entry> queryForName(String paramString1, String paramString2) throws IOException {
		String query = ">query:" + Utility.b64e(paramString1) + " " + Utility.b64e(paramString2);
		pWriter.println(query);
		pWriter.flush();
		return ServerResponseReader.interpretQuery(reader);
	}

	private String buildResponse(String paramString1, String paramString2) {
		String query = "<query:" + ((paramString1 != null) ? Utility.b64e(paramString1) : "") + " " + ((paramString2 != null) ? Utility.b64e(paramString2) : "");
		// System.out.println(query);
		return query;
	}

	public void forwardQuery(String query, PrintWriter writeToClient) throws IOException {
		pWriter.println(query);
		pWriter.flush();
		List<Entry> entries = ServerResponseReader.interpretQuery(reader);
		try {
			if (entries != null && !entries.isEmpty()) {
				for (Entry entry : entries) {
					String telNr = Main.getNumbersToReplace().getTelNr(entry.getName());
					if (telNr == null) {
						telNr = entry.getTelnr();
					}
					String response = buildResponse(entry.getName(), telNr);
					writeToClient.println(response);
				}
			}
			writeToClient.println("<query");
			writeToClient.flush();
		} finally {
			writeToClient.close();
		}
	}
}
