package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The form for setting program.
 */
public class SettingsForm extends JDialog {

    private static Logger log = Logger.getLogger(UpdateBaseForm.class.getName());

    private static final int WIDTH_WINDOW = 400;
    private static final int HEIGHT_WINDOW = 205;

    JLabel path1cLabel = new JLabel();
    JLabel pathBaseLabel = new JLabel();
    JLabel pathBackupLabel = new JLabel();
    JTextField path1cText = new JTextField();
    JTextField pathBaseText = new JTextField();
    JTextField pathBackupText = new JTextField();
    JButton saveButton = new JButton();
    JButton closeButton = new JButton();

    public SettingsForm(JFrame parent) {
        super(parent, Resource.getString("strTitleSettingForm"));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.log(Level.WARNING, "Error set look and feel in update base form.");
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocale(Resource.getCurrentLocale());
        setSize(WIDTH_WINDOW, HEIGHT_WINDOW);
        setPositionWindow();

        add(createGUI());
        setVisible(true);
    }

    public class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("closeButton")) {
                dispose();
            }
        }
    }

    private void setPositionWindow() {
        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        int positionX = (int) ((dimScreen.getWidth() - WIDTH_WINDOW) / 2);
        int positionY = (int) ((dimScreen.getHeight() - HEIGHT_WINDOW) / 2);

        setLocation(positionX, positionY);
    }

    private JPanel createGUI() {
        // Settings objects
        path1cLabel.setText(Resource.getString("path1cLabel") + ":");
        pathBaseLabel.setText(Resource.getString("pathBaseLabel") + ":");
        pathBackupLabel.setText(Resource.getString("pathBackupLabel") + ":");

        try {
            path1cText.setText(Settings.getString("path.1c"));
            pathBaseText.setText(Settings.getString("path.base"));
            pathBackupText.setText(Settings.getString("path.backup"));
        } catch (NotFoundSettingException e) {
            log.log(Level.CONFIG, e.getMessage());
        }

        ButtonListener buttonListener = new ButtonListener();
        saveButton.addActionListener(buttonListener);
        closeButton.addActionListener(buttonListener);

        saveButton.setActionCommand("saveButton");
        closeButton.setActionCommand("closeButton");

        saveButton.setText(Resource.getString("saveButton"));
        closeButton.setText(Resource.getString("CloseButton"));

        // Layout objects on the form
        JPanel pMain = BoxLayoutUtils.createVerticalPanel();
        pMain.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel pText = BoxLayoutUtils.createVerticalPanel();
        pText.add(path1cLabel);
        pText.add(path1cText);
        pText.add(BoxLayoutUtils.createVerticalStrut(6));
        pText.add(pathBaseLabel);
        pText.add(pathBaseText);
        pText.add(BoxLayoutUtils.createVerticalStrut(6));
        pText.add(pathBackupLabel);
        pText.add(pathBackupText);

        JPanel pButton = BoxLayoutUtils.createHorizontalPanel();
        pButton.add(BoxLayoutUtils.createHorizontalGlue());
        pButton.add(saveButton);
        pButton.add(BoxLayoutUtils.createHorizontalStrut(6));
        pButton.add(closeButton);

        GUITools.makeSameSize(saveButton, closeButton);

        BoxLayoutUtils.setGroupAlignmentY(Component.TOP_ALIGNMENT, pText, pButton, pMain);

        pMain.add(pText);
        pMain.add(BoxLayoutUtils.createHorizontalStrut(10));
        pMain.add(pButton);

        return pMain;
    }
}
