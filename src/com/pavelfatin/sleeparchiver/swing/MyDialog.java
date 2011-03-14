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

import com.pavelfatin.sleeparchiver.gui.night.NightDialog;
import com.pavelfatin.sleeparchiver.lang.Utilities;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MyDialog extends ApplicationDialog {
    private JPanel _content;
    private JPanel _footer;
    private JButton _ok;

    private String _title = "";
    private boolean _modified;
    private boolean _accepted;

    private boolean _acceptEnabled = true;


    public MyDialog(JFrame frame, String name, boolean cancelable) {
        super(frame);
        init(name, cancelable);
    }

    public MyDialog(JDialog dialog, String name, boolean cancelable) {
        super(dialog);
        init(name, cancelable);
    }

    private void init(String name, boolean cancelable) {
        setName(name);
        setModal(true);
        setDefaultCloseOperation(NightDialog.DISPOSE_ON_CLOSE);

        getContentPane().add(createRoot(cancelable));
        getResources().injectComponents(this);

        getRootPane().setDefaultButton(_ok);

        Utilities.registerAction(getRootPane(), JRootPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), getOwnAction("escape"), "ACTION_ESCAPE");
    }

    public void setContent(JComponent content) {
        getResources().injectComponents(content);
        _content.removeAll();
        _content.add(content);
    }

    public void setFooter(JComponent footer) {
        getResources().injectComponents(footer);
        _footer.removeAll();
        _footer.add(footer);
    }

    private JPanel createRoot(boolean cancelable) {
        Builder b = new Builder("default:grow",
                "fill:default:grow, 6dlu, default");

        _content = new JPanel(new BorderLayout());

        b.add(_content, 1, 1);
        b.add(createButtons(cancelable), 1, 3);

        b.setDialogBorder();

        return b.getPanel();
    }

    private JPanel createButtons(boolean cancelable) {
        Builder b = new Builder(cancelable ? "default:grow, 50dlu, $lcgap, 50dlu" : "default:grow, 50dlu",
                "default");

        _footer = new JPanel(new BorderLayout());
        _ok = b.createButton(getOwnAction("ok"));

        b.add(_footer, 1, 1);
        b.add(_ok, 2, 1);
        if (cancelable) {
            b.add(b.createButton(getOwnAction("cancel")), 4, 1);
        }

        return b.getPanel();
    }

    private javax.swing.Action getOwnAction(String key) {
        return getContext().getActionMap(MyDialog.class, this).get(key);
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
        updateDialogTitle();
    }

    public boolean isModified() {
        return _modified;
    }

    public void setModified(boolean modified) {
        _modified = modified;
        updateDialogTitle();
    }

    private void updateDialogTitle() {
        super.setTitle(_title + (_modified ? " *" : ""));
    }

    public boolean isAccepted() {
        return _accepted;
    }

    public boolean isAcceptEnabled() {
        return _acceptEnabled;
    }

    public void setAcceptEnabled(boolean enabled) {
        boolean previous = _acceptEnabled;
        _acceptEnabled = enabled;
        firePropertyChange("acceptEnabled", enabled, previous);
    }

    @Action
    public void escape() {
        if (!_modified) {
            cancel();
        }
    }

    @Action(enabledProperty = "acceptEnabled")
    public void ok() {
        _accepted = true;
        onAccept();
        dispose();
    }

    @Action
    public void cancel() {
        dispose();
    }

    protected void onAccept() {
        // default implementation
    }
}
