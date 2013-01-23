package at.ac.tuwien.inso.esse.advancedsecsys.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import at.ac.tuwien.inso.esse.advancedsecsys.client.dto.Entry;
import at.ac.tuwien.inso.esse.advancedsecsys.client.transfer.Connection;
import at.ac.tuwien.inso.esse.advancedsecsys.client.transfer.PostExecute;
import at.ac.tuwien.inso.esse.advancedsecsys.client.transfer.ServerBackend;

public class Main extends Activity {
	public static final String HOST = "10.0.0.2"; // 10.0.2.2
	public static final String EXTRA_AUTH_KEY = "at.ac.tuwien.inso.esse.advancedsecsys.client.AUTH_KEY";
	public static final String EXTRA_ENTRIES = "at.ac.tuwien.inso.esse.advancedsecsys.client.ENTRIES";
	private static final int SHOW_ENTRIES_REQUEST = 1001;

	// b2c22c650
	public void login(View paramView) {
		ServerBackend localServerBackend = new ServerBackend();
		localServerBackend.connect(HOST, 8080, new QueryPostExecute(this));
	}
	
	public void showEntries(IServerBackend server, List<Entry> entries, String authKey) {
		try {
			server.close();
			if ((entries != null) && (entries.size() > 0)) {
				Intent intent = new Intent(this, ShowEntriesActivity.class);
				intent.putExtra(Main.EXTRA_AUTH_KEY, authKey);
				ArrayList<String> ens = new ArrayList<String>();
				for (Entry entry : entries) {
					ens.add(entry.toString());
				}
				intent.putStringArrayListExtra(Main.EXTRA_ENTRIES, ens);
				startActivityForResult(intent, SHOW_ENTRIES_REQUEST);
			} else {
				EditText localEditText = (EditText) findViewById(2131099650);
				localEditText.setText("");
				Toast.makeText(getApplicationContext(),
						"Wrong authentication key!", Toast.LENGTH_LONG).show();
			}
		} catch (IOException e) {
			showError(e);
			e.printStackTrace();
		}
	}


	public void queryForEntries(IServerBackend server) {
		EditText localEditText = (EditText) findViewById(2131099650);
		String authKey = localEditText.getText().toString()
				.replaceAll("[\\W&&[^\\\\]]+", "");
		server.queryForName("", authKey, new ShowEntriesPostExecute(this, authKey));
	}

	private static class ShowEntriesPostExecute implements PostExecute<List<Entry>> {
		private Main main;
		private String authKey;

		public ShowEntriesPostExecute(Main main, String authKey) {
			this.main = main;
			this.authKey = authKey;
		}

		@Override
		public void doOnPostExecute(IServerBackend server, List<Entry> entries) {
			main.showEntries(server, entries, authKey);
		}
	}

	private static class QueryPostExecute implements PostExecute<Connection> {
		private Main main;

		public QueryPostExecute(Main main) {
			this.main = main;
		}

		@Override
		public void doOnPostExecute(IServerBackend server, Connection con) {
			main.queryForEntries(server);
		}
	}

	public static void showError(Exception localException) {
		Toast.makeText(
				EsseClient.getAppContext(),
				"Exception has occured! Check debug log: "
						+ localException.getMessage(), Toast.LENGTH_LONG)
				.show();
	}


	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(2130903041);
	}
}
