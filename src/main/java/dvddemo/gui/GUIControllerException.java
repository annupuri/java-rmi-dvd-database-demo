package dvddemo.gui;

/**
 * Holds all exceptions that may occur in the <code>DVDController</code>
 *
 * @see GUIController
 */
public class GUIControllerException extends Exception {

	/** Serialization identity; this exception can cross the RMI boundary. */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a default <code>GUIControllerException</code> instance.
	 */
	public GUIControllerException() {
	}

	/**
	 * Creates a <code>GUIControllerException</code> instance and chains an exception.
	 *
	 * @param e The exception to wrap and chain.
	 */
	public GUIControllerException(Throwable e) {
		super(e);
	}
}
