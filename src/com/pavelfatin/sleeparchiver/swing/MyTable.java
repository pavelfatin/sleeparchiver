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

// To overcome http://bugs.sun.com/view_bug.do?bug_id=4127936
public class MyTable extends JTable {
    // when the viewport shrinks below the preferred size, stop tracking the viewport width
    public boolean getScrollableTracksViewportWidth() {
        if (autoResizeMode != AUTO_RESIZE_OFF) {
            if (getParent() instanceof JViewport) {
                return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
            }
        }
        return false;
    }

    // when the viewport shrinks below the preferred size, return the minimum size
    // so that scrollbars will be shown
    public Dimension getPreferredSize() {
        if (getParent() instanceof JViewport) {
            if (((JViewport) getParent()).getWidth() < super.getPreferredSize().width) {
                return getMinimumSize();
            }
        }

        return super.getPreferredSize();
    }

    public void scrollToSelection() {
        ListSelectionModel selection = getSelectionModel();
        if (!selection.isSelectionEmpty()) {
            int min = selection.getMinSelectionIndex();
            int max = selection.getMaxSelectionIndex();

            Rectangle area = null;
            for (int i = min; i <= max; i++) {
                if (selection.isSelectedIndex(i)) {
                    Rectangle r = getCellRect(i, 0, true);
                    area = area == null ? r : area.union(r);
                }
            }
            scrollRectToVisible(area);
        }
    }
}
