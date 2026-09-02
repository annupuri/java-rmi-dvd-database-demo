package dvddemo.db;

import java.io.IOException;

/**
 * A handful of sample DVDs for trying out the application. Written by {@code ./gradlew seed}, and by
 * {@link DOSClient} menu option 8.
 */
public final class SampleData {

	private SampleData() {
	}

	/**
	 * Returns the built-in sample catalogue.
	 *
	 * @return a fresh array of sample DVDs.
	 */
	public static DVD[] dvds() {
		return new DVD[] {
				new DVD("794051161727", "The Hitchhiker's Guide to the Galaxy", "na", "J.W Alan", "Martin Benson",
						"Sandra Dickson", "12/02/1981", 20),
				new DVD("85392246724", "Harry Potter and the Sorcerer's Stone", "John Williams", "Chris Columbus",
						"Daniel Radcliffe", "Emma Watson", "8/04/2001", 1),
				new DVD("86162118456", "Office Space", "John Frizzell", "Mike Judge", "Jennifer Aniston", "Ron Livingston",
						"7/16/1999", 1),
				new DVD("27203882703", "Metropolis", "Thomas Newman", "Martha Coolidge", "Val Kilmer", "Gabriel Jarret",
						"02/25/1985", 1),
				new DVD("32728999302", "Edward Scissorhands", "Danny Elfman", "Tim Burton", "Johnny Depp", "Winona Ryder",
						"5/02/1990", 1),
				new DVD("78564599302", "Requiem for a Heavyweight", "Laurence Rosenthal", "Ralph Nelson", "Anthony Quinn",
						"Jackie Gleason", "9/18/1962", 1),
				new DVD("32725349302", "Night of the Ghouls", "Edward D. Wood Jr.", "Edward D. Wood Jr.", "Kenne Duncan",
						"Duke Moore", "2/25/1959", 1),
				new DVD("327254654435", "Army of Darkness", "na", "Sam Raimi", "Bruce Campbell", "Embeth Davidtz",
						"11/3/1993", 1),
		};
	}

	/**
	 * Writes {@link #dvds()} into the given database.
	 *
	 * @param db the database to seed.
	 * @return the number of records written.
	 * @throws IOException if a record cannot be written.
	 */
	public static int seed(DVDDatabase db) throws IOException {
		DVD[] samples = dvds();
		for (DVD sample : samples) {
			db.addDVD(sample);
		}
		return samples.length;
	}

	/**
	 * Seeds the database directory named by {@code args[0]} (default {@code dvd_db}).
	 *
	 * @param args optionally, {@code args[0]} is the database directory.
	 * @throws IOException if a record cannot be written.
	 */
	public static void main(String[] args) throws IOException {
		String dir = args.length > 0 ? args[0] : "dvd_db";
		int n = seed(new DVDDatabase(dir));
		System.out.println("Seeded " + n + " sample DVDs into " + dir + ".");
	}
}
