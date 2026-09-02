package dvddemo.gui;

import java.util.*;
import javax.swing.table.*;
import dvddemo.db.*;

/**
 * The custom table model used by the <code>MainWindow</code> instance.
 *
 * @see dvddemo.gui.MainWindow
 */
public class DVDTableModel extends AbstractTableModel {

	/** Serialization identity; {@code AbstractTableModel} is {@code Serializable}. */
	private static final long serialVersionUID = 1L;

	/**
	 * An array of <code>String</code> objects representing the table headers.
	 */
	private String[] headerNames = { "UPC", "Movie Title", "Director", "Lead Actor", "Supporting Actor", "Composer",
			"Copies in Stock" };

	/**
	 * One {@code String[]} row per displayed DVD, column order matching {@link #headerNames}.
	 */
	private final ArrayList<String[]> dvdRecords = new ArrayList<>(5);

	/**
	 * Creates an empty table model. Rows are added with {@link #addDVDRecord(dvddemo.db.DVD)}.
	 */
	public DVDTableModel() {
	}

	/**
	 * Returns the column count of the table.
	 *
	 * @return An integer indicating the number or columns in the table.
	 */
	public int getColumnCount() {
		return this.headerNames.length;
	}

	/**
	 * Returns the number of rows in the table.
	 *
	 * @return An integer indicating the number of rows in the table.
	 */
	public int getRowCount() {
		return this.dvdRecords.size();
	}

	/**
	 * Gets a value from a specified index in the table.
	 *
	 * @param row An integer representing the row index.
	 * @param column An integer representing the column index.
	 * @return The object located at the specified row and column.
	 */
	public Object getValueAt(int row, int column) {
		return this.dvdRecords.get(row)[column];
	}

	/**
	 * Sets the cell value at a specified index.
	 *
	 * @param obj The object that is placed in the table cell.
	 * @param row The row index.
	 * @param column The column index.
	 */
	public void setValueAt(Object obj, int row, int column) {
		this.dvdRecords.get(row)[column] = (String) obj;
	}

	/**
	 * Returns the name of a column at a given column index.
	 *
	 * @param column The specified column index.
	 * @return A String containing the column name.
	 */
	public String getColumnName(int column) {
		return headerNames[column];
	}

	/**
	 * Given a row and column index, indicates if a table cell can be edited.
	 *
	 * @param row Specified row index.
	 * @param column Specified column index.
	 * @return A boolean indicating if a cell is editable.
	 */
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * Adds a row of DVD data to the table.
	 *
	 * @param upc The DVD upc.
	 * @param name The name of the movie.
	 * @param director The movie's director.
	 * @param leadActor The movie's lead actor.
	 * @param supportingActor The movie's supporting actor.
	 * @param composer The movie's composer.
	 * @param numberOfCopies The number of DVD copies in stock.
	 */
	public void addDVDRecord(String upc, String name, String director, String leadActor, String supportingActor,
			String composer, int numberOfCopies) {

		String[] temp = { upc, name, director, leadActor, supportingActor, composer, Integer.toString(numberOfCopies) };
		this.dvdRecords.add(temp);
	}

	/**
	 * Adds a DVD object to the table.
	 *
	 * @param dvd The DVD object to add to the table.
	 */
	public void addDVDRecord(DVD dvd) {

		String[] temp = { dvd.getUPC(), dvd.getName(), dvd.getDirector(), dvd.getLeadActor(), dvd.getSupportingActor(),
				dvd.getComposer(), Integer.toString(dvd.getCopy()) };
		this.dvdRecords.add(temp);
	}
}
