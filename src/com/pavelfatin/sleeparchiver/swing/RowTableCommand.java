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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class RowTableCommand<T> extends AbstractCommand {
    private RowTableModel<T> _model;
    private MySelectionModel _selection;

    private List<Integer> _indicies;


    protected RowTableCommand(String name, RowTableModel<T> model, MySelectionModel selection) {
        super(name);

        _model = model;
        _selection = selection;
    }

    protected RowTableModel<T> getModel() {
        return _model;
    }

    protected MySelectionModel getSelection() {
        return _selection;
    }

    protected static List<Integer> reversed(List<Integer> indices) {
        List<Integer> reversed = new ArrayList<Integer>(indices);
        Collections.reverse(reversed);
        return reversed;
    }

    protected static List<Integer> sorted(List<Integer> indices) {
        List<Integer> sorted = new ArrayList<Integer>(indices);
        Collections.sort(sorted);
        return sorted;
    }

    protected int findIndexFor(T row, Comparator<T> comparator) {
        int i;
        for (i = 0; i < _model.getRowCount(); i++) {
            T each = _model.getRowAt(i);
            if (comparator.compare(row, each) < 0) {
                break;
            }
        }
        return i;
    }

    public void execute() {
        _indicies = _selection.getSelectedIndices();

        doExecute();
    }

    public void revert() {
        doRevert();

        _selection.setSelectedIndices(_indicies);
    }


    protected abstract void doExecute();

    protected abstract void doRevert();
}
