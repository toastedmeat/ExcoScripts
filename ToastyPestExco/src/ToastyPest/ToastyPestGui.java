/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToastyPest;

/**
 *
 * @author Eric
 */
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ToastyPestGui extends JFrame {

    public static JFrame frame;
    public static JPanel panelSettings;
    public static JPanel panelPrayer;
    public static JPanel panelDebug;
    public static JTabbedPane tabbedPane;

    //Main panel
    public static JLabel lblNewLabel = new JLabel("Boat");
    public static JLabel lblPaint = new JLabel("Paint");
    public static JLabel lblWeaponSpeed = new JLabel("Weapon Speed");
    public static JLabel lblItemsToBuy = new JLabel("Item to buy");
    //public static JLabel lblDoors = new JLabel("Door Opening");

    public static JTextField textWeaponSpeed;

    final private static ButtonGroup buttonGroup = new ButtonGroup();//boats
    final private static ButtonGroup buttonGroup_1 = new ButtonGroup();//paint
    final private static ButtonGroup buttonGroup_PaintInfo = new ButtonGroup();
    final private static ButtonGroup buttonGroup_2 = new ButtonGroup();//items
    final private static ButtonGroup buttonGroup_3 = new ButtonGroup();//Str
    final private static ButtonGroup buttonGroup_4 = new ButtonGroup();//Att
    final private static ButtonGroup buttonGroup_5 = new ButtonGroup();//piety chiv
    final private static ButtonGroup buttonGroup_RngPray = new ButtonGroup();

    public static JRadioButton rdbtnNovice = new JRadioButton("Novice");
    public static JRadioButton rdbtnMedium = new JRadioButton("Medium");
    public static JRadioButton rdbtnHard = new JRadioButton("Hard");

    public static JRadioButton rdbtnShowPaint = new JRadioButton("Show");
    public static JRadioButton rdbtnHidePaint = new JRadioButton("Hide");
    
    public static JRadioButton rdbtnShowPaintInfo = new JRadioButton("Show");
    public static JRadioButton rdbtnHidePaintInfo = new JRadioButton("Hide");

    public static JRadioButton rdbtnBody = new JRadioButton("Body");
    public static JRadioButton rdbtnLegs = new JRadioButton("Legs");
    public static JRadioButton rdbtnGloves = new JRadioButton("Gloves");
    public static JRadioButton rdbtnRHelm = new JRadioButton("Range Helm");
    public static JRadioButton rdbtnMeleeHelm = new JRadioButton("Melee Helm");
    public static JRadioButton rdbtnMageHelm = new JRadioButton("Mage Helm");

    public static JRadioButton rdbtnAtt = new JRadioButton("Att Exp");
    public static JRadioButton rdbtnStr = new JRadioButton("Str Exp");
    public static JRadioButton rdbtnDef = new JRadioButton("Def Exp");
    public static JRadioButton rdbtnRng = new JRadioButton("Rng Exp");
    public static JRadioButton rdbtnMage = new JRadioButton("Magic Exp");
    public static JRadioButton rdbtnHP = new JRadioButton("Hp Exp");

    public static JCheckBox chkboxDontBuy = new JCheckBox("Dont Buy!?");

    public static JCheckBox chkboxPureMode = new JCheckBox("Pure mode");
    public static JCheckBox chkboxBanking = new JCheckBox("Banking");
    public static JCheckBox chkboxAtkPortal = new JCheckBox("Atk Portals");

    public static JButton btnStart = new JButton("Start/Hide GUI");
    public static JButton btnUpdate = new JButton("Update Settings");

    //prayer panel
    public static JCheckBox chkboxPrayer = new JCheckBox("Using Prayer?");
    public static JRadioButton rdbtnStr1 = new JRadioButton("Burst of Str");
    public static JRadioButton rdbtnAtt1 = new JRadioButton("Clarity of Thought");
    public static JRadioButton rdbtnStr2 = new JRadioButton("Superhuman Str");
    public static JRadioButton rdbtnAtt2 = new JRadioButton("Improved Reflex");
    public static JRadioButton rdbtnStr3 = new JRadioButton("Ultimate Str");
    public static JRadioButton rdbtnAtt3 = new JRadioButton("Incredible Reflex");

    public static JRadioButton rdbtnChiv = new JRadioButton("Chivalry");
    public static JRadioButton rdbtnPiety = new JRadioButton("Piety");
    
    public static JRadioButton rdbtnRng1 = new JRadioButton("Sharp Eye");
    public static JRadioButton rdbtnRng2 = new JRadioButton("Hawk Eye");
    public static JRadioButton rdbtnRng3 = new JRadioButton("Eagle Eye");
    
    
    public static JCheckBox chkboxSpec = new JCheckBox("Use Special?");
    public static JLabel lblSpec = new JLabel("% to use Spec");
    public static JTextField textSpecPercent;

    public static JButton btnUpdatePrayer = new JButton("Update Pray/Spec");
    public static JButton btnStartPrayer = new JButton("Start/Hide GUI");

    //Debug Panel    
    public static JCheckBox chkboxStarted = new JCheckBox("Started?");

    public static JButton btnScreenShotDebug = new JButton("ScreenShot");
    public static JButton btnStartDebug = new JButton("Start/Hide GUI");
    public static JButton btnReset = new JButton("Reset Paint");

    // end
    private static boolean initialized = false;

    public static void buildGUI() {
        if (initialized) {
            frame.setTitle("Toasty Pest");
            return;
        } else {
            initialized = true;
        }
        frame = new JFrame("Toasty's Pests");
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setResizable(false);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        panelSettings = new JPanel();
        panelSettings.setLayout(null);
        tabbedPane.addTab("Settings", null, panelSettings, null);

        panelPrayer = new JPanel();
        panelPrayer.setLayout(null);
        tabbedPane.addTab("Pray/Spec", null, panelPrayer, null);

        panelDebug = new JPanel();
        panelDebug.setLayout(null);
        tabbedPane.addTab("Debug", null, panelDebug, null);

        // panel Settings starts here
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel.setBounds(21, 12, 46, 14);
        panelSettings.add(lblNewLabel);

        rdbtnNovice.setSelected(true);
        buttonGroup.add(rdbtnNovice);
        rdbtnNovice.setBounds(6, 33, 109, 23);
        panelSettings.add(rdbtnNovice);

        buttonGroup.add(rdbtnMedium);
        rdbtnMedium.setBounds(6, 59, 109, 23);
        panelSettings.add(rdbtnMedium);

        buttonGroup.add(rdbtnHard);
        rdbtnHard.setBounds(6, 85, 109, 23);
        panelSettings.add(rdbtnHard);

        lblWeaponSpeed.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblWeaponSpeed.setBounds(6, 115, 120, 16);
        panelSettings.add(lblWeaponSpeed);

        textWeaponSpeed = new JTextField();
        textWeaponSpeed.setText("1.5");
        textWeaponSpeed.setToolTipText("This is the time to wait before checking "
                + "\nif your still attacking/need a new target "
                + "\nif other conditions are not fulfilled."
                + "\n This time is in seconds");

        textWeaponSpeed.setBounds(6, 135, 105, 25);
        panelSettings.add(textWeaponSpeed);
        textWeaponSpeed.setColumns(10);

        lblPaint.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblPaint.setBounds(134, 14, 46, 14);
        panelSettings.add(lblPaint);

        buttonGroup_1.add(rdbtnShowPaint);
        rdbtnShowPaint.setSelected(true);
        rdbtnShowPaint.setBounds(117, 30, 91, 23);
        panelSettings.add(rdbtnShowPaint);

        buttonGroup_1.add(rdbtnHidePaint);
        rdbtnHidePaint.setBounds(117, 50, 91, 23);
        panelSettings.add(rdbtnHidePaint);

        buttonGroup_PaintInfo.add(rdbtnShowPaintInfo);
        
        buttonGroup_PaintInfo.add(rdbtnHidePaintInfo);
        rdbtnHidePaintInfo.setSelected(true);
        
        btnUpdate.setBounds(6, 320, 205, 35);
        panelSettings.add(btnUpdate);

        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                frame.setVisible(false);
            }
        });
        btnStart.setBounds(6, 360, 205, 35);
        panelSettings.add(btnStart);

        chkboxPureMode.setBounds(117, 80, 97, 23);
        chkboxPureMode.setToolTipText("Check this box if you want to cut and repair barricades for points.");
        panelSettings.add(chkboxPureMode);
        
        chkboxBanking.setBounds(117, 100, 97, 23);
        chkboxBanking.setToolTipText("Check this box if you want to use logs from you bank.");
        panelSettings.add(chkboxBanking);
        
        chkboxAtkPortal.setBounds(117, 120, 97, 30);
        chkboxAtkPortal.setSelected(true);
        chkboxAtkPortal.setToolTipText("Check this box if you want to attack portals.");
        panelSettings.add(chkboxAtkPortal);

        chkboxDontBuy.setBounds(100, 165, 97, 23);
        chkboxDontBuy.setToolTipText("Check this box if you don't want to buy items."
                + " \n This will set the script to log off after 250 points.");
        panelSettings.add(chkboxDontBuy);

        lblItemsToBuy.setBounds(6, 170, 95, 14);
        panelSettings.add(lblItemsToBuy);

        buttonGroup_2.add(rdbtnBody);
        rdbtnBody.setSelected(true);
        rdbtnBody.setBounds(6, 190, 75, 23);
        panelSettings.add(rdbtnBody);

        buttonGroup_2.add(rdbtnLegs);
        rdbtnLegs.setBounds(6, 210, 75, 23);
        panelSettings.add(rdbtnLegs);

        buttonGroup_2.add(rdbtnGloves);
        rdbtnGloves.setBounds(6, 230, 75, 23);
        panelSettings.add(rdbtnGloves);

        buttonGroup_2.add(rdbtnRHelm);
        rdbtnRHelm.setBounds(6, 250, 95, 23);
        panelSettings.add(rdbtnRHelm);

        buttonGroup_2.add(rdbtnMeleeHelm);
        rdbtnMeleeHelm.setBounds(6, 270, 90, 23);
        panelSettings.add(rdbtnMeleeHelm);

        buttonGroup_2.add(rdbtnMageHelm);
        rdbtnMageHelm.setBounds(6, 290, 90, 23);
        panelSettings.add(rdbtnMageHelm);

        buttonGroup_2.add(rdbtnAtt);
        rdbtnAtt.setBounds(117, 190, 90, 23);
        panelSettings.add(rdbtnAtt);

        buttonGroup_2.add(rdbtnStr);
        rdbtnStr.setBounds(117, 210, 90, 23);
        panelSettings.add(rdbtnStr);

        buttonGroup_2.add(rdbtnDef);
        rdbtnDef.setBounds(117, 230, 90, 23);
        panelSettings.add(rdbtnDef);

        buttonGroup_2.add(rdbtnRng);
        rdbtnRng.setBounds(117, 250, 90, 23);
        panelSettings.add(rdbtnRng);

        buttonGroup_2.add(rdbtnMage);
        rdbtnMage.setBounds(117, 270, 90, 23);
        panelSettings.add(rdbtnMage);

        buttonGroup_2.add(rdbtnHP);
        rdbtnHP.setBounds(117, 290, 90, 23);
        panelSettings.add(rdbtnHP);

        //Prayer Panel
        chkboxPrayer.setBounds(6, 0, 186, 23);
        panelPrayer.add(chkboxPrayer);

        buttonGroup_3.add(rdbtnStr1);
        rdbtnStr1.setBounds(6, 20, 186, 23);
        panelPrayer.add(rdbtnStr1);

        buttonGroup_3.add(rdbtnStr2);
        rdbtnStr2.setBounds(6, 40, 186, 23);
        panelPrayer.add(rdbtnStr2);

        buttonGroup_3.add(rdbtnStr3);
        rdbtnStr3.setBounds(6, 60, 186, 23);
        panelPrayer.add(rdbtnStr3);

        buttonGroup_4.add(rdbtnAtt1);
        rdbtnAtt1.setBounds(6, 80, 186, 23);
        panelPrayer.add(rdbtnAtt1);

        buttonGroup_4.add(rdbtnAtt2);
        rdbtnAtt2.setBounds(6, 100, 186, 23);
        panelPrayer.add(rdbtnAtt2);

        buttonGroup_4.add(rdbtnAtt3);
        rdbtnAtt3.setBounds(6, 120, 186, 23);
        panelPrayer.add(rdbtnAtt3);

        buttonGroup_5.add(rdbtnChiv);
        rdbtnChiv.setBounds(6, 140, 186, 23);
        panelPrayer.add(rdbtnChiv);

        buttonGroup_5.add(rdbtnPiety);
        rdbtnPiety.setBounds(6, 160, 186, 23);
        panelPrayer.add(rdbtnPiety);
        
        buttonGroup_RngPray.add(rdbtnRng1);
        rdbtnRng1.setBounds(6, 180, 186, 23);
        panelPrayer.add(rdbtnRng1);
        
        buttonGroup_RngPray.add(rdbtnRng2);
        rdbtnRng2.setBounds(6, 200, 186, 23);
        panelPrayer.add(rdbtnRng2);
        
        buttonGroup_RngPray.add(rdbtnRng3);
        rdbtnRng3.setBounds(6, 220, 186, 23);
        panelPrayer.add(rdbtnRng3);
        
        chkboxSpec.setBounds(6, 240, 186, 23);
        panelPrayer.add(chkboxSpec);
        
        lblSpec.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblSpec.setBounds(6, 270, 120, 14);
        panelPrayer.add(lblSpec);
        
        textSpecPercent = new JTextField();
        textSpecPercent.setText("600");
        textSpecPercent.setToolTipText("The Percentage at which you can use spec or when you want to use it. 0 - 1000");

        textSpecPercent.setBounds(6, 290, 105, 25);
        panelPrayer.add(textSpecPercent);
        textSpecPercent.setColumns(10);
        
        

        
        //Prayer buttons
        btnUpdatePrayer.setBounds(6, 320, 205, 35);
        panelPrayer.add(btnUpdatePrayer);

        btnStartPrayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                frame.setVisible(false);
            }
        });
        btnStartPrayer.setBounds(6, 360, 205, 35);
        panelPrayer.add(btnStartPrayer);

        //Panel Debug starts here

        // buttons
        btnReset.setBounds(6, 230, 205, 35);
        panelDebug.add(btnReset);

        //btnScreenShotDebug.setBounds(6, 270, 205, 35);
        //panelDebug.add(btnScreenShotDebug);
        
        btnStartDebug.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                frame.setVisible(false);
            }
        });
        btnStartDebug.setBounds(6, 360, 205, 35);
        panelDebug.add(btnStartDebug);

    }

    public static void CloseGUI() {
        frame.setVisible(false);
        frame.dispose();
        panelSettings = null;
        panelPrayer = null;
        panelDebug = null;
        frame = null;
        tabbedPane = null;
        initialized = false;
    }
}
