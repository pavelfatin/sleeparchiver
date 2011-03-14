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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class MyObjectTest {
    @Test
    public void equals() {
        assertThat(new Fields(1, 2), equalTo(new Fields(1, 2)));
    }

    @Test
    public void notEquals() {
        assertThat(new Fields(3, 2), not(equalTo(new Fields(1, 2))));
        assertThat(new Fields(1, 3), not(equalTo(new Fields(1, 2))));

        assertThat(new Fields(2, 1), not(equalTo(new Fields(1, 2))));
    }

    @Test
    public void equalsWithNull() {
        assertThat(new Fields(1, 2).equals(null), equalTo(false));
    }

    @Test
    public void equalsWithSelf() {
        Fields fields = new Fields(1, 2);
        assertThat(fields.equals(fields), equalTo(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void equalsShort() {
        new Fields(1, 2).equals(new Fields(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void equalsLong() {
        new Fields(1).equals(new Fields(1, 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void equalsForeignClass() {
        new Fields(1).equals(1);
    }

    @Test
    public void hash() {
        assertThat(new Fields(1, 2).hashCode(), equalTo(new Fields(1, 2).hashCode()));

        assertThat(new Fields(3, 2).hashCode(), not(equalTo(new Fields(1, 2).hashCode())));
        assertThat(new Fields(1, 3).hashCode(), not(equalTo(new Fields(1, 2).hashCode())));
        assertThat(new Fields(2, 1).hashCode(), not(equalTo(new Fields(1, 2).hashCode())));
    }

    @Test
    public void string() {
        assertThat(new Fields(1).toString(), equalTo("1"));
        assertThat(new Fields(1, 2).toString(), equalTo("1, 2"));

        assertThat(new Fields(1, null, 2).toString(), equalTo("1, null, 2"));
    }
}
