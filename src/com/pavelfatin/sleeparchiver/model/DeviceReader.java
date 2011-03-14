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

import java.io.IOException;
import java.io.InputStream;

public class DeviceReader {
    private static final int HANDSHAKE = 86;
    private static final int ENDING = 26;

    private InputStream _stream;
    private int _year;

    int _sum;


    public DeviceReader(InputStream stream, int year) {
        _stream = stream;
        _year = year;
    }

    public Date readDate() throws IOException {
        int months = readByte();
        int days = readByte();
        try {
            return new Date(_year, months, days);
        } catch (IllegalArgumentException e) {
            throw new ProtocolException(e);
        }
    }

    public Time readTime() throws IOException {
        int hours = readByte();
        int minutes = readByte();
        try {
            return new Time(hours, minutes);
        } catch (IllegalArgumentException e) {
            throw new ProtocolException(e);
        }
    }

    public int readByte() throws IOException {
        int i = _stream.read();
        if (i == -1) {
            throw new ProtocolException("Data is incomplete");
        }
        _sum += i;
        return i;
    }

    public void skip() throws IOException {
        readByte();
    }

    public int getChecksum() {
        return _sum % 256;
    }

    public void readHandshake() throws IOException {
        int i = readByte();
        _sum = 0;
        if (i != HANDSHAKE) {
            throw new ProtocolException(String.format(
                    "Incorrect handshake: %d, expected: %d", i, HANDSHAKE));
        }
    }

    public void readEnding() throws IOException {
        int i = readByte();
        if (i != ENDING) {
            throw new ProtocolException(String.format(
                    "Incorrect ending: %d, expected: %d", i, ENDING));
        }
    }
}
