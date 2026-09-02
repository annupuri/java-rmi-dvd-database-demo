/**
 * RMI access to the DVD catalogue.
 *
 * <p>{@link dvddemo.remote.DVDDatabaseRemote} is the remote view of {@link dvddemo.db.DBClient};
 * {@link dvddemo.remote.DVDDatabaseImpl} implements it on the server over a
 * {@link dvddemo.db.DVDDbAdapter}. {@link dvddemo.remote.RegDVDDatabase} starts a registry and binds the
 * service under the name {@code DVDMediator}; {@link dvddemo.remote.DVDConnector} is the client-side
 * lookup, with a {@code getLocal()} shortcut for an in-process database.
 */
package dvddemo.remote;
