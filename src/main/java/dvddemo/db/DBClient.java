package dvddemo.db;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * An interface implemented by classes that provide access to the DVD data store, including DVDDatabase.
 */
public interface DBClient {
	/**
	 * Locates a DVD using the upc identification number.
	 *
	 * @param upc The UPC of the DVD to locate.
	 * @return The DVD object which matches the UPC.
	 * @throws IOException if there is a problem accessing the data.
	 * @throws ClassNotFoundException if the DVD class definition cannot be found.
	 */
	DVD getDVD(String upc) throws IOException, ClassNotFoundException;

	/**
	 * Removes DVDs from inventory using the unique upc.
	 *
	 * @param upc The upc or key of the DVD to be removed.
	 * @return Returns true if the upc was found and the DVD was removed.
	 * @throws IOException Indicates there is a problem accessing the data.
	 */
	boolean removeDVD(String upc) throws IOException;

	/**
	 * Rents a DVD.
	 *
	 * @param upc The id of the DVD to remove.
	 * @return Returns true if the DVD was found and rented.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 */
	boolean rent(String upc) throws IOException, ClassNotFoundException;

	/**
	 * returns a rented DVD.
	 *
	 * @param upc The id of the DVD in to rent.
	 * @return Returns true if the DVD was returned.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 */

	boolean returnRental(String upc) throws IOException, ClassNotFoundException;

	/**
	 * Changes existing information of a DV item. Modifications can occur on any of the attributes of DVD except upc.
	 * The UPC is used to identify the DVD to be modified.
	 *
	 * @param dvd The DVD to modify.
	 * @return Returns true if the DVD was found and modified.
	 * @throws IOException Indicates there is a problem accessing the data.
	 */
	boolean modifyDVD(DVD dvd) throws IOException;

	/**
	 * Gets the store's inventory. All of the DVDs in the system.
	 *
	 * @return A List containing all found DVD's.
	 * @throws IOException Indicates there is a problem accessing the data.
	 * @throws ClassNotFoundException Indicates the DVD class definition cannot be found.
	 */
	List<DVD> getDVDs() throws IOException, ClassNotFoundException;

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
	Collection<DVD> find(String query) throws IOException, ClassNotFoundException, PatternSyntaxException;
}
