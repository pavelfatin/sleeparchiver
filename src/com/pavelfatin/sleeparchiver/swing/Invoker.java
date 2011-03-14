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
import java.util.List;


public class Invoker {
    private List<Command> _commands;
    private int _lastCommandIndex;


    public Invoker() {
        reset();
    }

    public void reset() {
        _commands = new ArrayList<Command>();
        _lastCommandIndex = -1;
    }

    public void invoke(Command command) {
        command.execute();

        int hightIndex = _commands.size() - 1;
        if (_lastCommandIndex < hightIndex) {
            _commands.subList(_lastCommandIndex + 1, hightIndex + 1).clear();
        }

        _commands.add(command);
        _lastCommandIndex = _commands.indexOf(command);
    }

    public void undo() {
        _commands.get(_lastCommandIndex).revert();
        _lastCommandIndex--;
    }

    public void redo() {
        _lastCommandIndex++;
        _commands.get(_lastCommandIndex).execute();
    }

    public boolean isUndoAvailable() {
        return _lastCommandIndex >= 0;
    }

    public boolean isRedoAvailable() {
        int highIndex = _commands.size() - 1;
        return _lastCommandIndex < highIndex;
    }

    public String getUndoCommandName() {
        if (!isUndoAvailable()) {
            throw new IllegalStateException("No undo command available");
        }
        return _commands.get(_lastCommandIndex).getName();
    }

    public String getRedoCommandName() {
        if (!isRedoAvailable()) {
            throw new IllegalStateException("No redo command available");
        }
        return _commands.get(_lastCommandIndex + 1).getName();
    }
}
