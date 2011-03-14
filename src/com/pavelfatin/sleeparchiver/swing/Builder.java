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

package com.pavelfatin.sleeparchiver.swing;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

public class Builder {
    private JPanel _panel;
    private CellConstraints _cc = new CellConstraints();


    public Builder(String cols, String rows) {
        _panel = createPanel(new FormLayout(cols, rows));
    }

    public JPanel createPanel(LayoutManager layout) {
        return new JPanel(layout);
    }

    public static JLabel createLabel(String name) {
        return withName(new JLabel(), name);
    }

    public JLabel createLabel(String name, JComponent component) {
        return labelFor(createLabel(name), component);
    }

    public static MyTable createTable(String name) {
        return withName(withNoFocusManagement(new MyTable()), name);
    }

    public MySeparator createSeparator(String name) {
        return withName(new MySeparator(), name);
    }

    public MyTimeField createTimeField(String name) {
        return withName(new MyTimeField(), name);
    }

    public MyDateField createDateField(String name) {
        return withName(new MyDateField(), name);
    }

    public static JList createList(String name) {
        return withName(new JList(), name);
    }

    public JComboBox createComboBox(String name) {
        return withName(new JComboBox(), name);
    }

    public MyComboBox createMyComboBox(String name) {
        return withName(new MyComboBox(), name);
    }

    public JButton createButton(Action action) {
        return new JButton(action);
    }

    public JTextField createTextField(String name) {
        return withName(new JTextField(), name);
    }

    public static JTextArea createTextArea(String name) {
        return withName(withNoFocusManagement(new MyTextArea()), name);
    }

    public JCheckBox createCheckBox(String name) {
        return withName(new JCheckBox(), name);
    }

    public void add(JComponent component, int col, int row) {
        _panel.add(component, _cc.xy(col, row));
    }

    public void add(JComponent component, int col, int row, int width) {
        _panel.add(component, _cc.xyw(col, row, width));
    }

    public void add(JComponent component, int col, int row, int width, int height) {
        _panel.add(component, _cc.xywh(col, row, width, height));
    }

    public void add(JComponent component, int col, int row, int width, int height,
                    CellConstraints.Alignment colAlign, CellConstraints.Alignment rowAlign) {
        _panel.add(component, _cc.xywh(col, row, width, height, colAlign, rowAlign));
    }

    public void add(JLabel label, int lcol, int lrow,
                    JComponent component, int ccol, int crow) {
        _panel.add(labelFor(label, component), _cc.xy(lcol, lrow));
        _panel.add(component, _cc.xy(ccol, crow));
    }

    public void setDialogBorder() {
        _panel.setBorder(Borders.DIALOG_BORDER);
    }

    public JPanel getPanel() {
        return _panel;
    }

    public static <T extends JComponent> T withName(T component, String name) {
        component.setName(name);
        return component;
    }

    public static <T extends JLabel> T labelFor(T label, JComponent component) {
        label.setLabelFor(component);
        return label;
    }

    public static <T extends JComponent> T withNoFocusManagement(T component) {
        component.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        component.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        return component;
    }

    public JSpinner createSpinner(String name) {
        return withName(new JSpinner(), name);
    }

    public static JPanel createLine(JComponent... components) {
        return createLine("0dlu", components);
    }

    public static JPanel createLine(String indent, JComponent... components) {
        String columns = String.format("%s, default", indent) +
                (components.length > 1
                        ? String.format(", %d * ($lcgap, default)", components.length - 1)
                        : "");
        Builder b = new Builder(columns,
                "default");
        for (int i = 0; i < components.length; i++) {
            b.add(components[i], 2 + i * 2, 1);
        }
        return b.getPanel();
    }

    public static JComponent withSeparator(String name, JComponent component) {
        Builder b = new Builder(
                "default:grow",
                "default, 6dlu, fill:default:grow");

        b.add(b.createSeparator(name), 1, 1);
        b.add(component, 1, 3);

        return b.getPanel();
    }
}
