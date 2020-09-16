package movie.management.app;

// libraries
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import static java.lang.Integer.parseInt;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.LineBorder;
import static javax.swing.BorderFactory.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * main class from which the program is run
 */
public class MovieManagementApp {
    // key used to search the TMDb API
    static final String API_KEY = "6a83e68b123fd320f7b72bd128ea5646";
    // chosen media player
    static final String MEDIA_PLAYER = "C:/Program Files (x86)/VideoLAN/VLC/vlc.exe";
    // appearance constants
    static final String FONT_DEFAULT = "Calibri";
    static final Color COLOR_RED = new Color( 60, 0, 0);
    static final Color COLOR_RED_DARK = new Color( 50, 0, 0);
    static final Color COLOR_RED_DARKER = new Color( 30, 0, 0);
    static final Color COLOR_BLACK_OFF = new Color( 40, 40, 40);
    // global frame variables
    JFrame welcomeFrame;
    JFrame moviesFrame;
    JFrame postersFrame;
    
    // constructor
    public MovieManagementApp() throws MalformedURLException, IOException {openWelcome();} // end of constructor
    
    // opens Welcome frame
    private void openWelcome() {
        // create frame and some panels
        welcomeFrame = new JFrame("Gio Movie Manager");
        JPanel welcomePanel = new JPanel( new BorderLayout() );
        welcomePanel.setPreferredSize( new Dimension(1000, 800) );
        welcomePanel.setBackground(COLOR_RED);
        welcomeFrame.add(welcomePanel);
        
        // create header panel and label
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel();
        headerLabel.setIcon( new ImageIcon( new ImageIcon("header.png").getImage().getScaledInstance(900, -1, Image.SCALE_SMOOTH) ) );
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont( new Font(FONT_DEFAULT, Font.BOLD, 72) );
        headerPanel.add(headerLabel);
        welcomePanel.add(headerPanel, BorderLayout.PAGE_START);
        
        // create main panel
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.setOpaque(false);
        
        // create button that opens movies table
        DefaultButton movieButton = new DefaultButton("Open movie library");
        movieButton.addActionListener( new OpenMovies() );
        movieButton.setPreferredSize( new Dimension(0, 80) );
        movieButton.setFont( new Font(FONT_DEFAULT, Font.BOLD, 20) );
        mainPanel.add(movieButton, BorderLayout.PAGE_START);
        
        // main content of frame, regarding directories list box
        JPanel directoryPanel = new JPanel( new BorderLayout() );
        directoryPanel.setOpaque(false);
        JPanel directoryPanelHeader = new JPanel ( new GridLayout(2, 1) );
        directoryPanelHeader.setOpaque(false);
        directoryPanelHeader.add( new JLabel() );
        JLabel directoryHeader = new JLabel("Directories", SwingConstants.CENTER);
        directoryHeader.setPreferredSize( new Dimension(1000, 100) );
        directoryHeader.setFont( new Font(FONT_DEFAULT, Font.BOLD, 40) );
        directoryHeader.setForeground(Color.WHITE);
        directoryPanelHeader.add(directoryHeader);
        directoryPanel.add(directoryPanelHeader, BorderLayout.PAGE_START);
        DefaultListModel directoryListModel = new DefaultListModel();
        JList directoryList = new JList(directoryListModel);
        directoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        directoryList.setBackground(COLOR_RED_DARK);
        directoryList.setForeground(Color.WHITE);
        directoryList.setSelectionBackground(COLOR_RED_DARKER);
        directoryList.setSelectionForeground(Color.WHITE);
        directoryList.setFont( new Font(FONT_DEFAULT, Font.PLAIN, 18) );
        JScrollPane directoryScroll = new JScrollPane(directoryList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getDefaultDirectories(directoryListModel);
        directoryPanel.add(directoryScroll, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel( new GridLayout(1, 3) );
        buttonPanel.setOpaque(false);
        DefaultButton addButton = new DefaultButton("Add new directory");
        DefaultButton updateButton = new DefaultButton("Update directory");
        DefaultButton removeButton = new DefaultButton("Remove directory");
        addButton.setPreferredSize( new Dimension(0, 60) );
        updateButton.setPreferredSize( new Dimension(0, 60) );
        removeButton.setPreferredSize( new Dimension(0, 60) );
        addButton.setFont( new Font(FONT_DEFAULT, Font.BOLD, 13) );
        updateButton.setFont( new Font(FONT_DEFAULT, Font.BOLD, 13) );
        removeButton.setFont( new Font(FONT_DEFAULT, Font.BOLD, 13) );
        addButton.addActionListener( new AddDirectory(directoryList, directoryListModel) );
        updateButton.addActionListener( new UpdateDirectory(directoryList) );
        removeButton.addActionListener( new RemoveDirectory(directoryList, directoryListModel) );
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        directoryPanel.add(buttonPanel, BorderLayout.PAGE_END);
        mainPanel.add(directoryPanel, BorderLayout.CENTER);
        welcomePanel.add(mainPanel, BorderLayout.CENTER);
        
        // spaccer components
        JLabel leftLabel = new JLabel();
        JLabel rightLabel = new JLabel();
        JLabel bottomLabel = new JLabel();
        leftLabel.setPreferredSize( new Dimension(150, 0) );
        rightLabel.setPreferredSize( new Dimension(150, 0) );
        bottomLabel.setPreferredSize( new Dimension(0, 100) );
        welcomePanel.add(leftLabel, BorderLayout.LINE_START);
        welcomePanel.add(rightLabel, BorderLayout.LINE_END);
        welcomePanel.add(bottomLabel, BorderLayout.PAGE_END);
        
        // prepare frame and make it visible
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.pack();
        welcomeFrame.setLocationRelativeTo(null);
        welcomeFrame.setResizable(false);
        welcomeFrame.setVisible(true);
    } // end of openWelcome method
    
    // opens Movies table frame
    private void openMovies() {
        // create frame and some panels
        moviesFrame = new JFrame("Gio Movie Manager");
        JPanel moviePanel = new JPanel( new BorderLayout() );
        moviePanel.setPreferredSize( new Dimension(1400, 800) );
        moviePanel.setBackground(COLOR_RED);
        moviesFrame.add(moviePanel);
        
        DefaultTableModel model = new DefaultTableModel(
                // set table header titles
                new Object[] {"ID", "Path", "Filename", "Title", "Date", "Rating", "Votes", "Director", "Apple", "Watched", "User rating", "Notes"}, 0
        ) {
            // set column classes, to set data types
            @Override public Class getColumnClass (int column) {
                if (column == 0) return Integer.class;
                if (column == 5) return Double.class;
                if (column == 6) return Integer.class;
                if (column == 8) return Boolean.class;
                if (column == 9) return Boolean.class;
                if (column == 10) return Boolean.class;
                return super.getColumnClass(column);
            }
        };
        
        JTable table = new JTable(model) {
            // disable user-editing for early columns
            @Override public boolean isCellEditable(int row, int column) {
                int modelColumn = convertColumnIndexToModel(column);
                return (modelColumn > 7);
            };
            // set alternating row colors
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if( !comp.getBackground().equals( getSelectionBackground() ) ) {
                   Color c = (row % 2 == 0 ? COLOR_RED : COLOR_RED_DARK);
                   comp.setBackground(c);
                }
                return comp;
            };
        };
        
        updateTable(model); // call method to update table data, to populate it
        
        // format number of votes to be comma-separated values
        table.setDefaultRenderer(
            Integer.class, new DefaultTableCellRenderer() {
                NumberFormat numberFormat = new DecimalFormat("#,###,###");
                @Override protected void setValue(Object aValue) {
                    Integer value = (Integer) aValue;
                    super.setValue(numberFormat.format(value));
                }
            }
        );
        
        // set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.getColumnModel().getColumn(1).setPreferredWidth(10);
        table.getColumnModel().getColumn(2).setPreferredWidth(320);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(40);
        table.getColumnModel().getColumn(5).setPreferredWidth(30);
        table.getColumnModel().getColumn(6).setPreferredWidth(30);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);
        table.getColumnModel().getColumn(8).setPreferredWidth(20);
        table.getColumnModel().getColumn(9).setPreferredWidth(20);
        table.getColumnModel().getColumn(10).setPreferredWidth(30);
        
        // set column alignments
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer(); leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer(); rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(10).setCellRenderer(centerRenderer);

        // create select box to allow users to edit personal movie rating
        JComboBox selectRating = new JComboBox();
        selectRating.addItem(0); selectRating.addItem(1); selectRating.addItem(2); selectRating.addItem(3); selectRating.addItem(4); selectRating.addItem(5);
        ( (JLabel) selectRating.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(10).setCellEditor( new DefaultCellEditor(selectRating) );
        
        // add listener to update Movies text file if table data is changed by user
        model.addTableModelListener(new TableModelListener() {
            @Override public void tableChanged(TableModelEvent e) {
                editMovie( table, e.getFirstRow() );
            }
        });
        
        // make each table row a double-click link to launch the file in the chosen media player
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint( e.getPoint() );
                    int col = table.columnAtPoint( e.getPoint() );
                    if(row >= 0 && col >= 0 && col < 8) {
                        String file = "" + table.getValueAt(row, 1) + table.getValueAt(row, 2);
                        Runtime run = Runtime.getRuntime();
                        String[] cmd = {MEDIA_PLAYER, file};
                        try {
                            run.exec(cmd, null);
                        } catch (IOException ex) {Logger.getLogger(MovieManagementApp.class.getName()).log(Level.SEVERE, null, ex);}
                    }
                }
            }
        });
        
        // set table and header properties
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setPreferredSize( new Dimension(0, 50) );
        table.getTableHeader().setBorder( createLineBorder(Color.BLACK) );
        table.getTableHeader().setBackground(Color.BLACK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont( new Font(FONT_DEFAULT, Font.BOLD, 11) );
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(32);
        table.setIntercellSpacing( new Dimension(0, 0) );
        table.setForeground(Color.WHITE);
        table.setFont( new Font(FONT_DEFAULT, Font.PLAIN, 13) );
        table.setSelectionBackground(COLOR_RED_DARKER);
        table.setSelectionForeground(Color.WHITE);
        JScrollPane tableScroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.setBorder( createEmptyBorder() );
        moviePanel.add(tableScroll, BorderLayout.CENTER);
        
        // create footer content
        JPanel footerPanel = new JPanel( new GridLayout(1, 2) );
        footerPanel.setBackground(Color.BLACK);
        DefaultButton searchButton = new DefaultButton("Add movie from online database");
        DefaultButton viewButton = new DefaultButton("Poster view");
        viewButton.addActionListener( new OpenPosters() );
        searchButton.setPreferredSize( new Dimension(50, 50) );
        viewButton.setPreferredSize( new Dimension(50, 50) );
        footerPanel.add(searchButton);
        footerPanel.add(viewButton);
        moviePanel.add(footerPanel, BorderLayout.PAGE_END);
        
        // prepare frame and make it visible
        moviesFrame.addWindowListener( new CloseMovies() );
        moviesFrame.pack();
        moviesFrame.setLocationRelativeTo(null);
        moviesFrame.setResizable(false);
        moviesFrame.setVisible(true);
    } // end of openMovies method
    
    // opens Poster view frame
    private void openPosters() {
        // create frame and its panels
        postersFrame = new JFrame("Gio Movie Manager");
        postersFrame.setPreferredSize( new Dimension(1400, 800) );
        JPanel postersPanel = new JPanel( new GridLayout(0, 9, 2, 2) );
        postersPanel.setBackground(Color.BLACK);
        JScrollPane posterScroll = new JScrollPane(postersPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        postersFrame.add(posterScroll);
        
        // add labels containing poster images to panel
        String line;
        try( BufferedReader readFile = new BufferedReader( new FileReader("movies.txt") ) ) {
            while( ( line = readFile.readLine() ) != null ) {
                String[] movie = line.split("  >>> ");
                JLabel poster = new JLabel();
                // use movie's id to determine which poster to add
                poster.setIcon( new ImageIcon( new ImageIcon("posters/"+movie[0]+".jpg").getImage().getScaledInstance(150, 225, Image.SCALE_SMOOTH) ) );
                // make each poster a double-click link to launch the file in the chosen media player
                poster.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            Runtime run = Runtime.getRuntime();
                            String[] cmd = {MEDIA_PLAYER, movie[1]+movie[2]};
                            try {
                                run.exec(cmd, null);
                            } catch (IOException ex) {Logger.getLogger(MovieManagementApp.class.getName()).log(Level.SEVERE, null, ex);}
                        }
                    }
                });
                postersPanel.add(poster);
                System.out.println("posters/"+movie[0]+".jpg");
            } readFile.close();
        } catch (IOException e) {System.out.println("ERROR: movies.txt can't be found!\n");}
        
        // prepare frame and make it visible
        postersFrame.addWindowListener( new ClosePosters() );
        postersFrame.pack();
        postersFrame.setLocationRelativeTo(null);
        postersFrame.setResizable(false);
        postersFrame.setVisible(true);
    } // end of openPosters method
    
    // disposes of Welcome frame and opens Movies table frame
    class OpenMovies implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {welcomeFrame.dispose(); openMovies();}
    } // end of OpenMovies class
    
    // opens Welcome frame upon closing Movies table frame
    class CloseMovies extends WindowAdapter {
        @Override public void windowClosing(WindowEvent e) { openWelcome();}
    } // end of CloseMovies class
    
    // displays confirmation dialog, before disposing of Movies table frame and opening Poster view frame
    class OpenPosters implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(
                    null,
                    "Posters could take while to load.\n\nAre you sure you want to proceed?\n ",
                    "Open poster view",
                    JOptionPane.YES_NO_OPTION
            ); if(option == 0) {moviesFrame.dispose(); openPosters();}
            
        }
    } // end of OpenPosters class
    
    // opens Movies table frame upon closing Posters table frame
    class ClosePosters extends WindowAdapter {
        @Override public void windowClosing(WindowEvent e) { openMovies();}
    } // end of ClosePosters class
    
    // updates Movies table data from Movies text file
    private void updateTable(DefaultTableModel table) {
        Object[] movieObject = new Object[13]; // array of objects, to be used as table data
        String line;
        try( BufferedReader readFile = new BufferedReader( new FileReader("movies.txt") ) ) {
            while( ( line = readFile.readLine() ) != null ) {
                String[] movie = line.split("  >>> "); // split line of data to get individual data
                // set data type for each piece of data, to be added to table row
                movieObject[0] = new Integer(movie[0]);
                movieObject[1] = movie[1];
                movieObject[2] = movie[2];
                movieObject[3] = movie[3];
                movieObject[4] = movie[4];
                movieObject[5] = new Double(movie[5]);
                movieObject[6] = new Integer(movie[6]);
                movieObject[7] = movie[7];
                movieObject[8] = Boolean.valueOf(movie[8]);
                movieObject[9] = Boolean.valueOf(movie[9]);
                movieObject[10] = movie[10];
                movieObject[11] = movie[11];
                // add array of objects to table row
                table.addRow(movieObject);
            }
            readFile.close();
        } catch (IOException e) {System.out.println("ERROR: movies.txt can't be found!\n");}
    } // end of updateTable method
    
    // updates Movies text file after being edited in a table cell
    private void editMovie(JTable table, int row) {
        String newRecord = "";
        // construct line for changed record to be re-added to be text file
        for(int i = 0; i < table.getColumnCount()-1; i++) {
            newRecord += table.getValueAt(row, i) + "  >>> ";
        } newRecord += table.getValueAt(row, table.getColumnCount()-1);
        ArrayList<String> existingMovies = new ArrayList<>(); // array of movies that haven't been edited
        String line;
        try( BufferedReader readFile = new BufferedReader( new FileReader("movies.txt") ) ) {
            while( ( line = readFile.readLine() ) != null ) {
                String[] movie = line.split("  >>> "); // split line of data to get individual data
                // if id, path, and filename match edited record, re-add it as new record
                if( movie[0].equals(table.getValueAt(row, 0)+"") && movie[1].equals(table.getValueAt(row, 1)+"") && movie[2].equals(table.getValueAt(row, 2)+"") ) {
                    existingMovies.add(newRecord);
                // otherwise, add line as it is to array of movies that haven't been edited
                } else {
                    existingMovies.add(line);
                }
            } readFile.close();
        } catch (IOException e) {System.out.println("ERROR: movies.txt can't be found!\n");}
        try( BufferedWriter bw = new BufferedWriter( new FileWriter("movies.txt", false) ) ) {
            // write newly-created array back to text file, replacing entire contents of previous text file
            for(int i = 0; i < existingMovies.size(); i++) {bw.write(existingMovies.get(i)+"\n");}
            bw.close();
        } catch (IOException ex) {Logger.getLogger(MovieManagementApp.class.getName()).log(Level.SEVERE, null, ex);}
    } // end of editMovie method
    
    // gets directories from Movies text file, to display upon opening
    private void getDefaultDirectories(DefaultListModel list) {
        String line;
        try( BufferedReader readFile = new BufferedReader( new FileReader("movies.txt") ) ) {
            while( ( line = readFile.readLine() ) != null ) {
                String[] movie = line.split("  >>> "); // split line of data to get individual data
                // if list box is empty, add movie file's directory to list box
                if( list.isEmpty() ) {list.addElement(movie[1]);}
                boolean unique = true;
                // loop through list box to check if directory matches movie file's directory
                for(int i = 0; i < list.size(); i++) {
                    // if match is found, directory isn't unique, so break operation
                    if( movie[1].equals( list.get(i) ) ) {unique = false; break;}
                }
                // if movie file's directory is unique, , add it to list box
                if(unique) {list.addElement(movie[1]);}
            } readFile.close();
        } catch (IOException e) {System.out.println("ERROR: movies.txt can't be found!\n");}
    } // end of getDefaultDirectories method
    
    // adds directory to the list after user selection
    class AddDirectory implements ActionListener {
        JList component;
        DefaultListModel list;
        public AddDirectory(JList component, DefaultListModel list) {
            this.component = component;
            this.list = list;
        }
        @Override public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser("C:\\"); // set C-drive as default chooser directory
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // only allow directories to be selected
            int selected = fc.showOpenDialog(null);
            if(selected == 0) {
                list.addElement( fc.getSelectedFile().getAbsoluteFile() ); // get full folder path
                component.setSelectedIndex( component.getLastVisibleIndex() ); // set added list element as selected
            }
        }
    } // end of AddDirectory class
    
    // scans selected directory for files not in Movies text file
    class UpdateDirectory implements ActionListener {
        JList list;
        public UpdateDirectory(JList list) {this.list = list;}
        @Override public void actionPerformed(ActionEvent e) {
            if(list.getSelectedValue() != null) {
                int option = JOptionPane.showConfirmDialog(
                        null,
                        "This could take a while.\n\nAre you sure you want to proceed?\n ",
                        "Update directory",
                        JOptionPane.YES_NO_OPTION
                );
                try {
                    if(option == 0) {
                        // scan files to update directory
                        int filesAdded = scanFiles( list.getSelectedValue().toString() );
                        String message;
                        // check variable to determine message to output to user
                        switch (filesAdded) {
                            case -1: message = "ERROR: directory not found!"; break;
                            case  0: message = "Scan complete!\n\nThere were no files to add...\n "; break;
                            case  1: message = "Scan complete!\n\n1 new file was added...\n "; break;
                            default: message = "Scan complete!\n\n" + filesAdded + " new files were added...\n "; break;
                        }
                        JOptionPane.showMessageDialog(null, message);
                    }
                } catch (IOException ex){Logger.getLogger( MovieManagementApp.class.getName() ).log(Level.SEVERE, null, ex);}
            }
        }
    } // end of UpdateDirectory class
    
    // displays confirmation dialog, before running method to remove directory's files from Movies text file
    class RemoveDirectory implements ActionListener {
        JList component;
        DefaultListModel list;
        public RemoveDirectory(JList component, DefaultListModel list) {
            this.component = component;
            this.list = list;
        }
        @Override public void actionPerformed(ActionEvent e) {
            if(component.getSelectedValue() != null) {
                int option = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to remove this directory?\n\nAll movies will be deleted from the local file...\n ",
                        "Remove directory",
                        JOptionPane.YES_NO_OPTION
                );
                if(option == 0) {
                    // remove directory's files from Movies text file
                    removeDirectoryFiles( component.getSelectedValue().toString() );
                    list.removeElementAt( component.getSelectedIndex() );
                }
            }
        }
    } // end of RemoveDirectory class
    
    // removes selected directory's files from Movies text file
    private void removeDirectoryFiles(String directory) {
        ArrayList<String> remainingMovies = new ArrayList<>(); // array containing lines to be re-added to Movies text file
        String line;
        try( BufferedReader readFile = new BufferedReader( new FileReader("movies.txt") ) ) {
            while( ( line = readFile.readLine() ) != null ) {
                String[] movie = line.split("  >>> "); // split line of data to get individual data
                // if file from Movies file has passed directory, DON'T add it to arry
                if( !movie[1].equals(directory) ) {remainingMovies.add(line);}
            } readFile.close();
        } catch (IOException e) {System.out.println("ERROR: movies.txt can't be found!\n");}
        try( BufferedWriter bw = new BufferedWriter( new FileWriter("movies.txt", false) ) ) {
            for(int i = 0; i < remainingMovies.size(); i++) {
                bw.write(remainingMovies.get(i)+"\n"); // add array of files back to text file
            }
            bw.close();
        } catch (IOException ex) {Logger.getLogger(MovieManagementApp.class.getName()).log(Level.SEVERE, null, ex);}
    } // end of removeDirectoryFiles method
    
    // scans directory's files, to use filename and year for movie search
    private int scanFiles(String directory) throws IOException {
        int filesAdded = 0;
        String filePath = directory; // set file path via passed directory
        try {
            File movieFile = new File(filePath); // get list of files in directory
            List<String> fileList = Arrays.asList( movieFile.list() ); // convert list of files into array list
//            String[] fileList = {"Back.to.the.Future.II.1988.1080p.mkv"}; // COMMENTED OUT -- used for testing purposes
            for(String file : fileList) {
                // if filename matches any of the following, iterate the loop instead of proceeding through the code
                if( file.equals("System Volume Information") || file.equals("$RECYCLE.BIN") || file.equals("Subtitles") || file.equals("desktop.ini") ) {continue;}
                String fileName = file; // construct filename variable before file variable is changed
                file = file.substring( 0, file.lastIndexOf(".") ); // remove file extension from file variable
                Pattern pattern = Pattern.compile("\\d\\d\\d\\d"); // pattern for four consecutive digits
                // search file using pattern, to try and find the year
                Matcher match = pattern.matcher(file);
                boolean yearFound = match.find();
                String fileYear;
                if(yearFound) {fileYear = match.group(0);} else {fileYear = "0000";} // set year to 0000 if not found
                // if four digits are first four characters of filename, do the following:
                if( file.substring(0,4).equals(fileYear) ) {
                    String filePlaceholder = fileYear; // store year in placeholder variable
                    fileYear = file.substring(5,9); // set year to match digits that follow
                    file = filePlaceholder; // replace file name with year
                // otherwise, set file to match text that precedes found year
                } else if( !fileYear.equals("0000") ) {file = file.substring(0, file.indexOf(fileYear)-1);}
                file = file.replace(".", "+"); // replace filename dots with plus symbols, for TMDb search
                file = file.replace(" ", "+"); // replace filename spaces with plus symbols, for TMDb search
                // count number of files added to text file, to display to user
                if( checkFileUnique(filePath, fileName) ) {
                    if( searchMovie(file, filePath, fileName, fileYear) ) {filesAdded++;}
                }
            }
        } catch(NullPointerException e) {
            System.out.println("ERROR: directory not found!\n");
            filesAdded = -1;
        }
        return filesAdded; // return number of files added, returning -1 if passed directory wasn't found
    } // end of scanFiles method
    
    // checks if file is already in Movies text file, to avoid unnecessary re-scanning
    private boolean checkFileUnique(String filePath, String fileName) {
        boolean unique = true;
        String line;
        try( BufferedReader readFile = new BufferedReader( new FileReader("movies.txt") ) ) {
            // loop through Movies text file, checking its data
            while( ( line = readFile.readLine() ) != null ) {
                String[] movie = line.split("  >>> "); // split line of data to get individual data
                String fileNameFull = movie[1] + "\\" + movie[2]; // construct full filename from path and file's name
                // if text file record's filename matches passed filename, it isn't unique, so break operation
                if( fileNameFull.equals(filePath+"\\"+fileName) ) {unique = false; break;}
            } readFile.close();
        } catch (IOException e) {System.out.println("ERROR: movies.txt can't be found!\n");}
        return unique;
    } // end of checkFileUnique method
    
    // unused method for searching for individual movies, to display multiple results
    // this method will be integrated with later features
    private void searchMovie(String movie) throws MalformedURLException, IOException {
        String searchResults = performSearch(movie); // get search results
        if(searchResults != null) {
            // prepare results to be split
            searchResults = searchResults.substring(searchResults.indexOf("\"results\":[")+11, searchResults.length()-3);
            searchResults = searchResults.replace("}, ", "} "); // replace potentially harmful character combination
            if( !searchResults.isEmpty() ) {
                String[] resultArray = searchResults.split("},"); // split result into different movies
                 // get Movie object by processing each movie result
                for(String result : resultArray) {Movie processedMovie = processResult(result);}
            }
        }
    } // end of searchMovie method
    
    // combs results from performSearch to find best movie match
    // once match is found, it's written to file, and poster is downloaded, both via other methods
    private boolean searchMovie(String movie, String filePath, String fileName, String fileYear) throws MalformedURLException, IOException {
        // variables to be populated as matches are found
        String matchedId = "", matchedTitle = "", matchedDate = "", matchedRating = "", matchedVotes = "", matchedPoster = "", matchedDirector = "";
        boolean firstEntry = false, matchedAdjacent = false; // variables to help get best match
        String searchResults = performSearch(movie); // get search results
        if(searchResults != null) {
            // prepare results to be split
            searchResults = searchResults.substring(searchResults.indexOf("\"results\":[")+11, searchResults.length()-3);
            searchResults = searchResults.replace("}, ", "} "); // replace potentially harmful character combination
            if( !searchResults.isEmpty() ) {
                String[] resultArray = searchResults.split("},"); // split result into different movies
                for(String result : resultArray) {
                    Movie processedMovie = processResult(result); // get Movie object by processing movie result
                    // get variables from processed movie
                    String id = processedMovie.getId();
                    String title = processedMovie.getTitle();
                    String date = processedMovie.getDate();
                    String year = processedMovie.getYear();
                    String rating = processedMovie.getRating();
                    String votes = processedMovie.getVotes();
                    String poster = processedMovie.getPoster();
                    String director = processedMovie.getDirector();
                    System.out.println(id + ", " + title + ", " + date + ", " + rating + ", " + votes + ", " + director);
                    
                    // if search result is first, store details as a match, and continue searching
                    if(!firstEntry) {
                        matchedId = id;
                        matchedTitle = title;
                        matchedDate = date;
                        matchedRating = rating;
                        matchedVotes = votes;
                        matchedPoster = poster;
                        matchedDirector = director;
                        firstEntry = true;
                    }
                    // set variables to allow adjacent years to be matched
                    String fileYearPlus = ""+(parseInt(fileYear)+1), fileYearMinus = ""+(parseInt(fileYear)-1);
                    // if year is perfect match, set variables and break operation, as match is deemed found
                    if( year.equals(fileYear) ) {
                        matchedId = id;
                        matchedTitle = title;
                        matchedDate = date;
                        matchedRating = rating;
                        matchedVotes = votes;
                        matchedPoster = poster;
                        matchedDirector = director;
                        break;
                    // if year is close match, set variables, but continue searching
                    } else if( ( year.equals(fileYearPlus) || year.equals(fileYearMinus) ) && !matchedAdjacent ) {
                        matchedId = id;
                        matchedTitle = title;
                        matchedDate = date;
                        matchedRating = rating;
                        matchedVotes = votes;
                        matchedPoster = poster;
                        matchedDirector = director;
                        matchedAdjacent = true;
                    }
                }
            }
            
            System.out.println("--------------------------------------------------------------------------------------------"
                    + "\nFilename: " + fileName
                    +       "\nID: " + matchedId
                    +    "\nTitle: " + matchedTitle
                    +     "\nDate: " + matchedDate
                    +   "\nRating: " + matchedRating
                    +    "\nVotes: " + matchedVotes
                    + "\nDirector: " + matchedDirector
                    + "\n--------------------------------------------------------------------------------------------"
            );
            writeMovie(matchedId, filePath, fileName, matchedTitle, matchedDate, matchedRating, matchedVotes, matchedDirector);
            downloadPoster(matchedId, matchedPoster);
            return true;
        } else {return false;}
    }
    
    // searches TMDb for movie records, returning results as a string
    private String performSearch(String movie) throws MalformedURLException, IOException {
        // perform search at TMDb, using API key and passed movie string
        URL url = new URL("http://api.themoviedb.org/3/search/movie?api_key="+API_KEY+"&append_to_response=credits&query="+movie);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
        String searchResults = br.readLine();
        System.out.println(searchResults);
        // check if search results are empty
        if( searchResults.equals("{\"page\":1,\"total_results\":0,\"total_pages\":0,\"results\":[]}") ) {
            System.out.println("no matching movies found on TMDb!");
            searchResults = null;
        }
        return searchResults;
    } // end of performSearch method
    
    // searches TMDb result for data that'll need storing, temporarily stored in Movie object
    private Movie processResult(String result) throws MalformedURLException, IOException {
        result = result.substring(2); // remove unwanted characters from start of result
        result = result.replace(",\",\"", "\",\""); // replace potentially harmful character combination
        String[] resultDetails = result.split(",\""); // split result by different details
        Map<String, String> resultDetailsMap = new HashMap<>(); // map to store json keys and values
        // store key and value from each detail in map
        for(String detail : resultDetails) {
            String detailKey = detail.substring( 0, detail.indexOf("\":") );
            String detailValue = detail.substring(detail.indexOf("\":")+2);
            resultDetailsMap.put(detailKey, detailValue);
        }
        
        // variables to be populated via results
        String id = "", title = "", date = "", year = "", rating = "", votes = "", poster = "", director = "";
        if( resultDetailsMap.containsKey("id") ) {id = resultDetailsMap.get("id");} // get id
        if( resultDetailsMap.containsKey("title") ) {
            title = resultDetailsMap.get("title");
            title = title.substring(1, title.length()-1); // get title, removing unwanted start and end characters
        }
        if( resultDetailsMap.containsKey("release_date") && !resultDetailsMap.get("release_date").equals("\"\"") ) {
            date = resultDetailsMap.get("release_date");
            year = date.substring(1, date.length()-7); // get year from the date
            date = date.substring(1, date.length()-1); // remove unwanted start and end characters to get date
        }
        // get vote details and poster path
        if( resultDetailsMap.containsKey("vote_average") ) {rating = resultDetailsMap.get("vote_average");}
        if( resultDetailsMap.containsKey("vote_count") ) {votes = resultDetailsMap.get("vote_count");}
        if( resultDetailsMap.containsKey("poster_path") ) {poster = resultDetailsMap.get("poster_path");}

        // search movie's credits on TMDb, to get movie director
        URL url = new URL("https://api.themoviedb.org/3/movie/"+id+"/credits?api_key="+API_KEY);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
            String crewData = br.readLine();
            crewData = crewData.substring(crewData.indexOf("\"crew\":[")+8, crewData.length()-2);

            if( !crewData.isEmpty() ) {
                String[] crewMembers = crewData.split("},");
                for(String member : crewMembers) {
                    member = member.substring(2);
                    String[] memberDetails = member.split(",\"");
                    Map<String, String> memberDetailsMap = new HashMap<>();
                    for(String detail : memberDetails) {
                        String detailKey =  detail.substring( 0, detail.indexOf("\":") );
                        String detailValue = detail.substring(detail.indexOf("\":")+2);
                        memberDetailsMap.put(detailKey, detailValue);
                    }
                    if( director.equals("") && memberDetailsMap.containsKey("job") && memberDetailsMap.get("job").equals("\"Director\"") ) {
                        director = memberDetailsMap.get("name");
                        director = director.substring(1, director.length()-1);
                    }
                }
            }
        } catch (FileNotFoundException e) {System.out.println("ERROR: TMDb record not found!\n");}
        // create and return Movie object, containing fetched movie data
        Movie movie = new Movie(id, title, date, year, rating, votes, poster, director);
        return movie;
    } // end of processResult method
    
    // writes found movie to text file
    private void writeMovie(String id, String path, String filename, String title, String date, String rating, String votes, String director) {
        try( BufferedWriter bw = new BufferedWriter( new FileWriter("movies.txt", true) ) ) {
            // write found movie details to text file, preserving chosen format
            bw.write(
                id + "  >>> "
                + path + "  >>> "
                + filename + "  >>> "
                + title + "  >>> "
                + date + "  >>> "
                + rating +  "  >>> "
                + votes + "  >>> "
                + director + "  >>> "
                + "false  >>> " // got apple
                + "false  >>> " // watched
                + "0  >>> " // rating
                + "-" // notes
            );
            bw.newLine();
            bw.close();
        } catch (IOException ex) {Logger.getLogger(MovieManagementApp.class.getName()).log(Level.SEVERE, null, ex);}
    } // end of writeMovie method
    
    // downloads found movie poster from web, to local directory
    private void downloadPoster(String id, String url) {
        url = url.substring(2, url.length()-1); // remove unnecessary characters from fetched url
        String filetype = url.substring( url.lastIndexOf(".") ); // get filetype from url
        url = "https://image.tmdb.org/t/p/w500"+url; // construct complete url string
        try (BufferedInputStream in = new BufferedInputStream( new URL(url).openStream() );
        // download specified file to posters folder
        FileOutputStream fo = new FileOutputStream("posters/"+id+filetype)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ( (bytesRead = in.read( dataBuffer, 0, 1024) ) != -1 ) {
                fo.write(dataBuffer, 0, bytesRead);
            } fo.close();
	} catch (IOException e) {System.out.println("Error!\n"+e);}
    } // end of downloadPoster method
    
    // custom button class, for aesthetic changes
    class DefaultButton extends JButton {
        public DefaultButton(String text) {
            super.setText(text);
            super.setBorder( new LineBorder(Color.BLACK, 0, false) );
            super.setFont(new Font(FONT_DEFAULT, Font.BOLD, 14));
            super.setBackground(Color.BLACK);
            super.setForeground(Color.WHITE);
            super.setOpaque(true);
            super.addMouseListener( new ButtonHover() );
        }
        class ButtonHover extends MouseAdapter {
            @Override public void mouseEntered(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(COLOR_BLACK_OFF);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override public void mouseExited(MouseEvent e) {
                JButton button = (JButton) e.getSource();
                button.setBackground(Color.BLACK);
            }
        }
    } // end of DefaultButton class
    
    // main method
    public static void main(String[] args) throws IOException {new MovieManagementApp();} // end of main method
}