package io.ryankshah;

import io.ryankshah.client.User;
import io.ryankshah.client.gui.QuickScanPanel;
import io.ryankshah.util.database.DBHelper;
import io.ryankshah.util.resource.ResourceLoader;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Client interface class
 */
public class Client extends JFrame
{
    protected static final int WIDTH = 800, HEIGHT = 640;
    protected static final String VERSION = "0.0.1-ALPHA";

    public static User user;

    public static JButton advancedScanButton, scanHistoryButton, performLastScanButton, editProfileButton, userGuideButton, logoutButton;
    public static JTextArea recentScanResult;

    public Client(User user) {
        this.user = user;

        setTitle("Miraihilate Client " + VERSION);
        setSize(new Dimension(WIDTH, HEIGHT));

        setIconImage(ResourceLoader.getImageIconResource("icon.png").getImage());


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        initComponents();

        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }


    private void initComponents() {
        ImageIcon img = ResourceLoader.getImageIconResource("logo.png");
        JLabel miraihilateLogo = new JLabel(img);
        miraihilateLogo.setBounds(10, 0, 300, 100);
        add(miraihilateLogo);

        //Add Client Information
        JLabel version = new JLabel("Version " + VERSION);
        version.setBounds(30 + (110/2), 110, 200, 30);
        add(version);
        JLabel userinfo = new JLabel(user.getFullName() + " - " + user.getEmail());
        userinfo.setBounds(50, 140, 240, 30);
        add(userinfo);

        // Add QuickScan Panel
        JPanel quickScanPanel = new QuickScanPanel();
        quickScanPanel.setBounds(10, 200, 300, HEIGHT - 233);
        add(quickScanPanel);

        // Add recent scan result
        JPanel scanResultPanel = new JPanel();
        TitledBorder resultBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Recent Scan");
        scanResultPanel.setBorder(resultBorder);
        scanResultPanel.setBounds(360, 10, WIDTH - (WIDTH - 420), HEIGHT - 233);
        scanResultPanel.setLayout(new GridLayout(1, 2));
            recentScanResult = new JTextArea();
            recentScanResult.setEditable(false);
            updateRecentScan();
            JScrollPane scroll = new JScrollPane(
                    recentScanResult,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            );
            scanResultPanel.add(scroll);
        add(scanResultPanel);

        // Add client utility buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBounds(360, 30 + (HEIGHT - 233), WIDTH - (WIDTH - 420), 170);
        buttonsPanel.setLayout(new GridLayout(2, 3));
            advancedScanButton = new JButton("Advanced Scan");
            buttonsPanel.add(advancedScanButton);
            scanHistoryButton = new JButton("Scan History");
            buttonsPanel.add(scanHistoryButton);
            performLastScanButton = new JButton("Perform Last Scan");
            buttonsPanel.add(performLastScanButton);
            editProfileButton = new JButton("Edit Profile");
            buttonsPanel.add(editProfileButton);
            userGuideButton = new JButton("User Guide");
            buttonsPanel.add(userGuideButton);
            logoutButton = new JButton("Logout");
            buttonsPanel.add(logoutButton);
        add(buttonsPanel);
    }

    public static void updateRecentScan() {
        Connection con = DBHelper.getDatabaseConnection();

        PreparedStatement stmt = null;
        try {
            String query = "SELECT * FROM scan_logs WHERE user_uuid = ? ORDER BY id DESC LIMIT 1";
            stmt = con.prepareStatement(query);
            stmt.setString(1, user.getUUID().toString());
            ResultSet results = stmt.executeQuery();

            // While a result exists
            while(results.next()) {
                String data = results.getString("data");
                if(data.equals(""))
                    recentScanResult.setText("No recent scan found!");
                else
                    recentScanResult.setText(data);
            }

            // Closing up the connection
            if(stmt != null)
                stmt.close();
            if(con != null)
                con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}