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
import com.pavelfatin.sleeparchiver.lang.Utilities;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

@XmlJavaTypeAdapter(Date.DateXmlAdapter.class)
public class Date extends MyComparable<Date> {
    private static final DateFormat FORMAT = createFormat();

    private int _years;
    private int _months;
    private int _days;


    private Date() {
    }

    public Date(int years, int months, int days) {
        if (years < 0) {
            throw new IllegalArgumentException("Year is negative: " + years);
        }
        if (months < 1 || months > 12) {
            throw new IllegalArgumentException("Month out of range [1; 12]: " + months);
        }
        if (days < 1 || days > 31) {
            throw new IllegalArgumentException("Day out of range [1; 31]: " + days);
        }

        _years = years;
        _months = months;
        _days = days;
    }

    protected Comparable[] getValues() {
        return new Comparable[]{_years, _months, _days};
    }

    public String format() {
        String s = FORMAT.format(toCalendar().getTime());
        return Utilities.prefixSingleDigits(s);
    }

    public int getYears() {
        return _years;
    }

    public int getMonths() {
        return _months;
    }

    public int getDays() {
        return _days;
    }

    public Calendar toCalendar() {
        return new GregorianCalendar(_years, _months - 1, _days);
    }

    private static DateFormat createFormat() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        return format;
    }

    @Override
    public String toString() {
        return format();
    }

    public static String format(Date date) {
        return date == null ? "" : date.format();
    }

    public static Date parse(String string) throws IllegalArgumentException {
        if (string.isEmpty()) {
            return null;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(FORMAT.parse(string));
            return fromCalendar(calendar);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date fromCalendar(Calendar calendar) {
        return new Date(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Date getPrototype() {
        return new Date(2000, 11, 22);
    }

    public static Date getCurrent() {
        return fromCalendar(Calendar.getInstance());
    }

    public String getDayOfWeek() {
        return getDayOfWeek(Locale.getDefault());
    }

    public String getDayOfWeek(Locale locale) {
        SimpleDateFormat format = new SimpleDateFormat("E", locale);
        return format.format(toCalendar().getTime());
    }

    public boolean isHoliday() {
        String day = getDayOfWeek(Locale.ENGLISH);
        return "Sat".equals(day) || "Sun".equals(day);
    }


    static class DateXmlAdapter extends XmlAdapter<String, Date> {
        @Override
        public Date unmarshal(String v) throws Exception {
            return v.isEmpty() ? null : parse(v);
        }

        @Override
        public String marshal(Date v) throws Exception {
            return v == null ? "" : v.format();
        }
    }
}