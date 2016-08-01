/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.alee.laf.WebLookAndFeel;
import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

/**
 *
 * @author Apollo
 */
public class MainFrame extends JFrame implements ActionListener, WindowListener, NativeMouseInputListener {

    private static final Logger LOGGER = Logger.getLogger(GlobalScreen.class.getPackage().getName());

    private static final Map<Integer, String> BUTTON_MAP = new Hashtable<>();

    private static final String IMAGE_URL = "/resource/image/camera.png";
    private static final Image IMAGE_ICON;
    private static final TrayIcon TRAY_ICON;
    private static final SystemTray SYSTEM_TRAY = SystemTray.getSystemTray();

    private static final String TRAY_MENU_ABOUT = "About";
    private static final String TRAY_MENU_SHOW = "Show";
    private static final String TRAY_MENU_EXIT = "Exit";
    
    private static final String TRAY_MENU_CAPTION = "Screen Capture";
    private static final String TRAY_MENU_MESSAGE = "Screen Capture now run in background";

    private static final String TRAY_MENU_ABOUT_MESSAGE = "Screen Capture version 1.01\n"
            + "Developed by Bao Ha (halab4it@gmail.com)";

    static {
        BUTTON_MAP.put(MouseEvent.BUTTON1, "LEFT MOUSE");
        BUTTON_MAP.put(MouseEvent.BUTTON2, "MIDDLE MOUSE");
        BUTTON_MAP.put(MouseEvent.BUTTON3, "RIGHT MOUSE");

        URL imageURL = MainFrame.class.getResource(IMAGE_URL);
        IMAGE_ICON = new ImageIcon(imageURL).getImage();
        TRAY_ICON = new TrayIcon(IMAGE_ICON, "Screen Capture");
    }

    private static final String START = "Start";
    private static final String STOP = "Stop";
    private static final String UNDERSCORE = "_";
    private static final String DOT = ".";

    private static final String DEFAULT_FOLDER_ADDRESS = "C:\\Screen Capture";

    private File saveFolder;
    private int hotKeyCode = NativeMouseEvent.BUTTON3;
    private boolean isCapturing = false;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        this.setIconImage(IMAGE_ICON);
        initComponents();
        initMenu();
        initComboBox();
        initDefaultValues();
        addKeyListener();
        initTrayIcon();
        try {
            GlobalScreen.setEventDispatcher(new SwingDispatchService());
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseListener(this);
        } catch (NativeHookException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initMenu() {
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, TRAY_MENU_ABOUT_MESSAGE, TRAY_MENU_ABOUT,
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SYSTEM_TRAY.remove(TRAY_ICON);
                System.exit(0);
            }
        });
    }

    private void initComboBox() {
        cbFormat.removeAllItems();
        cbFormat.addItem(PictureFormat.JPG);
        cbFormat.addItem(PictureFormat.BMP);
        cbFormat.addItem(PictureFormat.PNG);
    }

    private void initDefaultValues() {
        txtSaveFolder.setText(DEFAULT_FOLDER_ADDRESS);
        saveFolder = new File(DEFAULT_FOLDER_ADDRESS);
        txtHotKey.setText(BUTTON_MAP.get(MouseEvent.BUTTON2));
        LOGGER.setUseParentHandlers(false); //disable jnativehook log        
    }

    private void addKeyListener() {
        txtHotKey.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int buttonCode = e.getButton();
                txtHotKey.setText(BUTTON_MAP.get(buttonCode));
                hotKeyCode = convertToNativeMouseEventButton(buttonCode);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    private void initTrayIcon() {
        PopupMenu popup = new PopupMenu();
        MenuItem aboutItem = new MenuItem(TRAY_MENU_ABOUT);
        MenuItem showItem = new MenuItem(TRAY_MENU_SHOW);
        MenuItem exitItem = new MenuItem(TRAY_MENU_EXIT);
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(showItem);
        popup.add(exitItem);
        TRAY_ICON.setPopupMenu(popup);

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, TRAY_MENU_ABOUT_MESSAGE, TRAY_MENU_ABOUT,
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        showItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showWindow();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SYSTEM_TRAY.remove(TRAY_ICON);
                System.exit(0);
            }
        });

        TRAY_ICON.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showWindow();
            }
        });
    }

    private int convertToNativeMouseEventButton(int mouseEventButton) {
        if (mouseEventButton == MouseEvent.BUTTON1) {
            return NativeMouseEvent.BUTTON1;
        }
        if (mouseEventButton == MouseEvent.BUTTON2) {
            return NativeMouseEvent.BUTTON3;
        }
        return NativeMouseEvent.BUTTON2;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSaveFolder = new javax.swing.JTextField();
        bntChoose = new javax.swing.JButton();
        btnOpen = new javax.swing.JButton();
        lbSaveFolder = new javax.swing.JLabel();
        lbHotKey = new javax.swing.JLabel();
        lbFormat = new javax.swing.JLabel();
        txtHotKey = new javax.swing.JTextField();
        cbFormat = new javax.swing.JComboBox();
        btnStartStop = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        mnAbout = new javax.swing.JMenu();
        aboutItem = new javax.swing.JMenuItem();
        menuSeperator = new javax.swing.JPopupMenu.Separator();
        exitItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bntChoose.setText("Choose");
        bntChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntChooseActionPerformed(evt);
            }
        });

        btnOpen.setText("Open");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        lbSaveFolder.setText("Save Folder:");

        lbHotKey.setText("Hot Key:");

        lbFormat.setText("Format:");

        txtHotKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHotKeyActionPerformed(evt);
            }
        });

        cbFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnStartStop.setText("Start");
        btnStartStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartStopActionPerformed(evt);
            }
        });

        mnAbout.setText("About");

        aboutItem.setText("About");
        mnAbout.add(aboutItem);
        mnAbout.add(menuSeperator);

        exitItem.setText("Exit");
        mnAbout.add(exitItem);

        menuBar.add(mnAbout);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbHotKey)
                            .addComponent(txtHotKey, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbFormat)
                            .addComponent(cbFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 86, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSaveFolder)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lbSaveFolder)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bntChoose)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnOpen)))
                        .addContainerGap())))
            .addGroup(layout.createSequentialGroup()
                .addGap(148, 148, 148)
                .addComponent(btnStartStop, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bntChoose)
                        .addComponent(btnOpen))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbSaveFolder)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSaveFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbHotKey)
                    .addComponent(lbFormat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHotKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnStartStop)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bntChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntChooseActionPerformed
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = folderChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            saveFolder = folderChooser.getSelectedFile();
            txtSaveFolder.setText(saveFolder.getAbsolutePath());
        }
    }//GEN-LAST:event_bntChooseActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        try {
            Desktop.getDesktop().open(new File(txtSaveFolder.getText()));
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "The folder " + txtSaveFolder.getText() + " doesn't existed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnOpenActionPerformed

    private void txtHotKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHotKeyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHotKeyActionPerformed

    private void btnStartStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartStopActionPerformed
        if (isCapturing) {
            btnStartStop.setText(START);
        } else {
            btnStartStop.setText(STOP);
            hideToSystemTray();
        }
        isCapturing = !isCapturing;
    }//GEN-LAST:event_btnStartStopActionPerformed

    private void hideToSystemTray() {
        try {
            SYSTEM_TRAY.add(TRAY_ICON);
            setVisible(false);
            TRAY_ICON.displayMessage(TRAY_MENU_CAPTION, TRAY_MENU_MESSAGE, TrayIcon.MessageType.INFO);
        } catch (AWTException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void showWindow() {
        SYSTEM_TRAY.remove(TRAY_ICON);
        setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WebLookAndFeel.install();
        /* Create and display the form */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutItem;
    private javax.swing.JButton bntChoose;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnStartStop;
    private javax.swing.JComboBox cbFormat;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JLabel lbFormat;
    private javax.swing.JLabel lbHotKey;
    private javax.swing.JLabel lbSaveFolder;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPopupMenu.Separator menuSeperator;
    private javax.swing.JMenu mnAbout;
    private javax.swing.JTextField txtHotKey;
    private javax.swing.JTextField txtSaveFolder;
    // End of variables declaration//GEN-END:variables
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowOpened(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosing(WindowEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // Clean up the native hook.
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            ex.printStackTrace();
        }
        System.runFinalization();
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nme) {
        if (nme.getButton() == hotKeyCode && isCapturing) {
            ScreenCapturer.createScreenCapture(generateFileName(), cbFormat.getSelectedItem().toString());
        }
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nme) {
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nme) {
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nme) {
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nme) {
    }

    private File generateFileName() {
        Date date = new Date();
        String fileName = date.getYear() + UNDERSCORE + date.getMonth() + UNDERSCORE + date.getDate()
                + UNDERSCORE + date.getHours() + UNDERSCORE + date.getMinutes() + UNDERSCORE + date.getSeconds()
                + UNDERSCORE + date.getTime() + DOT + cbFormat.getSelectedItem().toString();
        return new File(saveFolder.getAbsolutePath() + File.separator + fileName);
    }
}
