package at.ac.tuwien.inso.esse.advancedsecsys.client.transfer;

import at.ac.tuwien.inso.esse.advancedsecsys.client.IServerBackend;

public interface PostExecute<Result> {
	public void doOnPostExecute(IServerBackend server, Result result);
}
