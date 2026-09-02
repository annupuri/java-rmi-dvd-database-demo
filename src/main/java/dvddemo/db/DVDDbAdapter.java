package dvddemo.db;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.logging.Logger;

/**
 * This class acts as a thread safe wrapper for the <code>DVDDatabase</code> command. Before each operation that
 * modifies database state, this class locks the record in question.
 *
 * @see DVDDatabase
 */
public class DVDDbAdapter implements DBClient {

	/**
	 * Internal reference to the <code>DVDDatabase</code> instance.
	 */
	private DVDDatabase db;

	/** Logger for this class. */
	private static final Logger log = Logger.getLogger(DVDDbAdapter.class.getName());

	/**
	 * Creates an adapter over a {@link DVDDatabase} backed by the default directory.
	 */
	public DVDDbAdapter() {
		db = new DVDDatabase();
	}

	/**
	 * Creates an adapter over a {@link DVDDatabase} backed by the given directory.
	 *
	 * @param dbPath the path to the directory that holds the DVD records
	 */
	public DVDDbAdapter(String dbPath) {
		db = new DVDDatabase(dbPath);
	}

	/**
	 * Locates a DVD using the upc identification number.
	 *
	 * @param upc The upc of the DVD to locate.
	 * @return The DVD object which matches the upc.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Thrown if the DVD class definition cannot be found.
	 * @throws InterruptedIOException Indicates that a threading error has occurred.
	 */
	public DVD getDVD(String upc) throws IOException, ClassNotFoundException, InterruptedIOException {
		try {
			db.reserveDVD(upc);
			try {
				return db.getDVD(upc);
			} finally {
				db.releaseDVD(upc);
			}
		} catch (InterruptedException ie) {
			log.throwing("DVDDbAdapter", "getDVD(" + upc + ")", ie);
			throw new InterruptedIOException("Threading Error");
		}
	}

	/**
	 * Removes a DVD from inventory by its unique UPC. The record is locked for the delete.
	 *
	 * @param upc The UPC of the DVD to remove.
	 * @return {@code true} if a record existed for that UPC and was removed, {@code false} otherwise.
	 * @throws IOException if there is a problem accessing the data.
	 * @throws InterruptedIOException if the thread is interrupted while waiting for the record lock.
	 */
	public boolean removeDVD(String upc) throws IOException {
		try {
			db.reserveDVD(upc);
			try {
				return db.removeDVD(upc);
			} finally {
				db.releaseDVD(upc);
			}
		} catch (InterruptedException ie) {
			log.throwing("DVDDbAdapter", "removeDVD(" + upc + ")", ie);
			throw new InterruptedIOException("Threading Error");
		}
	}

	/**
	 * Rents a DVD.
	 *
	 * @param upc the id of the DVD in question
	 * @return A true value if the DVD was found and rented.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Thrown if the DVD class definition \ cannot be found.
	 * @throws InterruptedIOException Indicates that a threading error has occurred.
	 */
	public boolean rent(String upc) throws IOException, ClassNotFoundException, InterruptedIOException {
		// DVDDatabase.rent() locks the record for the duration of the read-modify-write.
		try {
			return db.rent(upc);
		} catch (InterruptedException ie) {
			log.throwing("DVDDbAdapter", "rent(" + upc + ")", ie);
			throw new InterruptedIOException("Threading Error");
		}
	}

	/**
	 * Returns a rented DVD.
	 *
	 * @param upc The id of the DVD in question
	 * @return A true value if the DVD was returned.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Thrown if the DVD class definition cannot be found.
	 * @throws InterruptedIOException Indicates that a threading error has occurred.
	 */
	public boolean returnRental(String upc) throws IOException, ClassNotFoundException, InterruptedIOException {
		// DVDDatabase.returnRental() locks the record for the duration of the read-modify-write.
		try {
			return db.returnRental(upc);
		} catch (InterruptedException ie) {
			log.throwing("DVDDbAdapter", "returnRental(" + upc + ")", ie);
			throw new InterruptedIOException("Threading Error");
		}
	}

	/**
	 * Changes existing information of a DVD item. Modifications can occur on any of the attributes of DVD except upc.
	 * The upc is used to identify the DVD to be modified.
	 *
	 * @param dvd A <code>DVD</code> instance that identifies the item to modify.
	 * @return A true value if the DVD was found and modified.
	 * @throws IOException Indicates there is a problem accessing the data.
	 */
	public boolean modifyDVD(DVD dvd) throws IOException {
		String upc = dvd.getUPC();
		try {
			db.reserveDVD(upc);
			try {
				return db.modifyDVD(dvd);
			} finally {
				db.releaseDVD(upc);
			}
		} catch (InterruptedException ie) {
			log.throwing("DVDDbAdapter", "modifyDVD(" + upc + ")", ie);
			throw new InterruptedIOException("Threading Error");
		}
	}

	/**
	 * Retrieves all dvd records in the database.
	 *
	 * @return A collection of all <code>DVD</code> instances in the current database.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Thrown if the DVD class definition cannot be found.
	 */
	public List<DVD> getDVDs() throws IOException, ClassNotFoundException {
		return db.getDVDs();
	}

	/**
	 * A properly formatted <code>String</code> expressions returns all matching DVD items. The <code>String</code> must
	 * be formatted as a regular expression.
	 *
	 * @param query The formatted regular expression used as the search criteria.
	 * @return The list of DVDs that match the query. Can be an empty Collection.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 * @throws PatternSyntaxException Indicates there is a syntax problem in the regular expression.
	 */
	public Collection<DVD> find(String query) throws IOException, ClassNotFoundException, PatternSyntaxException {
		return db.find(query);
	}
}
