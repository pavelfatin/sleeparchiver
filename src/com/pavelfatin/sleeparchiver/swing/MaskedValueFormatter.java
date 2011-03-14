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


import javax.swing.text.MaskFormatter;
import java.text.ParseException;

abstract class MaskedValueFormatter<T> extends MaskFormatter {
    private String _blank;


    protected MaskedValueFormatter(T prototype) {
        try {
            String mask = createMask(prototype);
            setMask(mask);
            setPlaceholderCharacter('_');
            setValueClass(String.class);
            _blank = valueToString(null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        setCommitsOnValidEdit(true);
    }

    private String createMask(T prototype) {
        String text = format(prototype);
        return text.replaceAll("\\d", "#").replaceAll("\\w", "?");
    }

    @Override
    public Object stringToValue(String string) throws ParseException {
        try {
            return _blank.equals(string) ? null : parse((String) super.stringToValue(string));
        } catch (IllegalArgumentException e) {
            throw new ParseException("Error parsing string: " + string, 0);
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        return super.valueToString(value == null ? null : format((T) value));
    }

    protected abstract T parse(String text) throws IllegalArgumentException;

    protected abstract String format(T value);
}
