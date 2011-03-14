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

package com.pavelfatin.sleeparchiver.gui.preferences;

import com.jgoodies.forms.layout.CellConstraints;
import com.pavelfatin.sleeparchiver.model.Language;
import com.pavelfatin.sleeparchiver.model.Preferences;
import com.pavelfatin.sleeparchiver.swing.Builder;
import com.pavelfatin.sleeparchiver.swing.MyDialog;
import com.pavelfatin.sleeparchiver.swing.NamedRenderer;
import com.pavelfatin.sleeparchiver.swing.NamedValue;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PreferencesDialog extends MyDialog {
    private Preferences _preferences;
    private JComboBox _language;
    private JCheckBox _backups;
    private JCheckBox _prefill;
    private JCheckBox _historyEnabled;
    private JSpinner _historyLimit;
    private JCheckBox _openRecent;
    private JLabel _labelHistoryLimit;


    public PreferencesDialog(JFrame frame, Preferences preferences) {
        super(frame, "dialogPreferences", true);

        _preferences = preferences;

        setContent(createControls());

        updateUI(_preferences);
        updateHistoryEnablement();
    }

    private void updateHistoryEnablement() {
        boolean b = _historyEnabled.isSelected();
        _historyLimit.setEnabled(b);
        _labelHistoryLimit.setEnabled(b);
        _openRecent.setEnabled(b);
    }

    private void updateUI(Preferences preferences) {
        _language.setSelectedItem(toNamed(preferences.getLanguage()));
        _backups.setSelected(preferences.isBackupsEnabled());
        _prefill.setSelected(preferences.isPrefillEnabled());
        _historyEnabled.setSelected(preferences.isHistoryEnabled());
        _historyLimit.setValue(preferences.getHistoryLimit());
        _openRecent.setSelected(preferences.isOpenRecentEnabled());
    }

    private void update(Preferences preferences) {
        Language language = ((NamedValue<Language>) _language.getSelectedItem()).getValue();
        preferences.setLanguage(language);
        preferences.setBackupsEnabled(_backups.isSelected());
        preferences.setPrefillEnabled(_prefill.isSelected());
        preferences.setHistoryEnabled(_historyEnabled.isSelected());
        preferences.setHistoryLimit((Integer) _historyLimit.getValue());
        preferences.setOpenRecentEnabled(_openRecent.isSelected());
    }

    private JPanel createControls() {
        Builder b = new Builder("default:grow",
                "default, 6dlu, default");

        b.add(createSection("separatorGeneral", createGeneral()), 1, 1);
        b.add(createSection("separatorHistory", createHistory()), 1, 3);

        return b.getPanel();
    }

    private JPanel createSection(String name, JPanel content) {
        Builder b = new Builder("6dlu, default:grow, 6dlu",
                "default, $lgap, default");

        b.add(b.createSeparator(name), 1, 1, 3);
        b.add(content, 2, 3);

        return b.getPanel();
    }

    private JPanel createGeneral() {
        Builder b = new Builder("default:grow",
                "default, 3 * ($lgap, default)");

        _language = b.createComboBox("comboboxLanguage");
        _language.setRenderer(new NamedRenderer());
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Language each : Language.values()) {
            model.addElement(toNamed(each));
        }
        _language.setModel(model);

        _backups = b.createCheckBox("checkboxBackups");
        _prefill = b.createCheckBox("checkboxPrefill");

        JLabel asterisk = b.createLabel("labelAsterisk");
        asterisk.setVerticalAlignment(SwingConstants.TOP);

        b.add(b.createLine(b.createLabel("labelLanguage", _language), _language, asterisk), 1, 1);
        b.add(b.createLabel("labelFootnote"), 1, 3, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT);
        b.add(_backups, 1, 5);
        b.add(_prefill, 1, 7);

        return b.getPanel();
    }

    private JPanel createHistory() {
        Builder b = new Builder("default:grow",
                "default, 2 * ($lgap, default)");

        _historyEnabled = b.createCheckBox("checkboxHistoryEnabled");
        _historyEnabled.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateHistoryEnablement();
            }
        });

        _openRecent = b.createCheckBox("checkboxOpenRecent");

        _historyLimit = b.createSpinner("spinnerHistoryLimit");
        _historyLimit.setModel(new SpinnerNumberModel(1, 1, 15, 1));

        _labelHistoryLimit = b.createLabel("labelHistoryLimit", _historyLimit);

        b.add(_historyEnabled, 1, 1);
        b.add(b.createLine("6dlu", _openRecent), 1, 3);
        b.add(b.createLine("6dlu", _labelHistoryLimit, _historyLimit), 1, 5);

        return b.getPanel();
    }

    private NamedValue<Language> toNamed(Language language) {
        return new NamedValue<Language>(language, language.getName());
    }

    @Override
    protected void onAccept() {
        update(_preferences);
        try {
            _preferences.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
