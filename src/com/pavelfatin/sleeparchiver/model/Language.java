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

import java.util.Locale;

public enum Language {
//    RU("ru", "ru"),
    EN("en", "en");

    private Locale _locale;

    Language(String language, String country) {
        _locale = new Locale(language, country);
    }

    public String getName() {
        return _locale.getDisplayName(_locale);
    }

    private Locale getLocale() {
        return _locale;
    }

    public void apply() {
        Locale.setDefault(_locale);
    }

    public static Language getDefault() {
        Locale preffered = Locale.getDefault();

        for (Language language : values()) {
            Locale each = language.getLocale();
            if (preffered.getLanguage().equals(each.getLanguage())
                    && preffered.getCountry().equals(each.getCountry())) {
                return language;
            }
        }

        return EN;
    }
}
