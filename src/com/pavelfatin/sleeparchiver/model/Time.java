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

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@XmlJavaTypeAdapter(Time.TimeXmlAdapter.class)
public class Time extends MyComparable<Time> {
    private static final DateFormat FORMAT_LONG = createLongFormat();
    private static final DateFormat FORMAT_SHORT = createShortFormat();

    private int _hours;
    private int _minutes;


    private Time() {
    }

    public Time(int hours, int minutes) {
        if (hours < 0 || hours > 23) {
            throw new IllegalArgumentException("Hours out of range [0; 23]: " + hours);
        }
        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Minutes out of range [0; 59]: " + minutes);
        }

        _hours = hours;
        _minutes = minutes;
    }

    protected Comparable[] getValues() {
        return new Comparable[]{_hours, _minutes};
    }

    public String format() {
        return FORMAT_LONG.format(toCalendar().getTime());
    }

    public String formatShort() {
        return FORMAT_SHORT.format(toCalendar().getTime());
    }

    public int getHours() {
        return _hours;
    }

    public int getMinutes() {
        return _minutes;
    }

    private Calendar toCalendar() {
        return new GregorianCalendar(1970, 0, 1, _hours, _minutes);
    }

    private static DateFormat createLongFormat() {
        DateFormat format = new SimpleDateFormat("HH:mm");
        format.setLenient(false);
        return format;
    }

    private static DateFormat createShortFormat() {
        DateFormat format = new SimpleDateFormat("H:mm");
        format.setLenient(false);
        return format;
    }

    @Override
    public String toString() {
        return format();
    }

    public static String format(Time time) {
        return time == null ? "" : time.format();
    }

    public static Time parse(String string) throws IllegalArgumentException {
        if (string.isEmpty()) {
            return null;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(FORMAT_LONG.parse(string));
            return new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Time getPrototype() {
        return new Time(22, 22);
    }


    static class TimeXmlAdapter extends XmlAdapter<String, Time> {
        @Override
        public Time unmarshal(String v) throws Exception {
            return v.isEmpty() ? null : parse(v);
        }

        @Override
        public String marshal(Time v) throws Exception {
            return v == null ? "" : v.format();
        }
    }
}
