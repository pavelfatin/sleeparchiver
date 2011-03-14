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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class MyComparableTest {
    @Test
    public void compareEqual() {
        assertThat(new Fields(1, 2).compareTo(new Fields(1, 2)), equalTo(0));
    }

    @Test
    public void compareGreater() {
        assertThat(new Fields(2, 2).compareTo(new Fields(1, 2)), equalTo(1));
        assertThat(new Fields(1, 3).compareTo(new Fields(1, 2)), equalTo(1));
    }

    @Test
    public void compareLess() {
        assertThat(new Fields(0, 2).compareTo(new Fields(1, 2)), equalTo(-1));
        assertThat(new Fields(1, 1).compareTo(new Fields(1, 2)), equalTo(-1));
    }

    @Test
    public void compareToSelf() {
        Fields fields = new Fields(1, 2);
        assertThat(fields.compareTo(fields), equalTo(0));
    }

    @Test(expected = NullPointerException.class)
    public void compareToNull() {
        new Fields(1, 2).compareTo(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareShort() {
        new Fields(1, 2).compareTo(new Fields(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareLong() {
        new Fields(1).compareTo(new Fields(1, 2));
    }
}