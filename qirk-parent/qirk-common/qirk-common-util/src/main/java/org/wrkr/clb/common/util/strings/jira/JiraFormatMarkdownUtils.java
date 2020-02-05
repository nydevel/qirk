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
package org.wrkr.clb.common.util.strings.jira;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.common.util.collections.MapBuilder;
import org.wrkr.clb.common.util.strings.ExtStringUtils;
import org.wrkr.clb.common.util.strings.MarkdownUtils;

public class JiraFormatMarkdownUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JiraFormatMarkdownUtils.class);

    private static class JiraTag {
        private static final char ESCAPE = '\\';

        private static final char BOLD = '*';
        private static final char ITALIC = '_';
        private static final char UNDERLINE = '+';
        private static final char STRIKE = '-';
        private static final char SUPERSCRIPT = '^';
        private static final char SUBSCRIPT = '~';

        private static final char LINK_OPEN = '[';
        private static final char LINK_CLOSE = ']';
        private static final char LINK_DELIMETER = '|';
        private static final String LINK_DELIMETER_ESCAPED = MarkdownUtils.escapeCharacter(LINK_DELIMETER);
        private static final String LINK_MAILTO = "mailto:";

        private static final char TAG_OPEN = '{';
        private static final char TAG_CLOSE = '}';

        private static final String TABLE_HEADER = "||";
        private static final String TABLE_ROW = "|";

        // tags at the start and the end of the line
        private static final String TABLE_HEADER_REGEXP = TABLE_HEADER.replace("|", "\\|");
        private static final String TABLE_ROW_REGEXP = TABLE_ROW.replace("|", "\\|");

        // lists
        private static final char UNORDERED_LIST1 = '*';
        private static final char UNORDERED_LIST2 = '-';
        private static final char ORDERED_LIST = '#';
        private static final char LIST_SUFFIX = ' ';

        // tags at the start of the line
        private static final String HORIZONTAL_RULER = "----";
        private static final String QUOTE = "bq. ";
        private static final String HEADER1 = "h1. ";
        private static final String HEADER2 = "h2. ";
        private static final String HEADER3 = "h3. ";
        private static final String HEADER4 = "h4. ";
        private static final String HEADER5 = "h5. ";
        private static final String HEADER6 = "h6. ";

        // tags inside braces
        private static final String COLOR = "color";
        private static final String CODE = "code";
        private static final String NO_FORMAT = "noformat";

        // structure tags inside braces
        private static final String QUOTE_MULTILINE = "{quote}";
        private static final String CODE_PREFIX = TAG_OPEN + CODE;
        private static final String CODE_SUFFIX = String.valueOf(TAG_CLOSE);
    }

    private static final Set<Character> JIRA_LINK_TAGS = Collections.unmodifiableSet(new HashSet<Character>(Arrays.asList(
            '#', '^', '~')));

    private static final Set<Character> JIRA_SPECIAL_SYMBOLS = Collections.unmodifiableSet(new HashSet<Character>(
            Arrays.asList(ArrayUtils.addAll(JIRA_LINK_TAGS.toArray(new Character[0]),
                    JiraTag.BOLD, JiraTag.ITALIC, JiraTag.UNDERLINE, JiraTag.STRIKE, JiraTag.SUPERSCRIPT, JiraTag.SUBSCRIPT,
                    JiraTag.LINK_OPEN, JiraTag.LINK_DELIMETER, JiraTag.LINK_CLOSE, JiraTag.TAG_OPEN, JiraTag.TAG_CLOSE,
                    JiraTag.UNORDERED_LIST1, JiraTag.UNORDERED_LIST2, JiraTag.ORDERED_LIST,
                    JiraTag.ESCAPE, ':'))));

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

    private static final Map<Character, String> JIRA_TAG_TO_MD_OPENING_TAG = Collections.unmodifiableMap(
            new MapBuilder<Character, String>()
                    .put(JiraTag.BOLD, "**")
                    .put(JiraTag.ITALIC, "*")
                    .put(JiraTag.UNDERLINE, "")
                    .put(JiraTag.STRIKE, "")
                    .put(JiraTag.SUPERSCRIPT, "")
                    .put(JiraTag.SUBSCRIPT, "")
                    .build());
    private static final Map<Character, String> JIRA_TAG_TO_MD_CLOSING_TAG = Collections.unmodifiableMap(
            new MapBuilder<Character, String>(JIRA_TAG_TO_MD_OPENING_TAG).build());

    private static final Map<Character, String> JIRA_LIST_TAG_TO_MD_TAG = Collections.unmodifiableMap(
            new MapBuilder<Character, String>()
                    .put(JiraTag.UNORDERED_LIST1, "- ")
                    .put(JiraTag.UNORDERED_LIST2, "- ")
                    .put(JiraTag.ORDERED_LIST, "1. ")
                    .build());

    private static enum Mode {
        DEFAULT("DEFAULT"),
        DEFAULT_NO_ESCAPE("DEFAULT_NO_ESCAPE"),
        LINK("LINK"),
        CODE("CODE"),
        NO_FORMAT("NO_FORMAT");

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

    private static Mode removeTagsAfterIndex(Map<Character, Integer> unclosedTagToLineElementsIdx, int idx,
            List<String> elements, Mode formattingMode, LineStructure lineStructure) {
        for (Iterator<Map.Entry<Character, Integer>> i = unclosedTagToLineElementsIdx.entrySet().iterator(); i.hasNext();) {
            Entry<Character, Integer> entry = i.next();
            if (entry.getValue() >= idx) {
                if (entry.getValue() > idx) {
                    elements.set(entry.getValue(), MarkdownUtils.escapeCharacter(entry.getKey()));
                }
                i.remove();
            }
        }
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

    private static void addCharacter(StringBuilder elementBuilder, char c) {
        elementBuilder.append(MarkdownUtils.escapeCharacter(c));
    }

    private static Mode addConvertedLine(StringBuilder mdBuilder, String line, int startIndex,
            Mode formattingMode, LineStructure lineStructure) {
        int endIndex = ExtStringUtils.lastIndexOfNonWhitespace(line);
        List<String> lineElements = new ArrayList<String>();

        StringBuilder elementBuilder = new StringBuilder();
        Map<Character, Integer> unclosedTagToLineElementsIdx = new HashMap<Character, Integer>(JIRA_OPENING_TAGS.size());
        Integer lastLinkTagLineIdx = null;
        for (int idx = startIndex; idx <= endIndex; idx++) {
            char c = line.charAt(idx);
            // check if it's an escape character
            if (formattingMode.allowsFormatting() &&
                    idx < endIndex && c == JiraTag.ESCAPE && JIRA_SPECIAL_SYMBOLS.contains(line.charAt(idx + 1))) {
                addCharacter(elementBuilder, line.charAt(idx + 1));
                idx++;
                continue;
            }

            Character openingCounterpart = JIRA_MIDDLE_OR_CLOSING_TAG_TO_JIRA_OPENING_TAG.get(c);
            Integer openingCounterpartIdx = (openingCounterpart == null ? null
                    : unclosedTagToLineElementsIdx.get(openingCounterpart));

            // character is a middle or closing tag
            if (formattingMode.allowsTag(c) && openingCounterpartIdx != null) {
                if (JIRA_TAG_TO_MD_OPENING_TAG.containsKey(c)) {
                    // replace jira tag with md tag
                    lineElements.set(openingCounterpartIdx, JIRA_TAG_TO_MD_OPENING_TAG.get(openingCounterpart));
                    // add last element
                    lineElements.add(elementBuilder.toString());
                    // remove closed and aborted tags
                    formattingMode = removeTagsAfterIndex(unclosedTagToLineElementsIdx, openingCounterpartIdx,
                            lineElements, formattingMode, lineStructure);
                    // add closing md tag
                    lineElements.add(JIRA_TAG_TO_MD_CLOSING_TAG.get(c));
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
                            int linkDelimeterIdx = link.lastIndexOf(JiraTag.LINK_DELIMETER_ESCAPED);
                            if (linkDelimeterIdx >= 0) {
                                linkText = link.substring(0, linkDelimeterIdx);
                                link = link.substring(linkDelimeterIdx + JiraTag.LINK_DELIMETER_ESCAPED.length());
                            } else {
                                if (link.startsWith(JiraTag.LINK_MAILTO)) {
                                    linkText = link.substring(JiraTag.LINK_MAILTO.length());
                                } else {
                                    linkText = link;
                                }
                            }
                            // replace [ with opening hyper reference tag
                            lineElements.set(openingCounterpartIdx, "[" + linkText + "](" + link + ")");
                        }

                        // remove closed and aborted tags
                        formattingMode = removeTagsAfterIndex(unclosedTagToLineElementsIdx, openingCounterpartIdx,
                                lineElements, formattingMode, lineStructure);
                        // start new element
                        elementBuilder = new StringBuilder();

                    } else {
                        // escape [
                        lineElements.set(openingCounterpartIdx, MarkdownUtils.escapeCharacter(openingCounterpart));
                        // remove [ from unclosed tags
                        formattingMode = removeTag(unclosedTagToLineElementsIdx, openingCounterpart,
                                formattingMode, lineStructure);
                        // add ] as a normal symbol
                        addCharacter(elementBuilder, c);
                    }
                }

                if (c == JiraTag.TAG_CLOSE) {
                    if (openingCounterpartIdx == lineElements.size() - 1) {
                        String element = elementBuilder.toString();

                        if (formattingMode.allowsFormatting() && element.startsWith(JiraTag.COLOR)) {
                            // replace { with font tag
                            lineElements.set(openingCounterpartIdx, "");
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
                                lineElements.set(openingCounterpartIdx, "``");
                            } else {
                                formattingMode = Mode.CODE;
                                // replace { with opening code tag
                                lineElements.set(openingCounterpartIdx, "``");
                            }
                            // remove all unclosed tags
                            unclosedTagToLineElementsIdx.clear();
                            // start new element
                            elementBuilder = new StringBuilder();
                            continue;
                        }
                    }

                    // invalid tag - treat } as a normal symbol

                    // escape {
                    lineElements.set(openingCounterpartIdx, MarkdownUtils.escapeCharacter(openingCounterpart));
                    // remove { from unclosed tags
                    formattingMode = removeTag(unclosedTagToLineElementsIdx, openingCounterpart, formattingMode, lineStructure);
                    // add } as a normal symbol
                    addCharacter(elementBuilder, c);
                }

            } else {
                // character is an opening tag
                if (formattingMode.allowsTag(c) && JIRA_OPENING_TAGS.contains(c)) {
                    // change format mode
                    if (c == JiraTag.LINK_OPEN) {
                        formattingMode = Mode.LINK;
                        lastLinkTagLineIdx = idx;
                    }

                    lineElements.add(elementBuilder.toString());
                    unclosedTagToLineElementsIdx.put(c, lineElements.size());
                    lineElements.add(String.valueOf(c));
                    elementBuilder = new StringBuilder();

                } else {
                    // character is just a symbol
                    addCharacter(elementBuilder, c);
                }
            }
        }
        lineElements.add(elementBuilder.toString());

        Integer linkTagElementIdx = unclosedTagToLineElementsIdx.get(JiraTag.LINK_OPEN);
        if (linkTagElementIdx != null) {
            for (int i = 0; i <= linkTagElementIdx; i++) {
                mdBuilder.append(lineElements.get(i));
            }
            formattingMode = Mode.getDefault(lineStructure);
            for (int i = linkTagElementIdx + 1; i < lineElements.size(); i++) {
                formattingMode = addConvertedLine(mdBuilder, line, lastLinkTagLineIdx + 1, formattingMode, lineStructure);
            }

        } else {
            for (String element : lineElements) {
                mdBuilder.append(element);
            }
        }

        if (formattingMode.equals(Mode.CODE)) {
            return Mode.getDefault(lineStructure);
        }
        return formattingMode;
    }

    private static Mode addConvertedLine(StringBuilder mdBuilder, String line,
            Mode formattingMode, LineStructure lineStructure) {
        return addConvertedLine(mdBuilder, line, 0, formattingMode, lineStructure);
    }

    private static enum LineStructure {
        DEFAULT("DEFAULT"),
        TABLE_HEADER("TABLE_HEADER"),
        TABLE_ROW("TABLE_ROW"),
        QUOTE("QUOTE"),
        CODE("CODE"),
        LIST("LIST"),
        INLINE_TAG("INLINE_TAG");

        @SuppressWarnings("unused")
        private final String nameCode;

        LineStructure(final String nameCode) {
            this.nameCode = nameCode;
        }
    }

    private static final Map<String, String> JIRA_INLINE_TAG_TO_MD_TAG = Collections.unmodifiableMap(
            new MapBuilder<String, String>()
                    .put(JiraTag.QUOTE, "> ")
                    .put(JiraTag.HEADER1, "# ")
                    .put(JiraTag.HEADER2, "## ")
                    .put(JiraTag.HEADER3, "### ")
                    .put(JiraTag.HEADER4, "#### ")
                    .put(JiraTag.HEADER5, "##### ")
                    .put(JiraTag.HEADER6, "###### ")
                    .build());

    private static String getListPrefix(String line) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (JIRA_LIST_TAG_TO_MD_TAG.keySet().contains(c)) {
                builder.append(c);
            } else {
                if (c == JiraTag.LIST_SUFFIX) {
                    return builder.toString();
                } else {
                    return "";
                }
            }
        }
        return "";
    }

    public static String jiraFormatToMarkdown(String jiraFormat) {
        long startTime = System.currentTimeMillis();

        StringBuilder mdBuilder = new StringBuilder(jiraFormat.length());

        int jiraFormatIdx = 0;
        Mode formattingMode = Mode.DEFAULT;
        LineStructure prevLineStructure = LineStructure.DEFAULT;
        while (jiraFormatIdx < jiraFormat.length()) {
            String line = ExtStringUtils.substringByFirstSymbol(jiraFormat, '\n', jiraFormatIdx);
            jiraFormatIdx += (line.length() + 1);

            LineStructure lineStructure = LineStructure.DEFAULT;
            String listPrefix = null;
            String mdInlineTag = null;
            Integer startIndex = 0;
            if (formattingMode.allowsStructures()) {
                if (prevLineStructure.equals(LineStructure.QUOTE)) {
                    if (ExtStringUtils.equalsWithoutWhitespace(line, JiraTag.QUOTE_MULTILINE)) {
                        prevLineStructure = LineStructure.DEFAULT;
                        mdBuilder.append('\n');
                        continue;
                    } else {
                        lineStructure = LineStructure.QUOTE;
                    }

                } else if (prevLineStructure.equals(LineStructure.CODE)) {
                    if (ExtStringUtils.startsWithWhitespaceAndPrefix(line, JiraTag.CODE_PREFIX) &&
                            ExtStringUtils.endsWithSuffixAndWhitespace(line, JiraTag.CODE_SUFFIX)) {
                        prevLineStructure = LineStructure.DEFAULT;
                        mdBuilder.append('\n');
                        continue;
                    } else {
                        lineStructure = LineStructure.CODE;
                    }

                } else {
                    if (ExtStringUtils.equalsWithoutWhitespace(line, JiraTag.HORIZONTAL_RULER)) {
                        prevLineStructure = LineStructure.DEFAULT;
                        mdBuilder.append("---\n");
                        continue;
                    }

                    if (ExtStringUtils.equalsWithoutWhitespace(line, JiraTag.QUOTE_MULTILINE)) {
                        prevLineStructure = LineStructure.QUOTE;
                        continue;
                    }

                    if (line.startsWith(JiraTag.CODE_PREFIX) &&
                            ExtStringUtils.endsWithSuffixAndWhitespace(line, JiraTag.CODE_SUFFIX)) {
                        prevLineStructure = LineStructure.CODE;
                        continue;
                    }

                    if (line.startsWith(JiraTag.TABLE_HEADER) &&
                            ExtStringUtils.endsWithSuffixAndWhitespace(line, JiraTag.TABLE_HEADER)) {
                        lineStructure = LineStructure.TABLE_HEADER;
                    } else {
                        if (line.startsWith(JiraTag.TABLE_ROW) &&
                                ExtStringUtils.endsWithSuffixAndWhitespace(line, JiraTag.TABLE_ROW)) {
                            lineStructure = LineStructure.TABLE_ROW;
                        }
                    }

                    listPrefix = getListPrefix(line);
                    if (!listPrefix.isEmpty()) {
                        lineStructure = LineStructure.LIST;
                    }

                    for (String jiraInlineTag : JIRA_INLINE_TAG_TO_MD_TAG.keySet()) {
                        if (line.startsWith(jiraInlineTag)) {
                            lineStructure = LineStructure.INLINE_TAG;
                            mdInlineTag = JIRA_INLINE_TAG_TO_MD_TAG.get(jiraInlineTag);
                            startIndex = ExtStringUtils.indexOfNonWhitespace(line) + (jiraInlineTag.length() - 1);
                        }
                    }
                }

                formattingMode = Mode.getDefault(lineStructure);
            }

            if (prevLineStructure.equals(LineStructure.INLINE_TAG) && !lineStructure.equals(LineStructure.INLINE_TAG)) {
                mdBuilder.append('\n');
            }

            switch (lineStructure) {
                case TABLE_HEADER:
                    mdBuilder.append('\n');

                    String[] headerCells = line.stripTrailing().split(JiraTag.TABLE_HEADER_REGEXP);
                    for (int cellIdx = 1; cellIdx < headerCells.length; cellIdx++) { // 0th is empty because of how split works
                        mdBuilder.append("| ");
                        formattingMode = addConvertedLine(mdBuilder, headerCells[cellIdx], formattingMode, lineStructure);
                        mdBuilder.append(' ');
                    }
                    mdBuilder.append("|\n");

                    for (int cellIdx = 1; cellIdx < headerCells.length; cellIdx++) { // 0th is empty because of how split works
                        mdBuilder.append("| --- ");
                    }
                    mdBuilder.append("|\n");
                    break;

                case TABLE_ROW:
                    String[] rowCells = line.stripTrailing().split(JiraTag.TABLE_ROW_REGEXP);
                    for (int cellIdx = 1; cellIdx < rowCells.length; cellIdx++) { // 0th is empty because of how split works
                        mdBuilder.append("| ");
                        formattingMode = addConvertedLine(mdBuilder, rowCells[cellIdx], formattingMode, lineStructure);
                        mdBuilder.append(' ');
                    }
                    mdBuilder.append("|\n");
                    break;

                case QUOTE:
                    mdBuilder.append(JIRA_INLINE_TAG_TO_MD_TAG.get(JiraTag.QUOTE));
                    formattingMode = addConvertedLine(mdBuilder, line, formattingMode, lineStructure);
                    mdBuilder.append("  \n");
                    break;

                case CODE:
                    mdBuilder.append("    ");
                    formattingMode = addConvertedLine(mdBuilder, line, formattingMode, lineStructure);
                    mdBuilder.append('\n');
                    break;

                case LIST:
                    for (int prefixIdx = 0; prefixIdx < listPrefix.length() - 1; prefixIdx++) {
                        mdBuilder.append("    ");
                    }
                    mdBuilder.append(JIRA_LIST_TAG_TO_MD_TAG.get(listPrefix.charAt(listPrefix.length() - 1)));
                    formattingMode = addConvertedLine(mdBuilder, line, listPrefix.length() + 1, formattingMode, lineStructure);
                    mdBuilder.append('\n');
                    break;

                case INLINE_TAG:
                    mdBuilder.append(mdInlineTag);
                    formattingMode = addConvertedLine(mdBuilder, line, startIndex, formattingMode, lineStructure);
                    mdBuilder.append('\n');
                    break;

                case DEFAULT:
                default:
                    int prevSymbolCount = mdBuilder.length();
                    formattingMode = addConvertedLine(mdBuilder, line, formattingMode, lineStructure);
                    if (line.isBlank() || (prevSymbolCount < mdBuilder.length())) {
                        mdBuilder.append("  \n");
                    }
                    break;
            }

            prevLineStructure = lineStructure;
        }

        long resultTime = System.currentTimeMillis() - startTime;
        LOG.debug("processed render from jira format to masrkdown in " + resultTime + " ms");

        return mdBuilder.toString();
    }
}
