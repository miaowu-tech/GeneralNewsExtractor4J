package tech.miaowu.spider.extractor.news;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

/**
 *
 * @author anselwang
 * @since v0.1.0
 */
public class TitleExtractor {
    private static final String TITLE_HTAG_XPATH = "//h1//text() | //h2//text() | //h3//text() | //h4//text()";
    private static final String TITLE_SPLIT_CHAR_PATTERN = "[-_|]";

    private String extractByXPath(Document document, String titleXpath) {
        if (titleXpath != null && !titleXpath.isEmpty()) {
            Elements titleList = document.select(titleXpath);
            if (!titleList.isEmpty()) {
                return titleList.first().text();
            }
        }
        return "";
    }

    private String extractByTitle(Document document) {
        Elements titleList = document.select("title");
        if (!titleList.isEmpty()) {
            String title = titleList.first().text();
            String[] titles = title.split(TITLE_SPLIT_CHAR_PATTERN);
            if (titles.length > 0 && titles[0].length() >= 4) {
                return titles[0];
            }
            return title;
        }
        return "";
    }

    private String extractByHtag(Document document) {
        Elements titleList = Xsoup.select(document, TITLE_HTAG_XPATH).getElements();
        if (!titleList.isEmpty()) {
            return titleList.first().text();
        }
        return "";
    }

    /**
     *
     * 一般来说，我们可以认为 title 中包含新闻标题，但是可能也含有其他文字，例如：
     * SmartSpider 成为全球最好的新闻提取模块-今日头条
     * 新华网：SmartSpider 成为全球最好的新闻提取模块
     *
     *  同时，新闻的某个 <h>标签中也会包含这个新闻标题。
     *
     *  因此，通过 h 标签与 title 的文字双向匹配，找到最适合作为新闻标题的字符串。
     *  但是，需要考虑到 title 与 h 标签中的文字可能均含有特殊符号，因此，不能直接通过
     *  判断 h 标签中的文字是否在 title 中来判断，这里需要中最长公共子串。
     *
     * @param document
     * @return
     */
    private String extractByHtagAndTitle(Document document) {
        Elements hTagTextsList = Xsoup.select(document, TITLE_HTAG_XPATH).getElements();
        String titleText = document.select("title").first().text();
        String newsTitle = "";
        for (Element hTagText : hTagTextsList) {
            String lcs = getLongestCommonSubstring(titleText, hTagText.text());
            if (lcs.length() > newsTitle.length()) {
                newsTitle = lcs;
            }
        }
        return newsTitle.length() > 4 ? newsTitle : "";
    }

    private String getLongestCommonSubstring(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        int maxLen = 0;
        int endIndex = 0;
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    if (dp[i][j] > maxLen) {
                        maxLen = dp[i][j];
                        endIndex = i;
                    }
                }
            }
        }
        return s1.substring(endIndex - maxLen, endIndex);
    }

    public String extract(Document document, String titleXpath) {
        String titleByXpath = extractByXPath(document, titleXpath);
        if (StringUtils.isNotEmpty(titleByXpath)) {
            return titleXpath.trim();
        }

        String titleByHtagAndTitle = extractByHtagAndTitle(document);
        if (StringUtils.isNotEmpty(titleByHtagAndTitle)) {
            return titleByHtagAndTitle.trim();
        }

        String titleByTitle = extractByTitle(document);
        if (StringUtils.isNotEmpty(titleByTitle)) {
            return titleByTitle.trim();
        }

        String titleByHtag = extractByHtag(document);
        if (StringUtils.isNotEmpty(titleByHtag)) {
            return titleByHtag.trim();
        }

        return "";
    }
}
