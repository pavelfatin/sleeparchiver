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
import java.util.Iterator;
import java.util.List;

public class Removal extends RowTableCommand<Night> {
    private List<Integer> _removalIndices;

    private List<Night> _backup;


    public Removal(String name,
                   RowTableModel<Night> model,
                   MySelectionModel selection) {
        super(name, model, selection);

        _removalIndices = selection.getSelectedIndices();
    }

    public void doExecute() {
        _backup = getNightsAt(_removalIndices);

        for (Integer index : reversed(_removalIndices)) {
            getModel().removeRowAt(index);
        }

        Integer min = _removalIndices.get(0);
        int index = min <= getModel().getLastIndex() ? min : getModel().getLastIndex();
        getSelection().setSelectionInterval(index, index);
    }

    public void doRevert() {
        Iterator<Night> it = _backup.iterator();
        for (Integer index : _removalIndices) {
            getModel().addRowAt(index, it.next());
        }
    }

    protected List<Night> getNightsAt(List<Integer> indices) {
        List<Night> nights = new ArrayList<Night>();
        for (Integer index : indices) {
            nights.add(getModel().getRowAt(index));
        }
        return nights;
    }
}