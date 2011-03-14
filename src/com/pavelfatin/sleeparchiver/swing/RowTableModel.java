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

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class RowTableModel<T> extends DefaultTableModel {
    private List<T> _rows = new ArrayList<T>();


    protected RowTableModel(String... columns) {
        super(columns, 0);
    }

    public List<T> getRows() {
        return Collections.unmodifiableList(_rows);
    }

    public void setRows(Collection<T> rows) {
        _rows.clear();
//        fireTableRowsDeleted(0, _rows.size() == 0 ? 0 : _rows.size() - 1);
        _rows.addAll(rows);
//        fireTableRowsInserted(0, _rows.size() == 0 ? 0 : _rows.size() - 1);
        fireTableDataChanged();
    }

    public T getRowAt(int index) {
        return _rows.get(index);
    }

    public List<T> getRowsAt(int min, int max) {
        return Collections.unmodifiableList(_rows.subList(min, max + 1));
    }

    public int indexOf(T row) {
        return _rows.indexOf(row);
    }

    public void addRow(T row) {
        _rows.add(row);
        fireTableRowsInserted(_rows.size() - 1, _rows.size() - 1);
    }

    public void addRowAt(int index, T row) {
        _rows.add(index, row);
        fireTableRowsInserted(index, index);
    }

    public void addRowsAt(int index, List<T> rows) {
        _rows.addAll(index, rows);
        fireTableRowsInserted(index, index + rows.size() - 1);
    }

    public void setRowAt(int index, T row) {
        _rows.set(index, row);
        fireTableRowsUpdated(index, index);
    }

    public void removeRowAt(int index) {
        _rows.remove(index);
        fireTableRowsDeleted(index, index);
    }

    public void removeRowsAt(int min, int max) {
        _rows.subList(min, max + 1).clear();
        fireTableRowsDeleted(min, max);
    }

    @Override
    public Object getValueAt(int row, int column) {
        return get(getRowAt(row), row, column);
    }

    @Override
    public int getRowCount() {
        return _rows == null ? 0 : _rows.size();
    }

    public boolean isEmpty() {
        return getRowCount() == 0;
    }

    public boolean isFilled() {
        return !isEmpty();
    }

    public int getLastIndex() {
        return getRowCount() - 1;
    }

    public T getFirstRow() {
        return getRowAt(0);
    }

    public T getLastRow() {
        return getRowAt(getLastIndex());
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    protected abstract Object get(T row, int rowIndex, int columnIndex);
}