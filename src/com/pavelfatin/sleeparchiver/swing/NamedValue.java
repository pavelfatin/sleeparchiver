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

import com.pavelfatin.sleeparchiver.lang.Utilities;

public class NamedValue<T> extends AbstractNamed {
    private T _value;


    public NamedValue(T value, String name) {
        super(name);

        _value = value;
    }

    public T getValue() {
        return _value;
    }

    @Override
    public boolean equals(Object obj) {
        return Utilities.equals(getValue(), ((NamedValue) obj).getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}
