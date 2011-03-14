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

package com.pavelfatin.sleeparchiver.gui.download;

import com.pavelfatin.sleeparchiver.gui.night.NightDialog;
import com.pavelfatin.sleeparchiver.lang.Utilities;
import com.pavelfatin.sleeparchiver.model.Device;
import com.pavelfatin.sleeparchiver.model.Night;
import com.pavelfatin.sleeparchiver.swing.ApplicationDialog;
import com.pavelfatin.sleeparchiver.swing.Builder;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutionException;

public class DownloadDialog extends ApplicationDialog {
    private JButton _cancel;
    private Device _device;
    private Night _data;


    public DownloadDialog(JFrame frame, String app, int year) {
        super(frame);

        _device = new Device(app, year);

        setName("dialogDownload");
        setModal(true);
        setDefaultCloseOperation(NightDialog.DISPOSE_ON_CLOSE);

        getContentPane().add(createUI());
        getResources().injectComponents(this);

        getRootPane().setDefaultButton(_cancel);

        Utilities.registerAction(getRootPane(), JRootPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), getAction("cancel"), "ACTION_ESCAPE");
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            Thread thread = new Thread(new Downloader());
            thread.start();
        }
        super.setVisible(true);
    }

    private JPanel createUI() {
        Builder b = new Builder("20dlu, center:default:grow, 20dlu",
                "$lgap, default, 6dlu, default");

        b.add(b.createLabel("labelMessage"), 2, 2);
        b.add(createButton(), 2, 4);

        b.setDialogBorder();

        return b.getPanel();
    }

    private JPanel createButton() {
        Builder b = new Builder("default, 50dlu, default",
                "default");

        _cancel = b.createButton(getAction("cancel"));

        b.add(_cancel, 2, 1);

        return b.getPanel();
    }


    @Action
    public void cancel() {
        dispose();
    }

    public boolean isDataAvailable() {
        return _data != null;
    }

    public Night getData() {
        if (isDataAvailable()) {
            return _data;
        } else {
            throw new IllegalStateException("No data available");
        }
    }


    private class Downloader extends SwingWorker {
        protected Object doInBackground() throws Exception {
            while (true) {
                Night night = _device.readData();
                if (!isShowing()) {
                    return null;
                }
                if (night != null) {
                    return night;
                }
            }
        }

        @Override
        protected void done() {
            try {
                _data = (Night) get();
                dispose();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                // Don't allow SwingWorker to swallow the exception
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                dispose();
            }
        }
    }
}
