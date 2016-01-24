package com.vladshkerin;

import javax.swing.*;
import java.awt.*;

/**
 * Набор инструментов для окончательной
 * шлифовки и придания блеска интерфейсу
 * <p/>
 * Код взят из книги:
 * Название: Swing эффектные пользовательские интерфейсы
 * Автор: Иван Портянкин
 */
public class GUITools {

    // Придание группе компонентов одинаковых размеров
    // (минимальных, предпочтительных и максимальных).
    private void makeSameSize(JComponent... cs) {
        Dimension maxSize = cs[0].getPreferredSize();
        for (JComponent c : cs) {
            if (c.getPreferredSize().width > maxSize.width) {
                maxSize = c.getPreferredSize();
            }
        }

        for (JComponent c : cs) {
            c.setPreferredSize(maxSize);
            c.setMinimumSize(maxSize);
            c.setMaximumSize(maxSize);
        }
    }

    // Позволяет исправить оплошность в размерах текстового поля JTextField
    public static void fixTxtFieldSize(JTextField field) {
        Dimension size = field.getPreferredSize();
        // чтобы текстовое поле по прежнему могло
        // увеличивать свой размер в длину
        size.width = field.getMaximumSize().width;
        // теперь текстовое поел не станет выше
        // своей оптимальной высоты
        field.setMaximumSize(size);
    }
}
