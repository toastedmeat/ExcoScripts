/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toastyflaxer;

/**
 *
 * @author Eric
 */
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class FlaxerGui extends JFrame {
    
    public static JFrame frame;
    public static JPanel panelSettings;
    public static JPanel panelPotions;
    public static JPanel panelDebug;
    public static JTabbedPane tabbedPane;
    
    //Main panel
    public static JLabel lblLoc = new JLabel("Location");
    public static JLabel lblSpot = new JLabel("Spot");
    public static JTextField textSpot;
    private static ButtonGroup buttonGroupSpot = new ButtonGroup();
    public static JRadioButton rdbtnGnome = new JRadioButton("Gnome");
    public static JRadioButton rdbtnSeers = new JRadioButton("Seers");
    public static JButton btnStart = new JButton("Start");
    
    
    // end
    private static boolean initialized = false;

    public static void buildGUI() {
        if(initialized){
            frame.setTitle("Toasty Flax");
            return;
        }else{
            initialized = true;
        }            
        frame = new JFrame("Flaxer");
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setResizable(false);
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        panelSettings = new JPanel();
        panelSettings.setLayout(null);
        tabbedPane.addTab("Settings", null, panelSettings, null);

        
        // panel Settings starts here
        
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                frame.setVisible(false);
            }
        });
        btnStart.setBounds(6, 135, 100, 35);
        panelSettings.add(btnStart);
        
        lblLoc.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblLoc.setBounds(21, 12, 125, 14);
        panelSettings.add(lblLoc);

        rdbtnGnome.setSelected(true);
        buttonGroupSpot.add(rdbtnGnome);
        rdbtnGnome.setBounds(6, 35, 109, 23);
        panelSettings.add(rdbtnGnome);

        buttonGroupSpot.add(rdbtnSeers);
        rdbtnSeers.setBounds(6, 55, 109, 23);
        panelSettings.add(rdbtnSeers);
        
        lblSpot.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblSpot.setBounds(10, 85, 65, 14);
        panelSettings.add(lblSpot);

        textSpot = new JTextField();
        textSpot.setText("1");
        textSpot.setHorizontalAlignment(JTextField.CENTER);
        textSpot.setBounds(6, 105, 86, 20);
        panelSettings.add(textSpot);
        textSpot.setColumns(10);
        
        
        
    }
    public static void CloseGUI() {
		frame.setVisible(false);
		frame.dispose();
                panelSettings = null;
                panelDebug = null;
		frame = null;
                tabbedPane = null;
		initialized = false;
	}
}
