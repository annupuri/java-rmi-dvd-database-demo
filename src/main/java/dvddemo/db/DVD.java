package dvddemo.db;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * A DVD object is a representation of DVD data. It is a plain serializable value object; identity is
 * the UPC (see {@link #equals(Object)}).
 *
 * @see dvddemo.db.DVDDatabase
 */
public class DVD implements Serializable {

	/** Serialization identity for the file-backed record store. */
	private static final long serialVersionUID = 1L;

	/**
	 * Stores the upc number. This number uniquely identifies a DVD.
	 */
	private String upc = ""; // Holds the record UPC identification

	/**
	 * Stores the title name of the movie.
	 */
	private String name = ""; // Holds the movie title

	/**
	 * Stores the name of the composer of the movie's soundtrack as a single <code>String</code> containing first and
	 * last name.
	 */
	private String composer = ""; // Holds the orchestral composer

	/**
	 * Stores the name of the movie's director as a single <code>String</code> containing first and last name.
	 */
	private String director = ""; // Holds the movie director's name

	/**
	 * Stores the name of the movie's main actor as a single <code>String</code> containing first and last name.
	 */
	private String leadActor = ""; // Holds the lead actor's name

	/**
	 * Stores the name of the movie's supporting actor as a single <code>String</code> containing first and last name.
	 */
	private String supportingActor = ""; // Holds the supporting actor

	/**
	 * Stores the release date of the film in month /day/ year format.
	 */
	private Date year = new Date(); // Holds the movie's release date

	/**
	 * An integer value that indicates the number of copies of this DVD that are currently in stock.
	 */
	private int copy = 1; // Holds the number of this DVD that are in stock

	/**
	 * Creates an instance of this object with default values.
	 */
	public DVD() {
	}

	/**
	 * Creates an instance of this object with a specified list of initial values.
	 *
	 * @param upc Holds the UPC value of the DVD.
	 * @param name Holds the title of the DVD.
	 * @param composer Holds the first and last name of the movie's composer.
	 * @param director Holds the first and last name of the movie's director.
	 * @param leadActor Holds the full name of the movie's leading actor.
	 * @param supportingActor Holds the first and last name of the movie's supporting actor.
	 * @param year Holds the month /day/ year the of the movie's release.
	 */
	public DVD(String upc, String name, String composer, String director, String leadActor, String supportingActor,
			Date year) {
		this(upc, name, composer, director, leadActor, supportingActor, year, 1);
	}

	/**
	 * Creates an instance of this object with a specified list of initial values.
	 *
	 * @param upc Holds the UPC value of the DVD.
	 * @param name Holds the title of the DVD.
	 * @param composer Holds the first and last name of the movie's composer.
	 * @param director Holds the first and last name of the movie's director.
	 * @param leadActor Holds the full name of the movie's leading actor.
	 * @param supportingActor Holds the first and last name of the movie's supporting actor.
	 * @param year Holds the month /day/ year the of the movie's release.
	 * @param copies Holds the number of copies in stock.
	 */
	public DVD(String upc, String name, String composer, String director, String leadActor, String supportingActor,
			Date year, int copies) {
		this.upc = upc;
		this.name = name;
		this.composer = composer;
		this.director = director;
		this.leadActor = leadActor;
		this.supportingActor = supportingActor;
		this.year = year;
		this.copy = copies;
	}

	/**
	 * Creates an instance of this object with a specified list of initial values.
	 *
	 * @param upc Holds the UPC value of the DVD.
	 * @param name Holds the title of the DVD.
	 * @param composer Holds the first and last name of the movie's composer.
	 * @param director Holds the first and last name of the movie's director.
	 * @param leadActor Holds the full name of the movie's leading actor.
	 * @param supportingActor Holds the first and last name of the movie's supporting actor.
	 * @param yearStr Holds the month /day/ year the of the movie's release in <code>String</code> format.
	 * @param copies Holds the number of copies in stock.
	 */
	public DVD(String upc, String name, String composer, String director, String leadActor, String supportingActor,
			String yearStr, int copies) {
		this.upc = upc;
		this.name = name;
		this.composer = composer;
		this.director = director;
		this.leadActor = leadActor;
		this.supportingActor = supportingActor;
		this.year = parseReleaseDate(yearStr);
		this.copy = copies;
	}

	/**
	 * Returns the first and last name of the movie's composer.
	 *
	 * @return A <code>String</code> containing the first and last name of the movie's composer.
	 */
	public String getComposer() {
		return this.composer;
	}

	/**
	 * Sets the first and last name of the movie's composer.
	 *
	 * @param composer A <code>String</code> containing the first and last name of the movie's composer.
	 */
	public void setComposer(String composer) {
		this.composer = composer;
	}

	/**
	 * Sets the number of available DVDs.
	 *
	 * @param copy The new amount of DVDs available.
	 */
	public void setCopy(int copy) {
		this.copy = copy;
	}

	/**
	 * Returns The number of DVDs currently available.
	 *
	 * @return An <code>int</code> count of the number of available copies of this DVD.
	 */
	public int getCopy() {
		return this.copy;
	}

	/**
	 * Returns the first and last name of the movie's director.
	 *
	 * @return A <code>String</code> containing the first and last name of the movie's director.
	 */
	public String getDirector() {
		return this.director;
	}

	/**
	 * Sets the first and last name of the movie's director.
	 *
	 * @param director A <code>String</code> containing the first and last name of the movie's director.
	 */
	public void setDirector(String director) {
		this.director = director;
	}

	/**
	 * Returns the DVD's U.P.C. identification code as a <code>String</code>.
	 *
	 * @return A <code>String</code> containing the DVD's UPC. identification code.
	 */
	public String getUPC() {
		return this.upc;
	}

	/**
	 * Sets the DVD's U.P.C. identification code.
	 *
	 * @param upc A <code>String</code> containing the DVD's upc identification code.
	 */
	public void setUPC(String upc) {
		this.upc = upc;
	}

	/**
	 * Sets the first and last name of the movie's lead actor.
	 *
	 * @param leadActor A <code>String</code> containing the first and last name of the movie's lead actor.
	 */
	public void setLeadActor(String leadActor) {
		this.leadActor = leadActor;
	}

	/**
	 * Returns the first and last name of the movie's lead actor.
	 *
	 * @return A <code>String</code> containing the first and last name of the movie's lead actor.
	 */
	public String getLeadActor() {
		return this.leadActor;
	}

	/**
	 * Returns this DVD title.
	 *
	 * @return A <code>String</code> containing the title of this DVD.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets this DVD's title.
	 *
	 * @param name A <code>String</code> containing this DVD's title.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the first and last name of the movie's supporting actor.
	 *
	 * @return A <code>String</code> containing the first and last name of the movie's supporting actor.
	 */
	public String getSupportingActor() {
		return this.supportingActor;
	}

	/**
	 * Sets the first and last name of the movie's supporting actor.
	 *
	 * @param supportingActor A <code>String</code> containing the first and last name of the movie's supporting actor.
	 */
	public void setSupportingActor(String supportingActor) {
		this.supportingActor = supportingActor;
	}

	/**
	 * Returns the date of release.
	 *
	 * @return A <code>Date</code> representing the month, day, and year of the movie's release.
	 */
	public Date getYear() {
		return year;
	}

	/**
	 * Sets the release date of the movie.
	 *
	 * @param year A <code>Date</code> containing the release date of the movie.
	 */
	public void setYear(Date year) {
		this.year = year;
	}

	/**
	 * Sets the release date of the movie from a <code>MM/DD/YYYY</code> string.
	 *
	 * @param year A <code>String</code> containing the movie's release date.
	 */
	public void setYear(String year) {
		this.year = parseReleaseDate(year);
	}

	/**
	 * Parses a <code>MM/DD/YYYY</code> release date into a <code>Date</code> at midnight.
	 *
	 * @param text A date string in <code>MM/DD/YYYY</code> format.
	 * @return The parsed release date.
	 */
	private static Date parseReleaseDate(String text) {
		StringTokenizer st = new StringTokenizer(text, "/");
		int month = Integer.parseInt(st.nextToken());
		int date = Integer.parseInt(st.nextToken());
		int movieyear = Integer.parseInt(st.nextToken());
		// Input is 1-based MM/DD/YYYY; Calendar months are 0-based. Build the value at midnight so
		// equal input strings yield equal Date values.
		return new GregorianCalendar(movieyear, month - 1, date).getTime();
	}

	/**
	 * Returns a <code>String</code> representation of the DVD class.
	 *
	 * @return A <code>String</code> representation of the DVD class.
	 */
	public String toString() {
		String retVal;

		retVal = ("upc :" + this.upc) + "\n";
		retVal += ("Name :" + this.name) + "\n";
		retVal += ("Director :" + this.director) + "\n";
		retVal += ("Composer :" + this.composer) + "\n";
		retVal += ("Lead Actor :" + this.leadActor) + "\n";
		retVal += ("Supporting Actor :" + this.supportingActor) + "\n";
		retVal += ("Copies :" + this.copy) + ":\n";

		return retVal;
	}

	/**
	 * Sets this DVD's availability status.
	 *
	 * @param dvdRented <code>boolean</code> dvdRented indicates if this DVD. should rented(true).or returned(false).
	 * @return boolean indicating whether the dvd operation was successful;
	 */
	public boolean setRented(boolean dvdRented) {
		boolean retVal = false;

		//handle copy count. Don't rent out more DVDs then we have
		if (dvdRented) {
			if (copy > 0) {
				this.copy--;
				retVal = true;
			}
		} else {
			this.copy++;
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Two DVDs are equal when they carry the same UPC - the value that uniquely identifies a title
	 * in the catalogue.
	 *
	 * @param obj The object to compare against.
	 * @return {@code true} if {@code obj} is a DVD with an equal UPC.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DVD)) {
			return false;
		}
		return Objects.equals(this.upc, ((DVD) obj).upc);
	}

	/**
	 * @return A hash code consistent with {@link #equals(Object)}, derived from the UPC.
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(this.upc);
	}
}
