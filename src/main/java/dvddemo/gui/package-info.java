/**
 * The Swing client: a table of DVDs with search, rent and return.
 *
 * <p>{@link dvddemo.gui.ApplicationRunner} is the entry point; {@link dvddemo.gui.MainWindow} builds the
 * connection dialog (local or RMI) and the main window; {@link dvddemo.gui.GUIController} mediates
 * between the widgets and a {@link dvddemo.db.DBClient}, translating failures into
 * {@link dvddemo.gui.GUIControllerException}. {@link dvddemo.gui.DVDTableModel} backs the table.
 */
package dvddemo.gui;
