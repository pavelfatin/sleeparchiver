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

package com.pavelfatin.sleeparchiver.gui.main.render;

import com.pavelfatin.sleeparchiver.lang.Utilities;
import com.pavelfatin.sleeparchiver.model.*;
import org.jdesktop.application.ResourceMap;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Renderer extends AbstractRenderer<Night> {
    private static final BasicStroke STROKE_WINDOW = new BasicStroke(
            1.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0F, new float[]{2.0F}, 0);

    private static final int H_GAP = 11;

    private static final Font FONT_BOLD = new Font("Arial", Font.BOLD, 12);
    private static final Font FONT_PLAIN = new Font("Arial", Font.PLAIN, 12);

    private static final Color COLOR_BLUE = new Color(0, 0, 148);
    private static final Color COLOR_GREEN = new Color(0, 110, 0);
    private static final Color COLOR_FRAME = new Color(204, 204, 204);
    private static final Color COLOR_SELECTED_BORDER = new Color(153, 153, 153);
    private static final Color COLOR_SELECTED_BACKGROUND = new Color(255, 255, 233);
    private static final Color COLOR_HOLIDAY = new Color(180, 0, 0);

    private static final int MIN_MOMENTS_SPACE = 3;

    private ResourceMap _map;
    private Transform _transform;


    public Renderer(ResourceMap map) {
        _map = map;
    }

    public void setTransform(Transform transform) {
        _transform = transform;
    }

    public void setNights(List<Night> nights) {
        _transform.setNights(completeOf(nights));
    }

    private static List<Night> completeOf(List<Night> nights) {
        ArrayList<Night> complete = new ArrayList<Night>();
        for (Night night : nights) {
            if (night.isComplete()) {
                complete.add(night);
            }
        }
        return complete;
    }

    public void setResolution(double resolution) {
        _transform.setResolution(resolution);
    }

    public int getPrefferedHeight() {
        return 2 + 96 + 2;
    }

    public int getPrefferedWidth(List<Night> nights) {
        return H_GAP + _transform.getPrefferedWidth(nights) + H_GAP;
    }

    private int toX(Instant instant) {
        return H_GAP + _transform.toX(instant);
    }

    protected void render(Graphics2D g, Night night, boolean selected, boolean focused) {
        drawFrame(g, selected, focused);

        Date date = night.getDate();
        if (date != null) {
            drawDate(g, 13, 22, date);
        }

        drawConditionsAndComments(g, 313, 22, night);
        drawObservations(g, 125, 13, night);

        if (night.isAlarmWorked()) {
            drawAlarm(g, 190, 16);
        }

        if (night.isComplete()) {
            Metrics metrics = night.getMetrics();

            drawEquation(g, 208, 302, 22, metrics);

            _transform.setNight(night);

            List<Span> spans = night.getMetrics().getSpans();

            drawBars(g, spans);
            drawGaps(g, spans, selected);
            if (night.hasWindow()) {
                drawWindow(g, night);
            }
            drawMoments(g, spans);
            drawLengths(g, spans);
        }
    }

    private void drawDate(Graphics2D g, int x, int y, Date date) {
        g.setFont(FONT_BOLD);
        g.setColor(date.isHoliday() ? COLOR_HOLIDAY : Color.BLACK);
        g.drawString(date.format() + " " + date.getDayOfWeek(), x, y);
    }

    private void drawConditionsAndComments(Graphics2D g, int x, int y, Night night) {
        g.setFont(FONT_BOLD);
        g.setColor(COLOR_GREEN);
        String conditions = Utilities.join(" | ", night.getConditions());
        g.drawString(conditions, x, y);

        int xx = conditions.isEmpty() ? 0 : widthOf(g, conditions) + 6;

        g.setFont(FONT_PLAIN);
        g.setColor(COLOR_BLUE);
        String comments = night.getComments().replaceAll("\\n", "; ");
        g.drawString(comments.isEmpty() ? "" : String.format("(%s)", comments), x + xx, y);
    }

    private void drawEquation(Graphics2D g, int x1, int x2, int y, Metrics metrics) {
        g.setFont(FONT_BOLD);
        g.setColor(COLOR_BLUE);
        String duration = new Instant(metrics.getDuration()).getTime().formatShort();
        String equation = String.format("%s / %d = %d",
                duration, metrics.getSpansCount(), metrics.getAverage());
        int gaps = x2 - x1 - widthOf(g, equation);
        int x = x1 + Math.round((float) gaps / 2.0F);
        g.drawString(equation, x, y);
    }

    private void drawAlarm(Graphics2D g, int x, int y) {
        g.setColor(Color.BLACK);
        g.drawLine(x, y, x + 2, y + 5);
        g.drawLine(x + 3, y + 3, x + 5, y - 3);
    }

    private void drawFrame(Graphics2D g, boolean selected, boolean focused) {
        if (selected) {
            g.setColor(backgroundOf(selected));
            g.fillRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 15, 15);
        }

        g.setColor(COLOR_FRAME);

        g.drawLine(3, 29, getWidth() - 4, 29);

        g.drawLine(114, 6, 114, 6 + 22);
        g.drawLine(175, 6, 175, 6 + 22);
        g.drawLine(208, 6, 208, 6 + 22);
        g.drawLine(302, 6, 302, 6 + 22);

        g.setColor(focused ? Color.BLACK : COLOR_SELECTED_BORDER);
        g.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 15, 15);
    }

    private Color backgroundOf(boolean selected) {
        return selected ? COLOR_SELECTED_BACKGROUND : Color.WHITE;
    }

    private void drawObservations(Graphics2D g, int x, int y, Night night) {
        Ease asleep = night.getEaseOfFallingAsleep();
        if (asleep.isKnown()) {
            g.setColor(asleep.color(_map));
            g.fillPolygon(new int[]{x, x + 9, x + 9}, new int[]{y, y, y + 9}, 3);
        }

        Quality quality = night.getQualityOfSleep();
        if (quality.isKnown()) {
            g.setColor(quality.color(_map));
            g.fillRect(x + 12, y, 16, 9);
        }

        Ease waking = night.getEaseOfWakingUp();
        if (waking.isKnown()) {
            g.setColor(waking.color(_map));
            g.fillPolygon(new int[]{x + 31, x + 31 + 9, x + 31}, new int[]{y, y, y + 9}, 3);
        }
    }

    private void drawMoments(Graphics2D g, List<Span> spans) {
        g.setFont(FONT_PLAIN);
        g.setColor(Color.BLACK);
        for (Span span : spans) {
            String moment = span.getBegin().getTime().formatShort();
            Rectangle r = rectangleOf(span);
            if (r.width > (widthOf(g, moment) + MIN_MOMENTS_SPACE)) {
                g.drawString(moment, r.x, r.y - 4);
            }
        }
    }

    private void drawLengths(Graphics2D g, List<Span> spans) {
        g.setFont(FONT_PLAIN);
        g.setColor(Color.BLACK);
        for (Span span : spans) {
            String length = String.format("%d", span.toMinutes());
            Rectangle2D bounds = boundsOf(g, length);
            Rectangle r = rectangleOf(span);
            if (r.width > bounds.getWidth()) {
                g.drawString(length,
                        r.x + Math.round((r.width - bounds.getWidth()) / 2),
                        r.y + r.height + (int) bounds.getHeight() + 4);
            }
        }
    }

    private void drawBars(Graphics2D g, List<Span> spans) {
        for (Span span : spans) {
            Rectangle r = rectangleOf(span);
            g.setColor(colorOf(span));
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    private void drawGaps(Graphics2D g, List<Span> spans, boolean selected) {
        g.setColor(backgroundOf(selected));
        for (Span span : spans) {
            Rectangle r = rectangleOf(span);
            g.fillRect(r.x - 1, r.y, 3, r.height);
        }
    }

    private void drawWindow(Graphics2D g, Night night) {
        g.setColor(Color.RED);
        g.setStroke(STROKE_WINDOW);
        Rectangle alarm = alarmRectangleOf(night);
        g.drawRect(alarm.x, alarm.y, alarm.width - 1, alarm.height - 1);
    }

    private Rectangle alarmRectangleOf(Night night) {
        Rectangle result = rectangleOf(night.getMetrics().getTotalSpan());
        int width = _transform.toWidth(night.getWindow());
        result.x = result.x + result.width - width;
        result.width = width;
        return result;
    }

    private Rectangle rectangleOf(Span span) {
        int x1 = toX(span.getBegin());
        int x2 = toX(span.getEnd());
        int y1 = 53;
        int y2 = y1 + 22;
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    private static Color colorOf(Span span) {
        int c = Math.min(span.toMinutes() / 3, 127);
        return new Color(127 - c, 212 - c, 255 - c);
    }

    private static Rectangle2D boundsOf(Graphics2D g, String string) {
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(string, g.getFont(), frc);
        return layout.getBounds();
    }

    private static int widthOf(Graphics2D g, String string) {
        return (int) Math.round(boundsOf(g, string).getWidth());
    }

    private static int heightOf(Graphics2D g, String string) {
        return (int) Math.round(boundsOf(g, string).getHeight());
    }

    private static Color mix(Color c1, Color c2, float k) {
        return new Color(mix(c1.getRed(), c2.getRed(), k),
                mix(c1.getGreen(), c2.getGreen(), k),
                mix(c1.getBlue(), c2.getBlue(), k));
    }

    private static int mix(float i1, float i2, float k) {
        return Math.round(i1 + (i2 - i1) * k);
    }
}