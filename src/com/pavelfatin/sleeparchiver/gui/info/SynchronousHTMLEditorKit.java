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

package com.pavelfatin.sleeparchiver.gui.info;

import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

class SynchronousHTMLEditorKit extends HTMLEditorKit {
    public Document createDefaultDocument() {
        AbstractDocument doc = (AbstractDocument) super.createDefaultDocument();
        doc.setAsynchronousLoadPriority(-1);
        return doc;
    }

    public ViewFactory getViewFactory() {
        return new SynchronousImageViewFactory(super.getViewFactory());
    }

    private static class SynchronousImageViewFactory implements ViewFactory {
        private ViewFactory _impl;

        private SynchronousImageViewFactory(ViewFactory impl) {
            _impl = impl;
        }

        public View create(Element elem) {
            View v = _impl.create(elem);

            if ((v != null) && (v instanceof ImageView)) {
                ((ImageView) v).setLoadsSynchronously(true);
            }

            return v;
        }
    }
}