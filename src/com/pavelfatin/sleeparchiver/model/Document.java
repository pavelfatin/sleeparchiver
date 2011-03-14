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

package com.pavelfatin.sleeparchiver.model;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.pavelfatin.sleeparchiver.lang.Utilities;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@XmlRootElement(name = "document")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Document {
    private static Schema _schema;

    @XmlAttribute(name = "version")
    private float _version = 1.0F;

    @XmlElement(name = "night")
    private List<Night> _nights = new ArrayList<Night>();

    @XmlTransient
    private File _location;


    public Document() {
    }

    public Document(List<Night> nights) {
        _nights = new ArrayList<Night>(nights);
    }

    public List<Night> getNights() {
        return Collections.unmodifiableList(_nights);
    }

    private void setLocation(File location) {
        _location = location;
    }

    public static Document load(File file) throws IOException {
        InputStream in = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
        try {
            Document result = loadFrom(in);
            result.setLocation(file);
            return result;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } finally {
            Utilities.close(in);
        }
    }

    static Document loadFrom(InputStream stream) throws JAXBException {
        Unmarshaller unmarshaller = createContext().createUnmarshaller();
        unmarshaller.setEventHandler(new ValidationHandler());
        unmarshaller.setSchema(getSchema());
        return (Document) unmarshaller.unmarshal(stream);
    }

    private static Schema getSchema() {
        if (_schema == null) {
            _schema = loadSchema("document.xsd");
        }
        return _schema;
    }

    private static Schema loadSchema(String file) {
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL url = Document.class.getResource(file);
            if (url == null) {
                throw new RuntimeException("Can't find document XML schema: " + file);
            }
            return sf.newSchema(url);
        } catch (SAXException e) {
            throw new RuntimeException("Can't load document XML schema", e);
        }
    }

    public void save(boolean backup) throws IOException {
        saveAs(_location, backup);
    }

    public void saveAs(File file, boolean backup) throws IOException {
        byte[] bytes = saveToBytes();

        if (backup && file.exists()) {
            createBackup(file);
        }

        OutputStream out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        try {
            out.write(bytes);
            out.flush();
            setLocation(file);
        } finally {
            Utilities.close(out);
        }
    }

    private byte[] saveToBytes() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            saveTo(buffer);
            return buffer.toByteArray();
        } catch (PropertyException e) {
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } finally {
            Utilities.close(buffer);
        }
    }

    void saveTo(OutputStream stream) throws JAXBException {
        Marshaller marshaller = createContext().createMarshaller();
        marshaller.setEventHandler(new ValidationHandler());
        marshaller.setSchema(getSchema());
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
                "http://pavelfatin.com/sleeparchiver http://pavelfatin.com/files/sleeparchiver/document.xsd");
        marshaller.marshal(this, stream);
    }

    private void createBackup(File file) throws IOException {
        File backup = new File(file.getPath() + ".bak");
        if (backup.exists()) {
            if (!backup.delete()) {
                throw new IOException(
                        "Unable to rename file " + backup.getPath());
            }
        }
        if (!file.renameTo(backup)) {
            throw new IOException(
                    "Unable to rename file " + file.getPath() + " to " + backup.getPath());
        }
    }

    private static JAXBContext createContext() throws JAXBException {
        return JAXBContext.newInstance(Document.class, Night.class, Date.class, Time.class);
    }

    public static List<Night> importData(File file) throws IOException {
        List<Night> nights;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            nights = importDataFrom(reader);
        } finally {
            Utilities.close(reader);
        }

        return nights;
    }

    static List<Night> importDataFrom(BufferedReader reader) throws IOException {
        List<Night> nights = new ArrayList<Night>();

        while (reader.ready()) {
            String line = reader.readLine();

            if (line == null) {
                break;
            }

            if (line.trim().length() == 0) {
                continue;
            }

            nights.add(parse(line));
        }

        Collections.sort(nights, Night.getComparator());

        return nights;
    }

    private static Night parse(String line) {
        List<String> columns = split(line);

        List<Time> moments = new ArrayList<Time>();
        List<String> conditions = new ArrayList<String>();

        boolean momentsColumns = true;
        for (int i = 9; i < columns.size(); i++) {
            String column = columns.get(i);
            if (column.isEmpty()) {
                momentsColumns = false;
                continue;
            }
            if (momentsColumns) {
                moments.add(Time.parse(column));
            } else {
                conditions.add(column);
            }
        }

        return new Night(Date.parse(columns.get(0)),
                Time.parse(columns.get(1)),
                Integer.parseInt(columns.get(2)),
                Time.parse(columns.get(3)),
                Ease.parse(columns.get(4)),
                Quality.parse(columns.get(5)),
                Ease.parse(columns.get(6)),
                "true".equalsIgnoreCase(columns.get(7)),
                columns.get(8),
                moments, conditions);
    }

    public static void exportData(File file, List<Night> nights) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        try {
            exportDataTo(writer, nights);
            writer.flush();
        } finally {
            Utilities.close(writer);
        }
    }

    static void exportDataTo(BufferedWriter writer, List<Night> nights) throws IOException {
        int maxMomentsCount = getMaxMomentsCount(nights);
        for (Night night : nights) {
            List<String> columns = new ArrayList<String>();
            columns.add(Date.format(night.getDate()));
            columns.add(Time.format(night.getAlarm()));
            columns.add(Integer.toString(night.getWindow()));
            columns.add(Time.format(night.getToBed()));

            columns.add(night.getEaseOfFallingAsleep().format());
            columns.add(night.getQualityOfSleep().format());
            columns.add(night.getEaseOfWakingUp().format());
            columns.add(night.isAlarmWorked() ? "true" : "false");
            columns.add(night.getComments());

            for (Time moment : night.getMoments()) {
                columns.add(Time.format(moment));
            }
            if (night.hasConditions()) {
                int levelers = maxMomentsCount - night.getMomentsCount();
                columns.addAll(Collections.nCopies(levelers + 1, ""));
                columns.addAll(night.getConditions());
            }
            String line = join(columns);
            writer.write(line);
            writer.newLine();
        }
    }

    public static int getMaxMomentsCount(List<Night> nights) {
        int result = 0;
        for (Night night : nights) {
            if (night.getMomentsCount() > result) {
                result = night.getMomentsCount();
            }
        }
        return result;
    }

    public boolean isNew() {
        return _location == null;
    }

    public File getLocation() {
        return _location;
    }

    public String getName() {
        return _location == null ? "" : _location.getName().replaceAll("\\.\\S{3}$", "");
    }

    static String join(List<String> columns) {
        try {
            StringWriter buffer = new StringWriter();
            CsvWriter writer = new CsvWriter(buffer, ';');
            writer.writeRecord(columns.toArray(new String[]{}));
            writer.close();
            return buffer.toString().trim().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<String> split(String line) {
        try {
            CsvReader reader = new CsvReader(new StringReader(line), ';');
            reader.readRecord();
            List<String> columns = new ArrayList<String>();
            for (String token : reader.getValues()) {
                columns.add(token.replaceAll("\\\\r", "\r").replaceAll("\\\\n", "\n"));
            }
            reader.close();
            return columns;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
