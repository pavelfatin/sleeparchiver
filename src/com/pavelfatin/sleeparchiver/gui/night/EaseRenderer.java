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

package com.pavelfatin.sleeparchiver.gui.night;

import com.pavelfatin.sleeparchiver.model.Ease;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;

class EaseRenderer extends DefaultListCellRenderer {
    private ResourceMap _map;


    EaseRenderer(ResourceMap map) {
        _map = map;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Ease ease = (Ease) value;
        String text = ease.format(_map);
        return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
    }
}