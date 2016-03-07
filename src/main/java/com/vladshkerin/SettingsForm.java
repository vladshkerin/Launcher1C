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

    private static Logger log = Logger.getLogger(UpdateBaseForm.class.getName());

    private static final int WIDTH_WINDOW = 410;
    private static final int HEIGHT_WINDOW = 210;

    private JLabel path1cLabel = new JLabel();
    private JLabel pathBaseLabel = new JLabel();
    private JLabel pathBackupLabel = new JLabel();
    private JTextField path1cText = new JTextField();
    private JTextField pathBaseText = new JTextField();
    private JTextField pathBackupText = new JTextField();
    private JButton choicePath1cButton = new JButton();
    private JButton choiceBaseButton = new JButton();
    private JButton choiceBackupButton = new JButton();
    private JButton saveButton = new JButton();
    private JButton closeButton = new JButton();
    private JFileChooser fileChooser = new JFileChooser();

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
//        setMaximumSize(new Dimension(WIDTH_WINDOW, HEIGHT_WINDOW));
        setPositionWindow();

//        Settings.initSettings();

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
            }
        }
    }

    private void saveSettings() {
        if (!path1cText.getText().isEmpty()) {
            Settings.setSetting("path.1c", path1cText.getText());
        }
        if (!pathBaseText.getText().isEmpty()) {
            Settings.setSetting("path.base", pathBaseText.getText());
        }
        if (!pathBackupText.getText().isEmpty()) {
            Settings.setSetting("path.backup", pathBackupText.getText());
        }
        try {
            Settings.storeSettings();
        } catch (IOException e) {
            log.log(Level.CONFIG, e.getMessage());
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
        choicePath1cButton.addActionListener(buttonListener);
        choiceBaseButton.addActionListener(buttonListener);
        choiceBackupButton.addActionListener(buttonListener);
        saveButton.addActionListener(buttonListener);
        closeButton.addActionListener(buttonListener);

        choicePath1cButton.setActionCommand("choicePath1cButton");
        choiceBaseButton.setActionCommand("choiceBaseButton");
        choiceBackupButton.setActionCommand("choiceBackupButton");
        saveButton.setActionCommand("saveButton");
        closeButton.setActionCommand("closeButton");

        choicePath1cButton.setText("...");
        choiceBaseButton.setText("...");
        choiceBackupButton.setText("...");
        saveButton.setText(Resource.getString("saveButton"));
        closeButton.setText(Resource.getString("CloseButton"));

        //TODO set text
        fileChooser.setDialogTitle("Выберите каталог");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Layout objects on the form
        JPanel pMain = BoxLayoutUtils.createVerticalPanel();
        pMain.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel pText = BoxLayoutUtils.createVerticalPanel();
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
