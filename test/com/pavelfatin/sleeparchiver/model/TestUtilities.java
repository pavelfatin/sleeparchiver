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

import java.io.*;

public class TestUtilities {
    public static File createTempFile(String name) {
        return new File(String.format("%s%s%s",
                System.getProperty("java.io.tmpdir"),
                System.getProperty("file.separator"),
                name));
    }

    public static String contentOf(String file, String cr) {
        try {
            StringBuilder builder = new StringBuilder();
            InputStream stream = TestUtilities.class.getResourceAsStream(file);
            if (stream == null) {
                throw new RuntimeException("File not found: " + file);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
                builder.append(cr);
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
