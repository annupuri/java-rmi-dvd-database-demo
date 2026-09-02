package dvddemo.db;

import java.util.List;
import java.io.*;

/**
 * A small text-menu admin console for the DVD database. From a command window it can:
 * <ul>
 * <li>Display all DVDs in the database.</li>
 * <li>Add a DVD to the database.</li>
 * <li>Modify an existing DVD, by upc, in the database.</li>
 * <li>Remove a DVD from the database.</li>
 * <li>Attempt to rent a DVD.</li>
 * <li>Return a rented DVD.</li>
 * <li>Seed a handful of sample records.</li>
 * </ul>
 *
 * The Swing client in {@code dvddemo.gui} is the end-user front end; it only searches, rents and
 * returns, so this tool covers the catalogue-management operations it does not expose. Pass a
 * directory path as the first argument to use a database other than the default {@code dvd_db}.
 *
 * @see dvddemo.db.DVDDatabase
 */
public class DOSClient {

	private static final String enterDVDMsg = "Enter an upc, name, composer," + " director, leadActor, supportingActor,"
			+ " strDate(ex - 12/06/1980), and number of" + " copies for dvd to modify";

	private static final String directions = "Enter 1 for printing existing "
			+ "records, 2 for adding records, 3 for modify, 4 for remove, "
			+ "5 rent, 6 to return a rental, 7 to quit, 8 to generate some dummy records";

	private DOSClient() {
	}

	/**
	 * Runs the text-menu loop until the user quits or input ends.
	 *
	 * @param args Optionally, {@code args[0]} is the database directory to use (default {@code dvd_db}).
	 */
	public static void main(String[] args) {
		DVDDatabase db = args.length > 0 ? new DVDDatabase(args[0]) : new DVDDatabase();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		boolean quitApp = false;

		while (!quitApp) {
			System.out.println(directions);
			try {
				String input = in.readLine();
				if (input == null) {
					break; // end of input (e.g. Ctrl+D)
				}

				switch (input.trim()) {
					case "1": {
						List<DVD> dvds = db.getDVDs();
						System.out.println(dvds.toString());
						break;
					}
					case "2": {
						DVD dvd = readDVD(in);
						db.addDVD(dvd);
						System.out.println("record added: " + dvd);
						break;
					}
					case "3": {
						DVD dvd = readDVD(in);
						if (db.modifyDVD(dvd)) {
							System.out.println("record modified: " + dvd);
						} else {
							System.out.println("record not modified. May not be unique: " + dvd);
						}
						break;
					}
					case "4": {
						System.out.print("to remove item with upc of: ");
						String upc = in.readLine();
						if (db.removeDVD(upc)) {
							System.out.println("item removed!");
						} else {
							System.out.println("item not removed. Check that upc is correct.");
						}
						break;
					}
					case "5": {
						System.out.print("rent DVD with upc of: ");
						String upc = in.readLine();
						if (db.rent(upc)) {
							System.out.println("item rented");
						} else {
							System.out.println("item not rented. Check that upc is correct "
									+ "and that there are available copies.");
						}
						break;
					}
					case "6": {
						System.out.print("return DVD with upc of: ");
						String upc = in.readLine();
						if (db.returnRental(upc)) {
							System.out.println("item returned.");
						} else {
							System.out.println("item not returned. Check that upc is correct.");
						}
						break;
					}
					case "7": {
						System.out.println("Thanks for working with DVD Database Demo.");
						quitApp = true;
						break;
					}
					case "8": {
						int n = SampleData.seed(db);
						System.out.println("Added " + n + " sample DVDs.");
						break;
					}
					default:
						System.out.println("Please enter a number from 1 to 8.");
				}
			} catch (Exception e) {
				System.out.println("Operation failed: " + e.getMessage());
			}
		}
	}

	/**
	 * Reads the eight DVD fields, one per line, from the given reader and builds a {@link DVD}.
	 *
	 * @param in The reader to read the field values from.
	 * @return A DVD populated from the entered values.
	 * @throws IOException if reading from the console fails.
	 */
	private static DVD readDVD(BufferedReader in) throws IOException {
		System.out.println(enterDVDMsg);
		String upc = in.readLine();
		String name = in.readLine();
		String composer = in.readLine();
		String director = in.readLine();
		String leadActor = in.readLine();
		String supportingActor = in.readLine();
		String strDate = in.readLine();
		String strCopies = in.readLine();
		return new DVD(upc, name, composer, director, leadActor, supportingActor, strDate,
				Integer.parseInt(strCopies));
	}
}
