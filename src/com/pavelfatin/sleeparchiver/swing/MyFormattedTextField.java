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

public class MyFormattedTextField extends JFormattedTextField {
    public MyFormattedTextField(AbstractFormatter formatter) {
        super(formatter);
    }

    public void setPrototype(Object prototype) {
        Object value = getValue();

        setValue(prototype);
        Dimension original = getPreferredSize();

        Dimension updated = new Dimension(original.width + widthOf("_ _ "), original.height);
        setPreferredSize(updated);

        setValue(value);
    }

    private int widthOf(String text) {
        FontMetrics metrics = getFontMetrics(getFont());
        return metrics.stringWidth(text);
    }
}
