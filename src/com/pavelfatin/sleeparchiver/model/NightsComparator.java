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

import java.util.Comparator;

public class NightsComparator implements Comparator<Night> {
    public int compare(Night n1, Night n2) {
        return compare(n1.getDate(), n2.getDate(), n1.getToBed(), n2.getToBed());
    }

    static int compare(Date d1, Date d2, Time t1, Time t2) {
        int dateComparison = comparison(d1, d2);
        return dateComparison == 0 ? comparison(t1, t2) : dateComparison;
    }

    static int comparison(Comparable v1, Comparable v2) {
        if (v1 == null) {
            return v2 == null ? 0 : 1;
        } else {
            return v2 == null ? -1 : v1.compareTo(v2);
        }
    }
}
