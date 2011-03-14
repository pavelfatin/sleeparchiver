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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateTest {
    @Test
    public void constructor() {
        new Date(1995, 5, 10);
        new Date(0, 1, 1);
        new Date(2005, 12, 31);
    }

    @Test
    public void accuracy() {
        Date date = new Date(2005, 12, 31);
        assertThat(date.getYears(), equalTo(2005));
        assertThat(date.getMonths(), equalTo(12));
        assertThat(date.getDays(), equalTo(31));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeYear() {
        new Date(-1, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeMonth() {
        new Date(1, -1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeDay() {
        new Date(1, 1, -1);
    }

    @Test
    public void zeroYear() {
        new Date(0, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroMonth() {
        new Date(1, 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroDay() {
        new Date(1, 1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void excessMonth() {
        new Date(1, 31, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void excessDay() {
        new Date(1, 1, 32);
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void excessCalendarDay() {
//        new Date(1, 2, 31);
//    }

    @Test
    public void format() {
        assertThat(new Date(2005, 1, 3).format(), equalTo("2005-01-03"));
        assertThat(new Date(2005, 12, 31).format(), equalTo("2005-12-31"));
    }

    @Test
    public void formatAncient() {
        assertThat(new Date(100, 1, 3).format(), equalTo("0100-01-03"));
    }

    @Test
    public void formatNull() {
        assertThat(Date.format((Date) null), equalTo(""));
    }

    @Test
    public void dayOfWeek() {
        assertThat(new Date(2005, 1, 3).getDayOfWeek(Locale.ENGLISH), equalTo("Mon"));
    }

    @Test
    public void dayOfWeekLocaleSpecific() {
        assertThat(new Date(2005, 1, 3).getDayOfWeek(Locale.GERMAN), equalTo("Mo"));
    }

    @Test
    public void isHoliday() {
        assertThat(new Date(2005, 1, 1).isHoliday(), equalTo(true));
        assertThat(new Date(2005, 1, 2).isHoliday(), equalTo(true));
        assertThat(new Date(2005, 1, 3).isHoliday(), equalTo(false));
        assertThat(new Date(2005, 1, 4).isHoliday(), equalTo(false));
    }

    @Test
    public void equals() {
        assertThat(new Date(2005, 1, 2), equalTo(new Date(2005, 1, 2)));
    }

    @Test
    public void notEquals() {
        assertThat(new Date(2005, 1, 3), not(equalTo(new Date(2005, 1, 2))));
        assertThat(new Date(2005, 3, 2), not(equalTo(new Date(2005, 1, 2))));
        assertThat(new Date(2006, 1, 2), not(equalTo(new Date(2005, 1, 2))));
    }

    @Test
    public void hash() {
        assertThat(new Date(2005, 1, 2).hashCode(), equalTo(new Date(2005, 1, 2).hashCode()));

        assertThat(new Date(2005, 3, 2).hashCode(), not(equalTo(new Date(2005, 1, 2).hashCode())));
        assertThat(new Date(2006, 1, 2).hashCode(), not(equalTo(new Date(2005, 1, 2).hashCode())));
    }

    @Test
    public void compareEqual() {
        assertThat(new Date(2005, 1, 2).compareTo(new Date(2005, 1, 2)), equalTo(0));
    }

    @Test
    public void compareGreater() {
        assertThat(new Date(2005, 1, 3).compareTo(new Date(2005, 1, 2)), equalTo(1));
        assertThat(new Date(2005, 2, 2).compareTo(new Date(2005, 1, 2)), equalTo(1));
        assertThat(new Date(2006, 1, 2).compareTo(new Date(2005, 1, 2)), equalTo(1));
    }

    @Test
    public void compareLess() {
        assertThat(new Date(2005, 2, 3).compareTo(new Date(2005, 2, 4)), equalTo(-1));
        assertThat(new Date(2005, 1, 4).compareTo(new Date(2005, 2, 4)), equalTo(-1));
        assertThat(new Date(2004, 2, 4).compareTo(new Date(2005, 2, 4)), equalTo(-1));
    }

    @Test
    public void compareEtc() {
        assertThat(new Date(2005, 1, 10).compareTo(new Date(2005, 2, 1)), equalTo(-1));
        assertThat(new Date(2005, 10, 1).compareTo(new Date(2006, 1, 1)), equalTo(-1));
    }

    @Test
    public void parse() {
        assertThat(Date.parse("2005-01-03"), equalTo(new Date(2005, 1, 3)));
    }

    @Test
    public void parseAncient() {
        assertThat(Date.parse("0100-01-03"), equalTo(new Date(100, 1, 3)));
    }

    @Test
    public void parseEmpty() {
        assertThat(Date.parse(""), equalTo(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMalformed() {
        Date.parse("20050103");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseExcessMonth() {
        Date.parse("2005-13-01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseExcessDay() {
        Date.parse("20050230");
    }

    @Test
    public void toCalendar() {
        Calendar calendar = new Date(2005, 1, 3).toCalendar();
        assertThat(calendar.get(Calendar.YEAR), equalTo(2005));
        assertThat(calendar.get(Calendar.MONTH), equalTo(0));
        assertThat(calendar.get(Calendar.DAY_OF_MONTH), equalTo(3));
    }

    @Test
    public void fromCalendar() {
        assertThat(Date.fromCalendar(new GregorianCalendar(2005, 0, 3)), equalTo(new Date(2005, 1, 3)));
    }
}