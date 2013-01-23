package at.ac.tuwien.inso.esse.advancedsecsys.client.transfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.os.AsyncTask;
import at.ac.tuwien.inso.esse.advancedsecsys.client.EsseClient;
import at.ac.tuwien.inso.esse.advancedsecsys.client.IServerBackend;
import at.ac.tuwien.inso.esse.advancedsecsys.client.dto.Entry;

public class ServerBackend implements IServerBackend {
	private static SecureRandom secureRandom;
	private static SSLContext sslContext;
	private Connection con;

	private void setConnection(Connection con) {
		this.con = con;
	}

	public void close() throws IOException {
		new Close(con).execute();
	}
	
	@Override
	public void connect(String paramString, int paramInt, PostExecute<Connection> postExecute) {
		HostInfo hostInfo = new HostInfo(paramString, paramInt);
		new Connect(this, postExecute).execute(hostInfo);
	}
	
	
	@Override
	public void queryForName(String name, String authKey, PostExecute<List<Entry>> postExecute) {
		new QueryForName(this, postExecute).execute(name, authKey);
	}


	private static class QueryForName extends AsyncTask<String, Integer, List<Entry>> {
		private final ServerBackend server;
		private final PostExecute<List<Entry>> postExecute;

		public QueryForName(ServerBackend server, PostExecute<List<Entry>> postExecute) {
			this.server = server;
			this.postExecute = postExecute;
		}

		@Override
		protected List<Entry> doInBackground(String... params) {
			String name = params[0];
			String authKey = params[1];
			server.con.getpWriter().println(">query:" + Utility.b64e(name) + " " + Utility.b64e(authKey));
			server.con.getpWriter().flush();
			try {
				return ServerResponseReader.interpretQuery(server.con.getReader());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Entry> result) {
			postExecute.doOnPostExecute(server, result);
		}
	}

	private static class Connect extends AsyncTask<HostInfo, Integer, Connection> {
		private final ServerBackend server;
		private final PostExecute<Connection> postExecute;

		public Connect(ServerBackend server, PostExecute<Connection> postExecute) {
			this.server = server;
			this.postExecute = postExecute;
		}

		@Override
		protected Connection doInBackground(HostInfo... params) {
			if (params == null || params.length == 0) {
				throw new RuntimeException("No Host Info available");
			}
			HostInfo hostInfo = params[0];
			X509TrustManager local1 = null;
			KeyStore localKeyStore = null;
			InputStream localInputStream = null;
			try {
				if (sslContext == null) {
					sslContext = SSLContext.getInstance("TLS");
				}
				try {
					local1 = new X509TrustManager() {
						public void checkClientTrusted(
								X509Certificate[] paramArrayOfX509Certificate,
								String paramString) throws CertificateException {
						}
	
						public void checkServerTrusted(
								X509Certificate[] paramArrayOfX509Certificate,
								String paramString) throws CertificateException {
							if (paramArrayOfX509Certificate[0]
									.getSubjectX500Principal()
									.getName()
									.equals("CN=Unknown,OU=ESSE,O=TUWIEN,L=Vienna,ST=Austria,C=AT"))
								return;
							throw new RuntimeException("server cert is invalid");
						}
	
						public X509Certificate[] getAcceptedIssuers() {
							return null;
						}
					};
					localInputStream = EsseClient.getAppContext().getResources()
							.openRawResource(2130968577);
					localKeyStore = KeyStore.getInstance("BKS");
					localKeyStore.load(localInputStream, "123456".toCharArray());
					KeyManagerFactory localKeyManagerFactory = KeyManagerFactory
							.getInstance("X509");
					localKeyManagerFactory
							.init(localKeyStore, "password".toCharArray());
					if (secureRandom == null)
						secureRandom = SecureRandom.getInstance("SHA1PRNG");
					// localObject4 = localKeyManagerFactory.getKeyManagers();
					// localKeyStore = new TrustManager[1];
					// localObject3[0] = local1;
					// localInputStream.init(localObject4, localObject3, secureRandom);
					sslContext.init(localKeyManagerFactory.getKeyManagers(),
							new TrustManager[] { local1 }, secureRandom);
					SSLSocket socket = ((SSLSocket) sslContext.getSocketFactory().createSocket(
							hostInfo.getHost(), hostInfo.getPort()));
					PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(
							socket.getOutputStream()));
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					return new Connection(pWriter, reader, socket);
				} finally {
					if (localInputStream != null)
						localInputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Connection result) {
			server.setConnection(result);
			postExecute.doOnPostExecute(server, result);
		}
	}
	
	private static class Close extends AsyncTask<Void, Void, Void> {
		private final Connection con;

		public Close(Connection con) {
			this.con = con;
		}

		@Override
		protected Void doInBackground(Void... params) {
			con.getpWriter().close();
			try {
				con.getReader().close();
				con.getSocket().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private static class HostInfo {
		private final String host;
		private final int port;

		public HostInfo(String host, int port) {
			this.host = host;
			this.port = port;
		}
		
		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}
	}

}
