package dvddemo.db;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

/**
 * A DVDDatabase object contains all of the DVDs in the system and operations that act upon the DVDs. <br/>
 * This class uses serialization to read and write its state. There are also methods provided which allow users to rent
 * or return DVDs, add or remove DVDs from inventory, and search the DVD database.
 *
 */
public class DVDDatabase {

	/**
	 * The UPCs of the DVD records that are currently locked, shared across every instance. Every
	 * access is guarded by {@code synchronized (reservedDVDs)}, which also serves as the wait/notify
	 * monitor, so the collection needs no thread-safety of its own.
	 */
	private static final Set<String> reservedDVDs = new HashSet<>();

	/**
	 * The database directory used when no path is supplied, resolved against the working directory.
	 */
	private static final String DEFAULT_DB_NAME = "dvd_db";

	/**
	 * The file extension associated with a serialized DVD object.
	 */
	private static final String recordExtension = ".dvd";

	/**
	 * The directory that holds this database's records.
	 */
	private final Path dbDir;

	/** Logger for this class. */
	private static final Logger log = Logger.getLogger(DVDDatabase.class.getName());

	/**
	 * Creates a database backed by the default directory ({@code dvd_db}) relative to the working
	 * directory.
	 */
	public DVDDatabase() {
		this(DEFAULT_DB_NAME);
	}

	/**
	 * Creates a database backed by the given directory.
	 *
	 * @param dbPath the path to the directory that holds the DVD records
	 */
	public DVDDatabase(String dbPath) {
		this.dbDir = Path.of(dbPath);
	}

	/**
	 * Adds a dvd to the database or inventory.
	 *
	 * @param dvd The DVD item to add to inventory.
	 * @return Indicates the success/failure of the add operation.
	 * @throws IOException Indicates there is a problem accessing the data.
	 */
	public synchronized boolean addDVD(DVD dvd) throws IOException {
		return persistDVD(dvd);
	}

	/**
	 * Locates a DVD using the upc identification number.
	 *
	 * @param upc The UPC of the DVD to locate.
	 * @return The DVD object which matches the upc.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 */
	public synchronized DVD getDVD(String upc) throws IOException, ClassNotFoundException {
		return readRecord(recordPath(upc));
	}

	/**
	 * Removes DVDs from inventory using the unique upc.
	 *
	 * @param upc The upc or key of the DVD to be removed.
	 * @return Returns true if the upc was found and the DVD was removed.
	 * @throws IOException Indicates there is a problem accessing the data.
	 */
	public synchronized boolean removeDVD(String upc) throws IOException {
		return Files.deleteIfExists(recordPath(upc));
	}

	/**
	 * Rents a DVD.
	 *
	 * @param upc The id of the DVD to rent
	 * @return Returns true if the DVD was found and rented.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 * @throws InterruptedException Indicates the DB thread has been interrupted.
	 */
	public boolean rent(String upc) throws IOException, ClassNotFoundException, InterruptedException {
		// rent is a read-modify-write: hold this record's lock for the whole operation so a
		// concurrent rent/return of the same DVD cannot interleave. The record lock is always
		// acquired before the instance monitor to keep a single, deadlock-free lock order.
		reserveDVD(upc);
		try {
			synchronized (this) {
				DVD dvd = getDVD(upc);
				if (dvd == null) {
					return false;
				}
				dvd.setRented(true);
				return modifyDVD(dvd);
			}
		} finally {
			releaseDVD(upc);
		}
	}

	/**
	 * Returns a rented DVD.
	 *
	 * @param upc The id of the DVD to return.
	 * @return Returns true if the DVD was returned.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 * @throws InterruptedException Indicates the DB thread has been interrupted.
	 */
	public boolean returnRental(String upc) throws IOException, ClassNotFoundException, InterruptedException {
		// Mirrors rent(): a read-modify-write guarded by this record's lock, taken before the
		// instance monitor.
		reserveDVD(upc);
		try {
			synchronized (this) {
				DVD dvd = getDVD(upc);
				if (dvd == null) {
					return false;
				}
				dvd.setRented(false);
				return modifyDVD(dvd);
			}
		} finally {
			releaseDVD(upc);
		}
	}

	/**
	 * Changes existing information of a DV item. Modifications can occur on any of the attributes of DVD except UPC.
	 * The UPC is used to identify the DVD to be modified.
	 *
	 * @param dvd The item in question
	 * @return Returns true if the DVD was found and modified.
	 * @throws IOException Indicates there is a problem accessing the data.
	 */
	public synchronized boolean modifyDVD(DVD dvd) throws IOException {
		return persistDVD(dvd);
	}

	/**
	 * Gets the store's inventory. All the DVDs in the system.
	 *
	 * @return A collection of all found DVD's.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 */
	public synchronized List<DVD> getDVDs() throws IOException, ClassNotFoundException {
		List<DVD> retVal = new ArrayList<>();
		if (!Files.isDirectory(dbDir)) {
			return retVal;
		}
		// Only the serialized record files, so a stray file in the directory cannot break a listing.
		try (DirectoryStream<Path> records = Files.newDirectoryStream(dbDir, "*" + recordExtension)) {
			for (Path record : records) {
				retVal.add(readRecord(record));
			}
		}
		return retVal;
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
	public synchronized Collection<DVD> find(String query)
			throws IOException, ClassNotFoundException, PatternSyntaxException {
		Collection<DVD> retVal = new ArrayList<>();
		Pattern p = Pattern.compile(query);
		for (DVD dvd : getDVDs()) {
			if (p.matcher(dvd.toString()).find()) {
				retVal.add(dvd);
			}
		}
		return retVal;
	}

	/**
	 * Lock the requested DVD. This method blocks until the lock succeeds.
	 *
	 * @param upc The upc of the DVD to reserve
	 * @throws InterruptedException Indicates the thread is interrupted.
	 */
	public void reserveDVD(String upc) throws InterruptedException {
		log.fine(() -> "reserve called " + upc);
		synchronized (reservedDVDs) {
			//if the record is already locked, then wait
			//until it is released.
			while (reservedDVDs.contains(upc)) {
				log.fine(() -> "Waiting = " + Thread.currentThread().getName());
				reservedDVDs.wait();
			}
			log.fine(() -> "Got lock = " + Thread.currentThread().getName());
			reservedDVDs.add(upc);
			log.fine(() -> "Locked record count = " + reservedDVDs.size());
			reservedDVDs.notifyAll();
		}
	}

	/**
	 * Unlock the requested record. Ignored if the caller does not have
	 *
	 * a current lock on the requested record.
	 *
	 * @param upc The upc of the DVD to release
	 */
	public void releaseDVD(String upc) {
		synchronized (reservedDVDs) {
			reservedDVDs.remove(upc);
			reservedDVDs.notifyAll();
		}
	}

	/**
	 * Resolves the on-disk path of the record file for a given UPC.
	 *
	 * @param upc The UPC of the DVD.
	 * @return The path of that DVD's serialized record, whether or not it exists yet.
	 */
	private Path recordPath(String upc) {
		return dbDir.resolve(upc + recordExtension);
	}

	/**
	 * Serializes a DVD to its record file, creating or overwriting it. The DVD's UPC determines the
	 * file name.
	 *
	 * @param dvd The DVD to store.
	 * @return Always {@code true}; a failure surfaces as an {@link IOException}.
	 * @throws IOException Indicates there is a problem writing the data.
	 */
	private boolean persistDVD(DVD dvd) throws IOException {
		Files.createDirectories(dbDir);
		try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(recordPath(dvd.getUPC())))) {
			oos.writeObject(dvd);
		}
		return true;
	}

	/**
	 * Deserializes the DVD stored at the given record path.
	 *
	 * @param record The path of a serialized DVD record.
	 * @return The stored DVD.
	 * @throws IOException Indicates the record is missing or cannot be read.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 */
	private DVD readRecord(Path record) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(record))) {
			return (DVD) ois.readObject();
		}
	}
}
