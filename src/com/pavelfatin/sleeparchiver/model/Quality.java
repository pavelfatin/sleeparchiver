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
import org.jdesktop.application.ResourceMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlJavaTypeAdapter(Quality.Adapter.class)
public enum Quality {
    Unknown,
    Bad,
    Average,
    Good;


    public boolean isUnknown() {
        return Unknown.equals(this);
    }

    public boolean isKnown() {
        return !isUnknown();
    }

    public String format(ResourceMap map) {
        return isUnknown() ? " " : map.getString("text" + toString());
    }

    public Color color(ResourceMap map) {
        return map.getColor("color" + toString());
    }

    public static List<Quality> members() {
        ArrayList<Quality> list = new ArrayList<Quality>();
        list.addAll(Arrays.asList(values()));
        list.remove(Unknown);
        return list;
    }

    public String format() {
        return isUnknown() ? "" : toString().toLowerCase();
    }

    public static Quality parse(String s) {
        return s.isEmpty()
                ? Quality.Unknown
                : Quality.valueOf(Quality.class, Utilities.capitalize(s));
    }


    static class Adapter extends XmlAdapter<String, Quality> {
        @Override
        public Quality unmarshal(String v) throws Exception {
            return v == null ? Quality.Unknown : parse(v);
        }

        @Override
        public String marshal(Quality v) throws Exception {
            return v.isUnknown() ? null : v.format();
        }
    }
}
