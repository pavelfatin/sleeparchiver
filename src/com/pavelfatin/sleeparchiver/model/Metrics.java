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

import com.pavelfatin.sleeparchiver.lang.Utilities;

import java.util.Collections;
import java.util.List;

public class Metrics {
    private List<Time> _moments;
    private List<Instant> _instants;
    private List<Span> _spans;


    Metrics(Night night) {
        _moments = night.getCompleteMoments();
        _instants = Instant.toInstants(_moments);
        _spans = Span.toSpans(_instants);
    }

    public Integer getAverage() {
        return getDuration() / getBreaksCount();
    }

    public int getBreaksCount() {
        return _moments.size() - 2;
    }

    public Instant getFirstInstant() {
        return Utilities.first(_instants);
    }

    public Instant getLastInstant() {
        return Utilities.last(_instants);
    }

    public int getSpansCount() {
        return _spans.size();
    }

    public Span getFirstSpan() {
        return Utilities.first(_spans);
    }

    public Span getLastSpan() {
        return Utilities.last(_spans);
    }

    public int getDuration() {
        return new Span(getFirstSpan().getBegin(), getLastSpan().getBegin()).toMinutes();
    }

    public Span getTotalSpan() {
        return new Span(getFirstInstant(), getLastInstant());
    }

    public List<Instant> getInstants() {
        return Collections.unmodifiableList(_instants);
    }

    public List<Span> getSpans() {
        return Collections.unmodifiableList(_spans);
    }
}
