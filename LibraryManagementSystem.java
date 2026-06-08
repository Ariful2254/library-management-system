package librarymanagementsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import net.proteanit.sql.DbUtils; 

public class LibraryManagementSystem {

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);   
    private static final Color ACCENT_COLOR = new Color(39, 174, 96);     
    private static final Color DANGER_COLOR = new Color(192, 57, 43);    
    private static final Color SIDEBAR_BG = new Color(44, 62, 80);       
    private static final Color BG_LIGHT = new Color(245, 247, 250);     
    private static final Color TEXT_DARK = new Color(52, 73, 94);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/LIBRARY", "root", "");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e);
            return null;
        }
    }

    private static void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE); 
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); 
        button.setOpaque(true);             
        button.setBorder(new LineBorder(bgColor.darker(), 1, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private static void styleField(JTextField field) {
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    public static void login() {
        JFrame f = new JFrame("Library Management System - Login");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(450, 300);
        f.setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Library Management System", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        title.setBounds(50, 20, 350, 30);
        mainPanel.add(title);

        JLabel l1 = new JLabel("Username");
        l1.setFont(BUTTON_FONT);
        l1.setBounds(50, 80, 100, 30);
        mainPanel.add(l1);

        JTextField F_user = new JTextField();
        F_user.setBounds(150, 80, 230, 30);
        styleField(F_user);
        mainPanel.add(F_user);

        JLabel l2 = new JLabel("Password");
        l2.setFont(BUTTON_FONT);
        l2.setBounds(50, 130, 100, 30);
        mainPanel.add(l2);

        JPasswordField F_pass = new JPasswordField();
        F_pass.setBounds(150, 130, 230, 30);
        styleField(F_pass);
        mainPanel.add(F_pass);

        JButton login_but = new JButton("Login");
        login_but.setBounds(150, 190, 110, 35);
        styleButton(login_but, PRIMARY_COLOR);
        
        login_but.addActionListener(e -> {
            String username = F_user.getText();
            String password = new String(F_pass.getPassword());

            if(username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection con = connect()) {
                String query = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String admin = rs.getString("ADMIN");
                    String uid = rs.getString("UID");
                    f.dispose();
                    new MainDashboard(username, uid, admin.equals("1"));
                } else {
                    JOptionPane.showMessageDialog(f, "Wrong Username or Password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        mainPanel.add(login_but);

        JButton reg_but = new JButton("Register");
        reg_but.setBounds(270, 190, 110, 35);
        styleButton(reg_but, ACCENT_COLOR);
        reg_but.addActionListener(e -> registerWindow());
        mainPanel.add(reg_but);

        f.add(mainPanel);
        f.setVisible(true);
    }

    public static void registerWindow() {
        JFrame g = new JFrame("User Registration");
        g.setSize(400, 320);
        g.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(null);
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(SUBTITLE_FONT);
        title.setForeground(TEXT_DARK);
        title.setBounds(30, 15, 320, 25);
        panel.add(title);

        JLabel l1 = new JLabel("Username");
        l1.setFont(BUTTON_FONT);
        l1.setBounds(40, 65, 100, 30);
        panel.add(l1);

        JTextField F_user = new JTextField();
        F_user.setBounds(140, 65, 200, 30);
        styleField(F_user);
        panel.add(F_user);

        JLabel l2 = new JLabel("Password");
        l2.setFont(BUTTON_FONT);
        l2.setBounds(40, 115, 100, 30);
        panel.add(l2);

        JPasswordField F_pass = new JPasswordField();
        F_pass.setBounds(140, 115, 200, 30);
        styleField(F_pass);
        panel.add(F_pass);

        JRadioButton a1 = new JRadioButton("Admin");
        a1.setBounds(140, 160, 80, 30);
        a1.setBackground(BG_LIGHT);
        JRadioButton a2 = new JRadioButton("User");
        a2.setBounds(230, 160, 80, 30);
        a2.setBackground(BG_LIGHT);
        a2.setSelected(true);
        ButtonGroup bg = new ButtonGroup(); bg.add(a1); bg.add(a2);
        panel.add(a1); panel.add(a2);

        JButton submit_but = new JButton("Sign Up");
        submit_but.setBounds(140, 210, 200, 35);
        styleButton(submit_but, ACCENT_COLOR);
        
        submit_but.addActionListener(e -> {
            String username = F_user.getText();
            String password = new String(F_pass.getPassword());
            int adminVal = a1.isSelected() ? 1 : 0;

            if(username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(g, "Please fill all fields!");
                return;
            }

            try (Connection con = connect()) {
                String query = "INSERT INTO USERS(USERNAME,PASSWORD,ADMIN) VALUES (?,?,?)";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setInt(3, adminVal);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(g, "Registration Successful!");
                g.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(g, "Error: " + ex.getMessage());
            }
        });
        panel.add(submit_but);

        g.add(panel);
        g.setVisible(true);
    }

    static class MainDashboard extends JFrame {
        private CardLayout cardLayout = new CardLayout();
        private JPanel contentPanel = new JPanel(cardLayout);
        private String currentUid;

        public MainDashboard(String username, String uid, boolean isAdmin) {
            super("Library Management System - Dashboard");
            this.currentUid = uid;
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1050, 650);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(SIDEBAR_BG);
            sidebar.setPreferredSize(new Dimension(220, 650));
            sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

            JLabel lblUser = new JLabel(username, SwingConstants.CENTER);
            lblUser.setFont(BUTTON_FONT);
            lblUser.setForeground(Color.WHITE);
            lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(lblUser);
            sidebar.add(Box.createVerticalStrut(30));

            if (isAdmin) {
                createAdminCards(sidebar);
            } else {
                createUserCards(sidebar);
            }

            sidebar.add(Box.createVerticalGlue());
            JButton btnLogout = new JButton("Logout");
            styleButton(btnLogout, DANGER_COLOR);
            btnLogout.setMaximumSize(new Dimension(200, 35));
            btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnLogout.addActionListener(e -> {
                this.dispose();
                LibraryManagementSystem.login();
            });
            sidebar.add(btnLogout);

            add(sidebar, BorderLayout.WEST);
            add(contentPanel, BorderLayout.CENTER);
            setVisible(true);
        }

        private void addNavButton(String text, String cardName, JPanel sidebar) {
            JButton btn = new JButton(text);
            styleButton(btn, PRIMARY_COLOR);
            btn.setMaximumSize(new Dimension(200, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(10));
        }

        private void createAdminCards(JPanel sidebar) {
            addNavButton("Home Menu", "Home", sidebar);
            addNavButton("View All Books", "ViewBooks", sidebar);
            addNavButton("View Users", "ViewUsers", sidebar);
            addNavButton("Issued History", "ViewIssued", sidebar);
            addNavButton("Add New Book", "AddBook", sidebar);
            addNavButton("Delete a Book", "DeleteBook", sidebar); 
            addNavButton("Issue a Book", "IssueBook", sidebar);
            addNavButton("Return Book", "ReturnBook", sidebar);

            JPanel home = createCardPanel();
            JLabel welcome = new JLabel("Welcome Back Admin!", SwingConstants.CENTER);
            welcome.setFont(TITLE_FONT);
            welcome.setForeground(PRIMARY_COLOR);
            home.add(welcome, BorderLayout.CENTER);
            contentPanel.add(home, "Home");

            // এডিটেড সার্চ ড্যাশবোর্ড প্যানেল (isBookTable = true)
            contentPanel.add(createTablePanel("SELECT BID, BNAME, AUTHOR, GENRE, PRICE FROM BOOKS", "All Books Registered in Library", true), "ViewBooks");
            contentPanel.add(createTablePanel("SELECT UID, USERNAME, ADMIN FROM USERS", "Registered Users List", false), "ViewUsers");
            contentPanel.add(createTablePanel("SELECT * FROM ISSUED", "Complete Book Issue & Return Records", false), "ViewIssued");
            contentPanel.add(createAddBookForm(), "AddBook");
            contentPanel.add(createDeleteBookForm(), "DeleteBook"); 
            contentPanel.add(createIssueBookForm(), "IssueBook");
            contentPanel.add(createReturnBookForm(), "ReturnBook");
        }

        private void createUserCards(JPanel sidebar) {
            addNavButton("Home Menu", "Home", sidebar);
            addNavButton("View All Books", "Browse", sidebar);
            addNavButton("My Issued Books", "MyIssued", sidebar);

            JPanel home = createCardPanel();
            JLabel welcome = new JLabel("Welcome to Library Dashboard!", SwingConstants.CENTER);
            welcome.setFont(TITLE_FONT);
            welcome.setForeground(ACCENT_COLOR);
            home.add(welcome, BorderLayout.CENTER);
            contentPanel.add(home, "Home");

            contentPanel.add(createTablePanel("SELECT BID, BNAME, AUTHOR, GENRE, PRICE FROM BOOKS", "Available Books Catalogue", true), "Browse");
            contentPanel.add(createTablePanel("SELECT IID, BID, ISSUED_DATE, RETURN_DATE, PERIOD, FINE FROM ISSUED WHERE UID=" + currentUid, "My Personally Issued Books", false), "MyIssued");
        }

        private JPanel createCardPanel() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(BG_LIGHT);
            p.setBorder(new EmptyBorder(25, 25, 25, 25));
            return p;
        }

      
        private JPanel createTablePanel(String baseQuery, String tableTitle, boolean isBookTable) {
            JPanel mainPanel = createCardPanel();
            mainPanel.setLayout(new BorderLayout());
       
       
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(BG_LIGHT);
            
            JLabel lblTableTitle = new JLabel(tableTitle);
            lblTableTitle.setFont(SUBTITLE_FONT);
            lblTableTitle.setForeground(TEXT_DARK);
            lblTableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); 
            topPanel.add(lblTableTitle, BorderLayout.NORTH);
            
            JTable table = new JTable();
            table.setRowHeight(30); 
            table.setFont(MAIN_FONT);

            
            java.util.function.Consumer<String> loadData = (targetQuery) -> {
                try (Connection con = connect(); Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery(targetQuery);
                    table.setModel(DbUtils.resultSetToTableModel(rs));
                    
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
                    int totalCalculatedWidth = 0;
                    int tableVisibleWidth = 800; 

                    for (int column = 0; column < table.getColumnCount(); column++) {
                        TableColumn tableColumn = table.getColumnModel().getColumn(column);
                        int preferredWidth = tableColumn.getMinWidth();

                        Object headerValue = tableColumn.getHeaderValue();
                        if (headerValue != null) {
                            preferredWidth = Math.max(preferredWidth, 
                                table.getTableHeader().getFontMetrics(table.getTableHeader().getFont()).stringWidth(headerValue.toString()) + 40);
                        }

                        for (int row = 0; row < table.getRowCount(); row++) {
                            Object value = table.getValueAt(row, column);
                            if (value != null) {
                                preferredWidth = Math.max(preferredWidth, 
                                    table.getFontMetrics(table.getFont()).stringWidth(value.toString()) + 35);
                            }
                        }
                        tableColumn.setPreferredWidth(preferredWidth);
                        totalCalculatedWidth += preferredWidth;
                    }
                    
                    if (totalCalculatedWidth < tableVisibleWidth) {
                        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
                    } else {
                        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            };

         
            loadData.accept(baseQuery);

            if (isBookTable) {
                JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
                searchBarPanel.setBackground(BG_LIGHT);

                JLabel lblSearch = new JLabel("Search Book (Name/ID): ");
                lblSearch.setFont(BUTTON_FONT);
                searchBarPanel.add(lblSearch);

                JTextField txtSearch = new JTextField();
                txtSearch.setPreferredSize(new Dimension(250, 30));
                styleField(txtSearch);
                searchBarPanel.add(txtSearch);

                searchBarPanel.add(Box.createHorizontalStrut(10));

                JButton btnSearch = new JButton("Search");
                btnSearch.setPreferredSize(new Dimension(90, 30));
                styleButton(btnSearch, PRIMARY_COLOR);
                
                
                btnSearch.addActionListener(e -> {
                    String keyword = txtSearch.getText().trim();
                    if (keyword.isEmpty()) {
                        loadData.accept(baseQuery); 
                    } else {
                     
                        String searchQuery = "SELECT BID, BNAME, AUTHOR, GENRE, PRICE FROM BOOKS WHERE BID LIKE '%" 
                                              + keyword + "%' OR BNAME LIKE '%" + keyword + "%' OR AUTHOR LIKE '%" + keyword + "%'";
                        loadData.accept(searchQuery);
                    }
                });
                searchBarPanel.add(btnSearch);

                JButton btnReset = new JButton("Reset");
                btnReset.setPreferredSize(new Dimension(80, 30));
                styleButton(btnReset, TEXT_DARK);
                btnReset.addActionListener(e -> {
                    txtSearch.setText("");
                    loadData.accept(baseQuery);
                });
                searchBarPanel.add(Box.createHorizontalStrut(5));
                searchBarPanel.add(btnReset);

                topPanel.add(searchBarPanel, BorderLayout.CENTER);
            }

            mainPanel.add(topPanel, BorderLayout.NORTH);

            JScrollPane scroll = new JScrollPane(table);
            mainPanel.add(scroll, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
            bottomPanel.setBackground(BG_LIGHT);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            JButton btnRefresh = new JButton("Refresh Data");
            styleButton(btnRefresh, ACCENT_COLOR);
            btnRefresh.setPreferredSize(new Dimension(120, 30)); 
            btnRefresh.addActionListener(e -> {
                if(isBookTable) loadData.accept(baseQuery);
                else loadData.accept(baseQuery);
            });
            
            bottomPanel.add(btnRefresh);
            mainPanel.add(bottomPanel, BorderLayout.SOUTH);

            return mainPanel;
        }

        private JPanel createAddBookForm() {
            JPanel panel = createCardPanel();
            panel.setLayout(null);

            JLabel title = new JLabel("Add New Book Record");
            title.setFont(SUBTITLE_FONT); title.setBounds(50, 20, 300, 30); panel.add(title);

            JLabel l1 = new JLabel("Book Name:"); l1.setBounds(50, 80, 100, 30); panel.add(l1);
            JTextField fName = new JTextField(); fName.setBounds(160, 80, 250, 30); styleField(fName); panel.add(fName);

            JLabel lAuth = new JLabel("Author Name:"); lAuth.setBounds(50, 130, 100, 30); panel.add(lAuth);
            JTextField fAuthor = new JTextField(); fAuthor.setBounds(160, 130, 250, 30); styleField(fAuthor); panel.add(fAuthor);

            JLabel l2 = new JLabel("Genre / Tag:"); l2.setBounds(50, 180, 100, 30); panel.add(l2);
            JTextField fGenre = new JTextField(); fGenre.setBounds(160, 180, 250, 30); styleField(fGenre); panel.add(fGenre);

            JLabel l3 = new JLabel("Price:"); l3.setBounds(50, 230, 100, 30); panel.add(l3);
            JTextField fPrice = new JTextField(); fPrice.setBounds(160, 230, 250, 30); styleField(fPrice); panel.add(fPrice);

            JButton btn = new JButton("Save Book"); btn.setBounds(160, 290, 150, 35); styleButton(btn, ACCENT_COLOR);
            btn.addActionListener(e -> {
                if(fName.getText().isEmpty() || fAuthor.getText().isEmpty() || fGenre.getText().isEmpty() || fPrice.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields!");
                    return;
                }
                try (Connection con = connect()) {
                    String q = "INSERT INTO BOOKS(BNAME,AUTHOR,GENRE,PRICE) VALUES (?,?,?,?)";
                    PreparedStatement pst = con.prepareStatement(q);
                    pst.setString(1, fName.getText());
                    pst.setString(2, fAuthor.getText()); 
                    pst.setString(3, fGenre.getText());
                    pst.setInt(4, Integer.parseInt(fPrice.getText()));
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book Saved Successfully!");
                    fName.setText(""); fAuthor.setText(""); fGenre.setText(""); fPrice.setText("");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            });
            panel.add(btn);
            return panel;
        }

        private JPanel createDeleteBookForm() {
            JPanel panel = createCardPanel();
            panel.setLayout(null);

            JLabel title = new JLabel("Delete Book from Records");
            title.setFont(SUBTITLE_FONT); title.setBounds(50, 20, 300, 30); panel.add(title);

            JLabel l1 = new JLabel("Book ID (BID):"); l1.setBounds(50, 80, 120, 30); panel.add(l1);
            JTextField fBid = new JTextField(); fBid.setBounds(180, 80, 220, 30); styleField(fBid); panel.add(fBid);

            JButton btnDelete = new JButton("Delete Book"); btnDelete.setBounds(180, 140, 150, 35); styleButton(btnDelete, DANGER_COLOR);
            btnDelete.addActionListener(e -> {
                String bid = fBid.getText();
                if(bid.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter Book ID!");
                    return;
                }
                
                int dialogResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Book ID: " + bid + "?", "Warning", JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION) {
                    try (Connection con = connect()) {
                        String q = "DELETE FROM BOOKS WHERE BID=?";
                        PreparedStatement pst = con.prepareStatement(q);
                        pst.setInt(1, Integer.parseInt(bid));
                        int rows = pst.executeUpdate();
                        
                        if(rows > 0) {
                            JOptionPane.showMessageDialog(this, "Book Deleted Successfully!");
                            fBid.setText("");
                        } else {
                            JOptionPane.showMessageDialog(this, "Book ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
                }
            });
            panel.add(btnDelete);
            return panel;
        }

        private JPanel createIssueBookForm() {
            JPanel panel = createCardPanel();
            panel.setLayout(null);

            JLabel title = new JLabel("Issue Book to Member");
            title.setFont(SUBTITLE_FONT); title.setBounds(50, 20, 300, 30); panel.add(title);

            JLabel l1 = new JLabel("Book ID (BID):"); l1.setBounds(50, 70, 120, 30); panel.add(l1);
            JTextField fBid = new JTextField(); fBid.setBounds(180, 70, 220, 30); styleField(fBid); panel.add(fBid);

            JLabel l2 = new JLabel("User ID (UID):"); l2.setBounds(50, 120, 120, 30); panel.add(l2);
            JTextField fUid = new JTextField(); fUid.setBounds(180, 120, 220, 30); styleField(fUid); panel.add(fUid);

            JLabel l3 = new JLabel("Period (Days):"); l3.setBounds(50, 170, 120, 30); panel.add(l3);
            JTextField fPeriod = new JTextField(); fPeriod.setBounds(180, 170, 220, 30); styleField(fPeriod); panel.add(fPeriod);

            JLabel l4 = new JLabel("Date (dd-MM-yyyy):"); l4.setBounds(50, 220, 130, 30); panel.add(l4);
            JTextField fDate = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date())); 
            fDate.setBounds(180, 220, 220, 30); styleField(fDate); panel.add(fDate);

            JButton btn = new JButton("Confirm Issue"); btn.setBounds(180, 280, 150, 35); styleButton(btn, ACCENT_COLOR);
            btn.addActionListener(e -> {
                try (Connection con = connect()) {
                    String q = "INSERT INTO ISSUED(UID,BID,ISSUED_DATE,PERIOD) VALUES (?,?,?,?)";
                    PreparedStatement pst = con.prepareStatement(q);
                    pst.setInt(1, Integer.parseInt(fUid.getText()));
                    pst.setInt(2, Integer.parseInt(fBid.getText()));
                    pst.setString(3, fDate.getText());
                    pst.setInt(4, Integer.parseInt(fPeriod.getText()));
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book successfully issued!");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error processing: " + ex.getMessage()); }
            });
            panel.add(btn);
            return panel;
        }

        private JPanel createReturnBookForm() {
            JPanel panel = createCardPanel();
            panel.setLayout(null);

            JLabel title = new JLabel("Return Books");
            title.setFont(SUBTITLE_FONT); title.setBounds(50, 20, 300, 30); panel.add(title);

            JLabel l1 = new JLabel("Issue ID (IID):"); l1.setBounds(50, 80, 120, 30); panel.add(l1);
            JTextField fIid = new JTextField(); fIid.setBounds(180, 80, 220, 30); styleField(fIid); panel.add(fIid);

            JLabel l2 = new JLabel("Return Date:"); l2.setBounds(50, 130, 120, 30); panel.add(l2);
            JTextField fReturn = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date())); 
            fReturn.setBounds(180, 130, 220, 30); styleField(fReturn); panel.add(fReturn);

            JButton btn = new JButton("Process Return"); btn.setBounds(180, 190, 150, 35); styleButton(btn, DANGER_COLOR);
            btn.addActionListener(e -> {
                String iid = fIid.getText();
                String returnDateStr = fReturn.getText();

                try (Connection con = connect()) {
                    String q1 = "SELECT ISSUED_DATE, PERIOD FROM ISSUED WHERE IID=?";
                    PreparedStatement pst1 = con.prepareStatement(q1);
                    pst1.setInt(1, Integer.parseInt(iid));
                    ResultSet rs = pst1.executeQuery();
                    
                    if(rs.next()) {
                        String issueDateStr = rs.getString("ISSUED_DATE");
                        int period = rs.getInt("PERIOD");

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date dIssue = sdf.parse(issueDateStr);
                        Date dReturn = sdf.parse(returnDateStr);

                        long diffInMillies = Math.abs(dReturn.getTime() - dIssue.getTime());
                        long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                        String q2 = "UPDATE ISSUED SET RETURN_DATE=? WHERE IID=?";
                        PreparedStatement pst2 = con.prepareStatement(q2);
                        pst2.setString(1, returnDateStr);
                        pst2.setInt(2, Integer.parseInt(iid));
                        pst2.executeUpdate();

                        if (diffDays > period) {
                            int fine = (int) (diffDays - period) * 10;
                            String q3 = "UPDATE ISSUED SET FINE=? WHERE IID=?";
                            PreparedStatement pst3 = con.prepareStatement(q3);
                            pst3.setInt(1, fine);
                            pst3.setInt(2, Integer.parseInt(iid));
                            pst3.executeUpdate();
                            JOptionPane.showMessageDialog(this, "Book Returned Late! Fine Incurred: Rs. " + fine);
                        } else {
                            JOptionPane.showMessageDialog(this, "Book Returned Successfully (No fine)!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Issue ID!");
                    }
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            });
            panel.add(btn);
            return panel;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        
        login();
    }
}