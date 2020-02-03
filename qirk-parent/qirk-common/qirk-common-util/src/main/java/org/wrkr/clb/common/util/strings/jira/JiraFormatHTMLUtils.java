/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.common.util.strings.jira;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTML;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.common.util.collections.MapBuilder;
import org.wrkr.clb.common.util.strings.ExtStringUtils;

public class JiraFormatHTMLUtils {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(JiraFormatHTMLUtils.class);

    private static class JiraTag {
        private static final char BOLD = '*';
        private static final char ITALIC = '_';
        private static final char UNDERLINE = '+';
        private static final char STRIKE = '-';
        private static final char SUPERSCRIPT = '^';
        private static final char SUBSCRIPT = '~';

        private static final char LINK_OPEN = '[';
        private static final char LINK_CLOSE = ']';
        private static final char LINK_DELIMETER = '|';
        private static final String LINK_MAILTO = "mailto:";

        private static final char TAG_OPEN = '{';
        private static final char TAG_CLOSE = '}';

        private static final String TABLE_HEADER = "||";
        private static final String TABLE_ROW = "|";

        // tags at the start and the end of the line
        private static final String TABLE_HEADER_REGEXP = TABLE_HEADER.replace("|", "\\|");
        private static final String TABLE_ROW_REGEXP = TABLE_ROW.replace("|", "\\|");

        // tags at the start of the line
        private static final String QUOTE = "bq. ";
        private static final String HEADER1 = "h1. ";
        private static final String HEADER2 = "h2. ";
        private static final String HEADER3 = "h3. ";
        private static final String HEADER4 = "h4. ";
        private static final String HEADER5 = "h5. ";
        private static final String HEADER6 = "h6. ";
        private static final String UNORDERED_LIST = "* ";
        private static final String ORDERED_LIST = "# ";

        // tags inside braces
        private static final String COLOR_CLOSE = "color";
        private static final String COLOR_OPEN = COLOR_CLOSE + ":";
        private static final String QUOTE_MULTILINE = "quote";
        private static final String NO_FORMAT = "noformat";
        private static final String CODE = "code";
    }

    private static final Set<Character> JIRA_LINK_TAGS = Collections.unmodifiableSet(new HashSet<Character>(Arrays.asList(
            '#', '^', '~')));

    private static final Map<Character, Character> JIRA_MIDDLE_OR_CLOSING_TAG_TO_JIRA_OPENING_TAG = Collections.unmodifiableMap(
            new MapBuilder<Character, Character>()
                    .put(JiraTag.BOLD, JiraTag.BOLD)
                    .put(JiraTag.ITALIC, JiraTag.ITALIC)
                    .put(JiraTag.UNDERLINE, JiraTag.UNDERLINE)
                    .put(JiraTag.STRIKE, JiraTag.STRIKE)
                    .put(JiraTag.SUPERSCRIPT, JiraTag.SUPERSCRIPT)
                    .put(JiraTag.SUBSCRIPT, JiraTag.SUBSCRIPT)
                    .put(JiraTag.TAG_CLOSE, JiraTag.TAG_OPEN)
                    .put(JiraTag.LINK_CLOSE, JiraTag.LINK_OPEN)
                    .build());
    private static final Set<Character> JIRA_OPENING_TAGS = Collections.unmodifiableSet(new HashSet<Character>(
            JIRA_MIDDLE_OR_CLOSING_TAG_TO_JIRA_OPENING_TAG.values()));
    private static final Map<Character, String> JIRA_TAG_TO_HTML_TAG = Collections.unmodifiableMap(
            new MapBuilder<Character, String>()
                    .put(JiraTag.BOLD, HTML.Tag.B.toString())
                    .put(JiraTag.ITALIC, HTML.Tag.I.toString())
                    .put(JiraTag.UNDERLINE, HTML.Tag.U.toString())
                    .put(JiraTag.STRIKE, HTML.Tag.STRIKE.toString())
                    .put(JiraTag.SUPERSCRIPT, HTML.Tag.SUP.toString())
                    .put(JiraTag.SUBSCRIPT, HTML.Tag.SUB.toString())
                    .build());

    private static enum Mode {
        DEFAULT("DEFAULT"),
        DEFAULT_NO_ESCAPE("DEFAULT_NO_ESCAPE"),
        LINK("LINK"),
        QUOTE("QUOTE"),
        NO_FORMAT("NO_FORMAT"),
        CODE("NO_FORMAT");

        @SuppressWarnings("unused")
        private final String nameCode;

        Mode(final String nameCode) {
            this.nameCode = nameCode;
        }

        private static Mode getDefault(LineStructure lineStructure) {
            return (lineStructure.equals(LineStructure.DEFAULT) ? DEFAULT : DEFAULT_NO_ESCAPE);
        }

        private boolean allowsFormatting() {
            return (this.equals(DEFAULT) || this.equals(DEFAULT_NO_ESCAPE));
        }

        private boolean allowsLinks() {
            return (allowsFormatting() || this.equals(LINK));
        }

        private boolean allowsStructures() {
            return (allowsFormatting() || this.equals(LINK));
        }

        private boolean allowsNoFormat() {
            return (this.equals(DEFAULT) || this.equals(NO_FORMAT));
        }

        private boolean allowsQuote() {
            return (this.equals(DEFAULT) || this.equals(QUOTE));
        }

        private boolean allowsCode() {
            return (this.equals(DEFAULT) || this.equals(CODE));
        }

        private boolean allowsTag(char tagSymbol) {
            if (tagSymbol == JiraTag.TAG_OPEN || tagSymbol == JiraTag.TAG_CLOSE) {
                return !this.equals(LINK);
            }
            if (tagSymbol == JiraTag.LINK_CLOSE) {
                return allowsLinks();
            }
            return allowsFormatting();
        }
    }

    private static String toOpeningTag(HTML.Tag htmlTag) {
        if (htmlTag.equals(HTML.Tag.TABLE)) {
            return "<table border=\"1\" style=\"border-collapse: collapse;\">";
        }
        return "<" + htmlTag.toString() + ">";
    }

    private static String toClosingTag(HTML.Tag htmlTag) {
        return "</" + htmlTag.toString() + ">";
    }

    private static String toOpeningTag(String jiraTag) {
        if (jiraTag.length() != 1) {
            throw new IllegalArgumentException();
        }
        return "<" + JIRA_TAG_TO_HTML_TAG.get(jiraTag.charAt(0)) + ">";
    }

    private static String toClosingTag(char jiraTag) {
        return "</" + JIRA_TAG_TO_HTML_TAG.get(jiraTag) + ">";
    }

    private static Mode removeTagsAfterIndex(Map<Character, Integer> unclosedTagToLineElementsIdx, int idx,
            Mode formattingMode, LineStructure lineStructure) {
        unclosedTagToLineElementsIdx.entrySet().removeIf(entry -> (entry.getValue() >= idx));
        if (formattingMode.allowsLinks() && !unclosedTagToLineElementsIdx.containsKey(JiraTag.LINK_OPEN)) {
            return Mode.getDefault(lineStructure);
        }
        return formattingMode;
    }

    private static Mode removeTag(Map<Character, Integer> unclosedTagToLineElementsIdx, char tagSymbol,
            Mode formattingMode, LineStructure lineStructure) {
        unclosedTagToLineElementsIdx.keySet().remove(tagSymbol);
        if (formattingMode.allowsLinks() && tagSymbol == JiraTag.LINK_OPEN) {
            return Mode.getDefault(lineStructure);
        }
        return formattingMode;
    }

    private static Mode addConvertedLine(StringBuilder htmlBuilder, String line, int startIndex,
            Mode formattingMode, LineStructure lineStructure) {
        List<String> lineElements = new ArrayList<String>();

        StringBuilder elementBuilder = new StringBuilder();
        Map<Character, Integer> unclosedTagToLineElementsIdx = new HashMap<Character, Integer>(JIRA_OPENING_TAGS.size());
        for (int idx = startIndex; idx <= ExtStringUtils.lastIndexOfNonWhitespace(line); idx++) {
            char c = line.charAt(idx);
            Character openingCounterpart = JIRA_MIDDLE_OR_CLOSING_TAG_TO_JIRA_OPENING_TAG.get(c);
            Integer openingCounterpartIdx = (openingCounterpart == null ? null
                    : unclosedTagToLineElementsIdx.get(openingCounterpart));

            // character is a middle or closing tag
            if (formattingMode.allowsTag(c) && openingCounterpartIdx != null) {
                if (JIRA_TAG_TO_HTML_TAG.containsKey(c)) {
                    // replace jira tag with html tag
                    lineElements.set(openingCounterpartIdx, toOpeningTag(lineElements.get(openingCounterpartIdx)));
                    // add last element
                    lineElements.add(elementBuilder.toString());
                    // remove closed and aborted tags
                    formattingMode = removeTagsAfterIndex(unclosedTagToLineElementsIdx, openingCounterpartIdx,
                            formattingMode, lineStructure);
                    // add closing html tag
                    lineElements.add(toClosingTag(c));
                    // start new element
                    elementBuilder = new StringBuilder();
                }

                if (c == JiraTag.LINK_CLOSE) {
                    if (openingCounterpartIdx == lineElements.size() - 1 && elementBuilder.length() > 0) {
                        String link = elementBuilder.toString();
                        if (!link.isEmpty() && (JIRA_LINK_TAGS.contains(link.charAt(0)) ||
                                link.startsWith("file:") || link.startsWith("anchor:"))) {
                            // replace [ with empty string
                            lineElements.set(openingCounterpartIdx, "");

                        } else {
                            String linkText;
                            int linkDelimeterIdx = link.lastIndexOf(JiraTag.LINK_DELIMETER);
                            if (linkDelimeterIdx >= 0) {
                                linkText = link.substring(0, linkDelimeterIdx);
                                link = link.substring(linkDelimeterIdx + 1);
                            } else {
                                if (link.startsWith(JiraTag.LINK_MAILTO)) {
                                    linkText = link.substring(JiraTag.LINK_MAILTO.length());
                                } else {
                                    linkText = link;
                                }
                            }
                            // replace [ with opening hyper reference tag
                            lineElements.set(openingCounterpartIdx, "<a href = \"" + link + "\">" + linkText);
                        }

                        // remove closed and aborted tags
                        formattingMode = removeTagsAfterIndex(unclosedTagToLineElementsIdx, openingCounterpartIdx,
                                formattingMode, lineStructure);
                        // add closing hyper reference tag
                        lineElements.add("</a>");
                        // start new element
                        elementBuilder = new StringBuilder();

                    } else {
                        // remove [ from unclosed tags
                        formattingMode = removeTag(unclosedTagToLineElementsIdx, openingCounterpart,
                                formattingMode, lineStructure);
                        // add ] as a normal symbol
                        elementBuilder.append(c);
                    }
                }

                if (c == JiraTag.TAG_CLOSE) {
                    if (openingCounterpartIdx == lineElements.size() - 1) {
                        String element = elementBuilder.toString();

                        if (formattingMode.allowsFormatting() && element.startsWith(JiraTag.COLOR_OPEN)) {
                            // replace { with font tag
                            lineElements.set(openingCounterpartIdx,
                                    "<font color=\"" + element.substring(JiraTag.COLOR_OPEN.length()) + "\">");
                            // start new element
                            elementBuilder = new StringBuilder();
                            continue;
                        }

                        if (formattingMode.allowsFormatting() && element.equals(JiraTag.COLOR_CLOSE)) {
                            // replace { with font tag
                            lineElements.set(openingCounterpartIdx, "</font>");
                            // start new element
                            elementBuilder = new StringBuilder();
                            continue;
                        }

                        if (formattingMode.allowsQuote() && element.equals(JiraTag.QUOTE_MULTILINE)) {
                            // change format mode
                            if (formattingMode == Mode.QUOTE) {
                                formattingMode = Mode.getDefault(lineStructure);
                                // replace { with closing quote tag
                                lineElements.set(openingCounterpartIdx, toClosingTag(HTML.Tag.BLOCKQUOTE));
                            } else {
                                formattingMode = Mode.QUOTE;
                                // replace { with opening quote tag
                                lineElements.set(openingCounterpartIdx, toOpeningTag(HTML.Tag.BLOCKQUOTE));
                            }
                            // remove all unclosed tags
                            unclosedTagToLineElementsIdx.clear();
                            // start new element
                            elementBuilder = new StringBuilder();
                            continue;
                        }

                        if (formattingMode.allowsNoFormat() && element.equals(JiraTag.NO_FORMAT)) {
                            // change format mode
                            if (formattingMode == Mode.NO_FORMAT) {
                                formattingMode = Mode.getDefault(lineStructure);
                            } else {
                                formattingMode = Mode.NO_FORMAT;
                            }
                            // remove { from elements
                            lineElements.remove(openingCounterpartIdx.intValue());
                            // remove all unclosed tags
                            unclosedTagToLineElementsIdx.clear();
                            // start new element
                            elementBuilder = new StringBuilder();
                            continue;
                        }

                        if (formattingMode.allowsCode() && element.startsWith(JiraTag.CODE)) {
                            // change format mode
                            if (formattingMode == Mode.CODE) {
                                formattingMode = Mode.getDefault(lineStructure);
                                // replace { with closing code tag
                                lineElements.set(openingCounterpartIdx, toClosingTag(HTML.Tag.CODE));
                            } else {
                                formattingMode = Mode.CODE;
                                // replace { with opening code tag
                                lineElements.set(openingCounterpartIdx, toOpeningTag(HTML.Tag.CODE));
                            }
                            // remove all unclosed tags
                            unclosedTagToLineElementsIdx.clear();
                            // start new element
                            elementBuilder = new StringBuilder();
                            continue;
                        }
                    }

                    // invalid tag - treat } as a normal symbol

                    // remove { from unclosed tags
                    formattingMode = removeTag(unclosedTagToLineElementsIdx, openingCounterpart, formattingMode, lineStructure);
                    // add } as a normal symbol
                    elementBuilder.append(c);
                }

            } else {
                // character is an opening tag
                if (formattingMode.allowsTag(c) && JIRA_OPENING_TAGS.contains(c)) {
                    // change format mode
                    if (c == JiraTag.LINK_OPEN) {
                        formattingMode = Mode.LINK;
                    }

                    lineElements.add(elementBuilder.toString());
                    unclosedTagToLineElementsIdx.put(c, lineElements.size());
                    lineElements.add(String.valueOf(c));
                    elementBuilder = new StringBuilder();

                } else {
                    // character is just a symbol
                    switch (c) {
                        case '<':
                            elementBuilder.append("&lt;");
                            break;
                        case '>':
                            elementBuilder.append("&gt;");
                            break;
                        default:
                            elementBuilder.append(c);
                            break;
                    }
                }
            }
        }
        lineElements.add(elementBuilder.toString());

        Integer linkTagIdx = unclosedTagToLineElementsIdx.get(JiraTag.LINK_OPEN);
        if (linkTagIdx != null) {
            for (int i = 0; i <= linkTagIdx; i++) {
                htmlBuilder.append(lineElements.get(i));
            }
            formattingMode = Mode.getDefault(lineStructure);
            for (int i = linkTagIdx + 1; i < lineElements.size(); i++) {
                formattingMode = addConvertedLine(htmlBuilder, lineElements.get(i), formattingMode, lineStructure);
            }

        } else {
            for (String element : lineElements) {
                htmlBuilder.append(element);
            }
        }

        return formattingMode;
    }

    private static Mode addConvertedLine(StringBuilder htmlBuilder, String line,
            Mode formattingMode, LineStructure lineStructure) {
        return addConvertedLine(htmlBuilder, line, ExtStringUtils.indexOfNonWhitespace(line), formattingMode, lineStructure);
    }

    private static enum LineStructure {
        DEFAULT("DEFAULT"),
        TABLE_HEADER("TABLE_HEADER"),
        TABLE_ROW("TABLE_ROW"),
        INLINE_TAG("INLINE_TAG"),
        UNORDERED_LIST("UNORDERED_LIST"),
        ORDERED_LIST("ORDERED_LIST");

        @SuppressWarnings("unused")
        private final String nameCode;

        LineStructure(final String nameCode) {
            this.nameCode = nameCode;
        }
    }

    private static final Map<String, HTML.Tag> JIRA_INLINE_TAG_TO_HTML_TAG = Collections.unmodifiableMap(
            new MapBuilder<String, HTML.Tag>()
                    .put(JiraTag.QUOTE, HTML.Tag.BLOCKQUOTE)
                    .put(JiraTag.HEADER1, HTML.Tag.H1)
                    .put(JiraTag.HEADER2, HTML.Tag.H2)
                    .put(JiraTag.HEADER3, HTML.Tag.H3)
                    .put(JiraTag.HEADER4, HTML.Tag.H4)
                    .put(JiraTag.HEADER5, HTML.Tag.H5)
                    .put(JiraTag.HEADER6, HTML.Tag.H6)
                    .build());

    public static String jiraFormatToHtml(String jiraFormat) {
        StringBuilder htmlBuilder = new StringBuilder(2 * jiraFormat.length());

        Mode formattingMode = Mode.DEFAULT;
        LineStructure prevLineStructure = LineStructure.DEFAULT;
        for (String line : jiraFormat.split("\n")) {
            LineStructure lineStructure = LineStructure.DEFAULT;
            HTML.Tag htmlInlineTag = null;
            Integer startIndex = 0;
            if (formattingMode.allowsStructures()) {
                if (line.startsWith(JiraTag.TABLE_HEADER) &&
                        ExtStringUtils.endsWithSuffixAndWhitespace(line, JiraTag.TABLE_HEADER)) {
                    lineStructure = LineStructure.TABLE_HEADER;
                } else {
                    if (line.startsWith(JiraTag.TABLE_ROW) &&
                            ExtStringUtils.endsWithSuffixAndWhitespace(line, JiraTag.TABLE_ROW)) {
                        lineStructure = LineStructure.TABLE_ROW;
                    }
                }

                if (line.startsWith(JiraTag.UNORDERED_LIST)) {
                    lineStructure = LineStructure.UNORDERED_LIST;
                }

                if (line.startsWith(JiraTag.ORDERED_LIST)) {
                    lineStructure = LineStructure.ORDERED_LIST;
                }

                for (String jiraInlineTag : JIRA_INLINE_TAG_TO_HTML_TAG.keySet()) {
                    if (ExtStringUtils.startsWithWhitespaceAndPrefix(line, jiraInlineTag)) {
                        lineStructure = LineStructure.INLINE_TAG;
                        htmlInlineTag = JIRA_INLINE_TAG_TO_HTML_TAG.get(jiraInlineTag);
                        startIndex = jiraInlineTag.length();
                    }
                }

                formattingMode = Mode.getDefault(lineStructure);
            }

            if ((prevLineStructure.equals(LineStructure.TABLE_HEADER) || prevLineStructure.equals(LineStructure.TABLE_ROW)) &&
                    !lineStructure.equals(LineStructure.TABLE_ROW)) {
                htmlBuilder.append(toClosingTag(HTML.Tag.TABLE));
                htmlBuilder.append("<br/>");
            }

            if (prevLineStructure.equals(LineStructure.UNORDERED_LIST) && !lineStructure.equals(LineStructure.UNORDERED_LIST)) {
                htmlBuilder.append(toClosingTag(HTML.Tag.UL));
            }

            if (prevLineStructure.equals(LineStructure.ORDERED_LIST) && !lineStructure.equals(LineStructure.ORDERED_LIST)) {
                htmlBuilder.append(toClosingTag(HTML.Tag.OL));
            }

            switch (lineStructure) {
                case TABLE_HEADER:
                    htmlBuilder.append(toOpeningTag(HTML.Tag.TABLE));
                    htmlBuilder.append(toOpeningTag(HTML.Tag.TR));

                    String[] headerCells = line.stripTrailing().split(JiraTag.TABLE_HEADER_REGEXP);
                    for (int cellIdx = 1; cellIdx < headerCells.length; cellIdx++) { // 0th is empty because of how split works
                        htmlBuilder.append(toOpeningTag(HTML.Tag.TH));
                        formattingMode = addConvertedLine(htmlBuilder, headerCells[cellIdx], formattingMode, lineStructure);
                        htmlBuilder.append(toClosingTag(HTML.Tag.TH));
                    }

                    htmlBuilder.append(toClosingTag(HTML.Tag.TR));
                    break;

                case TABLE_ROW:
                    if (!prevLineStructure.equals(LineStructure.TABLE_HEADER) &&
                            !prevLineStructure.equals(LineStructure.TABLE_ROW)) {
                        htmlBuilder.append(toOpeningTag(HTML.Tag.TABLE));
                    }
                    htmlBuilder.append(toOpeningTag(HTML.Tag.TR));

                    String[] rowCells = line.stripTrailing().split(JiraTag.TABLE_ROW_REGEXP);
                    for (int cellIdx = 1; cellIdx < rowCells.length; cellIdx++) { // 0th is empty because of how split works
                        htmlBuilder.append(toOpeningTag(HTML.Tag.TD));
                        formattingMode = addConvertedLine(htmlBuilder, rowCells[cellIdx], formattingMode, lineStructure);
                        htmlBuilder.append(toClosingTag(HTML.Tag.TD));
                    }

                    htmlBuilder.append(toClosingTag(HTML.Tag.TR));
                    break;

                case UNORDERED_LIST:
                    if (!prevLineStructure.equals(LineStructure.UNORDERED_LIST)) {
                        htmlBuilder.append(toOpeningTag(HTML.Tag.UL));
                    }
                    htmlBuilder.append(toOpeningTag(HTML.Tag.LI));
                    formattingMode = addConvertedLine(htmlBuilder, line, JiraTag.UNORDERED_LIST.length(),
                            formattingMode, lineStructure);
                    htmlBuilder.append(toClosingTag(HTML.Tag.LI));
                    break;

                case ORDERED_LIST:
                    if (!prevLineStructure.equals(LineStructure.ORDERED_LIST)) {
                        htmlBuilder.append(toOpeningTag(HTML.Tag.OL));
                    }
                    htmlBuilder.append(toOpeningTag(HTML.Tag.LI));
                    formattingMode = addConvertedLine(htmlBuilder, line, JiraTag.ORDERED_LIST.length(),
                            formattingMode, lineStructure);
                    htmlBuilder.append(toClosingTag(HTML.Tag.LI));
                    break;

                case INLINE_TAG:
                    htmlBuilder.append(toOpeningTag(htmlInlineTag));
                    formattingMode = addConvertedLine(htmlBuilder, line, startIndex, formattingMode, lineStructure);
                    htmlBuilder.append(toClosingTag(htmlInlineTag));
                    break;

                case DEFAULT:
                default:
                    int prevSymbolCount = htmlBuilder.length();
                    formattingMode = addConvertedLine(htmlBuilder, line, formattingMode, lineStructure);
                    if (line.isBlank() || (prevSymbolCount < htmlBuilder.length())) {
                        htmlBuilder.append("<br/>");
                    }
                    break;
            }

            prevLineStructure = lineStructure;
        }

        return htmlBuilder.toString();
    }
}
