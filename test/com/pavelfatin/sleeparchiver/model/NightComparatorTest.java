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
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class NightComparatorTest {
    private static final Date D1 = new Date(2000, 1, 1);
    private static final Date D2 = new Date(2001, 1, 1);
    private static final Time T1 = new Time(10, 0);
    private static final Time T2 = new Time(11, 0);

    @Test
    public void datesEquals() {
        assertThat(NightsComparator.compare(D1, D1, null, null), equalTo(0));
    }

    @Test
    public void nullDatesEquals() {
        assertThat(NightsComparator.compare(null, null, null, null), equalTo(0));
    }

    @Test
    public void dateLess() {
        assertThat(NightsComparator.compare(D1, D2, null, null), equalTo(-1));
        assertThat(NightsComparator.compare(D1, D2, T1, null), equalTo(-1));
        assertThat(NightsComparator.compare(D1, D2, null, T1), equalTo(-1));
        assertThat(NightsComparator.compare(D1, D2, T1, T2), equalTo(-1));
        assertThat(NightsComparator.compare(D1, D2, T2, T1), equalTo(-1));
    }

    @Test
    public void dateGreater() {
        assertThat(NightsComparator.compare(D2, D1, null, null), equalTo(1));
        assertThat(NightsComparator.compare(D2, D1, T1, null), equalTo(1));
        assertThat(NightsComparator.compare(D2, D1, null, T1), equalTo(1));
        assertThat(NightsComparator.compare(D2, D1, T1, T2), equalTo(1));
        assertThat(NightsComparator.compare(D2, D1, T2, T1), equalTo(1));
    }

    @Test
    public void dateNull() {
        assertThat(NightsComparator.compare(null, D1, null, null), equalTo(1));
        assertThat(NightsComparator.compare(null, D1, T1, null), equalTo(1));
        assertThat(NightsComparator.compare(null, D1, null, T1), equalTo(1));
        assertThat(NightsComparator.compare(null, D1, T1, T2), equalTo(1));
        assertThat(NightsComparator.compare(null, D1, T2, T1), equalTo(1));
    }

    @Test
    public void dateToNull() {
        assertThat(NightsComparator.compare(D1, null, null, null), equalTo(-1));
        assertThat(NightsComparator.compare(D1, null, T1, null), equalTo(-1));
        assertThat(NightsComparator.compare(D1, null, null, T1), equalTo(-1));
        assertThat(NightsComparator.compare(D1, null, T1, T2), equalTo(-1));
        assertThat(NightsComparator.compare(D1, null, T2, T1), equalTo(-1));
    }

    @Test
    public void timesEquals() {
        assertThat(NightsComparator.compare(D1, D1, T1, T1), equalTo(0));
    }

    @Test
    public void nullTimesEquals() {
        assertThat(NightsComparator.compare(D1, D1, null, null), equalTo(0));
    }

    @Test
    public void timeLess() {
        assertThat(NightsComparator.compare(D1, D1, T1, T2), equalTo(-1));
    }

    @Test
    public void timeGreater() {
        assertThat(NightsComparator.compare(D1, D1, T2, T1), equalTo(1));
    }

    @Test
    public void timeNull() {
        assertThat(NightsComparator.compare(D1, D1, null, T1), equalTo(1));
    }

    @Test
    public void timeToNull() {
        assertThat(NightsComparator.compare(D1, D1, T1, null), equalTo(-1));
    }
}