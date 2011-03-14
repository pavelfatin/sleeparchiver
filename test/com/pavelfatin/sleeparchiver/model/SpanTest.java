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

import static com.pavelfatin.sleeparchiver.lang.Utilities.newList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.Collections;

public class SpanTest {
    private Instant _i1 = new Instant(1, new Time(14, 10));
    private Instant _i2 = new Instant(2, new Time(15, 20));
    private Instant _i3 = new Instant(3, new Time(16, 30));


    @Test
    public void accuracy() {
        Span span = new Span(_i1, _i2);
        assertThat(span.getBegin(), equalTo(_i1));
        assertThat(span.getEnd(), equalTo(_i2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeSpan() {
        new Span(_i2, _i1);
    }

    @Test
    public void equals() {
        assertThat(new Span(_i1, _i3), equalTo(new Span(_i1, _i3)));
    }

    @Test
    public void notEquals() {
        assertThat(new Span(_i1, _i2), not(equalTo(new Span(_i1, _i3))));
        assertThat(new Span(_i2, _i3), not(equalTo(new Span(_i1, _i3))));
    }

    @Test
    public void hash() {
        assertThat(new Span(_i1, _i3).hashCode(),
                equalTo(new Span(_i1, _i3).hashCode()));

        assertThat(new Span(_i1, _i2).hashCode(),
                not(equalTo(new Span(_i1, _i3).hashCode())));
        assertThat(new Span(_i2, _i3).hashCode(),
                not(equalTo(new Span(_i3, _i3).hashCode())));
    }

    @Test
    public void compareEqual() {
        assertThat(new Span(_i1, _i3).compareTo(new Span(_i1, _i3)), equalTo(0));
    }

    @Test
    public void compareGreater() {
        assertThat(new Span(_i1, _i3).compareTo(new Span(_i1, _i2)), equalTo(1));
        assertThat(new Span(_i1, _i3).compareTo(new Span(_i2, _i3)), equalTo(1));
    }

    @Test
    public void compareLess() {
        assertThat(new Span(_i1, _i2).compareTo(new Span(_i1, _i3)), equalTo(-1));
        assertThat(new Span(_i2, _i3).compareTo(new Span(_i1, _i3)), equalTo(-1));
    }

    @Test
    public void toMinutes() {
        assertThat(new Span(_i1, _i3).toMinutes(), equalTo(3020));
    }

    @Test
    public void toMinutesEmpty() {
        assertThat(new Span(_i1, _i1).toMinutes(), equalTo(0));
    }

    @Test
    public void toSpansSingle() {
        assertThat(Span.toSpans(newList(
                _i1,
                _i3)),
                equalTo(newList(
                        new Span(_i1, _i3))));
    }

    @Test
    public void toSpansMultiple() {
        assertThat(Span.toSpans(newList(
                _i1,
                _i2,
                _i3)),
                equalTo(newList(
                        new Span(_i1, _i2),
                        new Span(_i2, _i3))));
    }

    @Test
    public void toSpansNone() {
        assertThat(Span.toSpans(newList(
                _i1)),
                equalTo(Collections.<Span>emptyList()));
    }

    @Test
    public void toSpansEmpty() {
        assertThat(Span.toSpans(Collections.<Instant>emptyList()),
                equalTo(Collections.<Span>emptyList()));
    }
}