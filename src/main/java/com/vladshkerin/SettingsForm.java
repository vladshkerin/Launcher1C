package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The form for setting program.
 */
public class SettingsForm extends JDialog {

    private static final int WIDTH_WINDOW = 430;
    private static final int HEIGHT_WINDOW = 220;

    private static final Logger logger = Logger.getLogger("com.vladshkerin.launcher1c");
    private static Settings settings = Property.getInstance();

    private JLabel path1cLabel = new JLabel();
    private JLabel pathBaseLabel = new JLabel();
    private JLabel pathBackupLabel = new JLabel();
    private JLabel radioButtonLabel = new JLabel();
    private JTextField path1cText = new JTextField();
    private JTextField pathBaseText = new JTextField();
    private JTextField pathBackupText = new JTextField();
    private JButton choicePath1cButton = new JButton();
    private JButton choiceBaseButton = new JButton();
    private JButton choiceBackupButton = new JButton();
    private JButton saveButton = new JButton();
    private JButton closeButton = new JButton();
    private JRadioButton saveSettingsFileButton = new JRadioButton();
    private JRadioButton saveSettingsRegisterButton = new JRadioButton();
    private JFileChooser fileChooser = new JFileChooser();

    public SettingsForm(JFrame parent) {
        super(parent, Resource.getString("strTitleSettingForm"));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.log(Level.FINE, "Error set look and feel in update base form.");
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocale(Resource.getCurrentLocale());
        setSize(WIDTH_WINDOW, HEIGHT_WINDOW);
        setMaximumSize(new Dimension(WIDTH_WINDOW, HEIGHT_WINDOW));
        setPositionWindow();

        add(createGUI());
    }

    public class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("saveButton")) {
                saveSettings();
            } else if (e.getActionCommand().equals("closeButton")) {
                dispose();
            } else if (e.getActionCommand().equals("choicePath1cButton")) {
                int res = fileChooser.showOpenDialog(SettingsForm.this);
                if (res == JFileChooser.APPROVE_OPTION) {
                    path1cText.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            } else if (e.getActionCommand().equals("choiceBaseButton")) {
                int res = fileChooser.showOpenDialog(SettingsForm.this);
                if (res == JFileChooser.APPROVE_OPTION) {
                    pathBaseText.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            } else if (e.getActionCommand().equals("choiceBackupButton")) {
                int res = fileChooser.showOpenDialog(SettingsForm.this);
                if (res == JFileChooser.APPROVE_OPTION) {
                    pathBackupText.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            } else if (e.getActionCommand().equals("saveSettingsRegisterButton")) {
                if (settings instanceof Property) {
                    settings = Preference.getInstance();
                }
            } else if (e.getActionCommand().equals("saveSettingsFileButton")) {
                if (settings instanceof Preference) {
                    settings = Property.getInstance();
                }
            }
        }
    }

    private void saveSettings() {
        if (!path1cText.getText().isEmpty()) {
            settings.setSetting("path.1c", path1cText.getText());
        }
        if (!pathBaseText.getText().isEmpty()) {
            settings.setSetting("path.base", pathBaseText.getText());
        }
        if (!pathBackupText.getText().isEmpty()) {
            settings.setSetting("path.backup", pathBackupText.getText());
        }
        try {
            settings.storeSettings();
        } catch (IOException e) {
            logger.log(Level.CONFIG, e.getMessage());
        }
    }

    private void setPositionWindow() {
        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        int positionX = (int) ((dimScreen.getWidth() - WIDTH_WINDOW) / 2);
        int positionY = (int) ((dimScreen.getHeight() - HEIGHT_WINDOW) / 2);

        setLocation(positionX, positionY);
    }

    private JPanel createGUI() {
        ButtonListener buttonListener = new ButtonListener();
        choicePath1cButton.addActionListener(buttonListener);
        choiceBaseButton.addActionListener(buttonListener);
        choiceBackupButton.addActionListener(buttonListener);
        saveButton.addActionListener(buttonListener);
        closeButton.addActionListener(buttonListener);
        saveSettingsRegisterButton.addActionListener(buttonListener);
        saveSettingsFileButton.addActionListener(buttonListener);

        choicePath1cButton.setActionCommand("choicePath1cButton");
        choiceBaseButton.setActionCommand("choiceBaseButton");
        choiceBackupButton.setActionCommand("choiceBackupButton");
        saveButton.setActionCommand("saveButton");
        closeButton.setActionCommand("closeButton");
        saveSettingsRegisterButton.setActionCommand("saveSettingsRegisterButton");
        saveSettingsFileButton.setActionCommand("saveSettingsFileButton");

        ButtonGroup bg = new ButtonGroup();
        bg.add(saveSettingsFileButton);
        bg.add(saveSettingsRegisterButton);

        fileChooser.setDialogTitle(Resource.getString("selectDirectory"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        path1cLabel.setText(Resource.getString("path1cLabel") + ":");
        pathBaseLabel.setText(Resource.getString("pathBaseLabel") + ":");
        pathBackupLabel.setText(Resource.getString("pathBackupLabel") + ":");
        radioButtonLabel.setText(Resource.getString("strRadioButtonSettings") + ":");

        try {
            path1cText.setText(settings.getString("path.1c"));
            pathBaseText.setText(settings.getString("path.base"));
            pathBackupText.setText(settings.getString("path.backup"));
        } catch (NotFoundSettingException e) {
            logger.log(Level.CONFIG, e.getMessage());
        }

        choicePath1cButton.setText("...");
        choiceBaseButton.setText("...");
        choiceBackupButton.setText("...");
        saveSettingsFileButton.setText(Resource.getString("saveSettingsFileButton"));
        saveSettingsRegisterButton.setText(Resource.getString("saveSettingsRegisterButton"));
        saveButton.setText(Resource.getString("saveButton"));
        closeButton.setText(Resource.getString("CloseButton"));

        // Layout objects on the form
        JPanel pMain = BoxLayoutUtils.createVerticalPanel();
        pMain.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel pText = BoxLayoutUtils.createVerticalPanel();
//        JPanel panel = BoxLayoutUtils.createHorizontalPanel();
//        panel.add(radioButtonLabel);
//        panel.add(saveSettingsRegisterButton);
//        panel.add(saveSettingsFileButton);
//        pText.add(panel);
        pText.add(BoxLayoutUtils.createVerticalStrut(6));
        pText.add(path1cLabel);
        JPanel panel1 = BoxLayoutUtils.createHorizontalPanel();
        panel1.add(path1cText);
        panel1.add(choicePath1cButton);
        pText.add(panel1);
        pText.add(BoxLayoutUtils.createVerticalStrut(6));
        pText.add(pathBaseLabel);
        JPanel panel2 = BoxLayoutUtils.createHorizontalPanel();
        panel2.add(pathBaseText);
        panel2.add(choiceBaseButton);
        pText.add(panel2);
        pText.add(BoxLayoutUtils.createVerticalStrut(6));
        pText.add(pathBackupLabel);
        JPanel panel3 = BoxLayoutUtils.createHorizontalPanel();
        panel3.add(pathBackupText);
        panel3.add(choiceBackupButton);
        pText.add(panel3);

        JPanel pButton = BoxLayoutUtils.createHorizontalPanel();
        pButton.add(BoxLayoutUtils.createHorizontalGlue());
        pButton.add(saveButton);
        pButton.add(BoxLayoutUtils.createHorizontalStrut(6));
        pButton.add(closeButton);

        GUITools.makeSameSize(saveButton, closeButton);

        BoxLayoutUtils.setGroupAlignmentY(Component.TOP_ALIGNMENT, pText, pButton, pMain);

        pMain.add(pText);
        pMain.add(BoxLayoutUtils.createVerticalStrut(4));
        pMain.add(pButton);

        return pMain;
    }
}
