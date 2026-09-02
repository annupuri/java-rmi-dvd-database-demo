package dvddemo.remote;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import dvddemo.db.DBClient;
import dvddemo.db.DVD;

/**
 * The remote view of {@link DBClient}. Every operation is re-declared here to add
 * {@link RemoteException} to its {@code throws} clause; without that the dynamic-proxy RMI stub
 * rejects the call with "Method is not Remote".
 */
public interface DVDDatabaseRemote extends Remote, DBClient {

	@Override
	DVD getDVD(String upc) throws RemoteException, IOException, ClassNotFoundException;

	@Override
	boolean removeDVD(String upc) throws RemoteException, IOException;

	@Override
	boolean rent(String upc) throws RemoteException, IOException, ClassNotFoundException;

	@Override
	boolean returnRental(String upc) throws RemoteException, IOException, ClassNotFoundException;

	@Override
	boolean modifyDVD(DVD dvd) throws RemoteException, IOException;

	@Override
	List<DVD> getDVDs() throws RemoteException, IOException, ClassNotFoundException;

	@Override
	Collection<DVD> find(String query)
			throws RemoteException, IOException, ClassNotFoundException, PatternSyntaxException;
}
