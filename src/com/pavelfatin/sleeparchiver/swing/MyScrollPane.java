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
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

public class MyScrollPane extends JScrollPane {
    private List<MouseZoomListen> _mouseZoomListeners = new ArrayList<MouseZoomListen>();


    public MyScrollPane() {
    }

    public MyScrollPane(Component view) {
        super(view);
    }

    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e) {
        if (e.isControlDown() && e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            processMouseZoomEvent(e);
            e.consume();
        } else {
            super.processMouseWheelEvent(e);
        }
    }

    private void processMouseZoomEvent(MouseWheelEvent e) {
        int rotation = e.getWheelRotation();

        if (rotation < 0) {
            fireZoomedIn();
        }
        if (rotation > 0) {
            fireZoomedOut();
        }
    }

    public void addMouseZoomListener(MouseZoomListen listen) {
        _mouseZoomListeners.add(0, listen);
    }

    private void fireZoomedIn() {
        for (MouseZoomListen listener : _mouseZoomListeners) {
            listener.zoomIn();
        }
    }

    private void fireZoomedOut() {
        for (MouseZoomListen listener : _mouseZoomListeners) {
            listener.zoomOut();
        }
    }
}
