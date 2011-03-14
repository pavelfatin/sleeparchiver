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
import java.util.List;

public class Replacing extends RowTableCommand<Night> {
    private List<Night> _replacement;
    private List<Night> _original;


    public Replacing(String name,
                     RowTableModel<Night> model,
                     MySelectionModel selection,
                     List<Night> replacement) {
        super(name, model, selection);

        _replacement = new ArrayList<Night>(replacement);
    }

    public void doExecute() {
        _original = new ArrayList(getModel().getRows());

        List<Integer> indices = getSelection().getSelectedIndices();
        getModel().setRows(_replacement);
        getSelection().setSelectedIndices(indices);
    }

    public void doRevert() {
        getModel().setRows(_original);
    }
}