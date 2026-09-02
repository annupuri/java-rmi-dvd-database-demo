package dvddemo.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Behavioural tests for the file-backed {@link DVDDatabase}. Each test runs against its own
 * {@link TempDir}, passed through the path constructor.
 */
class DVDDatabaseTest {

	@TempDir
	Path dbDir;

	private DVDDatabase db;

	@BeforeEach
	void setUp() {
		db = new DVDDatabase(dbDir.toString());
	}

	private static DVD sampleDVD(String upc, String name, int copies) {
		return new DVD(upc, name, "Composer", "Director", "Lead Actor", "Supporting Actor", "01/01/2000", copies);
	}

	@Test
	void addThenGetRoundTripsTheRecord() throws Exception {
		db.addDVD(sampleDVD("111", "Alpha", 3));

		DVD loaded = db.getDVD("111");

		assertEquals("111", loaded.getUPC());
		assertEquals("Alpha", loaded.getName());
		assertEquals(3, loaded.getCopy());
	}

	@Test
	void modifyOverwritesTheStoredRecord() throws Exception {
		db.addDVD(sampleDVD("111", "Alpha", 3));

		assertTrue(db.modifyDVD(sampleDVD("111", "Alpha Remastered", 7)));

		DVD loaded = db.getDVD("111");
		assertEquals("Alpha Remastered", loaded.getName());
		assertEquals(7, loaded.getCopy());
	}

	@Test
	void getDVDsReturnsEveryStoredRecord() throws Exception {
		db.addDVD(sampleDVD("111", "Alpha", 1));
		db.addDVD(sampleDVD("222", "Beta", 1));
		db.addDVD(sampleDVD("333", "Gamma", 1));

		List<?> all = db.getDVDs();

		assertEquals(3, all.size());
	}

	@Test
	void getDVDsIsEmptyWhenNothingStored() throws Exception {
		assertTrue(db.getDVDs().isEmpty());
	}

	@Test
	void findMatchesTheRegexAgainstRecordFields() throws Exception {
		db.addDVD(sampleDVD("111", "The Matrix", 1));
		db.addDVD(sampleDVD("222", "Amelie", 1));

		Collection<?> hits = db.find("Matrix");

		assertEquals(1, hits.size());
	}

	@Test
	void findReturnsAnEmptyCollectionWhenNothingMatches() throws Exception {
		db.addDVD(sampleDVD("111", "The Matrix", 1));

		assertTrue(db.find("no-such-title").isEmpty());
	}

	@Test
	void findRejectsAnInvalidRegex() throws Exception {
		db.addDVD(sampleDVD("111", "The Matrix", 1));

		assertThrows(PatternSyntaxException.class, () -> db.find("[unclosed"));
	}

	@Test
	void rentDecrementsTheCopyCount() throws Exception {
		db.addDVD(sampleDVD("111", "Alpha", 2));

		assertTrue(db.rent("111"));

		assertEquals(1, db.getDVD("111").getCopy());
	}

	@Test
	void returnRentalIncrementsTheCopyCount() throws Exception {
		db.addDVD(sampleDVD("111", "Alpha", 2));

		assertTrue(db.returnRental("111"));

		assertEquals(3, db.getDVD("111").getCopy());
	}

	@Test
	void rentThenReturnLeavesTheCountUnchanged() throws Exception {
		db.addDVD(sampleDVD("111", "Alpha", 5));

		db.rent("111");
		db.returnRental("111");

		assertEquals(5, db.getDVD("111").getCopy());
	}

	@Test
	void rentStillReportsSuccessWhenNoCopiesRemain() throws Exception {
		// Known limitation: rent() returns the result of the persist step and ignores whether a
		// copy was actually available, so the count floors at zero without signalling the caller.
		db.addDVD(sampleDVD("111", "Alpha", 1));

		assertTrue(db.rent("111"));
		assertTrue(db.rent("111"));

		assertEquals(0, db.getDVD("111").getCopy());
	}

	@Test
	void rentOnAMissingRecordFails() {
		// readRecord opens the record file directly, so an unknown UPC surfaces as a
		// NoSuchFileException rather than a false return.
		assertThrows(NoSuchFileException.class, () -> db.rent("does-not-exist"));
	}

	@Test
	void removeDeletesTheStoredRecord() throws Exception {
		db.addDVD(sampleDVD("111", "Alpha", 1));

		assertTrue(db.removeDVD("111"));

		assertThrows(NoSuchFileException.class, () -> db.getDVD("111"));
		assertTrue(db.getDVDs().isEmpty());
	}

	@Test
	void removeReturnsFalseWhenTheRecordDoesNotExist() throws Exception {
		assertFalse(db.removeDVD("does-not-exist"));
	}

	@Test
	void addCreatesTheStoreDirectoryWhenAbsent() throws Exception {
		DVDDatabase fresh = new DVDDatabase(dbDir.resolve("not-created-yet").toString());

		fresh.addDVD(sampleDVD("111", "Alpha", 1));

		assertEquals("Alpha", fresh.getDVD("111").getName());
	}

	@Test
	void getDVDsIgnoresNonRecordEntries() throws Exception {
		db.addDVD(sampleDVD("111", "Alpha", 1));
		Files.writeString(dbDir.resolve("notes.txt"), "not a record");
		Files.createDirectory(dbDir.resolve("nested"));

		List<?> all = db.getDVDs();

		assertEquals(1, all.size());
	}

	@Test
	void separateInstancesUseSeparateDirectories(@TempDir Path otherDir) throws Exception {
		db.addDVD(sampleDVD("111", "Alpha", 1));

		DVDDatabase other = new DVDDatabase(otherDir.toString());
		other.addDVD(sampleDVD("222", "Beta", 1));

		// Each instance only sees the records under the directory it was constructed with.
		assertEquals(1, db.getDVDs().size());
		assertEquals("111", db.getDVD("111").getUPC());
		assertEquals(1, other.getDVDs().size());
		assertEquals("222", other.getDVD("222").getUPC());
	}
}
