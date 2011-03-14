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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Importing extends RowTableCommand<Night> {
    private Comparator<Night> _order;
    private List<Night> _data;

    private List<Integer> _insertionIndices;


    public Importing(String name,
                     RowTableModel<Night> model,
                     MySelectionModel selection,
                     Comparator<Night> order,
                     List<Night> nights) {
        super(name, model, selection);

        _order = order;
        _data = new ArrayList<Night>(nights);
    }

    public void doExecute() {
        _insertionIndices = new ArrayList<Integer>();
        for (Night night : _data) {
            int index = findIndexFor(night, _order);
            getModel().addRowAt(index, night);
            _insertionIndices.add(index);
        }

        getSelection().setSelectedIndices(_insertionIndices);
    }

    public void doRevert() {
        for (Integer index : reversed(sorted(_insertionIndices))) {
            getModel().removeRowAt(index);
        }
    }
}