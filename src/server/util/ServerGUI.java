package server.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import server.Server;
import server.net.HostGateway;
import server.world.World;
import server.world.entity.player.Player;

/**
 * A graphical user interface that is opened on startup for server management.
 * 
 * @author lare96
 */
public final class ServerGUI {

    /** The graphical user interface window. */
    private static JFrame applet = new JFrame();

    /** The area used for printing text onto the graphical user interface. */
    private static JTextArea consoleTextArea = new JTextArea();

    /** The area used for typing onto the notepad. */
    private static JTextArea notepadTextArea = new JTextArea();

    /** The footer for the progress bar. */
    private static JTextField serverStressFooter;

    /** The footer for the statistics graph. */
    private static JTextField statisticsGraphFooter;

    /** The statistics table. */
    private static JTable statisticsTable;

    /** The server stress progress bar. */
    private static JProgressBar progressBar = new JProgressBar();

    /** The list of online players. */
    private static JList<String> playerList = new JList<String>();

    /** The area used for typing a message to send to a player. */
    private static JTextArea sendMessageTextArea = new JTextArea();;

    /**
     * Runs this graphical user interface.
     * 
     * @wbp.parser.entryPoint
     */
    public static void start() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    load();
                    prepareForActivation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Sets the contents of the graphical user interface.
     */
    @SuppressWarnings("serial")
    private static void prepareForActivation() {

        /** Creates the graphical user interface window. */
        applet.setVisible(true);
        applet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applet.setResizable(true);
        applet.setTitle("Asteria #317");
        applet.setBounds(100, 100, 450, 300);

        /** Creates the menu bar on the window. */
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(false);
        menuBar.setToolTipText("");
        applet.setJMenuBar(menuBar);

        /**
         * Creates a new button on the menu bar that allows the user to shutdown
         * the server.
         */
        JButton btnShutdown = new JButton("Shutdown");
        btnShutdown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    World.shutdown();
                } catch (InterruptedException interrupt) {
                    interrupt.printStackTrace();
                }
            }
        });
        menuBar.add(btnShutdown);
        JMenu mnGlobal = new JMenu("Global Options");
        mnGlobal.setHorizontalAlignment(SwingConstants.CENTER);
        menuBar.add(mnGlobal);

        /**
         * Creates a new menu item on the menu that allows the user to disable
         * dropping items.
         */
        JCheckBoxMenuItem chckbxmntmDisableDroppingItems = new JCheckBoxMenuItem("Disable Dropping Items");
        chckbxmntmDisableDroppingItems.setSelected(!World.isCanDrop());
        chckbxmntmDisableDroppingItems.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (World.isCanDrop()) {
                    World.setCanDrop(false);
                    Server.print("Dropping items is disabled!");
                    World.sendMessage("Dropping items has been disabled for all players!");
                } else {
                    World.setCanDrop(true);
                    Server.print("Dropping items is enabled!");
                    World.sendMessage("Dropping items has been enabled for all players!");
                }
            }
        });
        mnGlobal.add(chckbxmntmDisableDroppingItems);

        /**
         * Creates a new menu item on the menu that allows the user to disable
         * trading.
         */
        JCheckBoxMenuItem chckbxmntmDisableTrading = new JCheckBoxMenuItem("Disable Trading");
        chckbxmntmDisableTrading.setSelected(!World.isCanTrade());
        chckbxmntmDisableTrading.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (World.isCanTrade()) {
                    World.setCanTrade(false);
                    Server.print("Trading is disabled!");
                    World.sendMessage("Trading items has been disabled for all players!");
                } else {
                    World.setCanTrade(true);
                    Server.print("Trading is enabled!");
                    World.sendMessage("Trading items has been enabled for all players!");
                }
            }
        });
        mnGlobal.add(chckbxmntmDisableTrading);

        /**
         * Creates a new menu item on the menu that allows the user to disable
         * picking up items.
         */
        JCheckBoxMenuItem chckbxmntmDisablePickingUp = new JCheckBoxMenuItem("Disable Picking Up Items");
        chckbxmntmDisablePickingUp.setSelected(!World.isCanPickup());
        chckbxmntmDisablePickingUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (World.isCanPickup()) {
                    World.setCanPickup(false);
                    Server.print("Picking up items is disabled!");
                    World.sendMessage("Picking up items has been disabled for all players!");
                } else {
                    World.setCanPickup(true);
                    Server.print("Picking up items is enabled!");
                    World.sendMessage("Picking up items has been enabled for all players!");
                }
            }
        });
        mnGlobal.add(chckbxmntmDisablePickingUp);

        /**
         * Creates a new menu item on the menu that allows the user to disable
         * shopping.
         */
        JCheckBoxMenuItem chckbxmntmDisableShopping = new JCheckBoxMenuItem("Disable Shopping");
        chckbxmntmDisableShopping.setSelected(!World.isCanShop());
        chckbxmntmDisableShopping.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (World.isCanShop()) {
                    World.setCanShop(false);
                    Server.print("Shopping is disabled!");
                    World.sendMessage("Shopping has been disabled for all players!");
                } else {
                    World.setCanShop(true);
                    Server.print("Shopping is enabled!");
                    World.sendMessage("Shopping has been enabled for all players!");
                }
            }
        });
        mnGlobal.add(chckbxmntmDisableShopping);

        /**
         * Creates a new menu item on the menu that allows the user to save all
         * players currently online.
         */
        JMenuItem mntmSaveAllPlayers = new JMenuItem("Save All Players");
        mntmSaveAllPlayers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                World.savePlayers();
                World.sendMessage("All players have been forcibly saved!");
                Server.print("All players have been saved!");
            }
        });

        JSeparator separator = new JSeparator();
        mnGlobal.add(separator);
        mnGlobal.add(mntmSaveAllPlayers);
        JMenu mnNetworkActions = new JMenu("Network Options");
        mnNetworkActions.setHorizontalAlignment(SwingConstants.CENTER);
        menuBar.add(mnNetworkActions);

        /**
         * Creates a new menu item on the menu that allows the user to change
         * the server into developer mode.
         */
        JCheckBoxMenuItem chckbxmntmDeveloperMode = new JCheckBoxMenuItem("Developer Mode");
        chckbxmntmDeveloperMode.setSelected(Server.isInDeveloperMode());
        chckbxmntmDeveloperMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (Server.isInDeveloperMode()) {
                    Server.setInDeveloperMode(false);
                    Server.print("The server is no longer in developer mode!");
                } else {
                    Server.setInDeveloperMode(true);
                    Server.print("The server is now in developer mode!");
                }
            }
        });
        mnNetworkActions.add(chckbxmntmDeveloperMode);

        /**
         * Creates a new menu item on the menu that allows the user to change
         * the server into beta mode.
         */
        JCheckBoxMenuItem chckbxmntmBetaMode = new JCheckBoxMenuItem("Beta Mode");
        chckbxmntmBetaMode.setSelected(Server.isInBetaMode());
        chckbxmntmBetaMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (Server.isInBetaMode()) {
                    Server.setInBetaMode(false);
                    Server.print("The server is no longer in beta mode!");
                } else {
                    Server.setInBetaMode(true);
                    Server.print("The server is now in beta mode!");
                }
            }
        });
        mnNetworkActions.add(chckbxmntmBetaMode);

        /**
         * Adds a new menu on the menu bar that contains all of the gui options.
         */
        JMenu mnGuiOptions = new JMenu("GUI Options");
        menuBar.add(mnGuiOptions);

        /**
         * Item on the gui options menu that clears the console when clicked.
         */
        JMenuItem mntmNewMenuItem = new JMenuItem("Clear Console");
        mntmNewMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                getConsoleTextArea().setText("");
            }
        });

        /**
         * Item on the gui options menu that saves the current settings when
         * clicked.
         */
        JMenuItem mntmSaveOptions = new JMenuItem("Save Settings");
        mntmSaveOptions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                save();
            }
        });
        mnGuiOptions.add(mntmSaveOptions);

        /**
         * Item on the gui options menu that divides the menu items.
         */
        JSeparator separator_1 = new JSeparator();
        mnGuiOptions.add(separator_1);

        /**
         * Adds an item to the gui options menu.
         */
        mnGuiOptions.add(mntmNewMenuItem);

        /**
         * Item on the gui options menu that saves the current settings when
         * clicked.
         */
        JMenuItem mntmC = new JMenuItem("Clear Notepad");
        mntmC.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                getNotepadTextArea().setText("");
            }
        });
        mnGuiOptions.add(mntmC);

        /**
         * Item on the gui options menu that divides the other menu options.
         */
        JSeparator separator_2 = new JSeparator();
        mnGuiOptions.add(separator_2);

        /**
         * Item on the gui options menu that allows for live updating of the
         * statistics table.
         */
        JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Live Statistics");
        chckbxmntmNewCheckItem.setSelected(Server.isLiveStatistics());
        chckbxmntmNewCheckItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (Server.isLiveStatistics()) {
                    Server.print("Live statistics have been turned off!");
                    Server.setLiveStatistics(false);
                } else {
                    Server.print("Live statistics have been turned on!");
                    Server.setLiveStatistics(true);
                }
            }
        });
        chckbxmntmNewCheckItem.setSelected(Server.isLiveStatistics());
        mnGuiOptions.add(chckbxmntmNewCheckItem);

        /**
         * Item on the menu bar that opens my profile page on moparscape and
         * rune-server.
         */
        JButton btnNewButton = new JButton("Contact");
        btnNewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.moparscape.org/smf/index.php?action=profile;u=490965"));
                    Desktop.getDesktop().browse(new URI("http://www.rune-server.org/members/lare96/"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        menuBar.add(btnNewButton);

        /**
         * Creates the tabbed pane so we can add tabs.
         */
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        applet.getContentPane().add(tabbedPane, BorderLayout.NORTH);
        consoleTextArea.setRows(12);
        consoleTextArea.setEditable(false);
        consoleTextArea.setWrapStyleWord(true);
        consoleTextArea.setLineWrap(true);

        /**
         * The scroll pane for the console tab.
         */
        JScrollPane scrollPane = new JScrollPane();
        tabbedPane.addTab("Console", null, scrollPane, null);
        tabbedPane.setEnabledAt(0, true);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(consoleTextArea);
        scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        scrollPane.setBackground(SystemColor.inactiveCaption);
        scrollPane.setForeground(SystemColor.infoText);

        /**
         * The scroll pane for the notepad tab.
         */
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane_1.setForeground(Color.BLACK);
        scrollPane_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        scrollPane_1.setBackground(SystemColor.inactiveCaption);
        tabbedPane.addTab("Notepad", null, scrollPane_1, null);
        tabbedPane.setEnabledAt(1, true);
        notepadTextArea.setWrapStyleWord(true);
        notepadTextArea.setRows(12);
        notepadTextArea.setLineWrap(true);
        scrollPane_1.setViewportView(notepadTextArea);

        /**
         * The desktop pane for the server tab.
         */
        JDesktopPane desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.WHITE);
        tabbedPane.addTab("Server", null, desktopPane, null);
        tabbedPane.setEnabledAt(2, true);
        desktopPane.setLayout(null);

        /**
         * The text below the server stress progress bar.
         */
        serverStressFooter = new JTextField();
        serverStressFooter.setBackground(UIManager.getColor("Button.background"));
        serverStressFooter.setEditable(false);
        serverStressFooter.setHorizontalAlignment(SwingConstants.CENTER);
        serverStressFooter.setText("Server Stress");
        serverStressFooter.setBounds(0, 25, 86, 17);
        desktopPane.add(serverStressFooter);
        serverStressFooter.setColumns(10);

        /**
         * The server stress progress bar.
         */
        progressBar.setValue(0);
        progressBar.setForeground(Color.RED);
        progressBar.setStringPainted(true);
        progressBar.setBounds(0, 0, 439, 17);
        desktopPane.add(progressBar);

        /**
         * The table of statistics in the server tab.
         */
        statisticsTable = new JTable();
        statisticsTable.setRowSelectionAllowed(false);
        statisticsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        statisticsTable.setModel(new DefaultTableModel(new String[][] { { "Running Threads: ", "" }, { "Tickrate:", "" }, { "Players Online: ", "" }, { "Total Time Online:", "" }, { "Memory Usage: ", "" }, }, new String[] { "Engine", "Value" }) {
            @SuppressWarnings("unchecked")
            Class[] columnTypes = new Class[] { String.class, String.class };

            @SuppressWarnings("unchecked")
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        statisticsTable.getColumnModel().getColumn(0).setResizable(false);
        statisticsTable.getColumnModel().getColumn(1).setResizable(false);
        statisticsTable.setForeground(Color.BLACK);
        desktopPane.setLayer(statisticsTable, 0);
        statisticsTable.setSurrendersFocusOnKeystroke(true);
        statisticsTable.setFillsViewportHeight(true);
        statisticsTable.setBackground(Color.WHITE);
        statisticsTable.setBounds(10, 53, 409, 80);
        desktopPane.add(statisticsTable);

        /**
         * The text below the statistics graph.
         */
        statisticsGraphFooter = new JTextField();
        statisticsGraphFooter.setEditable(false);
        statisticsGraphFooter.setText("Statistics");
        statisticsGraphFooter.setHorizontalAlignment(SwingConstants.CENTER);
        statisticsGraphFooter.setColumns(10);
        statisticsGraphFooter.setBackground(SystemColor.menu);
        statisticsGraphFooter.setBounds(0, 144, 86, 17);
        desktopPane.add(statisticsGraphFooter);

        /**
         * The desktop pane for the player tab.
         */
        JDesktopPane desktopPane_1 = new JDesktopPane();
        desktopPane_1.setBackground(Color.WHITE);
        tabbedPane.addTab("Player", null, desktopPane_1, null);
        tabbedPane.setEnabledAt(3, true);

        /**
         * The list of players to be displayed in the list.
         */
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.setVisibleRowCount(0);
        playerList.setModel(new AbstractListModel<String>() {

            private static final long serialVersionUID = 1L;

            List<String> values = new ArrayList<String>();

            @Override
            public int getSize() {
                return values.size();
            }

            @Override
            public String getElementAt(int index) {
                return values.get(index);
            }
        });
        playerList.setBounds(10, 0, 105, 202);

        /**
         * The scroll pane for the player list.
         */
        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane_2.setEnabled(true);
        scrollPane_2.setBounds(10, 11, 105, 171);
        scrollPane_2.setViewportView(playerList);
        desktopPane_1.add(scrollPane_2);

        /**
         * The kick button for the player list.
         */
        JButton btnNewButton_1 = new JButton("Kick");
        btnNewButton_1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (getPlayerList().getSelectedIndex() == -1) {
                    return;
                }

                Player player = World.getPlayer(getPlayerList().getModel().getElementAt(getPlayerList().getSelectedIndex()));

                if (player != null) {
                    try {
                        player.logout();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        btnNewButton_1.setBounds(125, 45, 89, 23);
        desktopPane_1.add(btnNewButton_1);

        /**
         * The ban button for the player list.
         */
        JButton btnNewButton_3 = new JButton("Ban");
        btnNewButton_3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (getPlayerList().getSelectedIndex() == -1) {
                    return;
                }

                Player player = World.getPlayer(getPlayerList().getModel().getElementAt(getPlayerList().getSelectedIndex()));

                if (player != null) {
                    try {
                        player.setBanned(true);
                        player.logout();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        btnNewButton_3.setBounds(125, 79, 89, 23);
        desktopPane_1.add(btnNewButton_3);

        /**
         * The ip ban button for the player list.
         */
        JButton btnNewButton_4 = new JButton("Ip Ban");
        btnNewButton_4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (getPlayerList().getSelectedIndex() == -1) {
                    return;
                }

                Player player = World.getPlayer(getPlayerList().getModel().getElementAt(getPlayerList().getSelectedIndex()));

                if (player != null) {
                    try {
                        HostGateway.getBanned().add(player.getNetwork().getHost());

                        FileWriter writer = new FileWriter(new File("./data/ip_banned.txt"), true);
                        writer.write(player.getNetwork().getHost());
                        writer.close();

                        player.logout();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        btnNewButton_4.setBounds(125, 113, 89, 23);
        desktopPane_1.add(btnNewButton_4);

        /**
         * The send message button for the player list.
         */
        JButton btnNewButton_6 = new JButton("Send Message");
        btnNewButton_6.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (getPlayerList().getSelectedIndex() == -1) {
                    return;
                }

                Player player = World.getPlayer(getPlayerList().getModel().getElementAt(getPlayerList().getSelectedIndex()));

                if (player != null) {
                    try {
                        player.getServerPacketBuilder().sendMessage(sendMessageTextArea.getText());
                        sendMessageTextArea.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        btnNewButton_6.setBounds(233, 114, 186, 57);
        desktopPane_1.add(btnNewButton_6);

        /**
         * The scroll pane for the send message text ares.
         */
        JScrollPane scrollPane_3 = new JScrollPane();
        scrollPane_3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane_3.setBounds(233, 58, 186, 45);
        desktopPane_1.add(scrollPane_3);
        scrollPane_3.setViewportView(sendMessageTextArea);
        sendMessageTextArea.setText("");
    }

    /**
     * Grabs the saved settings and sets the settings to the current values.
     */
    public static void load() {
        Scanner s;
        boolean firstTextIndex = false;
        String text = null;
        File settings = new File("./data/gui_settings.txt");

        /** If the settings file exists. */
        if (settings.exists()) {

            /** Scan and load the data from the file. */
            try {
                s = new Scanner(settings);

                while (s.hasNextLine()) {
                    if (!firstTextIndex) {
                        String next = s.next();
                        if (next.equals("[drop]")) {
                            World.setCanDrop(s.nextBoolean());
                        } else if (next.equals("[trade]")) {
                            World.setCanTrade(s.nextBoolean());
                        } else if (next.equals("[pickup]")) {
                            World.setCanPickup(s.nextBoolean());
                        } else if (next.equals("[shop]")) {
                            World.setCanShop(s.nextBoolean());
                        } else if (next.equals("[devmode]")) {
                            Server.setInDeveloperMode(s.nextBoolean());
                        } else if (next.equals("[betamode]")) {
                            Server.setInBetaMode(s.nextBoolean());
                        } else if (next.equals("[livestats]")) {
                            Server.setLiveStatistics(s.nextBoolean());
                            firstTextIndex = true;

                        }
                    } else {
                        if (text == null) {
                            text = "";
                            text = s.nextLine();
                            continue;
                        } else {
                            text += s.nextLine() + "\n";
                        }
                    }
                }

                notepadTextArea.setText(text);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /** If it doesn't exist. */
        } else {

            /** Create a new settings file with the following data inside. */
            try {
                settings.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(settings));
                writer.write("[drop] true");
                writer.newLine();
                writer.write("[trade] true");
                writer.newLine();
                writer.write("[pickup] true");
                writer.newLine();
                writer.write("[shop] true");
                writer.newLine();
                writer.write("[devmode] false");
                writer.newLine();
                writer.write("[betamode] false");
                writer.newLine();
                writer.write("[livestats] false");
                writer.newLine();
                writer.write("The settings file was deleted, and therefore all saved settings have been reset to their default states. If you did not delete the file, please use the 'contact' button in the upper right corner to report this bug. Thanks!");
                writer.newLine();
                writer.newLine();
                writer.write("- lare96");
                writer.close();

                if (!settings.exists()) {
                    throw new IllegalStateException("The settings file was somehow not created, plase restart the server.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the current data to the settings file.
     */
    public static void save() {

        /** Write the currently set data to the text file. */
        try {
            File settings = new File("./data/gui_settings.txt");

            if (settings.exists()) {
                settings.delete();
            }

            settings.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(settings));
            writer.write("[drop] " + Boolean.toString(World.isCanDrop()));
            writer.newLine();
            writer.write("[trade] " + Boolean.toString(World.isCanTrade()));
            writer.newLine();
            writer.write("[pickup] " + Boolean.toString(World.isCanPickup()));
            writer.newLine();
            writer.write("[shop] " + Boolean.toString(World.isCanShop()));
            writer.newLine();
            writer.write("[devmode] " + Boolean.toString(Server.isInDeveloperMode()));
            writer.newLine();
            writer.write("[betamode] " + Boolean.toString(Server.isInBetaMode()));
            writer.newLine();
            writer.write("[livestats] " + Boolean.toString(Server.isLiveStatistics()));
            writer.newLine();
            writer.write(notepadTextArea.getText());
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Server.print("The current settings have been saved!");
    }

    /**
     * Updates the players in the player list.
     */
    public static void updatePlayers() {

        /** Reset the model. */
        ServerGUI.getPlayerList().setModel(new AbstractListModel<String>() {
            private static final long serialVersionUID = 1L;

            String[] values = World.getPlayerNames();

            @Override
            public int getSize() {
                return values.length;
            }

            @Override
            public String getElementAt(int index) {
                return values[index];
            }
        });
    }

    /**
     * @return the consoleTextArea.
     */
    public static JTextArea getConsoleTextArea() {
        return consoleTextArea;
    }

    /**
     * @return the notepadTextArea.
     */
    public static JTextArea getNotepadTextArea() {
        return notepadTextArea;
    }

    /**
     * @return the statisticsTable.
     */
    public static JTable getStatisticsTable() {
        return statisticsTable;
    }

    /**
     * @return the progressBar.
     */
    public static JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * @return the playerList.
     */
    public static JList<String> getPlayerList() {
        return playerList;
    }

    /**
     * @return the sendMessageTextArea.
     */
    public JTextArea getSendMessageTextArea() {
        return sendMessageTextArea;
    }
}
