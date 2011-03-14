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
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Device {
    private static final int HANDSHAKE = 86;

    private static final int TIMEOUT = 2000;
    private static final int BUFFER = 250;
    private static final int DELAY = 500;

    private String _app;
    private int _year;


    public Device(String app, int year) {
        _app = app;
        _year = year;
    }

    public Night readData() {
        List<CommPortIdentifier> ids = findFreeSerialPorts();

        for (CommPortIdentifier id : ids) {
            try {
                return readNight(id, _app, _year);
            } catch (ProtocolException e) {
                // do nothing
            } catch (IOException e) {
                // do nothing
            } catch (UnsupportedCommOperationException e) {
                // do nothing
            } catch (PortInUseException e) {
                // do nothing
            }
        }

        return null;
    }

    private static Night readNight(CommPortIdentifier id, String app, int year)
            throws IOException, UnsupportedCommOperationException, PortInUseException {
        SerialPort port = (SerialPort) id.open(app, TIMEOUT);
        try {
            port.setSerialPortParams(2400,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            port.setInputBufferSize(BUFFER);

            port.setRTS(false);
            port.setDTR(true);

            sendHandshake(port);
            sleep(DELAY);

            return readNight(port, year);
        } finally {
            port.close();
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendHandshake(SerialPort port) throws IOException {
        OutputStream out = port.getOutputStream();
        try {
            out.write(HANDSHAKE);
            out.flush();
        } finally {
            Utilities.close(out);
        }
    }

    private static Night readNight(SerialPort port, int year) throws IOException {
        InputStream in = new BufferedInputStream(port.getInputStream());
        try {
            return readNight(new DeviceReader(in, year));
        } finally {
            Utilities.close(in);
        }
    }

    static Night readNight(InputStream stream, int year) throws IOException {
        return readNight(new DeviceReader(stream, year));
    }

    private static Night readNight(DeviceReader reader) throws IOException {
        reader.readHandshake();

        Date date = reader.readDate();
        reader.skip();
        int window = reader.readByte();
        Time toBed = reader.readTime();
        Time alarm = reader.readTime();

        int count = reader.readByte();
        List<Time> moments = new ArrayList<Time>();
        for (int i = 0; i < count; i++) {
            moments.add(reader.readTime());
            reader.skip();
        }

        int minutesLow = reader.readByte();
        int minutesHigh = reader.readByte();

        int dataChecksum = reader.getChecksum();

        int checksum = reader.readByte();
        if (dataChecksum != checksum) {
            throw new ProtocolException(String.format(
                    "Incorrect checksum: %d, expected: %d", dataChecksum, checksum));
        }

        reader.readEnding();

        return new Night(date, alarm, window, toBed, moments);
    }

    private static List<CommPortIdentifier> findFreeSerialPorts() {
        List<CommPortIdentifier> result = new ArrayList<CommPortIdentifier>();
        Enumeration e = CommPortIdentifier.getPortIdentifiers();
        while (e.hasMoreElements()) {
            CommPortIdentifier id = (CommPortIdentifier) e.nextElement();
            if (id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (!id.isCurrentlyOwned()) {
                    result.add(id);
                }
            }
        }
        return result;
    }
}
