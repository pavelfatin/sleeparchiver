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

public class TimeTest {
    @Test
    public void constructor() {
        new Time(0, 0);
        new Time(12, 30);
        new Time(23, 59);
    }

    @Test
    public void accuracy() {
        Time time = new Time(23, 59);
        assertThat(time.getHours(), equalTo(23));
        assertThat(time.getMinutes(), equalTo(59));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeHour() {
        new Time(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeMinute() {
        new Time(0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void excessHour() {
        new Time(24, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void excessMinute() {
        new Time(0, 60);
    }

    @Test
    public void format() {
        assertThat(new Time(0, 0).format(), equalTo("00:00"));
        assertThat(new Time(5, 30).format(), equalTo("05:30"));
        assertThat(new Time(23, 59).format(), equalTo("23:59"));
    }

    @Test
    public void formatNull() {
        assertThat(Time.format((Time) null), equalTo(""));
    }

    @Test
    public void formatShort() {
        assertThat(new Time(0, 0).formatShort(), equalTo("0:00"));
        assertThat(new Time(5, 30).formatShort(), equalTo("5:30"));
        assertThat(new Time(23, 59).formatShort(), equalTo("23:59"));
    }

    @Test
    public void equals() {
        assertThat(new Time(12, 30), equalTo(new Time(12, 30)));
    }

    @Test
    public void notEquals() {
        assertThat(new Time(11, 30), not(equalTo(new Time(12, 30))));
        assertThat(new Time(12, 40), not(equalTo(new Time(12, 30))));
        assertThat(new Time(5, 0), not(equalTo(new Time(17, 0))));
    }

    @Test
    public void hash() {
        assertThat(new Time(12, 30).hashCode(), equalTo(new Time(12, 30).hashCode()));

        assertThat(new Time(11, 30).hashCode(), not(equalTo(new Time(12, 30).hashCode())));
        assertThat(new Time(12, 40).hashCode(), not(equalTo(new Time(12, 30).hashCode())));
    }

    @Test
    public void compareEqual() {
        assertThat(new Time(12, 30).compareTo(new Time(12, 30)), equalTo(0));
    }

    @Test
    public void compareGreater() {
        assertThat(new Time(12, 31).compareTo(new Time(12, 30)), equalTo(1));
        assertThat(new Time(13, 30).compareTo(new Time(12, 30)), equalTo(1));
    }

    @Test
    public void compareLess() {
        assertThat(new Time(11, 30).compareTo(new Time(12, 30)), equalTo(-1));
        assertThat(new Time(12, 29).compareTo(new Time(12, 30)), equalTo(-1));
    }

    @Test
    public void compareNoAmPm() {
        assertThat(new Time(13, 0).compareTo(new Time(12, 30)), equalTo(1));
        assertThat(new Time(11, 50).compareTo(new Time(12, 30)), equalTo(-1));

        assertThat(new Time(14, 0).compareTo(new Time(3, 0)), equalTo(1));
    }

    @Test
    public void parse() {
        assertThat(Time.parse("00:00"), equalTo(new Time(0, 0)));
        assertThat(Time.parse("05:30"), equalTo(new Time(5, 30)));
        assertThat(Time.parse("23:59"), equalTo(new Time(23, 59)));
    }

    @Test
    public void parseEmpty() {
        assertThat(Time.parse(""), equalTo(null));
    }

    @Test
    public void parseShort() {
        assertThat(Time.parse("0:00"), equalTo(new Time(0, 0)));
        assertThat(Time.parse("5:30"), equalTo(new Time(5, 30)));
        assertThat(Time.parse("23:59"), equalTo(new Time(23, 59)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseMalformed() {
        Time.parse("0000");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseExcessHours() {
        Time.parse("24:00");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseExcessMinutes() {
        Time.parse("00:60");
    }
}
