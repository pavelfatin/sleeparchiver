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

package com.pavelfatin.sleeparchiver.lang;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class UtilitiesTest {
    @Test
    public void prefixSingleDigits() {
        assertThat(Utilities.prefixSingleDigits("1"), equalTo("01"));
        assertThat(Utilities.prefixSingleDigits("0"), equalTo("00"));
        assertThat(Utilities.prefixSingleDigits("00"), equalTo("00"));
        assertThat(Utilities.prefixSingleDigits("000"), equalTo("000"));
        assertThat(Utilities.prefixSingleDigits("123"), equalTo("123"));
        assertThat(Utilities.prefixSingleDigits("0123"), equalTo("0123"));
        assertThat(Utilities.prefixSingleDigits(" 1"), equalTo(" 01"));
        assertThat(Utilities.prefixSingleDigits("1 "), equalTo("01 "));
        assertThat(Utilities.prefixSingleDigits(" 1 "), equalTo(" 01 "));
        assertThat(Utilities.prefixSingleDigits("c1"), equalTo("c01"));
        assertThat(Utilities.prefixSingleDigits("1c"), equalTo("01c"));
        assertThat(Utilities.prefixSingleDigits("c1c"), equalTo("c01c"));
        assertThat(Utilities.prefixSingleDigits("cc1cc"), equalTo("cc01cc"));
        assertThat(Utilities.prefixSingleDigits("1 2 3"), equalTo("01 02 03"));
        assertThat(Utilities.prefixSingleDigits("1c2cc03 123ccc"), equalTo("01c02cc03 123ccc"));
    }
}
