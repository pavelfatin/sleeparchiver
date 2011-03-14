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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DeviceTest {
    @Test
    public void normal() throws IOException {
        assertThat(readNight("normal.dat"),
                equalTo(new Night(new Date(2009, 6, 14),
                        new Time(10, 10),
                        30,
                        new Time(2, 16),
                        Utilities.newList(
                                new Time(2, 42),
                                new Time(3, 15),
                                new Time(4, 48),
                                new Time(5, 16),
                                new Time(5, 53),
                                new Time(6, 27),
                                new Time(6, 38),
                                new Time(8, 11),
                                new Time(8, 27),
                                new Time(8, 58),
                                new Time(9, 11),
                                new Time(9, 33),
                                new Time(9, 40)))));
    }

    @Test
    public void empty() throws IOException {
        assertThat(readNight("empty.dat"),
                equalTo(new Night(new Date(2009, 6, 14),
                        new Time(10, 10),
                        30,
                        new Time(2, 16),
                        new ArrayList<Time>())));
    }

    @Test(expected = ProtocolException.class)
    public void checksum() throws IOException {
        readNight("checksum.dat");
    }

    @Test(expected = ProtocolException.class)
    public void handshake() throws IOException {
        readNight("handshake.dat");
    }

    @Test(expected = ProtocolException.class)
    public void ending() throws IOException {
        readNight("ending.dat");
    }

    @Test(expected = ProtocolException.class)
    public void incomplete() throws IOException {
        readNight("incomplete.dat");
    }

    private Night readNight(String file) throws IOException {
        URL url = getClass().getResource("device/" + file);
        BufferedInputStream stream = new BufferedInputStream(url.openStream());
        Night night = Device.readNight(stream, 2009);
        stream.close();
        return night;
    }
}
