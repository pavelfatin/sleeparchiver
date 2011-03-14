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

package com.pavelfatin.sleeparchiver.swing;

public class MyDatePicker {
//    extends JXDatePicker {
//    public MyDatePicker() {
//        JFormattedTextField editor = new JFormattedTextField(new DateFormatter());
//
//        editor.setColumns(7);
//        editor.setHorizontalAlignment(JTextField.CENTER);
//        editor.addFocusListener(new TextSelector(editor));
//
//        setEditor(editor);
//    }
//
//    public Date getMyDate() {
//        return getDate() == null ? null : toDate(getDate());
//    }
//
//    public void setMyDate(Date date) {
//        setDate(date == null ? null : date.toCalendar().getTime());
//    }
//
//    private static Date toDate(java.util.Date value) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(value);
//        return Date.fromCalendar(calendar);
//    }
//
//
//    private static class DateFormatter extends MaskedValueFormatter<java.util.Date> {
//        protected DateFormatter() {
//            super(Date.getPrototype().toCalendar().getTime());
//        }
//
//        protected java.util.Date parse(String text) throws IllegalArgumentException {
//            return Date.parse(text).toCalendar().getTime();
//        }
//
//        protected String format(java.util.Date value) {
//            return toDate(value).format();
//        }
//    }
}
