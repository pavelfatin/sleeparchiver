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

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@XmlRootElement(name = "night")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Night extends MyObject {
    private static final Integer[] WINDOWS = new Integer[]{0, 10, 20, 30, 40, 50, 60, 70, 80, 90};
    private static final NightsComparator NIGHTS_COMPARATOR = new NightsComparator();


    @XmlAttribute(name = "date")
    private Date _date;

    @XmlAttribute(name = "alarm")
    private Time _alarm;

    @XmlAttribute(name = "window")
    private int _window;

    @XmlAttribute(name = "toBed")
    private Time _toBed;

    @XmlAttribute(name = "easeOfFallingAsleep")
    private Ease _easeOfFallingAsleep = Ease.Unknown;

    @XmlAttribute(name = "qualityOfSleep")
    private Quality _qualityOfSleep = Quality.Unknown;

    @XmlAttribute(name = "easeOfWakingUp")
    private Ease _easeOfWakingUp = Ease.Unknown;

    @XmlAttribute(name = "alarmWorked")
    private Boolean _alarmWorked;

    @XmlAttribute(name = "comments")
    private String _comments;

    @XmlElement(name = "moment")
    @XmlElementWrapper(name = "moments")
    private List<Time> _moments = new ArrayList<Time>();

    @XmlElement(name = "condition")
    @XmlElementWrapper(name = "conditions")
    private List<String> _conditions = new ArrayList<String>();

    @XmlTransient
    private Metrics _metrics;


    private Night() {
    }

    public Night(Date date, Time alarm, int window, Time toBed, List<Time> moments) {
        _date = date;
        _alarm = alarm;
        _window = window;
        _toBed = toBed;
        _moments = new ArrayList<Time>(moments);
    }

    public Night(Date date, Time alarm, int window, Time toBed,
                 Ease easeOfFallingAsleep, Quality qualityOfSleep, Ease easeOfWakingUp,
                 boolean alarmWorked, String comments,
                 List<Time> moments, List<String> conditions) {
        _date = date;
        _alarm = alarm;
        _window = window;
        _toBed = toBed;
        _easeOfFallingAsleep = easeOfFallingAsleep;
        _qualityOfSleep = qualityOfSleep;
        _easeOfWakingUp = easeOfWakingUp;
        _alarmWorked = alarmWorked ? true : null;
        _comments = comments.isEmpty() ? null : comments;
        _moments = new ArrayList<Time>(moments);
        _conditions = new ArrayList<String>(conditions);
    }

    protected Object[] getValues() {
        return new Object[]{_date,
                _alarm,
                _window,
                _toBed,
                _easeOfFallingAsleep,
                _qualityOfSleep,
                _easeOfWakingUp,
                _alarmWorked,
                _comments,
                _moments,
                _conditions};

    }

    public Date getDate() {
        return _date;
    }

    public Time getAlarm() {
        return _alarm;
    }

    public int getWindow() {
        return _window;
    }

    public boolean hasWindow() {
        return _window > 0;
    }

    public Time getToBed() {
        return _toBed;
    }

    public Ease getEaseOfFallingAsleep() {
        return _easeOfFallingAsleep;
    }

    public Quality getQualityOfSleep() {
        return _qualityOfSleep;
    }

    public Ease getEaseOfWakingUp() {
        return _easeOfWakingUp;
    }

    public boolean isAlarmWorked() {
        return _alarmWorked == null ? false : _alarmWorked;
    }

    public String getComments() {
        return _comments == null ? "" : _comments;
    }

    public List<Time> getMoments() {
        return Collections.unmodifiableList(_moments);
    }

    public int getMomentsCount() {
        return _moments.size();
    }

    public boolean hasMoments() {
        return getMomentsCount() > 0;
    }

    public List<String> getConditions() {
        return Collections.unmodifiableList(_conditions);
    }

    public int getConditionsCount() {
        return _conditions.size();
    }

    public boolean hasConditions() {
        return getConditionsCount() > 0;
    }

    public Night with(List<String> conditions) {
        return new Night(_date, _alarm, _window, _toBed,
                _easeOfFallingAsleep, _qualityOfSleep, _easeOfWakingUp, _alarmWorked, _comments,
                _moments, conditions);
    }

    public boolean isComplete() {
        return _alarm != null
                && _toBed != null
                && hasMoments();
    }

    List<Time> getCompleteMoments() {
        List<Time> moments = new ArrayList<Time>();
        moments.add(_toBed);
        moments.addAll(_moments);
        moments.add(_alarm);
        return moments;
    }

    public Metrics getMetrics() {
        if (!isComplete()) {
            throw new IllegalStateException("Metrics are unavailable: data is not complete.");
        }
        if (_metrics == null) {
            _metrics = new Metrics(this);
        }
        return _metrics;
    }

    public static Integer[] getWindows() {
        return WINDOWS;
    }

    public static Comparator<Night> getComparator() {
        return NIGHTS_COMPARATOR;
    }
}
