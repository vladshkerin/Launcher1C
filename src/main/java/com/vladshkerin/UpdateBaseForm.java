package com.vladshkerin;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The form for update base 1C.
 */
public class UpdateBaseForm extends JDialog {

    private static Logger log = Logger.getLogger(UpdateBaseForm.class.getName());

    private static final int WIDTH_WINDOW = 380;
    private static final int HEIGHT_WINDOW = 250;

    private final JTextArea textArea = new JTextArea();
    private JProgressBar progressBar = new JProgressBar();
    private JButton stopButton = new JButton();
    private JButton closeButton = new JButton();
    private Thread threadUpdateBase;

    public UpdateBaseForm(JFrame parent) {
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
    }

    public void runUpdateBase() {
        ArrayList<String> errorList = (ArrayList<String>) Path.checkPath(Operations.UNLOAD_DB);
        if (!errorList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String str : errorList) {
                sb.append(str);
            }
            JOptionPane.showMessageDialog(null,
                    Resource.getString("ErrorRunUpdateBase") + ".\n\n" + sb,
                    Resource.getString("ErrorForm"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        TaskPool taskPool = new TaskPool();
        taskPool.setTextArea(textArea);
        taskPool.setProgressBar(progressBar);
        threadUpdateBase = new Thread(taskPool);
        threadUpdateBase.start();
    }

    public class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("closeButton")) {
                dispose();
            } else if (e.getActionCommand().equals("stopButton")) {
                threadUpdateBase.interrupt();
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
        ButtonListener buttonListener = new ButtonListener();
        stopButton.addActionListener(buttonListener);
        closeButton.addActionListener(buttonListener);

        stopButton.setActionCommand("stopButton");
        closeButton.setActionCommand("closeButton");

        stopButton.setText(Resource.getString("StopButton"));
        closeButton.setText(Resource.getString("CloseButton"));

        textArea.setEditable(false);
        textArea.setFont(Resource.getCurrentFont());
        JScrollPane scrollPane = new JScrollPane(textArea);

        progressBar.setOrientation(SwingConstants.HORIZONTAL);

        JPanel pMain = BoxLayoutUtils.createVerticalPanel();
        pMain.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel pText = BoxLayoutUtils.createVerticalPanel();
        pText.setBorder(new CompoundBorder(
                new TitledBorder(Resource.getString("strTitleUpgradeProcess")),
                new EmptyBorder(4, 4, 4, 4)));
        pText.add(scrollPane);
        pText.add(BoxLayoutUtils.createVerticalStrut(4));
        pText.add(progressBar);

        JPanel pButton = BoxLayoutUtils.createHorizontalPanel();
        pButton.add(BoxLayoutUtils.createHorizontalGlue());
        pButton.add(stopButton);
        pButton.add(BoxLayoutUtils.createHorizontalStrut(6));
        pButton.add(closeButton);
        GUITools.makeSameSize(stopButton, closeButton);

        pMain.add(pText);
        pMain.add(BoxLayoutUtils.createVerticalStrut(4));
        pMain.add(pButton);

        return pMain;
    }
}