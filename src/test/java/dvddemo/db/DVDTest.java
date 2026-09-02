package dvddemo.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;

/** Unit tests for the {@link DVD} value object. */
class DVDTest {

	@Test
	void setRentedTrueDecrementsStockWhenCopiesAreAvailable() {
		DVD dvd = new DVD();
		dvd.setCopy(2);

		assertTrue(dvd.setRented(true));
		assertEquals(1, dvd.getCopy());
	}

	@Test
	void setRentedTrueFailsWhenNoCopiesRemain() {
		DVD dvd = new DVD();
		dvd.setCopy(0);

		assertFalse(dvd.setRented(true));
		assertEquals(0, dvd.getCopy());
	}

	@Test
	void setRentedFalseAlwaysIncrementsStock() {
		DVD dvd = new DVD();
		dvd.setCopy(0);

		assertTrue(dvd.setRented(false));
		assertEquals(1, dvd.getCopy());
	}

	@Test
	void dateConstructorStoresTheReleaseDateAndDefaultsToOneCopy() {
		Date release = new GregorianCalendar(1999, Calendar.MARCH, 31).getTime();

		DVD dvd = new DVD("111", "Alpha", "c", "d", "l", "s", release);

		assertEquals(release, dvd.getYear());
		assertEquals(1, dvd.getCopy());
	}

	@Test
	void stringConstructorParsesMonthDayYearAtMidnight() {
		DVD dvd = new DVD("111", "Alpha", "c", "d", "l", "s", "06/15/1985", 1);

		Calendar cal = new GregorianCalendar();
		cal.setTime(dvd.getYear());

		assertEquals(1985, cal.get(Calendar.YEAR));
		assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH)); // 1-based "06" -> June
		assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
		assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, cal.get(Calendar.MINUTE));
		assertEquals(0, cal.get(Calendar.SECOND));
		assertEquals(0, cal.get(Calendar.MILLISECOND));
	}

	@Test
	void stringConstructorProducesEqualDatesForEqualInput() {
		DVD a = new DVD("1", "A", "c", "d", "l", "s", "12/25/1999", 1);
		DVD b = new DVD("2", "B", "c", "d", "l", "s", "12/25/1999", 1);

		assertEquals(a.getYear(), b.getYear());
	}

	@Test
	void equalityIsBasedOnUpcAlone() {
		DVD a = new DVD("111", "Alpha", "c", "d", "l", "s", "01/01/2000", 1);
		DVD sameUpc = new DVD("111", "Totally Different", "x", "y", "z", "w", "12/31/2020", 99);
		DVD otherUpc = new DVD("222", "Alpha", "c", "d", "l", "s", "01/01/2000", 1);

		assertEquals(a, sameUpc);
		assertEquals(a.hashCode(), sameUpc.hashCode());
		assertNotEquals(a, otherUpc);
	}
}
