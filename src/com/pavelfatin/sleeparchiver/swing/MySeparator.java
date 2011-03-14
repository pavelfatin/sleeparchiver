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

import javax.swing.*;
import java.awt.*;

public class MySeparator extends JComponent {
    private JLabel _label = new JLabel();
    private JSeparator _separator = new JSeparator();


    public MySeparator() {
        setLayout(new GridBagLayout());

        add(_label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(Box.createHorizontalStrut(3), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(_separator, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        setFont(UIManager.getFont("TitledBorder.font"));
    }

    public String getTitle() {
        return _label.getText();
    }

    public void setTitle(String title) {
        String old = getTitle();
        _label.setText(title);
        firePropertyChange("title", old, getTitle());
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        _label.setFont(font);
    }

}
