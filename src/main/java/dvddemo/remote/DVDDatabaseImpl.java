package dvddemo.remote;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Collection;
import dvddemo.db.*;
import java.io.*;
import java.util.regex.*;

/**
 * A DVDDatabaseImpl object is the implementation of the DVDDatabaseRemote interface. This class is an RMI
 * implementation.
 *
 * A DVDDatabaseImpl object contains a reference to the db. This class acts primarily as a wrapper for the database.
 *
 * @see dvddemo.remote.DVDDatabaseRemote
 */
public class DVDDatabaseImpl extends UnicastRemoteObject implements DVDDatabaseRemote {

	/** Serialization identity; {@code UnicastRemoteObject} is {@code Serializable}. */
	private static final long serialVersionUID = 1L;

	/**
	 * The database handle. Not serialized: this object is exported and only its stub travels.
	 */
	private final transient DBClient db = new DVDDbAdapter();

	/**
	 * DVDDatabaseImpl default constructor
	 *
	 * @throws RemoteException Thrown if a <code>DVDDatabaseImpl</code> instance cannot be created.
	 */
	public DVDDatabaseImpl() throws RemoteException {
	}

	/**
	 * Returns the dvddemo.db.DVD object matching the UPC.
	 *
	 * @param upc The upc code of the DVD to retrieve.
	 * @return The matching DVD object.
	 * @throws RemoteException Thrown if an exception occurs in the <code>DVDDatabaseImpl</code> class.
	 * @throws IOException Thrown if an <code>IOException</code> is encountered in the <code>db</code> class. <br>
	 * For more information, see {@link DVDDatabase}.
	 * @throws ClassNotFoundException Thrown if an <code>ClassNotFoundException</code> is encountered in the
	 * <code>db</code> class. <br>
	 * For more information, see {@link DVDDatabase}.
	 */
	public DVD getDVD(String upc) throws RemoteException, IOException, ClassNotFoundException {
		return db.getDVD(upc);
	}

	/**
	 * Gets the store's inventory. All of the DVDs in the system.
	 *
	 * @return A collection of all found DVD's.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 */
	public List<DVD> getDVDs() throws IOException, ClassNotFoundException {

		return db.getDVDs();
	}

	/**
	 * A properly formatted <code>String</code> expressions returns all matching DVD items. The <code>String</code> must
	 * be formatted as a regular expression.
	 *
	 * @param query A regular expression search string.
	 * @return A <code>Collection</code> of <code>DVD</code> objects that match the search criteria.
	 * @throws IOException Thrown if an <code>IOException</code> is encountered in the <code>db</code> class.
	 * @throws ClassNotFoundException Thrown if an <code>ClassNotFoundException</code> is encountered in the
	 * <code>db</code> class.
	 * @throws PatternSyntaxException Thrown if an <code>PatternSyntaxException</code> is encountered in the
	 * <code>db</code> class.
	 */
	public Collection<DVD> find(String query) throws IOException, ClassNotFoundException, PatternSyntaxException {
		return db.find(query);
	}

	/**
	 * Modifies a DVD database entry specified by a DVD object.
	 *
	 * @param item The DVD to modify.
	 * @return A boolean indicating the success or failure of the modify operation.
	 * @throws RemoteException Thrown if an exception occurs in the <code>DVDDatabaseImpl</code> class.
	 * @throws IOException Thrown if an <code>IOException</code> is encountered in the <code>db</code> class. <br>
	 * For more information, see {@link DVDDatabase}.
	 */
	public boolean modifyDVD(DVD item) throws RemoteException, IOException {
		return db.modifyDVD(item);
	}

	/**
	 * Removes a DVD database entry specified by a UPC.
	 *
	 * @param upc The UPC number of the DVD to remove.
	 * @return A boolean indicating the success or failure of the removal operation.
	 * @throws RemoteException Thrown if an exception occurs in the <code>DVDDatabaseImpl</code> class.
	 * @throws IOException Thrown if an <code>IOException</code> is encountered in the <code>db</code> class. <br>
	 * For more information, see {@link DVDDatabase}.
	 */
	public boolean removeDVD(String upc) throws RemoteException, IOException {
		return db.removeDVD(upc);
	}

	/**
	 * Attempts to rent the DVD matching the provided UPC.
	 *
	 * @param upc The upc code of the DVD to rent.
	 * @return Returns a boolean indicating if the item was successfully rented.
	 * @throws RemoteException Thrown if an exception occurs in the <code>DVDDatabaseImpl</code> class.
	 * @throws IOException Thrown if an <code>IOException</code> is encountered in the <code>db</code> class. <br>
	 * For more information, see {@link DVDDatabase}.
	 * @throws InterruptedIOException Thrown if an <code>interrupted Threading exception</code> is encountered in the
	 * <code>db</code> class.
	 * @throws ClassNotFoundException Thrown if an <code>ClassNotFoundException</code> is encountered in the
	 * <code>db</code> class. <br>
	 * For more information, see {@link DVDDatabase}.
	 */
	public boolean rent(String upc)
			throws RemoteException, IOException, InterruptedIOException, ClassNotFoundException {
		return db.rent(upc);
	}

	/**
	 * Attempts to return a DVD matching the UPC.
	 *
	 * @param upc The upc code of the DVD to return.
	 * @return Returns a boolean indicating if the item was successfully returned.
	 * @throws RemoteException Thrown if an exception occurs in the <code>DVDDatabaseImpl</code> class.
	 * @throws IOException Thrown if an <code>IOException</code> is encountered in the <code>db</code> class. <br>
	 * For more information, see {@link DVDDatabase}.
	 * @throws ClassNotFoundException Thrown if an <code>ClassNotFoundException</code> is encountered in the
	 * <code>db</code> class. <br>
	 * For more information, see {@link DVDDatabase}.
	 */
	public boolean returnRental(String upc) throws RemoteException, IOException, ClassNotFoundException {
		return db.returnRental(upc);
	}

}
