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

import com.pavelfatin.sleeparchiver.lang.MyComparable;

import java.util.ArrayList;
import java.util.List;

public class Instant extends MyComparable<Instant> {
    private static final int MINUTES_IN_HOUR = 60;
    private static final int MINUTES_IN_DAY = MINUTES_IN_HOUR * 24;

    private static final Time DAY_BOUNDARY = new Time(16, 0);

    private int _days;
    private Time _time;


    public Instant(Time time) {
        this(0, time);
    }

    public Instant(int days, Time time) {
        if (days < 0) {
            throw new IllegalArgumentException("Days value is negative: " + days);
        }
        _days = days;
        _time = time;
    }

    public Instant(int minutes) {
        this(minutes / MINUTES_IN_DAY,
                new Time((minutes % MINUTES_IN_DAY) / MINUTES_IN_HOUR,
                        (minutes % MINUTES_IN_DAY) % MINUTES_IN_HOUR));
    }

    protected Comparable[] getValues() {
        return new Comparable[]{_days, _time};
    }

    public int getDays() {
        return _days;
    }

    public Time getTime() {
        return _time;
    }

    public int toMinutes() {
        return MINUTES_IN_DAY * _days +
                MINUTES_IN_HOUR * _time.getHours() +
                _time.getMinutes();
    }

    @Override
    public String toString() {
        return String.format("%d:%s", _days, _time.format());
    }

    public static List<Instant> toInstants(List<Time> moments) {
        List<Instant> instants = new ArrayList<Instant>();
        int days = 0;
        for (int i = 0; i < moments.size(); i++) {
            Time time = moments.get(i);
            if (i == 0 && time.isLessThan(DAY_BOUNDARY)) {
                days++;
            }
            if (i > 0 && time.isLessThan(moments.get(i - 1))) {
                days++;
            }
            instants.add(new Instant(days, time));
        }
        return instants;
    }
}
