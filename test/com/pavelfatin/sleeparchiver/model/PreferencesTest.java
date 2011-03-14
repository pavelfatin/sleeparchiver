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

package com.pavelfatin.sleeparchiver.model;

import com.pavelfatin.sleeparchiver.lang.Utilities;
import static com.pavelfatin.sleeparchiver.model.TestUtilities.contentOf;
import static com.pavelfatin.sleeparchiver.model.TestUtilities.createTempFile;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class PreferencesTest {
    private Preferences createPreferences() {
        Preferences preferences = Preferences.createDefault(null, Language.EN);
        preferences.addRecentFile("file A");
        preferences.addRecentFile("file B");
        return preferences;
    }

    @Test
    public void fileAddition() {
        Preferences preferences = new Preferences();
        preferences.setHistoryEnabled(true);
        preferences.setHistoryLimit(5);

        preferences.addRecentFile("file A");
        preferences.addRecentFile("file B");
        preferences.addRecentFile("file C");

        assertThat(preferences.getRecentFiles(), equalTo(Utilities.newList("file C", "file B", "file A")));
    }

    @Test
    public void filesClearing() {
        Preferences preferences = new Preferences();
        preferences.setHistoryEnabled(true);
        preferences.setHistoryLimit(5);
        preferences.addRecentFile("file A");
        preferences.addRecentFile("file B");
        preferences.addRecentFile("file C");

        preferences.clearRecentFiles();

        assertThat(preferences.getRecentFiles().size(), equalTo(0));
    }


    @Test
    public void fileAdditionWithDisabledHistory() {
        Preferences preferences = new Preferences();
        preferences.setHistoryLimit(5);
        preferences.setHistoryEnabled(false);

        preferences.addRecentFile("file");

        assertThat(preferences.getRecentFiles().size(), equalTo(0));
    }

    @Test
    public void historyDisablement() {
        Preferences preferences = new Preferences();
        preferences.addRecentFile("file");

        preferences.setHistoryLimit(5);
        preferences.setHistoryEnabled(false);

        assertThat(preferences.getRecentFiles().size(), equalTo(0));
    }

    @Test
    public void filesAdditionWithHistoryLimit() {
        Preferences preferences = new Preferences();
        preferences.setHistoryEnabled(true);
        preferences.setHistoryLimit(2);

        preferences.addRecentFile("file A");
        preferences.addRecentFile("file B");
        preferences.addRecentFile("file C");

        assertThat(preferences.getRecentFiles(), equalTo(Utilities.newList("file C", "file B")));
    }

    @Test
    public void historyLimitChange() {
        Preferences preferences = new Preferences();
        preferences.setHistoryEnabled(true);
        preferences.setHistoryLimit(5);
        preferences.addRecentFile("file A");
        preferences.addRecentFile("file B");
        preferences.addRecentFile("file C");

        preferences.setHistoryLimit(2);

        assertThat(preferences.getRecentFiles(), equalTo(Utilities.newList("file C", "file B")));
    }

    @Test
    public void saveToStream() throws JAXBException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        createPreferences().saveTo(buffer);
        assertThat(buffer.toString(), equalTo(contentOf("preferences.xml", "\n")));
    }

    @Test
    public void loadFromStream() throws JAXBException {
        ByteArrayInputStream buffer = new ByteArrayInputStream(contentOf("preferences.xml", "\n").getBytes());
        Preferences loaded = Preferences.loadFrom(buffer);
        assertThat(loaded, equalTo(createPreferences()));
    }

    @Test
    public void saveAndLoad() throws IOException {
        File file = createTempFile("preferences.tmp");

        Preferences preferences = createPreferences();
        preferences.setFile(file);

        preferences.save();

        Preferences loaded = Preferences.load(file);
        file.delete();

        assertThat(loaded, equalTo(preferences));
    }
}