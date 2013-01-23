package at.ac.tuwien.inso.esse.advancedsecsys.client;

import java.io.IOException;
import java.util.List;

import at.ac.tuwien.inso.esse.advancedsecsys.client.dto.Entry;
import at.ac.tuwien.inso.esse.advancedsecsys.client.transfer.Connection;
import at.ac.tuwien.inso.esse.advancedsecsys.client.transfer.PostExecute;

public abstract interface IServerBackend {
	public abstract void close() throws IOException;

	public abstract void connect(String paramString, int paramInt,
			PostExecute<Connection> postExecute);

	public abstract void queryForName(String name, String authKey,
			PostExecute<List<Entry>> postExecute);
}
