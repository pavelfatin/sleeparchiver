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

import com.pavelfatin.sleeparchiver.gui.main.MainView;
import com.pavelfatin.sleeparchiver.lang.Utilities;
import com.pavelfatin.sleeparchiver.model.Document;
import com.pavelfatin.sleeparchiver.model.Language;
import com.pavelfatin.sleeparchiver.model.Preferences;
import org.jdesktop.application.Application;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.SingleFrameApplication;

import java.io.File;

public class SleepArchiver extends SingleFrameApplication {
    private static final File SETTINGS = new File("settings");
    private static final String PREFERENCES = "preferences.xml";
    private Preferences _preferences;
    private String[] _args;


    public static void main(String[] args) {
        Application.launch(SleepArchiver.class, args);
    }

    @Override
    protected void initialize(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        _args = args;

        LocalStorage storage = Application.getInstance().getContext().getLocalStorage();

        if (SETTINGS.exists() && SETTINGS.isDirectory()) {
            storage.setDirectory(SETTINGS);
        } else {
            createIfNotExists(storage.getDirectory());
        }

        File file = new File(storage.getDirectory(), PREFERENCES);
        _preferences = Preferences.loadOrCreateDefault(file, Language.getDefault());

        _preferences.getLanguage().apply();
    }

    private static void createIfNotExists(File directory) {
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                throw new RuntimeException("Can't create directory: " + directory.getPath());
            }
        }
    }

    protected void startup() {
        MainView mainView = new MainView(this, new Document(), _preferences);

        if (_args.length > 0) {
            mainView.doOpen(new File(Utilities.join(" ", _args)));
        } else if (_preferences.isOpenRecentEnabled() && _preferences.hasRecentFiles()) {
            File file = new File(_preferences.getRecentFile());
            if (file.exists()) {
                mainView.doOpen(file);
            }
        }

        show(mainView);
    }
}
