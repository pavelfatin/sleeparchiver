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

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MySelectionModel extends DefaultListSelectionModel {
    public List<Integer> getSelectedIndices() {
        int min = getMinSelectionIndex();
        int max = getMaxSelectionIndex();

        List<Integer> list = new ArrayList<Integer>();
        for (int i = min; i <= max; i++) {
            if (isSelectedIndex(i)) {
                list.add(i);
            }
        }

        return list;
    }

    public void setSelectedIndices(List<Integer> list) {
        clearSelection();
        for (Integer index : list) {
            addSelectionInterval(index, index);
        }
    }

    public boolean isSelectionExists() {
        return !isSelectionEmpty();
    }

    public boolean isSelectionSingle() {
        return isSelectionExists() && getMinSelectionIndex() == getMaxSelectionIndex();
    }

    public void setSelectedIndex(int index) {
        setSelectionInterval(index, index);
    }

    public int getSelectionSize() {
        return getMaxSelectionIndex() - getMinSelectionIndex() + 1;
    }
}
