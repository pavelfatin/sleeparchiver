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

package com.pavelfatin.sleeparchiver.gui.main.commands;

import com.pavelfatin.sleeparchiver.model.Night;
import com.pavelfatin.sleeparchiver.swing.MySelectionModel;
import com.pavelfatin.sleeparchiver.swing.RowTableCommand;
import com.pavelfatin.sleeparchiver.swing.RowTableModel;

import java.util.Comparator;

public class Editing extends RowTableCommand<Night> {
    private Comparator<Night> _order;
    private Night _data;

    private int _editingIndex;
    private Night _backup;
    private int _insertionIndex;


    public Editing(String name,
                   RowTableModel<Night> model,
                   MySelectionModel selection,
                   Comparator<Night> order,
                   Night data) {
        super(name, model, selection);
        _order = order;

        _editingIndex = selection.getMinSelectionIndex();
        _data = data;
    }

    public void doExecute() {
        _backup = getModel().getRowAt(_editingIndex);

        getModel().removeRowAt(_editingIndex);

        _insertionIndex = findIndexFor(_data, _order);
        getModel().addRowAt(_insertionIndex, _data);

        getSelection().setSelectionInterval(_insertionIndex, _insertionIndex);
    }

    public void doRevert() {
        getModel().removeRowAt(_insertionIndex);
        getModel().addRowAt(_editingIndex, _backup);
    }
}