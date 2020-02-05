/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.common.util.strings;

import java.io.IOException;
import java.io.StringReader;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HtmlUtils {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(HtmlUtils.class);

    public static String htmlToPlainText(String html) throws IOException {
        final StringBuilder sb = new StringBuilder();

        @SuppressWarnings("unused")
        HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
            @Override
            public void handleText(final char[] data, final int pos) {
                String s = new String(data);
                sb.append(s.strip());
            }

            @Override
            public void handleSimpleTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
                handleStartTag(t, a, pos);
            }
        };

        new ParserDelegator().parse(new StringReader(html), parserCallback, false);
        return sb.toString();
    }

    public static String htmlToPlainTextWithNewLines(String html) throws IOException {
        final StringBuilder sb = new StringBuilder();

        @SuppressWarnings("unused")
        HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
            public boolean readyForNewline;

            @Override
            public void handleText(final char[] data, final int pos) {
                String s = new String(data);
                sb.append(s.strip());
                readyForNewline = true;
            }

            @Override
            public void handleStartTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
                if (readyForNewline && (t == HTML.Tag.DIV || t == HTML.Tag.BR || t == HTML.Tag.P)) {
                    sb.append("\n");
                    readyForNewline = false;
                }
            }

            @Override
            public void handleSimpleTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
                handleStartTag(t, a, pos);
            }
        };

        new ParserDelegator().parse(new StringReader(html), parserCallback, false);
        return sb.toString();
    }
}
