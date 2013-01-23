package at.ac.tuwien.inso.esse.advancedsecsys.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.ac.tuwien.inso.esse.advancedsecsys.client.dto.Entry;
import at.ac.tuwien.inso.esse.advancedsecsys.client.transfer.Connection;
import at.ac.tuwien.inso.esse.advancedsecsys.client.transfer.PostExecute;
import at.ac.tuwien.inso.esse.advancedsecsys.client.transfer.ServerBackend;

public class ShowEntriesActivity extends ListActivity {
	@SuppressWarnings("unchecked")
	private EntryAdapter adapter = new EntryAdapter(EsseClient.getAppContext(),
			2130903042, (List<Entry>) Collections.EMPTY_LIST);
	private String authKey;
	private IServerBackend backend;
	private List<Entry> entries;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(2130903040);
		authKey = getIntent().getStringExtra(Main.EXTRA_AUTH_KEY);
		List<String> entriesAsStrings = getIntent().getStringArrayListExtra(
				Main.EXTRA_ENTRIES);
		entries = new ArrayList<Entry>();
		for (String s : entriesAsStrings) {
			entries.add(Entry.fromString(s));
		}
		this.adapter = new EntryAdapter(EsseClient.getAppContext(), 2130903042,
				this.entries);
		setListAdapter(this.adapter);
		backend = new ServerBackend();
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
	}

	public void search(View paramView) {
		String search = ((EditText) findViewById(2131099648)).getText()
				.toString(); // .replaceAll("[\\W&&[^\\\\]]+", "");
		adapter.clear();
		backend.connect(Main.HOST, 8080, new EntriesPostExecute(this, search));

	}
	
	private void queryForEntries(IServerBackend server, String search) {
		server.queryForName(search, authKey, new ShowEntriesPostExecute(this));
	}

	private static class ShowEntriesPostExecute implements PostExecute<List<Entry>> {
		private final ShowEntriesActivity activity;

		public ShowEntriesPostExecute(ShowEntriesActivity activity) {
			this.activity = activity;
		}

		@Override
		public void doOnPostExecute(IServerBackend server, List<Entry> entries) {
			activity.showEntries(server, entries);
		}
	}
	
	private void showEntries(IServerBackend server, List<Entry> entries) {
		try {
			server.close();
			Iterator<Entry> localIterator = entries.iterator();
			while (localIterator.hasNext()) {
				Entry entry = localIterator.next();
				adapter.add(entry);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			adapter.notifyDataSetChanged();
		}
	}
	
	private static class EntriesPostExecute implements PostExecute<Connection> {
		private final ShowEntriesActivity activity;
		private final String search;

		public EntriesPostExecute(ShowEntriesActivity activity, String search) {
			this.activity = activity;
			this.search = search;
		}

		@Override
		public void doOnPostExecute(IServerBackend server, Connection connection) {
			activity.queryForEntries(server, search);
		}
	}

	private class EntryAdapter extends ArrayAdapter<Entry> {
		private List<Entry> entries;

		public EntryAdapter(Context context, int textViewResourceId,
				List<Entry> localList) {
			super(context, textViewResourceId, localList);
			this.entries = localList;
		}

		public void add(Entry entry) {
			super.add(entry);
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			View localView = paramView;
			if (localView == null)
				localView = ((LayoutInflater) ShowEntriesActivity.this
						.getSystemService("layout_inflater")).inflate(
						2130903042, null);
			Entry localEntry = this.entries.get(paramInt);
			if (localEntry != null) {
				TextView localTextView1 = (TextView) localView
						.findViewById(2131099654);
				TextView localTextView2 = (TextView) localView
						.findViewById(2131099655);
				localTextView1.setText(localEntry.getName());
				localTextView2.setText(localEntry.getTelnr());
			}
			return localView;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}
	}
}
