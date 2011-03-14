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
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyTextArea extends JTextArea {
    private UndoManager _undoManager;


    public MyTextArea() {
        addKeyListener(new UndoKeysHandler());
        addFocusListener(new FocusHandler());
    }

    private void undo() {
        try {
            _undoManager.undo();
        }
        catch (CannotUndoException cue) {
            // do nothing
        }
    }

    private void redo() {
        try {
            _undoManager.redo();
        }
        catch (CannotRedoException cue) {
            // do nothing
        }
    }


    private class UndoKeysHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                undo();
            }

            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
                redo();
            }
        }
    }

    private class FocusHandler extends FocusAdapter {
        @Override
        public void focusGained(FocusEvent e) {
            if (_undoManager == null) {
                _undoManager = new UndoManager();
                getDocument().addUndoableEditListener(_undoManager);
            }
        }
    }
}
