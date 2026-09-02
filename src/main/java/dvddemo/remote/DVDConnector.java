package dvddemo.remote;

import java.rmi.Naming;
import java.rmi.*;
import dvddemo.db.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A DVDConnector is used in cases where the GUI client wants to make a network connection. In this case, that
 * connection is an RMI connection. Also supplies a getLocal method if the client is only interested in a local
 * connection.
 *
 * @see dvddemo.db.DVDDatabase
 */
public class DVDConnector {

	/** Logger for this class. */
	private static final Logger log = Logger.getLogger(DVDConnector.class.getName());

	private DVDConnector() {
	}

	/**
	 * Static method that creates an RMI connection. The DBClient is a remote object.
	 *
	 * @param ip The ip of the host machine.
	 * @return A DBClient instance.
	 * @throws RemoteException Indicates that a remote instance of the DBClient interface cannot be created.
	 */
	public static DBClient getRemote(String ip) throws RemoteException {
		try {
			DVDDatabaseRemote remote = (DVDDatabaseRemote) Naming.lookup("rmi://" + ip + "/DVDMediator");
			return new RemoteDVDClient(remote);
		} catch (Exception e) {
			log.log(Level.WARNING, "RMI lookup failed for host " + ip, e);
			throw new RemoteException("RMI lookup failed for host " + ip, e);
		}
	}

	/**
	 * Static method that gets a database handle. The DBClient is a local object.
	 *
	 * @return A <code>DBClient</code> instance.
	 * @throws IOException Thrown if an <code>IOException</code> is encountered in the <code>DVDDatabase</code> class.
	 * <br>
	 * For more information, see {@link DVDDatabase}.
	 * @throws ClassNotFoundException Thrown if an <code>ClassNotFoundException</code> is encountered in the
	 * <code>DVDDatabase</code> class. <br>
	 * For more information, see {@link DVDDatabase}.
	 */
	public static DBClient getLocal() throws IOException, ClassNotFoundException {
		return new DVDDbAdapter();
	}
}
