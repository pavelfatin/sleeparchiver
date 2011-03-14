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

import com.pavelfatin.sleeparchiver.model.Date;

import javax.swing.*;

public class MyDateField extends MyFormattedTextField {
    public MyDateField() {
        super(new DateFormatter());

        setPrototype(Date.getPrototype());
        setHorizontalAlignment(JTextField.CENTER);
        addFocusListener(new TextSelector(this));
    }

    public Date getDate() {
        return (Date) getValue();
    }

    public void setDate(Date date) {
        setValue(date);
    }


    private static class DateFormatter extends MaskedValueFormatter<Date> {
        protected DateFormatter() {
            super(Date.getPrototype());
        }

        protected Date parse(String text) throws IllegalArgumentException {
            return Date.parse(text);
        }

        protected String format(Date value) {
            return value.format();
        }
    }
}