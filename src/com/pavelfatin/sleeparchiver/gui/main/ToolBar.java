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

package com.pavelfatin.sleeparchiver.gui.main;

import com.pavelfatin.sleeparchiver.gui.main.render.Transform;
import com.pavelfatin.sleeparchiver.gui.main.render.Zoom;
import com.pavelfatin.sleeparchiver.swing.NamedRenderer;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

class ToolBar extends JToolBar {
    private JComboBox _zooms;
    private JComboBox _transforms;


    ToolBar(ResourceMap resources, ApplicationActionMap actions) {
        add(actions.get("blank"));
        add(actions.get("open"));
        add(actions.get("save"));
        addSeparator();
        add(actions.get("undo"));
        add(actions.get("redo"));
        addSeparator();
        add(actions.get("download"));
        addSeparator();
        add(actions.get("add"));
        add(actions.get("edit"));
        add(actions.get("remove"));
        addSeparator();
        add(actions.get("conditions"));
        setFloatable(false);
        setBorderPainted(false);

        add(Box.createHorizontalGlue());

        NamedRenderer namedRenderer = new NamedRenderer();
        namedRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        add(actions.get("zoomOut"));
        _zooms = new JComboBox(new DefaultComboBoxModel());
        _zooms.setRenderer(namedRenderer);
        _zooms.addItemListener(new ZoomListener());
        add(_zooms);
        add(actions.get("zoomIn"));

        add(Box.createHorizontalStrut(5));

        _transforms = new JComboBox(new DefaultComboBoxModel());
        _transforms.setRenderer(namedRenderer);
        _transforms.addItemListener(new TransformListener());
        add(_transforms);

        setBorder(new EmptyBorder(2, 0, 0, 0));
    }

    public void setZooms(List<Zoom> zooms) {
        _zooms.setModel(new DefaultComboBoxModel());
        for (Zoom zoom : zooms) {
            addZoom(zoom);
        }
    }

    public void addZoom(Zoom zoom) {
        ((DefaultComboBoxModel) _zooms.getModel()).addElement(zoom);
    }

    public Zoom getZoom() {
        return (Zoom) (_zooms.getSelectedItem());
    }

    public void setZoom(Zoom zoom) {
        _zooms.setSelectedItem(zoom);
    }

    public void increaseZoom() {
        int index = _zooms.getSelectedIndex();
        if (index < _zooms.getItemCount() - 1) {
            _zooms.setSelectedIndex(index + 1);
        }
    }

    public void decreaseZoom() {
        int index = _zooms.getSelectedIndex();
        if (index > 0) {
            _zooms.setSelectedIndex(index - 1);
        }
    }

    public void resetZoom() {
        _zooms.setSelectedIndex(_zooms.getItemCount() / 2);
    }

    public Transform getTransform() {
        return (Transform) _transforms.getSelectedItem();
    }

    public void setTransforms(List<Transform> transforms) {
        _transforms.setModel(new DefaultComboBoxModel());
        for (Transform transform : transforms) {
            addTransform(transform);
        }
    }

    public void addTransform(Transform transform) {
        ((DefaultComboBoxModel) _transforms.getModel()).addElement(transform);
    }

    public void setTransform(Transform transform) {
        _transforms.setSelectedItem(transform);
    }


    private class ZoomListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                firePropertyChange("zoom", null, getZoom());
            }
        }
    }

    private class TransformListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                firePropertyChange("transform", null, getTransform());
            }
        }
    }
}
