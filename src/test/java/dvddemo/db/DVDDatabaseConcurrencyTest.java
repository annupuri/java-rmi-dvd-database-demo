package dvddemo.db;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/**
 * Concurrency tests for the per-record locking added around {@link DVDDatabase#rent} and
 * {@link DVDDatabase#returnRental}, and for the {@code reserveDVD} / {@code releaseDVD} primitive
 * they build on.
 */
class DVDDatabaseConcurrencyTest {

	@TempDir
	Path dbDir;

	private DVDDatabase db;

	@BeforeEach
	void setUp() {
		db = new DVDDatabase(dbDir.toString());
	}

	@AfterEach
	void releaseAnyHeldLocks() {
		// reservedDVDs is static; make sure a failed test cannot leak a held record to the next.
		for (int i = 0; i < 4; i++) {
			db.releaseDVD("111");
			db.releaseDVD("222");
		}
	}

	@Test
	@Timeout(60)
	void concurrentReturnsDoNotLoseUpdates() throws Exception {
		db.addDVD(newDVD("111", 0));

		int threads = 8;
		int perThread = 120;
		runConcurrently(threads, () -> {
			for (int i = 0; i < perThread; i++) {
				db.returnRental("111");
			}
		});

		// returnRental() is a read-modify-write of +1; with the record lock every increment survives.
		assertEquals(threads * perThread, db.getDVD("111").getCopy());
	}

	@Test
	@Timeout(60)
	void concurrentRentsStopExactlyAtZero() throws Exception {
		int threads = 8;
		int perThread = 120;
		db.addDVD(newDVD("111", threads * perThread));

		runConcurrently(threads, () -> {
			for (int i = 0; i < perThread; i++) {
				db.rent("111");
			}
		});

		assertEquals(0, db.getDVD("111").getCopy());
	}

	@Test
	@Timeout(10)
	void reserveBlocksASecondCallerForTheSameRecordUntilRelease() throws Exception {
		db.reserveDVD("111");

		CountDownLatch started = new CountDownLatch(1);
		CountDownLatch acquired = new CountDownLatch(1);
		Thread other = new Thread(() -> {
			started.countDown();
			try {
				db.reserveDVD("111");
				acquired.countDown();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		other.start();

		assertTrue(started.await(2, TimeUnit.SECONDS));
		assertFalse(acquired.await(500, TimeUnit.MILLISECONDS), "second reserve must block while the record is held");

		db.releaseDVD("111");

		assertTrue(acquired.await(2, TimeUnit.SECONDS), "second reserve must proceed once the record is released");
		other.join(2000);
		db.releaseDVD("111");
	}

	@Test
	@Timeout(10)
	void reserveDoesNotBlockAcrossDifferentRecords() throws Exception {
		db.reserveDVD("111");
		try {
			assertTimeoutPreemptively(Duration.ofSeconds(2), () -> db.reserveDVD("222"),
					"a different UPC is an independent lock and must be granted immediately");
		} finally {
			db.releaseDVD("111");
			db.releaseDVD("222");
		}
	}

	@Test
	void releaseForAnUnheldRecordIsANoOp() {
		assertDoesNotThrow(() -> db.releaseDVD("never-locked"));
	}

	private static DVD newDVD(String upc, int copies) {
		return new DVD(upc, "Title " + upc, "c", "d", "l", "s", "01/01/2000", copies);
	}

	private static void runConcurrently(int threads, ThrowingRunnable body) throws InterruptedException {
		CountDownLatch ready = new CountDownLatch(threads);
		CountDownLatch go = new CountDownLatch(1);
		AtomicReference<Throwable> failure = new AtomicReference<>();
		Thread[] pool = new Thread[threads];
		for (int i = 0; i < threads; i++) {
			pool[i] = new Thread(() -> {
				ready.countDown();
				try {
					go.await();
					body.run();
				} catch (Throwable t) {
					failure.compareAndSet(null, t);
				}
			});
			pool[i].start();
		}
		ready.await();
		go.countDown();
		for (Thread t : pool) {
			t.join();
		}
		if (failure.get() != null) {
			throw new AssertionError("a worker thread failed", failure.get());
		}
	}

	@FunctionalInterface
	private interface ThrowingRunnable {
		void run() throws Exception;
	}
}
