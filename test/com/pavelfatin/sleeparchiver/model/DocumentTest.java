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

import static com.pavelfatin.sleeparchiver.lang.Utilities.newList;
import static com.pavelfatin.sleeparchiver.model.TestUtilities.contentOf;
import static com.pavelfatin.sleeparchiver.model.TestUtilities.createTempFile;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DocumentTest {
    private static final Night EMPTY_NIGHT = new Night(null, null, 0, null, new ArrayList<Time>());

    private List<Night> _nights = newList(
            new Night(new Date(2005, 2, 4), new Time(8, 45), 30, new Time(22, 15),
                    Ease.Hard, Quality.Good, Ease.Normal, true, "Foo comment",
                    newList(new Time(23, 30), new Time(3, 0), new Time(8, 30)),
                    newList("Condition A", "Condition B")),
            new Night(new Date(2006, 3, 5), new Time(9, 55), 20, new Time(23, 15),
                    Ease.Unknown, Quality.Unknown, Ease.Hard, false, "",
                    newList(new Time(23, 40), new Time(5, 0), new Time(9, 10)),
                    newList("Condition D", "Condition B", "Condition E")));


    @Test
    public void saveToStream() throws JAXBException {
        testSaveToStream();
    }

    @Test
    public void saveToStreamIgnoresLocale() throws JAXBException {
        Locale.setDefault(Locale.JAPAN);
        testSaveToStream();
    }

    private void testSaveToStream() throws JAXBException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        new Document(_nights).saveTo(buffer);
        assertThat(buffer.toString(), equalTo(contentOf("document/document.xml", "\n")));
    }

    @Test
    public void loadFromStream() throws JAXBException {
        Document document = doLoad("document/document.xml");
        assertThat(document.getNights(), equalTo(_nights));
    }

    @Test
    public void loadFromStreamOptional() throws JAXBException {
        Document document = doLoad("document/optional.xml");
        assertThat(document.getNights().get(0), equalTo(EMPTY_NIGHT));
    }


    @Test(expected = JAXBException.class)
    public void loadFromStreamWithMalformedDate() throws JAXBException {
        doLoad("document/malformedDate.xml");
    }

    @Test(expected = JAXBException.class)
    public void loadFromStreamWithMalformedTime() throws JAXBException {
        doLoad("document/malformedTime.xml");
    }

    @Test(expected = JAXBException.class)
    public void loadFromStreamWithMalformedEnum() throws JAXBException {
        doLoad("document/malformedEnum.xml");
    }

    @Test(expected = JAXBException.class)
    public void loadFromStreamWithMalformedInteger() throws JAXBException {
        doLoad("document/malformedInteger.xml");
    }

    @Test(expected = JAXBException.class)
    public void loadFromStreamWithMalformedBoolean() throws JAXBException {
        doLoad("document/malformedBoolean.xml");
    }

    @Test(expected = JAXBException.class)
    public void loadFromStreamWithMalformedElement() throws JAXBException {
        doLoad("document/malformedElement.xml");
    }

    @Test(expected = JAXBException.class)
    public void loadFromStreamWithMalformedAttribute() throws JAXBException {
        doLoad("document/malformedAttribute.xml");
    }

    @Test(expected = JAXBException.class)
    public void loadFromStreamWithMalformedNamespace() throws JAXBException {
        doLoad("document/malformedNamespace.xml");
    }

    private Document doLoad(String file) throws JAXBException {
        return Document.loadFrom(new ByteArrayInputStream(contentOf(file, "\n").getBytes()));
    }

    @Test
    public void saveAndLoadFile() throws IOException {
        Document document = new Document(_nights);
        assertThat(document.isNew(), equalTo(true));

        File file = createTempFile("document.tmp");

        document.saveAs(file, false);

        assertThat(document.isNew(), equalTo(false));
        assertThat(document.getLocation(), equalTo(file));
        assertThat(document.getNights(), equalTo(_nights));
        assertThat(document.getName(), equalTo("document"));

        Document loaded = Document.load(file);
        file.delete();

        assertThat(document.isNew(), equalTo(false));
        assertThat(loaded.getLocation(), equalTo(file));
        assertThat(loaded.getNights(), equalTo(_nights));
        assertThat(loaded.getName(), equalTo("document"));
    }

    @Test
    public void maxMomentCounts() {
        assertThat(Document.getMaxMomentsCount(_nights), equalTo(3));
    }

    @Test
    public void join() {
        assertThat(Document.join(newList("a", "foo", "bar")), equalTo("a;foo;bar"));
        assertThat(Document.join(newList("a", "", "bar")), equalTo("a;;bar"));
        assertThat(Document.join(newList("", "", "")), equalTo("\"\";;"));
        assertThat(Document.join(newList("")), equalTo("\"\""));
    }

    @Test
    public void joinNewLines() {
        assertThat(Document.join(newList("a", "line\rfeed", "bar")), equalTo("a;\"line\\rfeed\";bar"));
        assertThat(Document.join(newList("a", "new\nline", "bar")), equalTo("a;\"new\\nline\";bar"));
    }

    @Test
    public void joinQuoted() {
        assertThat(Document.join(newList("a", "semi;colon", "bar")), equalTo("a;\"semi;colon\";bar"));
        assertThat(Document.join(newList("a", "\"quoted\"", "bar")), equalTo("a;\"\"\"quoted\"\"\";bar"));
        assertThat(Document.join(newList("a", "partialy \"quoted\"", "semi;colon")),
                equalTo("a;\"partialy \"\"quoted\"\"\";\"semi;colon\""));
    }

    @Test
    public void split() {
        assertThat(Document.split("a;foo;bar"), equalTo(newList("a", "foo", "bar")));
        assertThat(Document.split("a;;bar"), equalTo(newList("a", "", "bar")));
        assertThat(Document.split(";;"), equalTo(newList("", "", "")));
        assertThat(Document.split("").size(), equalTo(0));
    }

    @Test
    public void splitNewLines() {
        assertThat(Document.split("a;\"line\\rfeed\";bar"), equalTo(newList("a", "line\rfeed", "bar")));
        assertThat(Document.split("a;\"new\\nline\";bar"), equalTo(newList("a", "new\nline", "bar")));
        assertThat(Document.split("\"\\r\\n\\n\""), equalTo(newList("\r\n\n")));
    }

    @Test
    public void splitQuoted() {
        assertThat(Document.split("a;\"foo\";bar"), equalTo(newList("a", "foo", "bar")));
        assertThat(Document.split("a;\"\";bar"), equalTo(newList("a", "", "bar")));
        assertThat(Document.split("a;\"semi;colon\";bar"), equalTo(newList("a", "semi;colon", "bar")));
        assertThat(Document.split("a;\"\"\"quoted\"\"\";bar"), equalTo(newList("a", "\"quoted\"", "bar")));
        assertThat(Document.split("a;\"partialy \"\"quoted\"\"\";\"semi;colon\""),
                equalTo(newList("a", "partialy \"quoted\"", "semi;colon")));
    }

    @Test
    public void exportDataToStream() throws IOException {
        String data = writeDataAsStream(_nights);
        assertThat(data, equalTo(contentOf("data/data.csv", System.getProperty("line.separator"))));
    }

    private String writeDataAsStream(List<Night> nights) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(buffer));
        Document.exportDataTo(writer, nights);
        writer.close();
        return buffer.toString();
    }

    @Test
    public void importDataFromStream() throws IOException {
        List<Night> nights = readDataAsStream("data/data.csv");
        assertThat(nights, equalTo(_nights));
    }

    @Test
    public void importBlankDataFromStream() throws IOException {
        List<Night> nights = readDataAsStream("data/blank.csv");
        assertThat(nights.get(0), equalTo(EMPTY_NIGHT));
    }

    @Test
    public void importEmptyLinesFromStream() throws IOException {
        List<Night> nights = readDataAsStream("data/empty.csv");
        assertThat(nights.size(), equalTo(0));
    }

    @Test
    public void importUnsortedDataFromStream() throws IOException {
        List<Night> nights = readDataAsStream("data/unsorted.csv");

        assertThat(nights.get(0).getDate(), equalTo(new Date(2001, 1, 1)));

        assertThat(nights.get(1).getDate(), equalTo(new Date(2002, 1, 1)));

        assertThat(nights.get(2).getDate(), equalTo(new Date(2002, 1, 1)));
        assertThat(nights.get(2).getToBed(), equalTo(new Time(1, 0)));
    }

    private List<Night> readDataAsStream(String file) throws IOException {
        String content = contentOf(file, "\r\n");
        ByteArrayInputStream buffer = new ByteArrayInputStream(content.getBytes());
        return Document.importDataFrom(new BufferedReader(new InputStreamReader(buffer)));
    }


    @Test
    public void importAndExportDataFile() throws IOException {
        File file = createTempFile("data.tmp");

        Document.exportData(file, _nights);

        List<Night> nights = Document.importData(file);
        file.delete();

        assertThat(nights, equalTo(_nights));
    }
}
