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

import com.pavelfatin.sleeparchiver.lang.MyObject;
import com.pavelfatin.sleeparchiver.lang.Utilities;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "preferences", namespace = "")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Preferences extends MyObject {
    @XmlTransient
    private File _file;

    @XmlElement(name = "language", namespace = "")
    private Language _language;

    @XmlElement(name = "backups", namespace = "")
    private boolean _backups;

    @XmlElement(name = "prefill", namespace = "")
    private boolean _prefill;

    @XmlElement(name = "history", namespace = "")
    private boolean _history;

    @XmlElement(name = "historyLimit", namespace = "")
    private int _historyLimit;

    @XmlElement(name = "openRecent", namespace = "")
    private boolean _openRecent;

    @XmlElement(name = "file", namespace = "")
    @XmlElementWrapper(name = "files", namespace = "")
    private List<String> _files = new ArrayList<String>();


    public Preferences() {
    }

    public void setFile(File file) {
        _file = file;
    }

    public Language getLanguage() {
        return _language;
    }

    public void setLanguage(Language language) {
        _language = language;
    }

    public boolean isBackupsEnabled() {
        return _backups;
    }

    public void setBackupsEnabled(boolean enabled) {
        _backups = enabled;
    }

    public boolean isPrefillEnabled() {
        return _prefill;
    }

    public void setPrefillEnabled(boolean enabled) {
        _prefill = enabled;
    }

    public boolean isHistoryEnabled() {
        return _history;
    }

    public void setHistoryEnabled(boolean enabled) {
        _history = enabled;
        truncateRecentFilesList();
    }

    public int getHistoryLimit() {
        return _historyLimit;
    }

    public void setHistoryLimit(int limit) {
        _historyLimit = limit;
        truncateRecentFilesList();
    }

    public boolean isOpenRecentEnabled() {
        return _openRecent;
    }

    public void setOpenRecentEnabled(boolean enabled) {
        _openRecent = enabled;
    }

    public String getRecentFile() {
        if (!hasRecentFiles()) {
            throw new RuntimeException("Recent files list is empty");
        }
        return _files.get(0);
    }

    public File getRecentDirectory() {
        return new File(getRecentFile()).getParentFile();
    }

    protected Object[] getValues() {
        return new Object[]{_file, _language, _backups, _prefill,
                _history, _historyLimit, _openRecent, _files};
    }

    public static Preferences createDefault(File file, Language language) {
        Preferences preferences = new Preferences();
        preferences.setFile(file);
        preferences.setLanguage(language);
        preferences.setBackupsEnabled(true);
        preferences.setPrefillEnabled(true);
        preferences.setHistoryEnabled(true);
        preferences.setHistoryLimit(5);
        preferences.setOpenRecentEnabled(true);
        return preferences;
    }

    public List<String> getRecentFiles() {
        return Collections.unmodifiableList(_files);
    }

    public boolean hasRecentFiles() {
        return !_files.isEmpty();
    }

    public void addRecentFile(String file) {
        _files.remove(file);
        _files.add(0, file);
        truncateRecentFilesList();
    }

    public void clearRecentFiles() {
        _files.clear();
    }

    private void truncateRecentFilesList() {
        int count = _history ? _historyLimit : 0;
        if (_files.size() > count) {
            _files = _files.subList(0, count);
        }
    }

    public static Preferences loadOrCreateDefault(File file, Language language) {
        Preferences preferences;
        try {
            preferences = load(file);
        } catch (IOException e) {
            preferences = createDefault(file, language);
        }
        return preferences;
    }

    public static Preferences load(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            Preferences preferences = loadFrom(in);
            preferences.setFile(file);
            return preferences;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } finally {
            Utilities.close(in);
        }
    }

    static Preferences loadFrom(InputStream stream) throws JAXBException {
        Unmarshaller unmarshaller = createContext().createUnmarshaller();
        unmarshaller.setEventHandler(new ValidationHandler());
        return (Preferences) unmarshaller.unmarshal(stream);
    }

    private static JAXBContext createContext() throws JAXBException {
        return JAXBContext.newInstance(Preferences.class);
    }

    public void save() throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(_file));
        try {
            saveTo(out);
            out.flush();
        } catch (PropertyException e) {
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } finally {
            Utilities.close(out);
        }
    }

    void saveTo(OutputStream stream) throws JAXBException {
        Marshaller marshaller = createContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setEventHandler(new ValidationHandler());
        marshaller.marshal(this, stream);
    }
}
