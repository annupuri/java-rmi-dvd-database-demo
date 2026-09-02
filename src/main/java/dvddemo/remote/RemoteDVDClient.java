package dvddemo.remote;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import dvddemo.db.DBClient;
import dvddemo.db.DVD;

/**
 * Client-side {@link DBClient} that forwards every call to a {@link DVDDatabaseRemote} RMI stub. It
 * lets the GUI treat a local database and a remote one through the same interface; the calls dispatch
 * remotely because they go through the {@code DVDDatabaseRemote} type. A {@link java.rmi.RemoteException}
 * from the stub propagates as the {@link IOException} the {@code DBClient} methods already declare.
 */
final class RemoteDVDClient implements DBClient {

	private final DVDDatabaseRemote remote;

	RemoteDVDClient(DVDDatabaseRemote remote) {
		this.remote = remote;
	}

	@Override
	public DVD getDVD(String upc) throws IOException, ClassNotFoundException {
		return remote.getDVD(upc);
	}

	@Override
	public boolean removeDVD(String upc) throws IOException {
		return remote.removeDVD(upc);
	}

	@Override
	public boolean rent(String upc) throws IOException, ClassNotFoundException {
		return remote.rent(upc);
	}

	@Override
	public boolean returnRental(String upc) throws IOException, ClassNotFoundException {
		return remote.returnRental(upc);
	}

	@Override
	public boolean modifyDVD(DVD dvd) throws IOException {
		return remote.modifyDVD(dvd);
	}

	@Override
	public List<DVD> getDVDs() throws IOException, ClassNotFoundException {
		return remote.getDVDs();
	}

	@Override
	public Collection<DVD> find(String query) throws IOException, ClassNotFoundException, PatternSyntaxException {
		return remote.find(query);
	}
}
