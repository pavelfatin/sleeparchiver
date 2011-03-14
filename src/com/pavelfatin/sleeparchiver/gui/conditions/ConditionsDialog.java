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

package com.pavelfatin.sleeparchiver.gui.conditions;

import com.pavelfatin.sleeparchiver.gui.editor.EditorDialog;
import com.pavelfatin.sleeparchiver.lang.Utilities;
import com.pavelfatin.sleeparchiver.model.Night;
import com.pavelfatin.sleeparchiver.swing.Builder;
import com.pavelfatin.sleeparchiver.swing.ListPopupHandler;
import com.pavelfatin.sleeparchiver.swing.MyDialog;
import com.pavelfatin.sleeparchiver.swing.MySelectionModel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class ConditionsDialog extends MyDialog {
    private static final Dimension DEFAULT_SIZE = new Dimension(380, 280);

    private JList _list;
    private DefaultListModel _model;
    private MySelectionModel _selection;

    private boolean _editEnabled;
    private boolean _removeEnabled;

    private List<Night> _nights;
    private List<Night> _originals;


    public ConditionsDialog(JFrame owner, List<Night> nights) {
        super(owner, "dialogConditions", true);

        setPreferredSize(DEFAULT_SIZE);

        _originals = Collections.unmodifiableList(nights);

        setContent(createUI());

        Utilities.registerAction(_list, getAction("edit"), "ACTION_EDIT");
        Utilities.registerAction(_list, getAction("remove"), "ACTION_REMOVE");

        setNights(nights);
    }

    protected JPanel createUI() {
        Builder b = new Builder("default:grow",
                "fill:default:grow");

        b.add(createManager(), 1, 1);

        return b.getPanel();
    }

    private JPanel createManager() {
        Builder b = new Builder("default:grow, $lcgap, default",
                "fill:default:grow");

        _list = createList();

        b.add(new JScrollPane(_list), 1, 1);
        b.add(createButtons(), 3, 1);

        return b.getPanel();
    }

    private JList createList() {
        _model = new DefaultListModel();

        _selection = new MySelectionModel();
        _selection.addListSelectionListener(new SelectionListener());

        JList list = Builder.createList("listConditions");
        list.setModel(_model);
        list.setSelectionModel(_selection);

        list.addMouseListener(new ListClickListener());

        PopupMenu menu = new PopupMenu(getActions());
        list.addMouseListener(new ListPopupHandler(list, menu));

        return list;
    }

    private JPanel createButtons() {
        Builder b = new Builder("default",
                "default, $lgap, default");

        b.add(b.createButton(getAction("edit")), 1, 1);
        b.add(b.createButton(getAction("remove")), 1, 3);

        return b.getPanel();
    }

    private void setNights(List<Night> nights) {
        _nights = new ArrayList<Night>(nights);

        List<String> conditions = conditionsOf(nights);
        setStrings(conditions);
        setModified(isDataChanged());
    }

    private List<String> conditionsOf(List<Night> nights) {
        Set<String> unique = new HashSet<String>();

        for (Night night : nights) {
            unique.addAll(night.getConditions());
        }

        List<String> list = new ArrayList<String>(unique);
        Collections.sort(list);

        return list;
    }

    private void setStrings(List<String> list) {
        _model.clear();
        for (String s : list) {
            _model.addElement(s);
        }
        _list.setSelectedIndex(list.isEmpty() ? -1 : 0);
    }

    protected boolean isDataChanged() {
        return !_originals.equals(_nights);
    }

    @Action(enabledProperty = "editEnabled")
    public void edit() {
        EditorDialog editor = new EditorDialog(this, getResources().getString("titleEditCondition"));
        String condition = (String) _list.getSelectedValue();
        editor.setText(condition);
        ((SingleFrameApplication) Application.getInstance()).show(editor);
        if (editor.isAccepted()) {
            String replacement = editor.getText();
            if (isSafeToReplace(condition, replacement)) {
                List<Night> data = doEdit(_nights, condition, replacement);
                setNights(data);
                _list.setSelectedValue(replacement, true);
            }
        }
    }

    private boolean isSafeToReplace(String condition, String replacement) {
        if (conditionsOf(_nights).contains(replacement)) {
            String format = getResources().getString("messageConditionExists");
            int result = JOptionPane.showConfirmDialog(this,
                    String.format(format, condition, replacement),
                    getResources().getString("titleConditionExists"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            return result == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private List<Night> doEdit(List<Night> nights, String condition, String replacement) {
        List<Night> result = new ArrayList<Night>();
        for (Night night : nights) {
            List<String> conditions = new ArrayList<String>(night.getConditions());
            if (conditions.contains(condition)) {
                conditions.set(conditions.indexOf(condition), replacement);
            }
            Collections.sort(conditions);
            result.add(night.with(conditions));
        }
        return result;
    }

    @Action(enabledProperty = "removeEnabled")
    public void remove() {
        Object[] values = _list.getSelectedValues();

        int selection = _list.getSelectedIndex();

        List<Night> data = doRemove(_nights, values);
        setNights(data);

        int size = _list.getModel().getSize();
        _list.setSelectedIndex(selection < size ? selection : size - 1);
    }

    private List<Night> doRemove(List<Night> nights, Object[] conditions) {
        List<Night> result = new ArrayList<Night>();
        for (Night night : nights) {
            List<String> copy = new ArrayList<String>(night.getConditions());
            for (Object condition : conditions) {
                copy.remove(condition);
            }
            result.add(night.with(copy));
        }
        return result;
    }

    public boolean isEditEnabled() {
        return _editEnabled;
    }

    private void setEditEnabled(boolean enabled) {
        boolean previous = _editEnabled;
        _editEnabled = enabled;
        firePropertyChange("editEnabled", previous, enabled);
    }

    public boolean isRemoveEnabled() {
        return _removeEnabled;
    }

    private void setRemoveEnabled(boolean enabled) {
        boolean previous = _removeEnabled;
        _removeEnabled = enabled;
        firePropertyChange("removeEnabled", previous, enabled);
    }

    private void updateListActions() {
        setEditEnabled(_selection.isSelectionSingle());
        setRemoveEnabled(_selection.isSelectionExists());
    }

    public List<Night> getData() {
        return Collections.unmodifiableList(_nights);
    }


    private class SelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            updateListActions();
        }
    }

    private class ListClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                if (isEditEnabled()) {
                    getActions().get("edit").actionPerformed(new ActionEvent(ConditionsDialog.this, 0, null));
                }
            }
        }
    }
}
