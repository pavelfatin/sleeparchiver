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

package com.pavelfatin.sleeparchiver.gui.editor;

import com.pavelfatin.sleeparchiver.swing.Builder;
import com.pavelfatin.sleeparchiver.swing.MyDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditorDialog extends MyDialog {
    private JTextField _value;
    private String _initial = "";


    public EditorDialog(JDialog owner, String title) {
        super(owner, "dialogEditor", true);

        setTitle(title);
        setContent(createUI());
        updateAcceptEnabled();
    }

    protected JPanel createUI() {
        Builder b = new Builder("[150dlu,default]:grow",
                "default");

        _value = b.createTextField("value");
        _value.getDocument().addDocumentListener(new ValueListener());

        b.add(_value, 1, 1);

        return b.getPanel();
    }

    public String getText() {
        return _value.getText().trim().toLowerCase().replaceAll("\\s+", " ");
    }

    public void setText(String s) {
        _initial = s;
        _value.setText(s);
        _value.selectAll();
    }

    private void updateAcceptEnabled() {
        setAcceptEnabled(!getText().isEmpty());
    }

    private void onChange() {
        updateAcceptEnabled();
        setModified(!_initial.equals(getText()));
    }


    private class ValueListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            onChange();
        }

        public void removeUpdate(DocumentEvent e) {
            onChange();
        }

        public void changedUpdate(DocumentEvent e) {
            // do nothing
        }
    }
}
