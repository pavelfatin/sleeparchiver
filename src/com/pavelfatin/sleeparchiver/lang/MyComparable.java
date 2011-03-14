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

package com.pavelfatin.sleeparchiver.lang;

public abstract class MyComparable<T extends MyComparable> extends MyObject implements Comparable<T> {
    protected abstract Comparable[] getValues();


    public int compareTo(T obj) {
        if (obj == null) {
            throw new NullPointerException("Comparison parameter is null");
        }

        if (obj == this) {
            return 0;
        }

        Object[] values = getValues();
        Object[] others = ((MyObject) obj).getValues();

        assertSizesEqual(values.length, others.length);

        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            Object other = others[i];

            int result = Utilities.compare((Comparable) value, other);
            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    public boolean isLessThan(T other) {
        return compareTo(other) < 0;
    }

    public boolean isGreaterThan(T other) {
        return compareTo(other) > 0;
    }
}
