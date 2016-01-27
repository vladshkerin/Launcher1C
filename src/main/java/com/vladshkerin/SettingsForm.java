package com.vladshkerin;

import com.vladshkerin.exception.NotFoundPropertyException;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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

    private static final int WIDTH_WINDOW = 350;
    private static final int HEIGHT_WINDOW = 300;

    JLabel path1cLabel = new JLabel();
    JLabel pathBaseLabel = new JLabel();
    JLabel pathBackupLabel = new JLabel();
    JTextField path1cText = new JTextField();
    JTextField pathBaseText = new JTextField();
    JTextField pathBackupText = new JTextField();
    JButton saveButton = new JButton();
    JButton closeButton = new JButton();

    public SettingsForm(JFrame parent) {
        super(parent, Resource.getString("UpdateBaseForm"));

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
            if (e.getActionCommand().equals("")) {

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
        path1cLabel.setText(Resource.getString("path1cLabel"));
        pathBaseLabel.setText(Resource.getString("pathBaseLabel"));
        pathBackupLabel.setText(Resource.getString("pathBackupLabel"));

        try {
            path1cText.setText(Settings.getString("path1cText"));
            pathBaseText.setText(Settings.getString("pathBaseText"));
            pathBackupText.setText(Settings.getString("pathBackupText"));
        } catch (NotFoundPropertyException e) {
            log.log(Level.CONFIG, e.getMessage());
        }

        ButtonListener buttonListener = new ButtonListener();
        saveButton.addActionListener(buttonListener);
        closeButton.addActionListener(buttonListener);

        saveButton.setActionCommand("saveButton");
        closeButton.setActionCommand("closeButton");

        saveButton.setText(Resource.getString("saveButton"));
        closeButton.setText(Resource.getString("CloseButton"));

        JPanel pMain = BoxLayoutUtils.createVerticalPanel();
        pMain.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel pPath1c = BoxLayoutUtils.createHorizontalPanel();
        pPath1c.add(path1cLabel);
        pPath1c.add(BoxLayoutUtils.createHorizontalStrut(4));
        pPath1c.add(path1cText);

        JPanel pBase = BoxLayoutUtils.createHorizontalPanel();
        pBase.add(pathBaseLabel);
        pBase.add(BoxLayoutUtils.createHorizontalStrut(4));
        pBase.add(pathBaseText);

        JPanel pBackup = BoxLayoutUtils.createHorizontalPanel();
        pBackup.add(pathBackupLabel);
        pBackup.add(BoxLayoutUtils.createHorizontalStrut(4));
        pBackup.add(pathBackupText);

        JPanel pText = BoxLayoutUtils.createVerticalPanel();
        pText.setBorder(new CompoundBorder(
                new TitledBorder(Resource.getString("strTitleSettings")),
                new EmptyBorder(4, 4, 4, 4)));
        pText.add(pPath1c);
        pText.add(BoxLayoutUtils.createVerticalStrut(4));
        pText.add(pBase);
        pText.add(BoxLayoutUtils.createVerticalStrut(4));
        pText.add(pBackup);

        JPanel pButton = BoxLayoutUtils.createHorizontalPanel();
        pButton.add(BoxLayoutUtils.createHorizontalGlue());
        pButton.add(saveButton);
        pButton.add(BoxLayoutUtils.createHorizontalStrut(6));
        pButton.add(closeButton);

        GUITools.makeSameSize(path1cLabel, pathBaseLabel, pathBackupLabel);
        GUITools.makeSameSize(path1cText, pathBaseText, pathBackupText);
        GUITools.makeSameSize(saveButton, closeButton);

        pMain.add(pText);
        pMain.add(BoxLayoutUtils.createHorizontalStrut(10));
        pMain.add(pButton);

        return pMain;
    }
}
