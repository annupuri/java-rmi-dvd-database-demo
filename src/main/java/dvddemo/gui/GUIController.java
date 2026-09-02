package dvddemo.gui;

import java.io.InterruptedIOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import dvddemo.db.DBClient;
import dvddemo.db.DVD;
import dvddemo.remote.DVDConnector;

/**
 * Handles all interactions between the GUI layer and the data layer.
 *
 * @see DBClient
 * @see GUIControllerException
 */
public class GUIController {
	/**
	 * Indicates a local connection
	 */
	public final static int LOCAL_CONNECTION = 1;

	/**
	 * Indicates a network connection.
	 */
	public final static int NETWORK_CONNECTION = 2;

	/**
	 * Indicates no established connection.
	 */
	public final static int NO_CONNECTION = 0;

	/**
	 * Holds a reference to the client interface of the <code>DVDMediator</code>
	 */
	private DBClient connection;

	/**
	 * The default connection String.
	 */
	private String connectionString = "localhost";

	/**
	 * Holds the current connection type.
	 */
	private int connectionType = GUIController.NO_CONNECTION;

	/** Logger for this class. */
	private static final Logger log = Logger.getLogger(GUIController.class.getName());

	/**
	 * Creates a default <code>GUIController</code> instance.
	 */
	public GUIController() {
	}

	/**
	 * Creates a controller and opens the requested connection.
	 *
	 * @param connectionType {@link #LOCAL_CONNECTION} or {@link #NETWORK_CONNECTION}.
	 * @throws GUIControllerException if the connection cannot be opened.
	 */
	public GUIController(int connectionType) throws GUIControllerException {
		this.setConnectionType(connectionType);
	}

	/**
	 * Locates a DVD record by and UPC number.
	 *
	 * @param upc A String representing the UPC identifying the DVD record.
	 * @return A DVDTableModel containing all found DVD records.
	 * @throws GUIControllerException Indicates a database or network level exception.
	 */
	public DVDTableModel findDVD(String upc) throws GUIControllerException {
		DVDTableModel out = new DVDTableModel();
		try {
			Collection<DVD> matches = this.connection.find(upc);
			out.addDVDRecord(matches.iterator().next());
		}

		catch (PatternSyntaxException pse) {
			log.log(Level.WARNING, pse.getMessage(), pse);
			throw new GUIControllerException(pse);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new GUIControllerException(e);
		}
		return out;
	}

	/**
	 * Locates a DVD based on a user defined search query.
	 *
	 * @param searchString The user defined search String
	 * @return A DVDTableModel containing all DVD records that met the search criteria.
	 * @throws GUIControllerException Indicates a database or network level exception.
	 */
	public DVDTableModel find(String searchString) throws GUIControllerException {
		DVDTableModel out = new DVDTableModel();
		try {
			for (DVD dvd : this.connection.find(searchString)) {
				out.addDVDRecord(dvd);
			}
		} catch (PatternSyntaxException pse) {
			log.log(Level.WARNING, pse.getMessage(), pse);
			throw new GUIControllerException(pse);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new GUIControllerException(e);
		}
		return out;
	}

	/**
	 * Retrieves all DVD records from the database.
	 *
	 * @return A DVDTableModel containing all DVD Records.
	 * @throws GUIControllerException Indicates a database or network level exception.
	 */
	public DVDTableModel getDVDs() throws GUIControllerException {
		DVDTableModel out = new DVDTableModel();
		try {
			for (DVD dvd : this.connection.getDVDs()) {
				out.addDVDRecord(dvd);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new GUIControllerException(e);
		}
		return out;
	}

	/**
	 * Decrements the number of DVD's in stock identified by their UPC number.
	 *
	 * @param upc The UPC of the DVD to rent.
	 * @return A boolean indicating if the rent operation was successful.
	 * @throws GUIControllerException Indicates a database or network level exception.
	 */
	public boolean rent(String upc) throws GUIControllerException {
		boolean retval = false;
		try {
			retval = this.connection.rent(upc);
		} catch (InterruptedIOException ie) {
			log.log(Level.SEVERE, ie.getMessage(), ie);
			throw new GUIControllerException(ie);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new GUIControllerException(e);
		}
		return retval;
	}

	/**
	 * Increments the number of DVD's in stock identified by their UPC number.
	 *
	 * @param upc The UPC of the DVD to return.
	 * @return A boolean indicating if the return operation was successful.
	 * @throws GUIControllerException Indicates a database or network level exception.
	 */
	public boolean returnRental(String upc) throws GUIControllerException {
		boolean retval = false;
		try {
			retval = this.connection.returnRental(upc);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new GUIControllerException(e);
		}
		return retval;
	}

	/**
	 * Sets the connection type of this controller instance.
	 *
	 * @param connectionType An integer specifying the connection type.
	 * @throws GUIControllerException Indicates a database or network level exception.
	 */
	public final void setConnectionType(int connectionType) throws GUIControllerException {
		try {
			if (connectionType == GUIController.LOCAL_CONNECTION) {
				this.connection = DVDConnector.getLocal();
				this.connectionType = GUIController.LOCAL_CONNECTION;
			} else if (connectionType == GUIController.NETWORK_CONNECTION) {
				this.connectionString = System.getProperty("dvddemo.hostname");
				if (this.connectionString == null) {
					this.connectionString = "localhost";
				}
				log.info("Using hostname " + this.connectionString);
				this.connection = DVDConnector.getRemote(this.connectionString);
				this.connectionType = GUIController.NETWORK_CONNECTION;
			} else {
				throw new IllegalArgumentException("Invalid connection type specified");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new GUIControllerException(e);
		}
	}

	/**
	 * Returns the current connection type.
	 *
	 * @return An integer indicating the current connection type of this class.
	 */
	public int getConnectionType() {
		return this.connectionType;
	}
}
