package com.vladshkerin;

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
 * Class form for update base 1C
 */
public class UpdateBaseForm extends JDialog {

    private static Logger log = Logger.getLogger(UpdateProgram.class.getName());

    private static final int WIDTH_WINDOW = 500;
    private static final int HEIGHT_WINDOW = 100;

    private JTextArea textArea = new JTextArea();
    private JButton stopButton = new JButton();
    private JButton closeButton = new JButton();

    UpdateBaseForm updateBaseForm;

    public UpdateBaseForm(JFrame parent) {
        super(parent, Resource.getString("UpdateBaseForm"));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.log(Level.WARNING, "Error set look and feel in update form.");
        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocale(Resource.getCurrentLocale());
        setSize(WIDTH_WINDOW, HEIGHT_WINDOW);

        add(createGUI(), BorderLayout.CENTER);
        setVisible(true);
    }

    public class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("closeButton")) {
                dispose();
            } else if (e.getActionCommand().equals("stopButton")) {
                //TODO empty
            }
        }
    }

    private JPanel createGUI() {
        ButtonListener buttonListener = new ButtonListener();
        stopButton.addActionListener(buttonListener);
        closeButton.addActionListener(buttonListener);

        stopButton.setText(Resource.getString("stopButton"));
        closeButton.setText(Resource.getString("closeButton"));

        JPanel pMain = BoxLayoutUtils.createVerticalPanel();
        pMain.setBorder(BorderFactory.createEmptyBorder(10, 6, 8, 6));

        JPanel pText = BoxLayoutUtils.createHorizontalPanel();
        pText.setBorder(new CompoundBorder(
                new TitledBorder("Процесс обновления"), new EmptyBorder(4, 4, 4, 4)));
        pText.add(textArea);

        JPanel pButton = BoxLayoutUtils.createHorizontalPanel();
        pButton.add(stopButton);
        pButton.add(BoxLayoutUtils.createHorizontalStrut(10));
        pButton.add(closeButton);

        pMain.add(pText);
        pMain.add(BoxLayoutUtils.createHorizontalStrut(8));
        pMain.add(pButton);

        GUITools.makeSameSize(pText, pButton);

        return pMain;
    }


}
