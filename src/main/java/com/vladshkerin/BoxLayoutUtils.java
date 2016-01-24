package com.vladshkerin;

import javax.swing.*;
import java.awt.*;

/**
 * Класс для удобной работы с менеджером BoxLayout
 * <p/>
 * Код взят из книги:
 * Название: Swing эффектные пользовательские интерфейсы
 * Автор: Иван Портянкин
 */
public class BoxLayoutUtils {

    // возвращает панель с установленным вертикальным блочным расположением
    public static JPanel createVerticalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    // возвращает панель с установленным горизонтальным блочным расположением
    public static JPanel createHorizontalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        return p;
    }

    // создаёт вертикальную распорку фиксированного размера
    public static Component createVerticalStrut(int size) {
        return Box.createRigidArea(new Dimension(0, size));
    }

    // создаёт горизонтальную распорку фиксированного размера
    public static Component createHorizontalStrut(int size) {
        return Box.createRigidArea(new Dimension(size, 0));
    }

    // задаёт единое выравнивание по оси X для группы компонентов
    public static void setGroupAlignmentX(float alignment, JComponent... cs) {
        for (JComponent c : cs) {
            c.setAlignmentX(alignment);
        }
    }

    // задаёт единое выравнивание по оси Y для группы компонентов
    public static void setGroupAlignmentY(float alignment, JComponent... cs) {
        for (JComponent c : cs) {
            c.setAlignmentY(alignment);
        }
    }
}