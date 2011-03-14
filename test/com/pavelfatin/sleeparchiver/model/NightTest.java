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

import java.util.ArrayList;
import java.util.Collections;

import com.pavelfatin.sleeparchiver.lang.Utilities;

public class NightTest {
    @Test
    public void empty() {
        Night night = new Night(null, null, 0, null, new ArrayList<Time>());

        assertThat(night.getDate(), equalTo(null));
        assertThat(night.getAlarm(), equalTo(null));
        assertThat(night.getWindow(), equalTo(0));
        assertThat(night.getToBed(), equalTo(null));
        assertThat(night.getEaseOfFallingAsleep(), equalTo(Ease.Unknown));
        assertThat(night.getQualityOfSleep(), equalTo(Quality.Unknown));
        assertThat(night.getEaseOfWakingUp(), equalTo(Ease.Unknown));
        assertThat(night.isAlarmWorked(), equalTo(false));
        assertThat(night.getComments(), equalTo(""));

        assertThat(night.getMoments(), equalTo(Collections.<Time>emptyList()));
        assertThat(night.getMomentsCount(), equalTo(0));

        assertThat(night.getConditions(), equalTo(Collections.<String>emptyList()));
        assertThat(night.hasConditions(), equalTo(false));
        assertThat(night.getConditionsCount(), equalTo(0));    }

    @Test
    public void accuracy() {
        Night night = new Night(
                new Date(2001, 1, 1),
                new Time(20, 0),
                50,
                new Time(10, 0),
                Ease.Easy,
                Quality.Good,
                Ease.Normal,
                true,
                "foo",
                Utilities.newList(new Time(1, 2)),
                Utilities.newList("A", "B"));

        assertThat(night.getDate(), equalTo(new Date(2001, 1, 1)));
        assertThat(night.getAlarm(), equalTo(new Time(20, 0)));
        assertThat(night.getWindow(), equalTo(50));
        assertThat(night.getToBed(), equalTo(new Time(10, 0)));
        assertThat(night.getEaseOfFallingAsleep(), equalTo(Ease.Easy));
        assertThat(night.getQualityOfSleep(), equalTo(Quality.Good));
        assertThat(night.getEaseOfWakingUp(), equalTo(Ease.Normal));
        assertThat(night.isAlarmWorked(), equalTo(true));
        assertThat(night.getComments(), equalTo("foo"));

        assertThat(night.getMoments(), equalTo(Utilities.newList(new Time(1, 2))));
        assertThat(night.getMomentsCount(), equalTo(1));

        assertThat(night.getConditions(), equalTo(Utilities.newList("A", "B")));
        assertThat(night.hasConditions(), equalTo(true));
        assertThat(night.getConditionsCount(), equalTo(2));
    }
}