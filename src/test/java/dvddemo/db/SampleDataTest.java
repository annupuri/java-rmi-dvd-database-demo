package dvddemo.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for {@link SampleData}. */
class SampleDataTest {

	@Test
	void seedWritesEverySampleRecord(@TempDir Path dbDir) throws Exception {
		DVDDatabase db = new DVDDatabase(dbDir.toString());

		int written = SampleData.seed(db);

		assertEquals(SampleData.dvds().length, written);
		assertEquals(written, db.getDVDs().size());
	}

	@Test
	void sampleUpcsAreUnique() {
		DVD[] dvds = SampleData.dvds();
		long distinctUpcs = java.util.Arrays.stream(dvds).map(DVD::getUPC).distinct().count();

		assertTrue(dvds.length > 0);
		assertEquals(dvds.length, distinctUpcs);
	}
}
