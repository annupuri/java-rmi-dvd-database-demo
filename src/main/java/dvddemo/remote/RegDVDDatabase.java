package dvddemo.remote;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import dvddemo.db.*;
import java.util.logging.*;

/**
 * Server entry point: starts an in-process RMI registry and binds the DVD database service so remote
 * clients can look it up.
 */
public class RegDVDDatabase {

	/** Logger for this class. */
	private static final Logger log = Logger.getLogger(RegDVDDatabase.class.getName());

	private RegDVDDatabase() {
	}

	/**
	 * Starts an RMI registry on port 1099 and binds a {@link DVDDatabaseImpl} under the name
	 * {@code DVDMediator}. Failures are logged, not thrown.
	 */
	public static void register() {
		try {
			LocateRegistry.createRegistry(1099); // the default RMI port

			DVDDatabaseImpl aDVD = new DVDDatabaseImpl();
			Naming.rebind("DVDMediator", aDVD);

		} catch (Exception e) {
			log.log(Level.SEVERE, "failed to start the RMI registry or bind the DVD service", e);
		}
	}

	/**
	 * Starts the service by calling {@link #register()}.
	 *
	 * @param args Ignored.
	 */
	public static void main(String[] args) {
		register();
	}
}
