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

package com.pavelfatin.sleeparchiver.gui.error;

import com.pavelfatin.sleeparchiver.lang.Utilities;
import com.pavelfatin.sleeparchiver.swing.ApplicationDialog;
import com.pavelfatin.sleeparchiver.swing.Builder;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorDialog extends ApplicationDialog {
    private JButton _ok;
    private JButton _details;

    private Throwable _exception;
    private String _info;
    private boolean _detailed;
    private JPanel _pane;
    private JTextArea _text;


    public ErrorDialog(JFrame owner, Throwable exception, String info) {
        super(owner);

        setName("dialogError");

        _exception = exception;
        _info = info;

        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().add(createUI());
        getResources().injectComponents(this);

        configureMainActions(getRootPane(), _ok);
        updateControls();
    }

    private void updateControls() {
        _details.setIcon(getResources().getIcon(_detailed ? "iconUp" : "iconDown"));
        _pane.setVisible(_detailed);
        if (isShowing()) {
            pack();
        }
    }

    protected JPanel createUI() {
        Builder b = new Builder("[250dlu,default]:grow",
                "default, 6dlu, default, default");

        JLabel message = b.createLabel("labelMessage");
        message.setIcon(UIManager.getIcon("OptionPane.errorIcon"));

        _pane = createPane();

        b.add(message, 1, 1);
        b.add(createButtons(), 1, 3);
        b.add(_pane, 1, 4);

        b.setDialogBorder();

        return b.getPanel();
    }

    private JPanel createButtons() {
        Builder b = new Builder("50dlu, default:grow, 50dlu",
                "default");

        _ok = b.createButton(getAction("ok"));

        _details = b.createButton(getAction("details"));
        _details.setHorizontalTextPosition(SwingConstants.LEADING);

        b.add(_details, 1, 1);
        b.add(_ok, 3, 1);

        return b.getPanel();
    }

    private JPanel createPane() {
        Builder b = new Builder("default:grow",
                "6dlu, default, 3dlu, default");

        _text = b.createTextArea("labelException");

        String message = String.format("%s\r\n\r\n%s",
                _info,
                detailsOf(_exception));

        _text.setRows(15);
        _text.setEditable(false);

        _text.setText(message);
        _text.setCaretPosition(0);

        b.add(new JScrollPane(_text), 1, 2);
        b.add(createDetaisButtons(), 1, 4);

        return b.getPanel();
    }

    private String detailsOf(Throwable exception) {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        return writer.getBuffer().toString();
    }

    private JPanel createDetaisButtons() {
        Builder b = new Builder("default, default:grow",
                "default");
        b.add(b.createButton(getAction("copy")), 1, 1);
        return b.getPanel();
    }

    private void configureMainActions(JRootPane root, JButton buttonOk) {
        root.setDefaultButton(buttonOk);
        Utilities.registerAction(root, JRootPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                getAction("ok"), "ACTION_OK");
    }

    @Action
    public void ok() {
        dispose();
    }

    @Action
    public void details() {
        _detailed = !_detailed;
        updateControls();
    }

    @Action
    public void copy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(_text.getText());
        clipboard.setContents(data, data);
    }

    public void open() {
        pack();
        setLocationRelativeTo(getParent());
        setVisible(true);
    }
}
