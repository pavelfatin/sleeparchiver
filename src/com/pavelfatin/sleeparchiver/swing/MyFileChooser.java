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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class MyFileChooser extends JFileChooser {
    private static File _location;

    private String _description;
    private String _extension;


    public MyFileChooser(String description, String extension) {
        _description = description;
        _extension = extension;

        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setMultiSelectionEnabled(false);
        setFileFilter(createFileFilter());
    }

    public FileFilter createFileFilter() {
        String line = String.format("%s (*.%s)", _description, _extension);
        return new FileNameExtensionFilter(line, _extension);
    }

    @Override
    public int showOpenDialog(Component parent) throws HeadlessException {
        restoreLocation();
        return super.showOpenDialog(parent);
    }

    @Override
    public int showSaveDialog(Component parent) throws HeadlessException {
        restoreLocation();
        return super.showSaveDialog(parent);
    }

    private void restoreLocation() {
        if (_location != null) {
            setCurrentDirectory(_location);
        }
    }

    @Override
    public void approveSelection() {
        super.approveSelection();
        _location = getSelectedFile().getParentFile();
    }

    @Override
    public File getSelectedFile() {
        File file = super.getSelectedFile();
        return file == null ? null : autocomplete(file);
    }

    public File autocomplete(File file) {
        if (!file.getName().toLowerCase().endsWith("." + _extension)) {
            return new File(file.getPath() + "." + _extension);
        }
        return file;
    }
}
