package at.ac.tuwien.inso.esse.advancedsecsys.client;

import at.ac.tuwien.inso.esse.advancedsecsys.client.dto.Entry;
import java.io.IOException;
import java.util.Collection;

public abstract interface IServerBackend {
	public abstract void close() throws IOException;

	public abstract void connect(String paramString, int paramInt)
			throws Exception;

	public abstract Collection<Entry> queryForName(String paramString1,
			String paramString2) throws Exception;
}
