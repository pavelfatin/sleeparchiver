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

package com.pavelfatin.sleeparchiver.gui.info;

import javax.swing.border.EtchedBorder;
import java.awt.*;

class EtchedBottomBorder extends EtchedBorder {
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.translate(x, y);

        g.setColor(etchType == LOWERED ? getShadowColor(c) : getHighlightColor(c));
        g.drawLine(0, height - 2, width - 1, height - 2);

        g.setColor(etchType == LOWERED ? getHighlightColor(c) : getShadowColor(c));
        g.drawLine(0, height - 1, width - 1, height - 1);

        g.translate(-x, -y);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 2, 0);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = 0;
        insets.top = 0;
        insets.right = 0;
        insets.bottom = 2;
        return insets;
    }
}
