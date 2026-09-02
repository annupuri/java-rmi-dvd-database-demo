package dvddemo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * The main application window of the DVD Database Demo application.
 *
 */
public class MainWindow {
	/**
	 * The internal reference to the GUI controller.
	 */
	private GUIController controller;

	/**
	 * The main application window instance.
	 */
	private JFrame mainWindow;

	/**
	 * The initial network selection dialog.
	 */
	private final JDialog connectionDialog;

	/**
	 * The <code>JTable</code> that displays the DVD held by the system.
	 */
	private final JTable mainTable = new JTable();

	/**
	 * The text field that contains the user defined search String.
	 */
	private final JTextField searchField = new JTextField(20);

	/**
	 * The internal reference to the currently displayed table data.
	 */
	private DVDTableModel tableData;

	/**
	 * Holds a copy of the last user defined search String.
	 */
	private String previousSearchString = "";

	/**
	 * Builds and displays the main application window. The constructor begins by building the connection selection
	 * dialog box. After the user selects a connection type, the method creates a <code>DVDMainWindow</code> instance.
	 */
	public MainWindow() {
		// Create the connection select dialog
		this.connectionDialog = new JDialog(this.mainWindow, "Select a Connection Type", true);
		connectionDialog.getContentPane().setLayout(new BorderLayout());
		JPanel connPanel = new JPanel();
		JPanel quitPanel = new JPanel();

		// Create the connection panels
		JButton networkButton = new JButton("Network Connection");
		JButton localButton = new JButton("Local Connection");
		JButton quitButton = new JButton("Quit");

		// Set the default button for this dialog
		connectionDialog.getRootPane().setDefaultButton(localButton);

		// Add Tool Tips
		localButton.setToolTipText("Connect to the local database.");
		networkButton.setToolTipText("Connect to the network database.");
		quitButton.setToolTipText("Quit the application.");
		// Add the keystroke mnemonics
		localButton.setMnemonic(KeyEvent.VK_L);
		networkButton.setMnemonic(KeyEvent.VK_N);
		quitButton.setMnemonic(KeyEvent.VK_Q);

		// Create ActionEvents that set the
		// connection type and closes the dialog
		networkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					controller = new GUIController(GUIController.NETWORK_CONNECTION);
					connectionDialog.dispose();
					mainWindow = new DVDMainWindow("DVD Database Demo");
				} catch (GUIControllerException gce) {
					ApplicationRunner.handleException("Failed to open network connection");
				}
			}
		});
		localButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					controller = new GUIController(GUIController.LOCAL_CONNECTION);
					connectionDialog.dispose();
					mainWindow = new DVDMainWindow("DVD Database Demo");
				} catch (GUIControllerException gce) {
					ApplicationRunner.handleException("Failed to open local connection");
				}
			}
		});

		quitButton.addActionListener(new QuitApplication());

		// Add the buttons to the main panel
		connPanel.add(localButton);
		connPanel.add(networkButton);
		quitPanel.add(quitButton);

		// Add the connection panels to the dialog
		connectionDialog.getContentPane().add(connPanel, BorderLayout.WEST);
		connectionDialog.getContentPane().add(quitPanel, BorderLayout.EAST);

		// Closing the connection chooser (the X button) quits the application - there is nothing
		// else to return to at this point.
		connectionDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		connectionDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Turn off the ability to resize the window
		connectionDialog.setResizable(false);

		// pack() sizes the dialog to fit the buttons. (A hard-coded setSize(400, 60) here used to
		// collapse the content to roughly the title bar under some look-and-feels.)
		connectionDialog.pack();

		// Center on screen
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((d.getWidth() - connectionDialog.getWidth()) / 2);
		int y = (int) ((d.getHeight() - connectionDialog.getHeight()) / 2);
		connectionDialog.setLocation(x, y);
		connectionDialog.setVisible(true);
	}

	/**
	 * Uses the <code>tableData</code> member to refresh the contents of the <code>mainTable</code>. The method will
	 * attempt to preserve all previous selections and contents displayed.
	 */
	private void setupTable() {
		String prevSelected = ""; // Preserve the previous selection

		int index = mainTable.getSelectedRow();
		if (index >= 0) {
			prevSelected = (String) mainTable.getValueAt(index, 0);
		}

		// Reset the table data
		this.mainTable.setModel(this.tableData);

		// Reselect the previous item if it still exists
		for (int i = 0; i < this.mainTable.getRowCount(); i++) {
			String selectedIsbn = (String) mainTable.getValueAt(i, 0);
			if (selectedIsbn.equals(prevSelected)) {
				this.mainTable.setRowSelectionInterval(i, i);
				break;
			}
		}
	}

	/**
	 * The main application window containing the DVD display table, search field, rent button, and return button.
	 *
	 */
	class DVDMainWindow extends JFrame {
		/** Serialization identity; {@code JFrame} is {@code Serializable}. */
		private static final long serialVersionUID = 1L;

		/**
		 * Builds a DVD main window
		 */
		public DVDMainWindow() {
			this("");

		}

		/**
		 * Builds a DVD main window with the specified window title.
		 *
		 * @param title A String used for the main window title.
		 */
		public DVDMainWindow(String title) {
			super(title);
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);

			// Add the menu bar
			JMenuBar menuBar = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			JMenuItem quitMenuItem = new JMenuItem("Quit");
			quitMenuItem.addActionListener(new QuitApplication());
			quitMenuItem.setMnemonic(KeyEvent.VK_Q);
			fileMenu.add(quitMenuItem);

			fileMenu.setMnemonic(KeyEvent.VK_F);
			menuBar.add(fileMenu);

			this.setJMenuBar(menuBar);

			// A full data set is returned from an empty search
			try {
				tableData = controller.getDVDs();
				setupTable();
			} catch (GUIControllerException gce) {
				ApplicationRunner.handleException(
						"Failed to acquire an initial DVD list." + "\nPlease check the DB connection.");
			}

			JScrollPane tableScroll = new JScrollPane(mainTable);
			tableScroll.setSize(500, 250);

			this.getContentPane().add(tableScroll, BorderLayout.CENTER);

			// Setup rent and return buttons
			JButton rentButton = new JButton("Rent DVD");
			JButton returnButton = new JButton("Return DVD");

			// Add the action listeners to rent and return buttons
			rentButton.addActionListener(new RentDVD());
			returnButton.addActionListener(new ReturnDVD());
			// Set the rent and return buttons to refuse focus
			rentButton.setRequestFocusEnabled(false);
			returnButton.setRequestFocusEnabled(false);
			// Add the keystroke mnemonics
			rentButton.setMnemonic(KeyEvent.VK_R);
			returnButton.setMnemonic(KeyEvent.VK_U);
			// Create a panel to hold the rent and return buttons
			JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			checkoutPanel.add(rentButton);
			checkoutPanel.add(returnButton);

			// Set up the search pane
			JButton searchButton = new JButton("Search");
			searchButton.addActionListener(new SearchDVD());
			searchButton.setMnemonic(KeyEvent.VK_S);
			// Search panel
			JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			searchPanel.add(searchField);
			searchPanel.add(searchButton);
			// Make the search button the default button for the main window
			this.getRootPane().setDefaultButton(searchButton);

			// bottom panel
			JPanel bottomPanel = new JPanel(new BorderLayout());
			bottomPanel.add(searchPanel, BorderLayout.NORTH);
			bottomPanel.add(checkoutPanel, BorderLayout.SOUTH);

			// Add the bottom panel to the main window
			this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

			// Set table properties
			mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			mainTable.setToolTipText("Select a DVD record to rent or return.");

			// Add Tool Tips
			returnButton.setToolTipText("Return the DVD item selected in the above table.");
			rentButton.setToolTipText("Rent the DVD item selected in the above table.");
			searchField.setToolTipText("Enter information about a DVD you want to locate.");
			searchButton.setToolTipText("Submit the DVD search.");

			this.pack();
			this.setSize(650, 300);
			// Center on screen
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int) ((d.getWidth() - this.getWidth()) / 2);
			int y = (int) ((d.getHeight() - this.getHeight()) / 2);
			this.setLocation(x, y);
			this.setVisible(true);
		}
	}

	/**
	 * Handles all DVD rental events.
	 *
	 */
	private class RentDVD implements ActionListener {

		/**
		 * Handles the actionPerformed event for the rent button.
		 *
		 * @param ae The event initiated by the rent button.
		 */
		public void actionPerformed(ActionEvent ae) {
			String rentalIsbn = "";
			int index = mainTable.getSelectedRow();
			if (index >= 0) {
				rentalIsbn = (String) mainTable.getValueAt(index, 0);
				try {
					controller.rent(rentalIsbn);
					tableData = controller.find(previousSearchString);
					setupTable();
				} catch (GUIControllerException gce) {
					ApplicationRunner.handleException("Rent operation failed.");
				}
			}
		}
	}

	/**
	 * Handles all DVD return events.
	 *
	 */
	private class ReturnDVD implements ActionListener {

		/**
		 * Handles the actionPerformed event for the return button.
		 *
		 * @param ae The event initiated by the return button.
		 */
		public void actionPerformed(ActionEvent ae) {
			String returnIsbn = "";
			int index = mainTable.getSelectedRow();
			if (index >= 0) {
				returnIsbn = (String) mainTable.getValueAt(index, 0);
				try {
					controller.returnRental(returnIsbn);
					tableData = controller.find(previousSearchString);
					setupTable();
				} catch (GUIControllerException gce) {
					ApplicationRunner.handleException("Return operation failed.");
				}
			}
		}
	}

	/**
	 * Handles all DVD search events.
	 *
	 */
	private class SearchDVD implements ActionListener {

		/**
		 * Handles the actionPerformed event for the search button. Uses the String values of the
		 * <code>searchField</code> member.
		 *
		 * @param ae The event initiated by the search button.
		 */
		public void actionPerformed(ActionEvent ae) {
			previousSearchString = searchField.getText();
			searchField.setText("");
			try {
				tableData = controller.find(previousSearchString);
				setupTable();
			} catch (GUIControllerException gce) {
				// Inspect the exception chain
				Throwable rootException = gce.getCause();
				String msg = "Search operation failed.";
				// If a syntax error occurred, get the message
				if (rootException instanceof PatternSyntaxException) {
					msg += ("\n" + rootException.getMessage());
				}
				ApplicationRunner.handleException(msg);
				previousSearchString = "";
			}
		}
	}

	/**
	 * Handles all application quit events.
	 *
	 */
	private class QuitApplication implements ActionListener {

		/**
		 * Quits the application when invoked.
		 *
		 * @param ae The event triggering the quit operation.
		 */
		public void actionPerformed(ActionEvent ae) {
			System.exit(0);
		}
	}
}
