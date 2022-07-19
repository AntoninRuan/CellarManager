package fr.antoninruan.cellarmanager.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

/**
 * @author Antonin Ruan
 */
public class MarkdownParser {

    public static String parseMarkdown(String markdown) {

        String header = "<html>\n <body class=\"markdown-body\">\n";
        String footer = "</body>\n </html>\n";

        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        String body = renderer.render(parser.parse(markdown));

        String html = header + body + footer;

        return html;
    }

}
