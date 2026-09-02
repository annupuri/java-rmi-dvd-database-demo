/**
 * The file-backed DVD catalogue and its concurrency control.
 *
 * <p>{@link dvddemo.db.DVDDatabase} stores each {@link dvddemo.db.DVD} as a serialized record file in a
 * directory and offers CRUD, regex search and rent/return operations. {@link dvddemo.db.DVDDbAdapter}
 * wraps it with per-record locking ({@code reserveDVD} / {@code releaseDVD}) and adapts it to the
 * {@link dvddemo.db.DBClient} interface shared with the remote layer. {@link dvddemo.db.DOSClient} is a
 * small text console for catalogue management and seeding sample data.
 */
package dvddemo.db;
