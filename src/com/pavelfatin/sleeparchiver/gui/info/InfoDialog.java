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

package com.pavelfatin.sleeparchiver.gui.info;

import com.pavelfatin.sleeparchiver.gui.night.NightDialog;
import com.pavelfatin.sleeparchiver.lang.Utilities;
import com.pavelfatin.sleeparchiver.swing.ApplicationDialog;
import com.pavelfatin.sleeparchiver.swing.Builder;
import org.jdesktop.application.Action;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;

public class InfoDialog extends ApplicationDialog {
    private JButton _ok;
    private JEditorPane _pane;


    public InfoDialog(JFrame owner, String title, String file, boolean scrolling) {
        super(owner);

        setName(null);
        setTitle(title);

        setModal(true);
        setDefaultCloseOperation(NightDialog.DISPOSE_ON_CLOSE);

        getContentPane().add(createUI(scrolling));
        setFile(file);
        getResources().injectComponents(this);

        getRootPane().setDefaultButton(_ok);

        Utilities.registerAction(getRootPane(), JRootPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), getAction("ok"), "ACTION_OK");
    }

    protected JPanel createUI(boolean scrolling) {
        Builder b = new Builder("default:grow",
                "fill:default:grow, $lgap, default, $lgap");

        EmptyBorder spacer = new EmptyBorder(5, 10, 10, 10);
        _pane = createPane();
        _pane.setBorder(scrolling ? spacer : new CompoundBorder(new EtchedBottomBorder(), spacer));

        b.add(scrolling ? new JScrollPane(_pane) : _pane, 1, 1);
        b.add(createButtonPanel(), 1, 3);

        return b.getPanel();
    }


    private JPanel createButtonPanel() {
        Builder b = new Builder("default:grow, 50dlu, $lcgap",
                "default");

        _ok = b.createButton(getAction("ok"));

        b.add(_ok, 2, 1);

        return b.getPanel();
    }

    private JEditorPane createPane() {
        JEditorPane pane = new JEditorPane();

        pane.setEditable(false);
        pane.setFocusable(false);
        pane.setEditorKit(new SynchronousHTMLEditorKit());
        pane.addHyperlinkListener(new LinkRedirector());

        return pane;
    }

    private void setFile(String file) {
        URL url = getClass().getResource(file);
        if (url == null) {
            throw new RuntimeException("File not found: " + file);
        }

        try {
            _pane.setPage(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Action
    public void ok() {
        dispose();
    }
}
