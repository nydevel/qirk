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
