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

package com.pavelfatin.sleeparchiver;

import com.pavelfatin.sleeparchiver.gui.error.ErrorDialog;
import com.pavelfatin.sleeparchiver.lang.Utilities;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;

class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        showErrorForm(e);
    }

    private void showErrorForm(Throwable e) {
        SingleFrameApplication application = (SingleFrameApplication) Application.getInstance();
        ResourceMap resources = application.getContext().getResourceManager().getResourceMap();

        String info = Utilities.join("; ", getAppInfo(resources), getOsInfo(), getJreInfo());

        JFrame frame = application.getMainFrame();
        ErrorDialog dialog = new ErrorDialog(frame, e, info);
        dialog.open();
    }

    private String getJreInfo() {
        return String.format("JRE %s (%s)",
                System.getProperty("java.version"),
                System.getProperty("java.vendor"));
    }

    private String getOsInfo() {
        return String.format("%s %s (%s)",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"));
    }

    private String getAppInfo(ResourceMap resources) {
        return String.format("%s %s",
                resources.getString("Application.name"),
                resources.getString("Application.version"));
    }
}
