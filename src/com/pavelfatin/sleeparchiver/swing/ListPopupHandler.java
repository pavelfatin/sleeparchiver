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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListPopupHandler extends MouseAdapter {
    private JList _list;
    private final JPopupMenu _menu;


    public ListPopupHandler(JList list, JPopupMenu menu) {
        _list = list;
        _menu = menu;
    }

    public void mousePressed(MouseEvent e) {
        if (isSelectionSingle()) {
            selectRow(e);
        }
        showPopup(e);
    }

    private boolean isSelectionSingle() {
        ListSelectionModel selection = _list.getSelectionModel();
        return selection.getMinSelectionIndex() >= 0 &&
                selection.getMinSelectionIndex() == selection.getMaxSelectionIndex();
    }

    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    private void selectRow(MouseEvent e) {
        int index = _list.locationToIndex(e.getPoint());
        if (index >= 0) {
            _list.getSelectionModel().setSelectionInterval(index, index);
        }
    }

    private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            _menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
