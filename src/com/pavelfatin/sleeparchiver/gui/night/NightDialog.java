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

package com.pavelfatin.sleeparchiver.gui.night;

import com.pavelfatin.sleeparchiver.gui.editor.EditorDialog;
import com.pavelfatin.sleeparchiver.lang.Utilities;
import com.pavelfatin.sleeparchiver.model.Ease;
import com.pavelfatin.sleeparchiver.model.Night;
import com.pavelfatin.sleeparchiver.model.Quality;
import com.pavelfatin.sleeparchiver.model.Time;
import com.pavelfatin.sleeparchiver.swing.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NightDialog extends MyDialog {
    private static final Dimension DEFAULT_SIZE = new Dimension(700, 500);

    private boolean _removeMomentsEnabled;
    private boolean _removeConditionsEnabled;

    private MyDateField _date;
    private MyTimeField _alarm;
    private JComboBox _window;
    private MyTimeField _toBed;
    private JFormattedTextField _moment;
    private JList _moments;
    private MyComboBox _condition;
    private JList _conditions;

    private Night _original;
    private ArrayList<String> _allConditions;
    private JLabel _average = Builder.createLabel("lavelAverage");
    private JComboBox _easeOfFallingAsleep;
    private JComboBox _qualityOfSleep;
    private JComboBox _easeOfWakingUp;
    private JTextArea _comments;
    private JCheckBox _wakeUpByAlarm;


    public NightDialog(JFrame owner, Night night, boolean isNew, List<String> conditions) {
        super(owner, "dialogNight", true);

        setPreferredSize(DEFAULT_SIZE);

        _original = night;
        _allConditions = new ArrayList<String>(conditions);

        setContent(createUI());
        setFooter(_average);

        setTitle(getString(isNew ? "titleAddition" : "titleEditing"));

        Utilities.registerAction(_moments, getAction("removeMoments"), "ACTION_REMOVE_MOMENTS");
        Utilities.registerAction(_conditions, getAction("removeConditions"), "ACTION_REMOVE_CONDITIONS");

        setData(night);
        registerChangesListener(new ChangesListener());
        updateModification();
        updateAverage();
        if (isNew) {
            if (_moments.getModel().getSize() > 0) {
                requestFocus(_condition);
            } else {
                positionFocus(_date, _alarm, _toBed, _moment);
            }
        } else {
            requestFocus(_date);
        }
    }

    private void updateAverage() {
        String format = getResources().getString("formatAverage");
        Night data = getData();
        _average.setText(data.isComplete() ? String.format(format, data.getMetrics().getAverage()) : "");
    }

    private void scrollToSelectionVisible(JList list) {
        int index = list.getSelectedIndex();
        if (index >= 0) {
            ListSelectionModel selection = list.getSelectionModel();
            list.scrollRectToVisible(
                    list.getCellBounds(selection.getMaxSelectionIndex(), selection.getMaxSelectionIndex()));
        }
    }

    private void registerChangesListener(ChangesListener listener) {
        _date.addPropertyChangeListener("date", listener);
        _alarm.addPropertyChangeListener("value", listener);
        _window.addActionListener(listener);
        _toBed.addPropertyChangeListener("value", listener);

        _moments.getModel().addListDataListener(listener);
        _conditions.getModel().addListDataListener(listener);

        _easeOfFallingAsleep.addActionListener(listener);
        _wakeUpByAlarm.addActionListener(listener);
        _easeOfWakingUp.addActionListener(listener);

        _comments.getDocument().addDocumentListener(listener);
    }

    protected void updateModification() {
        setModified(!getData().equals(_original));
    }

    private void setData(Night night) {
        _date.setDate(night.getDate());
        _alarm.setValue(night.getAlarm());
        _window.setSelectedItem(night.getWindow());
        _toBed.setValue(night.getToBed());

        _easeOfFallingAsleep.setSelectedItem(night.getEaseOfFallingAsleep());
        _qualityOfSleep.setSelectedItem(night.getQualityOfSleep());
        _easeOfWakingUp.setSelectedItem(night.getEaseOfWakingUp());
        _wakeUpByAlarm.setSelected(night.isAlarmWorked());

        _comments.setText(night.getComments());

        setListItems(_moments, night.getMoments());
        setListItems(_conditions, night.getConditions());
    }

    private void positionFocus(JFormattedTextField... fields) {
        for (final JFormattedTextField field : fields) {
            if (field.getValue() == null) {
                requestFocus(field);
                return;
            }
        }
    }

    private void requestFocus(final JComponent component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                component.requestFocusInWindow();
            }
        });
    }

    private void setListItems(JList list, List items) {
        DefaultListModel model = new DefaultListModel();
        for (Object item : items) {
            model.addElement(item);
        }
        list.setModel(model);
        if (model.getSize() > 0) {
            list.setSelectedValue(model.get(0), true);
        }
    }

    public Night getData() {
        return new Night(_date.getDate(),
                _alarm.getTime(),
                (Integer) _window.getSelectedItem(),
                _toBed.getTime(),
                (Ease) _easeOfFallingAsleep.getSelectedItem(),
                (Quality) _qualityOfSleep.getSelectedItem(),
                (Ease) _easeOfWakingUp.getSelectedItem(),
                _wakeUpByAlarm.isSelected(),
                _comments.getText().trim(),
                getListItems(_moments),
                getListItems(_conditions));
    }

    private List getListItems(JList list) {
        ListModel model = list.getModel();
        List items = new ArrayList();
        for (int i = 0; i < model.getSize(); i++) {
            items.add(model.getElementAt(i));
        }
        return items;
    }

    protected JPanel createUI() {
        Builder b = new Builder(
                "default:grow",
                "default, 9dlu, fill:default:grow");

        b.add(createTop(), 1, 1);
        b.add(createBottom(), 1, 3);

        return b.getPanel();
    }

    private JPanel createTop() {
        Builder b = new Builder(
                "3 * (default, $lcgap, pref, 9dlu), default, $lcgap, pref",
                "default");

        _date = b.createDateField("fieldDate");
        _alarm = b.createTimeField("fieldAlarm");
        _window = b.createComboBox("fieldWindow");
        _toBed = b.createTimeField("fieldToBed");
        _window.setModel(new DefaultComboBoxModel(Night.getWindows()));
        _window.setSelectedItem(20);

        b.add(b.createLabel("labelDate"), 1, 1, _date, 3, 1);
        b.add(b.createLabel("labelAlarm"), 5, 1, _alarm, 7, 1);
        b.add(b.createLabel("labelWindow"), 9, 1, _window, 11, 1);
        b.add(b.createLabel("labelToBed"), 13, 1, _toBed, 15, 1);

        return b.getPanel();
    }

    private JPanel createBottom() {
        Builder b = new Builder(
                "default, 6dlu, default:grow",
                "fill:default:grow");

        b.add(b.withSeparator("separatorMoments", createMoments()), 1, 1);
        b.add(createRight(), 3, 1);

        return b.getPanel();
    }

    private JPanel createMoments() {
        Builder b = new Builder(
                "pref, $lcgap, default, 6dlu, default",
                "default, $lgap, fill:default:grow");

        _moment = b.createTimeField("fieldMoment");
        _moment.addPropertyChangeListener("value", new MomentFieldListener());

        _moments = b.createList("listMoments");
        _moments.setCellRenderer(new TimeRenderer());
        _moments.getSelectionModel().addListSelectionListener(new MomentsSelectionListener());
        _moments.setPrototypeCellValue(Time.getPrototype());
        _moments.addMouseListener(new ListPopupHandler(_moments, new MomentsPopupMenu(getActions())));

        b.add(_moment, 1, 1);
        b.add(b.createLabel("labelAddMoment"), 3, 1);
        b.add(b.createButton(getAction("removeMoments")), 5, 1);

        b.add(new JScrollPane(_moments), 1, 3, 5);

        return b.getPanel();
    }


    private JPanel createRight() {
        Builder b = new Builder(
                "default:grow, 6dlu, default",
                "fill:default:grow, 6dlu, pref");

        b.add(b.withSeparator("separatorConditions", createConditions()), 1, 1);
        b.add(b.withSeparator("separatorObservations", createObservations()), 3, 1);
        b.add(b.withSeparator("separatorComments", createComments()), 1, 3, 3);

        return b.getPanel();
    }

    private JPanel createConditions() {
        Builder b = new Builder(
                "default, $lcgap, default, 6dlu, right:default:grow",
                "default, $lgap, fill:default:grow");

        _condition = b.createMyComboBox("fieldCondition");
        _condition.setInsertion(getResources().getString("newItem"));
        _condition.setModel(new DefaultComboBoxModel(_allConditions.toArray()));
        _condition.addComboBoxListener(new ConditionFieldListener());

        _conditions = b.createList("listConditions");
        _conditions.setModel(new DefaultListModel());
        _conditions.getSelectionModel().addListSelectionListener(new ConditionsSelectionListener());
        _conditions.addMouseListener(new ListPopupHandler(_conditions, new ConditionsPopupMenu(getActions())));

        b.add(_condition, 1, 1);
        b.add(b.createLabel("labelAddCondition"), 3, 1);
        b.add(b.createButton(getAction("removeConditions")), 5, 1);

        b.add(new JScrollPane(_conditions), 1, 3, 5);

        return b.getPanel();
    }

    private JComponent createComments() {
        _comments = Builder.createTextArea("fieldComments");
        _comments.setRows(3);

        return new JScrollPane(_comments);
    }

    private JPanel createObservations() {
        Builder b = new Builder(
                "right:default, $lcgap, default",
                "default, $lgap, default, $lgap, default, $lgap, default");

        ResourceMap resources = getContext().getResourceMap();

        _easeOfFallingAsleep = b.createComboBox("fieldEaseOfFallingAsleep");
        _easeOfFallingAsleep.setModel(new DefaultComboBoxModel(Ease.values()));
        _easeOfFallingAsleep.setRenderer(new EaseRenderer(resources));

        _qualityOfSleep = b.createComboBox("fieldQualityOfSleep");
        _qualityOfSleep.setModel(new DefaultComboBoxModel(Quality.values()));
        _qualityOfSleep.setRenderer(new QualityRenderer(resources));

        _easeOfWakingUp = b.createComboBox("fieldEaseOfWakingUp");
        _easeOfWakingUp.setModel(new DefaultComboBoxModel(Ease.values()));
        _easeOfWakingUp.setRenderer(new EaseRenderer(resources));

        _wakeUpByAlarm = b.createCheckBox("fieldWakeUpByAlarm");

        b.add(b.createLabel("labelEaseOfFallingAsleep"), 1, 1, _easeOfFallingAsleep, 3, 1);
        b.add(b.createLabel("labelQualityOfSleep"), 1, 3, _qualityOfSleep, 3, 3);
        b.add(b.createLabel("labelEaseOfWakingUp"), 1, 5, _easeOfWakingUp, 3, 5);
        b.add(b.createLabel("labelWakeUpByAlarm"), 1, 7, _wakeUpByAlarm, 3, 7);

        return b.getPanel();

    }

    private void addValueTo(JList list, Comparable value) {
        DefaultListModel model = (DefaultListModel) list.getModel();
        int index = model.getSize();
        model.insertElementAt(value, index);
        list.getSelectionModel().setSelectionInterval(index, index);
        scrollToSelectionVisible(list);
    }

    private void insertValueInto(JList list, Comparable value) {
        DefaultListModel model = (DefaultListModel) list.getModel();

        if (!model.contains(value)) {
            int i;
            for (i = 0; i < model.getSize(); i++) {
                Comparable each = (Comparable) model.getElementAt(i);
                if (value.compareTo(each) < 0) {
                    break;
                }
            }
            model.insertElementAt(value, i);
        }

        int index = model.indexOf(value);
        list.getSelectionModel().setSelectionInterval(index, index);
        scrollToSelectionVisible(list);
    }

    @Action(enabledProperty = "removeMomentsEnabled")
    public void removeMoments() {
        removeSelection(_moments);
    }

    private void removeSelection(JList list) {
        DefaultListModel model = (DefaultListModel) list.getModel();
        ListSelectionModel selection = list.getSelectionModel();

        int min = selection.getMinSelectionIndex();
        int max = selection.getMaxSelectionIndex();

        for (int i = max; i >= min; i--) {
            if (selection.isSelectedIndex(i)) {
                model.remove(i);
            }
        }

        int index = min < model.size() ? min : model.size() - 1;
        selection.setSelectionInterval(index, index);
        scrollToSelectionVisible(list);
    }

    public boolean isRemoveMomentsEnabled() {
        return _removeMomentsEnabled;
    }

    private void setRemoveMomentsEnabled(boolean enabled) {
        boolean oldValue = _removeMomentsEnabled;
        _removeMomentsEnabled = enabled;
        firePropertyChange("removeMomentsEnabled", oldValue, enabled);
    }

    @Action(enabledProperty = "removeConditionsEnabled")
    public void removeConditions() {
        removeSelection(_conditions);
    }

    public boolean isRemoveConditionsEnabled() {
        return _removeConditionsEnabled;
    }

    private void setRemoveConditionsEnabled(boolean enabled) {
        boolean oldValue = _removeConditionsEnabled;
        _removeConditionsEnabled = enabled;
        firePropertyChange("removeConditionsEnabled", oldValue, enabled);
    }


    private class MomentFieldListener implements PropertyChangeListener, Runnable {
        public void propertyChange(PropertyChangeEvent evt) {
            Time time = (Time) evt.getNewValue();
            if (time != null) {
                SwingUtilities.invokeLater(this);
                addValueTo(_moments, time);
            }
        }

        public void run() {
            _moment.setValue(null);
        }
    }

    private class MomentsSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            setRemoveMomentsEnabled(_moments.getSelectedIndex() >= 0);
        }
    }

    private class ConditionFieldListener implements ComboBoxListener {
        public void itemSelected(Object item) {
            doAddCondition((String) item);
        }

        public void insertionSelected() {
            EditorDialog editor = new EditorDialog(NightDialog.this, getResources().getString("titleAddCondition"));
            ((SingleFrameApplication) Application.getInstance()).show(editor);
            if (editor.isAccepted()) {
                String condition = editor.getText();

                doAddCondition(condition);

                if (!_allConditions.contains(condition)) {
                    _allConditions.add(condition);
                    Collections.sort(_allConditions);
                    _condition.setModel(new DefaultComboBoxModel(_allConditions.toArray()));
                }
            }
        }

        private void doAddCondition(String condition) {
            insertValueInto(_conditions, condition);
        }
    }

    private class ConditionsSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            setRemoveConditionsEnabled(_conditions.getSelectedIndex() >= 0);
        }
    }

    private class ChangesListener implements PropertyChangeListener, ActionListener, ListDataListener, DocumentListener {
        public void propertyChange(PropertyChangeEvent evt) {
            processChange();
        }

        public void actionPerformed(ActionEvent e) {
            processChange();
        }

        public void intervalAdded(ListDataEvent e) {
            processChange();
        }

        public void intervalRemoved(ListDataEvent e) {
            processChange();
        }

        public void contentsChanged(ListDataEvent e) {
            processChange();
        }

        public void insertUpdate(DocumentEvent e) {
            processChange();
        }

        public void removeUpdate(DocumentEvent e) {
            processChange();
        }

        public void changedUpdate(DocumentEvent e) {
        }

        private void processChange() {
            updateModification();
            updateAverage();
        }
    }
}
