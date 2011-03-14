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
import com.pavelfatin.sleeparchiver.lang.Utilities;
import org.jdesktop.application.ApplicationActionMap;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

class Menu extends JMenuBar {
    private JMenu _recents;
    private JMenuItem _clearRecent;
    private Action _reopen;
    private JMenu _zooms;
    private JMenu _transforms;

    private Zoom _zoom;
    private Transform _transform;


    Menu(ApplicationActionMap actions) {
        JMenu file = createMenu("menuFile");
        file.add(new JMenuItem(actions.get("blank")));
        file.add(new JMenuItem(actions.get("open")));

        _recents = createMenu("menuFileReopen");
        _clearRecent = new JMenuItem(actions.get("clearRecent"));
        _reopen = actions.get("reopen");
        setRecents(new ArrayList<String>());

        file.add(_recents);
        file.addSeparator();
        file.add(new JMenuItem(actions.get("save")));
        file.add(new JMenuItem(actions.get("saveAs")));
        file.addSeparator();
        file.add(new JMenuItem(actions.get("importData")));
        file.add(new JMenuItem(actions.get("exportData")));
        file.addSeparator();
        file.add(new JMenuItem(actions.get("exit")));

        JMenu edit = createMenu("menuEdit");
        edit.add(new JMenuItem(actions.get("undo")));
        edit.add(new JMenuItem(actions.get("redo")));
        edit.addSeparator();
        edit.add(new JMenuItem(actions.get("selectAll")));
        edit.addSeparator();
        edit.add(new JMenuItem(actions.get("preferences")));

        JMenu view = createMenu("menuView");
        _transforms = createMenu("menuViewTransform");
        view.add(_transforms);
        view.addSeparator();
        _zooms = createMenu("menuViewZoom");
        view.add(_zooms);
        view.addSeparator();
        view.add(new JMenuItem(actions.get("zoomIn")));
        view.add(new JMenuItem(actions.get("zoomOut")));
        view.add(new JMenuItem(actions.get("zoomReset")));

        JMenu nights = createMenu("menuNights");
        nights.setMnemonic('N');
        nights.add(new JMenuItem(actions.get("download")));
        nights.addSeparator();
        nights.add(new JMenuItem(actions.get("add")));
        nights.add(new JMenuItem(actions.get("edit")));
        nights.add(new JMenuItem(actions.get("remove")));
        nights.addSeparator();
        nights.add(new JMenuItem(actions.get("conditions")));

        JMenu about = createMenu("menuHelp");
        about.setMnemonic('H');
        about.add(new JMenuItem(actions.get("license")));
        about.add(new JMenuItem(actions.get("about")));

        add(file);
        add(edit);
        add(view);
        add(nights);
        add(about);
    }

    private JMenu createMenu(String name) {
        JMenu menu = new JMenu();
        menu.setName(name);
        return menu;
    }

    public void setRecents(List<String> list) {
        _recents.removeAll();

        for (int i = list.size() - 1; i >= 0; i--) {
            String text = list.get(i);
            JMenuItem item = new JMenuItem(new ReopenActionProxy(text));
            _recents.add(item, 0);
        }

        _recents.addSeparator();
        _recents.add(_clearRecent);

        _recents.setEnabled(!list.isEmpty());
    }

    public void setTransforms(List<Transform> transforms) {
        _transforms.removeAll();
        for (Transform transform : transforms) {
            addTransform(transform);
        }
    }

    public void addTransform(Transform transform) {
        String name = Utilities.capitalize(transform.getName());
        TransformAction action = new TransformAction(name, transform);
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(action);
        item.setMnemonic(name.charAt(0));
        _transforms.add(item);
    }

    public Transform getTransform() {
        return _transform;
    }

    public void setTransform(Transform transform) {
        Transform previous = _transform;
        _transform = transform;
        selectTransform(transform);
        firePropertyChange("transform", previous, transform);
    }

    private void selectTransform(Transform transform) {
        for (int i = 0; i < _transforms.getItemCount(); i++) {
            JMenuItem item = _transforms.getItem(i);
            TransformAction action = (TransformAction) item.getAction();
            item.setSelected(transform.equals(action.getValue()));
        }
    }

    public Zoom getZoom() {
        return _zoom;
    }

    public void setZooms(List<Zoom> zooms) {
        _zooms.removeAll();
        for (Zoom zoom : zooms) {
            addZoom(zoom);
        }
    }

    public void addZoom(Zoom zoom) {
        ZoomAction action = new ZoomAction(zoom.getName(), zoom);
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(action);
        _zooms.add(item);
    }

    public void setZoom(Zoom zoom) {
        Zoom previous = _zoom;
        _zoom = zoom;
        selectZoom(zoom);
        firePropertyChange("zoom", previous, zoom);
    }

    private void selectZoom(Zoom zoom) {
        for (int i = 0; i < _zooms.getItemCount(); i++) {
            JMenuItem item = _zooms.getItem(i);
            ZoomAction action = (ZoomAction) item.getAction();
            item.setSelected(zoom.equals(action.getValue()));
        }
    }


    private class ReopenActionProxy extends AbstractAction {
        public ReopenActionProxy(String line) {
            super(line);

            putValue(AbstractAction.SHORT_DESCRIPTION, _reopen.getValue(AbstractAction.SHORT_DESCRIPTION));
        }

        public void actionPerformed(ActionEvent e) {
            _reopen.actionPerformed(new ActionEvent(e.getSource(), e.getID(), getName()));
        }
    }

    private class ZoomAction extends AbstractAction {
        private Zoom _value;


        public ZoomAction(String name, Zoom value) {
            super(name);
            _value = value;
        }

        public void actionPerformed(ActionEvent e) {
            setZoom(_value);
        }

        public Zoom getValue() {
            return _value;
        }
    }

    private class TransformAction extends AbstractAction {
        private Transform _value;


        public TransformAction(String name, Transform value) {
            super(name);
            _value = value;
        }

        public void actionPerformed(ActionEvent e) {
            setTransform(_value);
        }

        public Transform getValue() {
            return _value;
        }
    }
}
