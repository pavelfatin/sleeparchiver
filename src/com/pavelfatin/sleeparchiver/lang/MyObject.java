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

public abstract class MyObject {
    protected abstract Object[] getValues();


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        assertClassEqual(obj, getClass());

        Object[] values = getValues();
        Object[] others = ((MyObject) obj).getValues();

        assertSizesEqual(values.length, others.length);

        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            Object other = others[i];

            if (!Utilities.equals(value, other)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        Object[] values = getValues();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            builder.append(value);
            if (i < (values.length - 1)) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        Object[] values = getValues();
        int result = 0;
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            result += value.hashCode() * 31 * (i + 1);
        }
        return result;
    }

    protected static void assertClassEqual(Object obj, Class expected) {
        if (obj == null) {
            throw new IllegalArgumentException("Argument was null");
        }
        Class actual = obj.getClass();
        if (!expected.equals(actual)) {
            String message = String.format("Argument class is not %s: %s", expected.getName(), actual.getName());
            throw new IllegalArgumentException(message);
        }
    }

    protected static void assertSizesEqual(int expected, int actual) {
        if (expected != actual) {
            String message = String.format("Fields size differs from %d: %d", expected, actual);
            throw new IllegalArgumentException(message);
        }
    }
}
