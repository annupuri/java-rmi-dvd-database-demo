package dvddemo.gui;

import java.awt.*;
import java.util.logging.*;
import javax.swing.*;

/**
 * The DVD Database Demo application loader.
 *
 */
public class ApplicationRunner {

	/** Logger for this class. */
	private static final Logger log = Logger.getLogger(ApplicationRunner.class.getName());

	/**
	 * The method that launches the DVD Database Demo application.
	 *
	 * @param args Holds the command line inputs
	 */
	public static void main(String[] args) {
		new ApplicationRunner();
	}

	/**
	 * Sets the default Swing look and feel, then instantiates the main application window.
	 */
	public ApplicationRunner() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException uex) {
			log.warning("Unsupported look and feel specified");
		} catch (ClassNotFoundException cex) {
			log.warning("Look and feel could not be located");
		} catch (InstantiationException iex) {
			log.warning("Look and feel could not be instantiated");
		} catch (IllegalAccessException iaex) {
			log.warning("Look and feel cannot be used on this platform");
		}

		// Create an instance of the main application window
		new MainWindow();
	}

	/**
	 * Prompts the user with an error message in an alert window.
	 *
	 * @param msg The message displayed in the error window.
	 */
	public static void handleException(String msg) {

		JOptionPane alert = new JOptionPane(msg, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
		JDialog dialog = alert.createDialog(null, "Alert");

		// Center on screen
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((d.getWidth() - dialog.getWidth()) / 2);
		int y = (int) ((d.getHeight() - dialog.getHeight()) / 2);
		dialog.setLocation(x, y);

		dialog.setVisible(true);
	}
}
