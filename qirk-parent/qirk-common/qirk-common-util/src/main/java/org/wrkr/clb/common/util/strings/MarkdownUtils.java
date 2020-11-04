package org.wrkr.clb.common.util.strings;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

public abstract class MarkdownUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MarkdownUtils.class);

    private static final Set<Character> SPECIAL_SYMBOLS = Collections.unmodifiableSet(new HashSet<Character>(Arrays.asList(
            '\\', '`', '*', '_', '{', '}', '[', ']', '(', ')', '#', '+', '-', '.', '!', '|')));

    private static final List<Extension> EXTENSIONS = Arrays.asList(TablesExtension.create());
    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();

    private static final List<String> SUMMARY_CUT_SYMBOLS = Collections.unmodifiableList(Arrays.asList("\n", ". ", ".\n"));

    public static String escapeCharacter(char c) {
        switch (c) {
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            default:
                if (SPECIAL_SYMBOLS.contains(c)) {
                    return "\\" + c;
                }
                return String.valueOf(c);
        }
    }

    public static String escapeMarkdownSpecialSymbols(String plainText) {
        StringBuilder builder = new StringBuilder(2 * plainText.length());
        for (int i = 0; i < plainText.length(); i++) {
            builder.append(escapeCharacter(plainText.charAt(i)));
        }
        return builder.toString();
    }

    public static String markdownToHtml(String md, boolean replaceLTAndGT) {
        if (md == null) {
            return "";
        }

        long startTime = System.currentTimeMillis();

        if (replaceLTAndGT) {
            md = md.replaceAll("<", "&lt;").replaceAll("(^\\n)>", "$1&gt;");
        }
        String html = HtmlRenderer.builder().extensions(EXTENSIONS).build().render(PARSER.parse(md));

        long resultTime = System.currentTimeMillis() - startTime;
        LOG.debug("processed render from markdown to html in " + resultTime + " ms");

        return html;
    }

    public static String markdownToHtml(String md) {
        return markdownToHtml(md, true);
    }

    public static String descriptionToSummary(String md, String html, int summaryLength) throws IOException {
        long startTime = System.currentTimeMillis();

        String firstLineHtml = markdownToHtml(ExtStringUtils.substringByFirstSymbol(md, '\n'));
        if (firstLineHtml != null) {
            html = firstLineHtml;
        }

        String plainText = HtmlUtils.htmlToPlainTextWithNewLines(html);
        String summary = ExtStringUtils.substringByLimitOrSymbols(plainText, summaryLength, SUMMARY_CUT_SYMBOLS);

        long resultTime = System.currentTimeMillis() - startTime;
        LOG.debug("processed render from html to summary in " + resultTime + " ms");

        return summary;
    }
}
