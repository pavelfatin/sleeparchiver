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

import com.pavelfatin.sleeparchiver.model.Time;

import javax.swing.*;

public class MyTimeField extends MyFormattedTextField {
    public MyTimeField() {
        super(new TimeFormatter());

        setPrototype(Time.getPrototype());
        setHorizontalAlignment(JTextField.CENTER);
        addFocusListener(new TextSelector(this));
    }

    public Time getTime() {
        return (Time) getValue();
    }

    public void setTime(Time time) {
        setValue(time);
    }


    private static class TimeFormatter extends MaskedValueFormatter<Time> {
        protected TimeFormatter() {
            super(Time.getPrototype());
        }

        protected Time parse(String text) throws IllegalArgumentException {
            return Time.parse(text);
        }

        protected String format(Time value) {
            return value.format();
        }
    }
}
