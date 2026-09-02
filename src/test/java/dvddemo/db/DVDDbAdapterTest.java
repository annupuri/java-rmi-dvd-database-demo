package dvddemo.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for the locking wrapper {@link DVDDbAdapter}, run against a temp directory. The adapter has
 * no {@code addDVD}; a record is created by {@code modifyDVD}, which persists whether or not the
 * record already exists.
 */
class DVDDbAdapterTest {

	@TempDir
	Path dbDir;

	private DVDDbAdapter adapter;

	@BeforeEach
	void setUp() {
		adapter = new DVDDbAdapter(dbDir.toString());
	}

	@AfterEach
	void releaseAnyHeldLocks() {
		// reservedDVDs is static; keep a failed test from leaking a held record to the next one.
		DVDDatabase lockView = new DVDDatabase(dbDir.toString());
		for (int i = 0; i < 4; i++) {
			lockView.releaseDVD("111");
		}
	}

	private static DVD sampleDVD(String upc) {
		return new DVD(upc, "Title " + upc, "c", "d", "l", "s", "01/01/2000", 3);
	}

	@Test
	void removeDeletesAnExistingRecord() throws Exception {
		adapter.modifyDVD(sampleDVD("111"));

		assertTrue(adapter.removeDVD("111"));
		assertThrows(NoSuchFileException.class, () -> adapter.getDVD("111"));
	}

	@Test
	void removeReturnsFalseForAnUnknownRecord() throws Exception {
		assertFalse(adapter.removeDVD("does-not-exist"));
	}

	@Test
	@Timeout(5)
	void removeReleasesTheRecordLock() throws Exception {
		adapter.modifyDVD(sampleDVD("111"));
		adapter.removeDVD("111");

		// A leaked lock would make this second round of reserve-guarded calls hang.
		adapter.modifyDVD(sampleDVD("111"));
		assertTrue(adapter.removeDVD("111"));
	}

	@Test
	void basicOperationsRoundTripThroughTheAdapter() throws Exception {
		adapter.modifyDVD(sampleDVD("111"));

		assertEquals("Title 111", adapter.getDVD("111").getName());
		assertEquals(1, adapter.getDVDs().size());
		assertEquals(1, adapter.find("Title 111").size());
	}
}
