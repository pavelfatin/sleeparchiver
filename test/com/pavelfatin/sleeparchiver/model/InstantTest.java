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

public class InstantTest {
    @Test(expected = IllegalArgumentException.class)
    public void negativeDays() {
        new Instant(-1, new Time(0, 0));
    }

    @Test
    public void accuracy() {
        Instant instant = new Instant(1, new Time(2, 3));
        assertThat(instant.getDays(), equalTo(1));
        assertThat(instant.getTime(), equalTo(new Time(2, 3)));
    }

    @Test
    public void equals() {
        assertThat(new Instant(1, new Time(12, 30)), equalTo(new Instant(1, new Time(12, 30))));
    }

    @Test
    public void notEquals() {
        assertThat(new Instant(1, new Time(14, 30)), not(equalTo(new Instant(1, new Time(12, 30)))));
        assertThat(new Instant(2, new Time(12, 30)), not(equalTo(new Instant(1, new Time(12, 30)))));
    }

    @Test
    public void hash() {
        assertThat(new Instant(1, new Time(12, 30)).hashCode(),
                equalTo(new Instant(1, new Time(12, 30)).hashCode()));

        assertThat(new Instant(1, new Time(14, 30)).hashCode(),
                not(equalTo(new Instant(1, new Time(12, 30)).hashCode())));
        assertThat(new Instant(2, new Time(12, 30)).hashCode(),
                not(equalTo(new Instant(1, new Time(12, 30)).hashCode())));
    }

    @Test
    public void compareEqual() {
        assertThat(new Instant(1, new Time(12, 30)).compareTo(new Instant(1, new Time(12, 30))), equalTo(0));
    }

    @Test
    public void compareGreater() {
        assertThat(new Instant(1, new Time(14, 30)).compareTo(new Instant(1, new Time(12, 30))), equalTo(1));
        assertThat(new Instant(2, new Time(12, 30)).compareTo(new Instant(1, new Time(12, 30))), equalTo(1));
    }

    @Test
    public void compareLess() {
        assertThat(new Instant(1, new Time(10, 30)).compareTo(new Instant(1, new Time(12, 30))), equalTo(-1));
        assertThat(new Instant(0, new Time(12, 30)).compareTo(new Instant(1, new Time(12, 30))), equalTo(-1));
    }

    @Test
    public void toMinutes() {
        assertThat(new Instant(0, new Time(0, 0)).toMinutes(), equalTo(0));
        assertThat(new Instant(0, new Time(0, 40)).toMinutes(), equalTo(40));
        assertThat(new Instant(0, new Time(3, 40)).toMinutes(), equalTo(220));
        assertThat(new Instant(2, new Time(3, 40)).toMinutes(), equalTo(3100));
    }

    @Test
    public void fromMinutes() {
        assertThat(new Instant(0), equalTo(new Instant(0, new Time(0, 0))));
        assertThat(new Instant(40), equalTo(new Instant(0, new Time(0, 40))));
        assertThat(new Instant(220), equalTo(new Instant(0, new Time(3, 40))));
        assertThat(new Instant(3100), equalTo(new Instant(2, new Time(3, 40))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromMinutesNegative() {
        new Instant(-1);
    }

//    @Test
//    public void toInstantsSingle() {
//        assertThat(Instant.toInstants(newList(new Time(0, 0))),
//                equalTo(newList(new Instant(0, new Time(0, 0)))));
//
//        assertThat(Instant.toInstants(newList(new Time(0, 2))),
//                equalTo(newList(new Instant(0, new Time(0, 2)))));
//    }
//
//    @Test
//    public void toInstantsMultiple() {
//        assertThat(Instant.toInstants(newList(
//                new Time(0, 0),
//                new Time(0, 2),
//                new Time(0, 4))),
//                equalTo(newList(
//                        new Instant(0, new Time(0, 0)),
//                        new Instant(0, new Time(0, 2)),
//                        new Instant(0, new Time(0, 4)))));
//    }
//
//    @Test
//    public void toInstantsMultipleEqual() {
//        assertThat(Instant.toInstants(newList(
//                new Time(0, 1),
//                new Time(0, 1),
//                new Time(0, 1))),
//                equalTo(newList(
//                        new Instant(0, new Time(0, 1)),
//                        new Instant(0, new Time(0, 1)),
//                        new Instant(0, new Time(0, 1)))));
//    }
//
//    @Test
//    public void toInstantsWrap() {
//        assertThat(Instant.toInstants(newList(
//                new Time(0, 4),
//                new Time(0, 2))),
//                equalTo(newList(
//                        new Instant(0, new Time(0, 4)),
//                        new Instant(1, new Time(0, 2)))));
//    }
//
//    @Test
//    public void toInstantsWrapRemains() {
//        assertThat(Instant.toInstants(newList(
//                new Time(0, 4),
//                new Time(0, 2),
//                new Time(0, 5))),
//                equalTo(newList(
//                        new Instant(0, new Time(0, 4)),
//                        new Instant(1, new Time(0, 2)),
//                        new Instant(1, new Time(0, 5)))));
//    }
//
//    @Test
//    public void toInstantsWrapRemainsWithEqual() {
//        assertThat(Instant.toInstants(newList(
//                new Time(0, 4),
//                new Time(0, 2),
//                new Time(0, 2))),
//                equalTo(newList(
//                        new Instant(0, new Time(0, 4)),
//                        new Instant(1, new Time(0, 2)),
//                        new Instant(1, new Time(0, 2)))));
//    }
//
//    @Test
//    public void toInstantsMultipleWraps() {
//        assertThat(Instant.toInstants(newList(
//                new Time(0, 4),
//                new Time(0, 2),
//                new Time(0, 5),
//                new Time(0, 3))),
//                equalTo(newList(
//                        new Instant(0, new Time(0, 4)),
//                        new Instant(1, new Time(0, 2)),
//                        new Instant(1, new Time(0, 5)),
//                        new Instant(2, new Time(0, 3)))));
//    }
//
//    @Test
//    public void toInstantsSequentialWraps() {
//        assertThat(Instant.toInstants(newList(
//                new Time(0, 5),
//                new Time(0, 4),
//                new Time(0, 3),
//                new Time(0, 2))),
//                equalTo(newList(
//                        new Instant(0, new Time(0, 5)),
//                        new Instant(1, new Time(0, 4)),
//                        new Instant(2, new Time(0, 3)),
//                        new Instant(3, new Time(0, 2)))));
//    }
//
//    @Test
//    public void toInstantsSequentialWrapsRemains() {
//        assertThat(Instant.toInstants(newList(
//                new Time(0, 5),
//                new Time(0, 4),
//                new Time(0, 3),
//                new Time(0, 7))),
//                equalTo(newList(
//                        new Instant(0, new Time(0, 5)),
//                        new Instant(1, new Time(0, 4)),
//                        new Instant(2, new Time(0, 3)),
//                        new Instant(2, new Time(0, 7)))));
//    }
}