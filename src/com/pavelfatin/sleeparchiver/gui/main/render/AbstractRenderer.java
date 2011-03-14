/*
 * SleepArchiver - cross-platform data manager for Sleeptracker-series watches.
 * Copyright (C) 2009-2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.sleeparchiver.gui.main.render;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public abstract class AbstractRenderer<T> extends JComponent implements TableCellRenderer {
    private T _value;
    private boolean _selected;
    private boolean _focused;


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        _selected = isSelected;
        _focused = hasFocus;
        _value = (T) value;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        Color color = g2d.getColor();
        Color background = g2d.getBackground();
        Stroke stroke = g2d.getStroke();

        render(g2d, _value, _selected, _focused);

        g2d.setColor(color);
        g2d.setBackground(background);
        g2d.setStroke(stroke);
    }

    protected abstract void render(Graphics2D g, T v, boolean selected, boolean focused);
}
