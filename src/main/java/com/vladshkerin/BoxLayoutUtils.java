package com.vladshkerin;

import javax.swing.*;
import java.awt.*;

/**
 * Class for easy work with a BoxLayout Manager
 * <p/>
 * Code taken from the book:
 * Title: Swing эффектные пользовательские интерфейсы (Издание 2-е)
 * Author: Иван Портянкин
 * Publisher: Лори
 */
public class BoxLayoutUtils {

    // Returns the panel with the installed vertical block layout.
    public static JPanel createVerticalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    // Returns the panel with the installed horizontal block layout.
    public static JPanel createHorizontalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        return p;
    }

    // Creates a vertical strut fixed size.
    public static Component createVerticalStrut(int size) {
        return Box.createRigidArea(new Dimension(0, size));
    }

    // Creates a horizontal strut of a fixed size.
    public static Component createHorizontalStrut(int size) {
        return Box.createRigidArea(new Dimension(size, 0));
    }

    // Creates a vertical spring.
    public static Component createVerticalGlue() {
        return Box.createVerticalGlue();
    }

    // Creates a horizontal spring.
    public static Component createHorizontalGlue() {
        return Box.createHorizontalGlue();
    }

    // Sets a common alignment along the X axis for component groups.
    public static void setGroupAlignmentX(float alignment, JComponent... cs) {
        for (JComponent c : cs) {
            c.setAlignmentX(alignment);
        }
    }

    // Sets a common alignment axis Y for component group.
    public static void setGroupAlignmentY(float alignment, JComponent... cs) {
        for (JComponent c : cs) {
            c.setAlignmentY(alignment);
        }
    }
}
