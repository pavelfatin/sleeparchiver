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
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

public class MyComboBox extends JComboBox {
    private String _insertion = "insertion";
    private List<ComboBoxListener> _listeners = new LinkedList<ComboBoxListener>();


    public MyComboBox() {
        putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);

        setModel(new DefaultComboBoxModel());
        setRenderer(new DefaultListCellRenderer());

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((e.getModifiers() & ActionEvent.MOUSE_EVENT_MASK) != 0) {
                    processSelection();
                }
            }
        });

        getActionMap().put("enterPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                boolean withPopup = isPopupVisible();
                getActionMap().getParent().get("enterPressed").actionPerformed(e);
                if (withPopup) {
                    processSelection();
                }
            }
        });

        getActionMap().put("hidePopup", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setSelectedIndex(-1);
                getActionMap().getParent().get("hidePopup").actionPerformed(e);
            }
        });
    }

    public void addComboBoxListener(ComboBoxListener listener) {
        _listeners.add(0, listener);
    }

    private void processSelection() {
        if (isInsertion(getSelectedItem())) {
            setSelectedIndex(-1);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireInsertionSelected();
                }
            });
        } else {
            if (getSelectedIndex() >= 0) {
                fireItemSelected(getSelectedItem());
                setSelectedIndex(-1);
            }
        }
    }

    protected void fireItemSelected(Object item) {
        for (ComboBoxListener listener : _listeners) {
            listener.itemSelected(item);
        }
    }

    protected void fireInsertionSelected() {
        for (ComboBoxListener listener : _listeners) {
            listener.insertionSelected();
        }
    }

    public String getInsertion() {
        return _insertion;
    }

    public void setInsertion(String insertion) {
        _insertion = insertion;
    }

    public boolean isInsertion(Object anItem) {
        return _insertion == anItem;
    }

    @Override
    public void setModel(ComboBoxModel aModel) {
        super.setModel(new ModelDecorator(aModel));
    }

    @Override
    public void setRenderer(ListCellRenderer aRenderer) {
        super.setRenderer(new RendererDecorator(aRenderer));
    }


    private class ModelDecorator extends AbstractListModel implements ComboBoxModel {
        private ComboBoxModel _delegate;
        private Object _selection;


        public ModelDecorator(ComboBoxModel delegate) {
            _delegate = delegate;
        }

        public int getSize() {
            return _delegate.getSize() + 1;
        }

        public Object getElementAt(int index) {
            return index < _delegate.getSize() ? _delegate.getElementAt(index) : _insertion;
        }

        public void setSelectedItem(Object anItem) {
            _selection = anItem;

            if (!isInsertion(anItem)) {
                _delegate.setSelectedItem(anItem);
            }
        }

        public Object getSelectedItem() {
            return _selection;
        }
    }


    private class RendererDecorator implements ListCellRenderer {
        private ListCellRenderer _delegate;


        public RendererDecorator(ListCellRenderer delegate) {
            _delegate = delegate;
        }

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JComponent component = (JComponent) _delegate.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            if (isInsertion(value)) {
                component.setBorder(new CompoundBorder(new TopBorder(), component.getBorder()));
                if (!isSelected) {
                    component.setForeground(Color.DARK_GRAY);
                }
            }

            return component;
        }
    }


    private static class TopBorder implements Border {
        private static final Insets INSETS = new Insets(1, 0, 0, 0);

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.GRAY);
            g.drawLine(x, y, width, y);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }

        public boolean isBorderOpaque() {
            return true;
        }
    }
}
