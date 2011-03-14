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

import com.pavelfatin.sleeparchiver.lang.MyObject;
import com.pavelfatin.sleeparchiver.lang.Utilities;

import java.util.ArrayList;
import java.util.List;

public class Span extends MyObject implements Comparable<Span> {
    private Instant _begin;
    private Instant _end;


    public Span(Instant begin, Instant end) {
        if (end.isLessThan(begin)) {
            throw new IllegalArgumentException("Begin is less than end: " + begin + ", " + end);
        }
        _begin = begin;
        _end = end;
    }

    protected Comparable[] getValues() {
        return new Comparable[]{_begin, _end};
    }

    public Instant getBegin() {
        return _begin;
    }

    public Instant getEnd() {
        return _end;
    }

    public int toMinutes() {
        return _end.toMinutes() - _begin.toMinutes();
    }

    @Override
    public String toString() {
        return String.format("%s-%s", _begin.toString(), _end.toString());
    }

    public int compareTo(Span o) {
        return Utilities.compare(toMinutes(), o.toMinutes());
    }

    public static List<Span> toSpans(List<Instant> instants) {
        List<Span> spans = new ArrayList<Span>();
        for (int i = 0; i < instants.size(); i++) {
            if (i > 0) {
                spans.add(new Span(instants.get(i - 1), instants.get(i)));
            }
        }
        return spans;
    }
}
