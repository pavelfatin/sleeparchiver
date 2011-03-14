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

import com.pavelfatin.sleeparchiver.gui.conditions.ConditionsDialog;
import com.pavelfatin.sleeparchiver.gui.download.DownloadDialog;
import com.pavelfatin.sleeparchiver.gui.info.InfoDialog;
import com.pavelfatin.sleeparchiver.gui.main.commands.*;
import com.pavelfatin.sleeparchiver.gui.main.render.*;
import com.pavelfatin.sleeparchiver.gui.main.render.Renderer;
import com.pavelfatin.sleeparchiver.gui.night.NightDialog;
import com.pavelfatin.sleeparchiver.gui.preferences.PreferencesDialog;
import com.pavelfatin.sleeparchiver.lang.Utilities;
import com.pavelfatin.sleeparchiver.model.Date;
import com.pavelfatin.sleeparchiver.model.*;
import com.pavelfatin.sleeparchiver.swing.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MainView extends FrameView {
    private static final Dimension DEFAULT_SIZE = new Dimension(900, 710);

    private Preferences _preferences;

    private Menu _menu;

    private MyTable _table;
    private Model _model;

    private Invoker _invoker = new Invoker();

    private boolean _undoEnabled;
    private boolean _redoEnabled;
    private boolean _editEnabled;
    private boolean _removeEnabled;

    private Document _document;
    private MySelectionModel _selection;
    private Renderer _renderer;
    private ToolBar _toolBar;

    private ToolBarListener _toolBarListener = new ToolBarListener();
    private MenuListener _menuListener = new MenuListener();
    private MyScrollPane _scroll;


    public MainView(Application application, Document document, Preferences preferences) {
        super(application);

        getFrame().setPreferredSize(DEFAULT_SIZE);

//        getFrame().setIconImages(Utilities.newList(
//                getResourceMap().getImageIcon("icon.large").getImage(),
//                getResourceMap().getImageIcon("icon.small").getImage()));

        _preferences = preferences;

        getResourceMap().injectFields(this);

        application.addExitListener(new ApplicationExitListener());

        List<Transform> transforms = createTransforms();
        List<Zoom> zooms = createZooms();

        _menu = createMenu(transforms, zooms);
        getResourceMap().injectComponents(_menu);
        updateRecentMenu();
        setMenuBar(_menu);

        _toolBar = createToolBar(transforms, zooms);
        setToolBar(_toolBar);
        _toolBarListener.updateMenu();

        setComponent(createUI());
        setStatusBar(new StatusBar());

        _scroll.addMouseZoomListener(new TableMouseZoomListen());

        Utilities.registerAction(getRootPane(), JRootPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                getActionMap().get("zoomIn"), "ACTION_ZOOM_IN");
        Utilities.registerAction(getRootPane(), JRootPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                getActionMap().get("zoomOut"), "ACTION_ZOOM_OUT");
        Utilities.registerAction(getRootPane(), JRootPane.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                getActionMap().get("zoomReset"), "ACTION_ZOOM_RESET");

        setDocument(document);
    }

    private String longDescriptionOf(String action) {
        return (String) getActionMap().get(action).getValue(AbstractAction.LONG_DESCRIPTION);
    }

    private Menu createMenu(List<Transform> transforms, List<Zoom> zooms) {
        Menu menu = new Menu(getActionMap());

        menu.setTransforms(transforms);
        menu.setZooms(zooms);

        menu.addPropertyChangeListener("zoom", _menuListener);
        menu.addPropertyChangeListener("transform", _menuListener);

        return menu;
    }

    private ToolBar createToolBar(List<Transform> transforms, List<Zoom> zooms) {
        ToolBar toolBar = new ToolBar(getResourceMap(), getActionMap());

        toolBar.setZooms(zooms);
        toolBar.resetZoom();

        toolBar.setTransforms(transforms);
        toolBar.setTransform(transforms.get(0));

        toolBar.addPropertyChangeListener("zoom", _toolBarListener);
        toolBar.addPropertyChangeListener("transform", _toolBarListener);

        return toolBar;
    }

    private List<Zoom> createZooms() {
        List<Zoom> result = new ArrayList<Zoom>();
        for (int zoom : new int[]{50, 70, 100, 140, 200}) {
            result.add(new Zoom(10.0D * zoom / (60.0D * 10.0D), String.format("%d%%", zoom)));
        }
        return result;
    }

    private List<Transform> createTransforms() {
        List<Transform> result = new ArrayList<Transform>();
        result.add(new RelativeTransform(getString("transformRelative")));
        result.add(new AbsoluteTransform(getString("transformAbsolute")));
        return result;
    }

    private void addRecent(File file) {
        _preferences.addRecentFile(file.getPath());
        try {
            _preferences.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        updateRecentMenu();
    }

    @Action
    public void clearRecent() {
        _preferences.clearRecentFiles();
        try {
            _preferences.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        updateRecentMenu();
    }

    @Action
    public void reopen(ActionEvent e) {
        if (isUserDataSafe()) {
            String path = ((JMenuItem) e.getSource()).getText();
            File file = new File(path);
            addRecent(file);
            doOpen(file);
        }
    }

    private void updateRecentMenu() {
        _menu.setRecents(_preferences.getRecentFiles());
    }

    private void setDocument(Document document) {
        _document = document;

        _model.setRows(_document.getNights());
        _selection.setSelectedIndex(_model.getLastIndex());
        _table.scrollToSelection();

        _invoker.reset();

        updateTitle();
        updateCommandActions();
        updateListActions();
        updateStatusBar();

        _table.requestFocusInWindow();
    }

    private boolean isModified() {
        return !_document.getNights().equals(_model.getRows());
    }

    private void updateTitle() {
        String format = getResourceMap().getString("titleFormat");
        String modification = isModified() ? " *" : "";
        getFrame().setTitle(String.format(format, getDocumentName()) + modification);
    }

    private String getDocumentName() {
        String newText = getResourceMap().getString("titleUntitled");
        return _document.isNew() ? newText : _document.getName();
    }

    private ApplicationActionMap getActionMap() {
        return getContext().getActionMap(this);
    }

    private JPanel createUI() {
        Builder b = new Builder(
                "default:grow",
                "fill:default:grow");

        _table = createTable();

        _scroll = new MyScrollPane(_table);
        _scroll.getViewport().addChangeListener(new ScrollpaneListener());

        b.add(_scroll, 1, 1);

        return b.getPanel();
    }

    private MyTable createTable() {
        _model = new Model();
        _model.addTableModelListener(new ModelListener());

        _selection = new MySelectionModel();
        _selection.addListSelectionListener(new SelectionListener());

        // null name to avoid column width persistence of SAF
        MyTable table = Builder.createTable(null);
        table.setModel(_model);
        table.setSelectionModel(_selection);
        table.setTableHeader(null);

        _renderer = new Renderer(getResourceMap());
        table.getColumnModel().getColumn(0).setCellRenderer(_renderer);
        table.setRowHeight(_renderer.getPrefferedHeight());

        InputMap map = table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
        if (map != null) {
            map.remove(KeyStroke.getKeyStroke("pressed ENTER"));
        }

        table.addMouseListener(new TableClickListener());

        PopupMenu menu = new PopupMenu(getActionMap());
        table.addMouseListener(new TablePopupHandler(table, menu));

        return table;
    }

    private void updateRenderer() {
        _renderer.setTransform(_toolBar.getTransform());
        _renderer.setResolution(_toolBar.getZoom().getResolution());
        updateRendererWidth();
        _table.repaint();
    }

    private void updateRendererWidth() {
        _renderer.setNights(_model.getRows());

        int width = _renderer.getPrefferedWidth(getVisibleNights());
        _table.getColumnModel().getColumn(0).setMinWidth(width);

        _table.revalidate();
    }

    private List<Night> getVisibleNights() {
        JViewport viewport = _scroll.getViewport();
        Rectangle rect = viewport.getViewRect();
        int first = _table.rowAtPoint(new Point(0, rect.y));
        int last = _table.rowAtPoint(new Point(0, rect.y + rect.height - 1));

        if (first < 0) {
            return Collections.emptyList();
        }

        if (last < 0) {
            return _model.getRows();
        }

        if (first > last) {
            return Collections.emptyList();
        }

        return _model.getRows().subList(first, last + 1);
    }

    private void invoke(Command command) {
        _invoker.invoke(command);
        updateCommandActions();
        _table.scrollToSelection();
    }

    private void updateCommandActions() {
        setUndoEnabled(_invoker.isUndoAvailable());
        setRedoEnabled(_invoker.isRedoAvailable());

        String undo = longDescriptionOf("undo")
                + (_invoker.isUndoAvailable() ? " " + _invoker.getUndoCommandName() : "");
        getActionMap().get("undo").putValue(AbstractAction.NAME, undo);
        getActionMap().get("undo").putValue(AbstractAction.SHORT_DESCRIPTION, undo);

        String redo = longDescriptionOf("redo")
                + (_invoker.isRedoAvailable() ? " " + _invoker.getRedoCommandName() : "");
        getActionMap().get("redo").putValue(AbstractAction.NAME, redo);
        getActionMap().get("redo").putValue(AbstractAction.SHORT_DESCRIPTION, redo);
    }

    public boolean isUndoEnabled() {
        return _undoEnabled;
    }

    private void setUndoEnabled(boolean enabled) {
        boolean previous = _undoEnabled;
        _undoEnabled = enabled;
        firePropertyChange("undoEnabled", previous, enabled);
    }

    public boolean isRedoEnabled() {
        return _redoEnabled;
    }

    private void setRedoEnabled(boolean enabled) {
        boolean previous = _redoEnabled;
        _redoEnabled = enabled;
        firePropertyChange("redoEnabled", previous, enabled);
    }

    @Action(enabledProperty = "undoEnabled")
    public void undo() {
        _invoker.undo();
        updateCommandActions();
        _table.scrollToSelection();
    }

    @Action(enabledProperty = "redoEnabled")
    public void redo() {
        _invoker.redo();
        updateCommandActions();
        _table.scrollToSelection();
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

    @Action
    public void download() {
        DownloadDialog dialog = new DownloadDialog(getFrame(),
                getString("Application.name"),
                Date.getCurrent().getYears());
        dialog.setIconImage(Utilities.imageOf(getActionMap().get("download")));
        ((SingleFrameApplication) Application.getInstance()).show(dialog);

        if (dialog.isDataAvailable()) {
            doAdd(dialog.getData());
        }
    }

    @Action
    public void add() {
        doAdd(createNewNight(Date.getCurrent()));
    }

    private void doAdd(Night prototype) {
        NightDialog dialog = new NightDialog(getFrame(), prototype, true, getAllConditions());
        dialog.setIconImage(Utilities.imageOf(getActionMap().get("add")));
        ((SingleFrameApplication) Application.getInstance()).show(dialog);

        if (dialog.isAccepted()) {
            Night data = dialog.getData();
            invoke(new Addition(longDescriptionOf("add"), _model, _selection, Night.getComparator(), data));
        }
    }

    private Night createNewNight(Date date) {
        Time alarm = null;
        int window = 20;
        Time toBed = null;
        if (_preferences.isPrefillEnabled() && _model.isFilled()) {
            Night last = _model.getLastRow();
            alarm = last.getAlarm();
            window = last.getWindow();
            toBed = last.getToBed();
        }
        return new Night(date, alarm, window, toBed, new ArrayList<Time>());
    }

    private List<String> getAllConditions() {
        Set<String> unique = new HashSet<String>();
        for (Night night : _model.getRows()) {
            unique.addAll(night.getConditions());
        }

        List<String> sorted = new ArrayList<String>(unique);
        Collections.sort(sorted);

        return sorted;
    }

    @Action(enabledProperty = "editEnabled")
    public void edit() {
        int index = _table.getSelectedRow();
        Night night = _model.getRowAt(index);

        NightDialog dialog = new NightDialog(getFrame(), night, false, getAllConditions());
        dialog.setIconImage(Utilities.imageOf(getActionMap().get("edit")));
        ((SingleFrameApplication) Application.getInstance()).show(dialog);

        if (dialog.isAccepted()) {
            invoke(new Editing(longDescriptionOf("edit"), _model, _selection, Night.getComparator(), dialog.getData()));
        }
    }

    @Action(enabledProperty = "removeEnabled")
    public void remove() {
        invoke(new Removal(longDescriptionOf("remove"), _model, _selection));
    }

    private void updateListActions() {
        setEditEnabled(_selection.isSelectionSingle());
        setRemoveEnabled(_selection.isSelectionExists());
    }

    private void updateStatusBar() {
        int count = _model.getRowCount();
        String status;
        if (count > 0) {
            if (_selection.isSelectionSingle()) {
                status = String.format(getResourceMap().getString("formatStatusPosition"),
                        _selection.getMaxSelectionIndex() + 1, count);
            } else {
                int selected = _selection.getSelectionSize();
                status = String.format(getResourceMap().getString("formatStatusSelection"), selected, count);
            }
        } else {
            status = getResourceMap().getString("formatStatusEmpty");
        }

        ((StatusBar) getStatusBar()).setStatus(status);
    }

    @Action
    public void blank() {
        if (isUserDataSafe()) {
            setDocument(new Document());
            updateTitle();
        }
    }

    private boolean isUserDataSafe() {
        if (_table.getRowCount() > 0 && isModified()) {
            String format = getResourceMap().getString("messageUnsavedDocument");
            int result = JOptionPane.showConfirmDialog(getFrame(),
                    String.format(format, getDocumentName()),
                    getResourceMap().getString("titleUnsavedDocument"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            switch (result) {
                case JOptionPane.YES_OPTION:
                    return save();
                case JOptionPane.NO_OPTION:
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    private boolean isSafeToWrite(File file) {
        if (file.exists()) {
            String format = getResourceMap().getString("messageFileExists");
            int result = JOptionPane.showConfirmDialog(getFrame(),
                    String.format(format, file.getPath()),
                    getResourceMap().getString("titleFileExists"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            return result == JOptionPane.YES_OPTION;
        } else {
            return true;
        }
    }

    @Action
    public void open() throws IOException {
        if (isUserDataSafe()) {
            JFileChooser chooser = createDocumentFileChooser();
            chooser.setDialogTitle(getResourceMap().getString("titleOpenDialog"));
            if (chooser.showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                addRecent(file);
                doOpen(file);
            }
        }
    }

    public void doOpen(File file) {
        try {
            setDocument(Document.load(file));
        } catch (FileNotFoundException e) {
            String format = getResourceMap().getString("messageOpenNotFound");
            JOptionPane.showMessageDialog(getFrame(), String.format(format, file.getPath()),
                    getResourceMap().getString("titleOpenError"), JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            String format = getResourceMap().getString("messageOpenError");
            JOptionPane.showMessageDialog(getFrame(), String.format(format, file.getPath()),
                    getResourceMap().getString("titleOpenError"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action
    public boolean save() {
        if (_document.isNew()) {
            return saveAs();
        } else {
            return doSave(_document.getLocation());
        }
    }

    @Action
    public boolean saveAs() {
        JFileChooser chooser = createDocumentFileChooser();
        chooser.setDialogTitle(getResourceMap().getString("titleSaveDialog"));
        if (chooser.showSaveDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (isSafeToWrite(file)) {
                addRecent(file);
                return doSave(file);
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean doSave(File file) {
        try {
            Document document = new Document(_model.getRows());
            document.saveAs(file, _preferences.isBackupsEnabled());
            _document = document;
            updateTitle();
            return true;
        } catch (FileNotFoundException e) {
            String format = getResourceMap().getString("messageSaveNotFound");
            JOptionPane.showMessageDialog(getFrame(), String.format(format, file.getPath()),
                    getResourceMap().getString("titleSaveError"), JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException e) {
            String format = getResourceMap().getString("messageSaveError");
            JOptionPane.showMessageDialog(getFrame(), String.format(format, file.getPath()),
                    getResourceMap().getString("titleSaveError"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private MyFileChooser createDocumentFileChooser() {
        MyFileChooser chooser = new MyFileChooser(getResourceMap().getString("fileDescription"),
                getResourceMap().getString("fileExtension"));
        if (_preferences.hasRecentFiles()) {
            File dir = _preferences.getRecentDirectory();
            if (dir.exists()) {
                chooser.setCurrentDirectory(dir);
            }
        }
        return chooser;
    }

    private MyFileChooser createDataFileChooser() {
        return new MyFileChooser(getResourceMap().getString("dataDescription"),
                getResourceMap().getString("dataExtension"));
    }

    @Action
    public void exit() {
        getApplication().exit();
    }

    @Action
    public void selectAll() {
        int count = _table.getRowCount();
        if (count > 0) {
            _selection.setSelectionInterval(0, count - 1);
        }
    }

    @Action
    public void conditions() {
        ConditionsDialog dialog = new ConditionsDialog(getFrame(), _model.getRows());
        dialog.setIconImage(Utilities.imageOf(getActionMap().get("conditions")));
        ((SingleFrameApplication) Application.getInstance()).show(dialog);
        if (dialog.isAccepted()) {
            invoke(new Replacing(longDescriptionOf("conditions"), _model, _selection, dialog.getData()));
        }
    }

    @Action
    public void preferences() {
        PreferencesDialog dialog = new PreferencesDialog(getFrame(), _preferences);
        dialog.setIconImage(Utilities.imageOf(getActionMap().get("preferences")));
        ((SingleFrameApplication) Application.getInstance()).show(dialog);
    }

    @Action
    public void about() {
        InfoDialog dialog = new InfoDialog(getFrame(), getString("titleAbout"),
                "/com/pavelfatin/sleeparchiver/resources/about.html", false);
        dialog.setIconImage(Utilities.imageOf(getActionMap().get("about")));
        ((SingleFrameApplication) Application.getInstance()).show(dialog);
    }

    @Action
    public void license() {
        InfoDialog dialog = new InfoDialog(getFrame(), getString("titleLicense"),
                "/com/pavelfatin/sleeparchiver/resources/license.html", true);
        dialog.setIconImage(Utilities.imageOf(getActionMap().get("license")));
        dialog.setPreferredSize(new Dimension(600, 500));
        ((SingleFrameApplication) Application.getInstance()).show(dialog);
    }

    @Action
    public void importData() {
        JFileChooser chooser = createDataFileChooser();
        chooser.setDialogTitle(getResourceMap().getString("titleImportDialog"));
        if (chooser.showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
            doImportData(chooser.getSelectedFile());
        }
    }

    private void doImportData(File file) {
        try {
            List<Night> nights = Document.importData(file);
            invoke(new Importing(longDescriptionOf("importData"), _model, _selection, Night.getComparator(), nights));
        } catch (FileNotFoundException e) {
            String format = getResourceMap().getString("messageImportNotFound");
            JOptionPane.showMessageDialog(getFrame(), String.format(format, file.getPath()),
                    getResourceMap().getString("titleImportError"), JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            String format = getResourceMap().getString("messageImportError");
            JOptionPane.showMessageDialog(getFrame(), String.format(format, file.getPath()),
                    getResourceMap().getString("titleImportError"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action
    public void exportData() {
        JFileChooser chooser = createDataFileChooser();
        chooser.setDialogTitle(getResourceMap().getString("titleExportDialog"));
        if (chooser.showSaveDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (isSafeToWrite(file)) {
                doExportData(file);
            }
        }
    }

    private void doExportData(File file) {
        try {
            Document.exportData(file, _model.getRows());
        } catch (FileNotFoundException e) {
            String format = getResourceMap().getString("messageExportNotFound");
            JOptionPane.showMessageDialog(getFrame(), String.format(format, file.getPath()),
                    getResourceMap().getString("titleExportError"), JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            String format = getResourceMap().getString("messageExportError");
            JOptionPane.showMessageDialog(getFrame(), String.format(format, file.getPath()),
                    getResourceMap().getString("titleExportError"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action
    public void zoomIn() {
        _toolBar.increaseZoom();
    }

    @Action
    public void zoomOut() {
        _toolBar.decreaseZoom();
    }

    @Action
    public void zoomReset() {
        _toolBar.resetZoom();
    }

    private String getString(String key) {
        return getResourceMap().getString(key);
    }


    private class SelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            updateListActions();
            updateStatusBar();
        }
    }

    private class ModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            updateRenderer();
            updateTitle();
            updateStatusBar();
        }
    }

    private class TableClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                if (isEditEnabled()) {
                    getActionMap().get("edit").actionPerformed(new ActionEvent(MainView.this, 0, null));
                }
            }
        }
    }

    private class ApplicationExitListener implements Application.ExitListener {
        public boolean canExit(EventObject event) {
            return isUserDataSafe();
        }

        public void willExit(EventObject event) {
        }
    }

    private class ToolBarListener implements PropertyChangeListener {
        private boolean _enabled = true;

        public void setEnabled(boolean enabled) {
            _enabled = enabled;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (_enabled) {
                updateMenu();
                updateRenderer();
            }
        }

        public void updateMenu() {
            _menuListener.setEnabled(false);
            _menu.setZoom(_toolBar.getZoom());
            _menu.setTransform(_toolBar.getTransform());
            _menuListener.setEnabled(true);
        }
    }

    private class MenuListener implements PropertyChangeListener {
        private boolean _enabled = true;

        public void setEnabled(boolean enabled) {
            _enabled = enabled;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (_enabled) {
                updateToolBar();
                updateRenderer();
            }
        }

        public void updateToolBar() {
            _toolBarListener.setEnabled(false);
            _toolBar.setZoom(_menu.getZoom());
            _toolBar.setTransform(_menu.getTransform());
            _toolBarListener.setEnabled(true);
        }
    }

    private class ScrollpaneListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            updateRendererWidth();
        }
    }

    private class TableMouseZoomListen implements MouseZoomListen {
        public void zoomIn() {
            MainView.this.zoomIn();
        }

        public void zoomOut() {
            MainView.this.zoomOut();
        }
    }
}
