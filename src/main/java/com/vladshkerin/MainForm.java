package com.vladshkerin;

import com.vladshkerin.exception.FTPException;
import com.vladshkerin.exception.NotFoundSettingException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main form, designed to run and update the data (configuration) 1C.
 */
public class MainForm extends JFrame {

    private static Logger log = Logger.getLogger(MainForm.class.getName());
    private static Settings settings = Preference.getInstance();

    DefaultListModel listModel = new DefaultListModel();

    private JList listBase = new JList(listModel);
    private JLabel labelBase = new JLabel();
    private JButton enterpriseButton = new JButton();
    private JButton configButton = new JButton();
    private JButton updateButton = new JButton();
    private JButton checkButton = new JButton();
    private JButton settingButton = new JButton();
    private JButton exitButton = new JButton();

    public MainForm() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            URL imageResource = Launcher1C.class.getResource("/images/ariant.png");
            setIconImage(ImageIO.read(imageResource));
        } catch (Exception e) {
            log.log(Level.WARNING, "Error set look and feel in main form");
        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocale(Resource.getCurrentLocale());
        setSizeWindow();
        setPositionWindow();

        add(createGUI());

        runCheckUpdate();
    }

    protected class WindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent event) {
            runSaveSettingsAndExit();
        }
    }

    protected class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("enterpriseButton")) {
                if (runProcessBuilder(Operations.ENTERPRISE)) {
                    runSaveSettingsAndExit();
                }
            } else if (e.getActionCommand().equals("configButton")) {
                if (runProcessBuilder(Operations.CONFIG)) {
                    runSaveSettingsAndExit();
                }
            } else if (e.getActionCommand().equals("updateButton")) {
                UpdateBaseForm form = new UpdateBaseForm(MainForm.this);
                form.setVisible(true);
                form.runUpdateBase();
            } else if (e.getActionCommand().equals("checkButton")) {
                runProcessBuilder(Operations.CHECK);
            } else if (e.getActionCommand().equals("settingButton")) {
                SettingsForm form = new SettingsForm(MainForm.this);
                form.setVisible(true);
            }
        }
    }

    protected class SelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int selected = ((JList) e.getSource()).getSelectedIndex();
            String name = listModel.getElementAt(selected).toString();
            labelBase.setText("File=\"" + name + "\";");
        }
    }

    protected class UpdateAction extends AbstractAction {

        UpdateAction() {
            putValue(NAME, Resource.getString("CheckUpdateButton"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new UpdateProgramForm(MainForm.this);
        }
    }

    protected class ExitAction extends AbstractAction {

        ExitAction() {
            putValue(NAME, Resource.getString("ExitButton"));
        }

        public void actionPerformed(ActionEvent e) {
            runSaveSettingsAndExit();
        }
    }

    private JPanel createGUI() {
        // Settings objects
        WindowListener windowListener = new WindowListener();
        addWindowListener(windowListener);

        ButtonListener buttonListener = new ButtonListener();
        enterpriseButton.addActionListener(buttonListener);
        configButton.addActionListener(buttonListener);
        updateButton.addActionListener(buttonListener);
        checkButton.addActionListener(buttonListener);
        settingButton.addActionListener(buttonListener);
        exitButton.addActionListener(new ExitAction());

        enterpriseButton.setActionCommand("enterpriseButton");
        configButton.setActionCommand("configButton");
        updateButton.setActionCommand("updateButton");
        checkButton.setActionCommand("checkButton");
        settingButton.setActionCommand("settingButton");
        exitButton.setActionCommand("exitButton");

        enterpriseButton.setText(Resource.getString("EnterpriseButton"));
        configButton.setText(Resource.getString("ConfigButton"));
        updateButton.setText(Resource.getString("UpdateButton") + "...");
        checkButton.setText(Resource.getString("CheckButton") + "...");
        settingButton.setText(Resource.getString("settingButton") + "...");
        exitButton.setText(Resource.getString("ExitButton"));

        enterpriseButton.setToolTipText(Resource.getString("strToolTipEnterpriseButton"));
        configButton.setToolTipText(Resource.getString("strToolTipConfigButton"));
        updateButton.setToolTipText(Resource.getString("strToolTipUpdateButton"));
        checkButton.setToolTipText(Resource.getString("strToolTipCheckButton"));
        settingButton.setToolTipText(Resource.getString("strToolTipSettingButton"));

        //TODO to finish
        listModel.addElement("Ревизор");

        listBase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listBase.setSelectedIndex(0);
        listBase.setVisibleRowCount(5);
        listBase.setFont(Resource.getCurrentFont());
        listBase.addListSelectionListener(new SelectionListener());
        JScrollPane scrollPane = new JScrollPane(listBase);

        try {
//            String name = listModel.getElementAt(listBase.getSelectedIndex()).toString();
            labelBase.setText("File=\"" + settings.getString("path.base") + "\";");
        } catch (NotFoundSettingException e) {
            log.log(Level.CONFIG, e.getMessage());
        }
        labelBase.setFont(Resource.getCurrentFont());

        // Layout objects on the form
        JPanel pMain = BoxLayoutUtils.createHorizontalPanel();
        pMain.setBorder(BorderFactory.createEmptyBorder(8, 5, 5, 8));

        JPanel pText = BoxLayoutUtils.createVerticalPanel();
        pText.setBorder(new CompoundBorder(
                new TitledBorder(Resource.getString("strTitleBase")),
                new EmptyBorder(4, 4, 4, 4)));
        pText.add(scrollPane);
        pText.add(BoxLayoutUtils.createVerticalStrut(5));
        pText.add(labelBase);

        JPanel pButton = BoxLayoutUtils.createVerticalPanel();
        pButton.add(BoxLayoutUtils.createVerticalStrut(6));
        pButton.add(enterpriseButton);
        pButton.add(BoxLayoutUtils.createVerticalStrut(10));
        pButton.add(configButton);
        pButton.add(BoxLayoutUtils.createVerticalStrut(25));
        pButton.add(updateButton);
        pButton.add(BoxLayoutUtils.createVerticalStrut(10));
        pButton.add(checkButton);
        pButton.add(BoxLayoutUtils.createVerticalStrut(25));
        pButton.add(settingButton);
        pButton.add(BoxLayoutUtils.createVerticalGlue());
        pButton.add(exitButton);
        pButton.add(BoxLayoutUtils.createVerticalStrut(2));

        GUITools.makeSameSize(enterpriseButton, configButton, updateButton,
                checkButton, settingButton, exitButton);

        BoxLayoutUtils.setGroupAlignmentX(Component.LEFT_ALIGNMENT, scrollPane, labelBase);
        BoxLayoutUtils.setGroupAlignmentY(Component.TOP_ALIGNMENT, pText, pButton, pMain);

        pMain.add(pText);
        pMain.add(Box.createHorizontalStrut(7));
        pMain.add(pButton);

        return pMain;
    }

    private void setSizeWindow() {
        int widthWindow;
        int heightWindow;
        try {
            widthWindow = Integer.parseInt(settings.getString("width.size.window"));
            widthWindow = (widthWindow < 400 ? 400 : widthWindow);
            heightWindow = Integer.parseInt(settings.getString("height.size.window"));
            heightWindow = (heightWindow < 250 ? 250 : heightWindow);
        } catch (NotFoundSettingException | NumberFormatException e) {
            widthWindow = 450;
            heightWindow = 300;
            log.log(Level.WARNING, e.getMessage());
        }
        setSize(widthWindow, heightWindow);
    }

    private void setPositionWindow() {
        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        int defPositionX = (int) ((dimScreen.getWidth() - getWidth()) / 2);
        int defPositionY = (int) ((dimScreen.getHeight() - getHeight()) / 2);

        int positionX;
        int positionY;
        try {
            positionX = Integer.parseInt(settings.getString("width.position.window"));
            positionY = Integer.parseInt(settings.getString("height.position.window"));

            if (positionX > dimScreen.getWidth() || positionY > dimScreen.getHeight()) {
                throw new NotFoundSettingException("Loaded position of the window exceeds screen size");
            }
        } catch (NotFoundSettingException | NumberFormatException e) {
            positionX = defPositionX;
            positionY = defPositionY;
            log.log(Level.WARNING, e.getMessage());
        }

        setLocation(positionX, positionY);
    }

    private void runSaveSettingsAndExit() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                settings.setSetting("width.size.window", String.valueOf((int) getSize().getWidth()));
                settings.setSetting("height.size.window", String.valueOf((int) getSize().getHeight()));
                settings.setSetting("width.position.window", String.valueOf(getX()));
                settings.setSetting("height.position.window", String.valueOf(getY()));
                try {
                    settings.storeSettings();
                } catch (IOException e) {
                    log.log(Level.WARNING, e.getMessage());
                }
                System.exit(0);
            }
        });
        t.setDaemon(true);
        t.start();
        setVisible(false);
    }

    private void runCheckUpdate() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UpdateProgram updateProgram = new UpdateProgram();
                    if (updateProgram.isUpdate()) {

                        StringBuilder sbMsg = new StringBuilder();
                        sbMsg.append(String.format("%s\n\n",
                                Resource.getString("strNewVersionUpdate") +
                                        " \"" + Resource.getString("MainForm") + "\"."));
                        sbMsg.append(String.format("%-24s%s\n",
                                Resource.getString("strCurrentVersion") + ":",
                                "v" + Resource.getString("Application.version")));
                        sbMsg.append(String.format("%-26s%s\n",
                                Resource.getString("strNewVersion") + ":",
                                "v" + updateProgram.getNewVersion()));
                        sbMsg.append(String.format(Resource.getCurrentLocale(), "%-21s%.3f MB\n\n",
                                Resource.getString("strSizeUpdate") + ":",
                                updateProgram.getSizeFile() / 1024.0 / 1024.0));
                        sbMsg.append(String.format("%s",
                                Resource.getString("strToUpgrade") + "?"));

                        int res = JOptionPane.showConfirmDialog(null,
                                sbMsg,
                                Resource.getString("UpdateForm"),
                                JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {

                            if (updateProgram.update()) {
                                String text = Resource.getString("strCompleteUpdate") + "."
                                        + "\n" + Resource.getString("strRestartProgram") + ".";
                                JOptionPane.showMessageDialog(null,
                                        text,
                                        Resource.getString("InformationForm"),
                                        JOptionPane.INFORMATION_MESSAGE);
                                System.exit(0);
                            }
                        }
                    }
                } catch (FTPException e) {
                    log.log(Level.SEVERE, e.getMessage());
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private boolean runProcessBuilder(Operations operations) {
        ArrayList<String> errorList = (ArrayList<String>) Path.checkPath(operations);
        if (!errorList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String str : errorList) {
                sb.append(str);
            }
            JOptionPane.showMessageDialog(null,
                    Resource.getString("ErrorRunProcess") + ".\n\n" + sb,
                    Resource.getString("ErrorForm"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        ProcessBuilder process = new ProcessBuilder(Command.getString(operations));
        try {
            process.start();
            return true;
        } catch (ArrayIndexOutOfBoundsException | SecurityException | IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            return false;
        }
    }
}
