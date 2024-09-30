package tech.miaowu.spider.extractor.news;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author anselwang
 * @since v0.1.0
 */
public class AuthorExtractor {
    private final static List<String> AUTHOR_PATTERN = new ArrayList<>();

    private final static List<String> AUTHOR_META = new ArrayList<>();

    static {
        AUTHOR_PATTERN.addAll(List.of(
                "责编[：|:| |丨|/]\\s*([\\u4E00-\\u9FA5a-zA-Z]{2,20})[^\\u4E00-\\u9FA5|:|：]",
                "责任编辑[：|:| |丨|/]\\s*([\\u4E00-\\u9FA5a-zA-Z]{2,20})[^\\u4E00-\\u9FA5|:|：]",
                "作者[：|:| |丨|/]\\s*([\\u4E00-\\u9FA5a-zA-Z]{2,20})[^\\u4E00-\\u9FA5|:|：]",
                "编辑[：|:| |丨|/]\\s*([\\u4E00-\\u9FA5a-zA-Z]{2,20})[^\\u4E00-\\u9FA5|:|：]",
                //"文[：|:| |丨|/]\\s*([\\u4E00-\\u9FA5a-zA-Z]{2,20})[^\\u4E00-\\u9FA5|:|：]",
                "原创[：|:| |丨|/]\\s*([\\u4E00-\\u9FA5a-zA-Z]{2,20})[^\\u4E00-\\u9FA5|:|：]",
                "撰文[：|:| |丨|/]\\s*([\\u4E00-\\u9FA5a-zA-Z]{2,20})[^\\u4E00-\\u9FA5|:|：]",
                "来源[：|:| |丨|/]\\s*([\\u4E00-\\u9FA5a-zA-Z]{2,20})[^\\u4E00-\\u9FA5|:|：|<]"
           /*
                # 以下正则表达式需要进一步测试
                # '(作者[：|:| |丨|/]\s*[\\u4E00-\\u9FA5a-zA-Z、 ]{2,20})[）】)]]?[^\\u4E00-\\u9FA5|:|：]',
                # '(记者[：|:| |丨|/]\s*[\\u4E00-\\u9FA5a-zA-Z、 ]{2,20})[）】)]]?[^\\u4E00-\\u9FA5|:|：]',
                # '(原创[：|:| |丨|/]\s*[\\u4E00-\\u9FA5a-zA-Z、 ]{2,20})[）】)]]?[^\\u4E00-\\u9FA5|:|：]',
                # '(撰文[：|:| |丨|/]\s*[\\u4E00-\\u9FA5a-zA-Z、 ]{2,20})[）】)]]?[^\\u4E00-\\u9FA5|:|：]',
                # '(文/图[：|:| |丨|/]?\s*[\\u4E00-\\u9FA5a-zA-Z、 ]{2,20})[）】)]]?[^\\u4E00-\\u9FA5|:|：]',
            */
        ));

        AUTHOR_META.addAll(List.of(
                "//meta[starts-with(@property, \"author\")]/@content",
                "//meta[starts-with(@property, \"article:author\")]/@content",
                "//meta[starts-with(@name, \"author\")]/@content",
                "//meta[starts-with(@name, \"article:author\")]/@content"
        ));
    }

    public String extractAuthor(Element element, String authorXpath) {
        Elements authorElements = Xsoup.select(element, authorXpath).getElements();
        if (!authorElements.isEmpty()) {
            return authorElements.first().text();
        }
        return "";
    }

    private String extractAuthorFromText(Document document) {
        String text = document.getAllElements().text();
        for (String patternStr : AUTHOR_PATTERN) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }

    private String extractFromMeta(Document document) {
        for (String xpath : AUTHOR_META) {
            String authorFromMeta = Xsoup.compile(xpath).evaluate(document).get();
            if (StringUtils.isNotEmpty(authorFromMeta)) {
                return authorFromMeta;
            }
        }
        return "";
    }

    public String extract(Document document, String authorXpath) {
        if (StringUtils.isNotEmpty(authorXpath)) {
            String authorByXpath = extractAuthor(document, authorXpath);
            if (StringUtils.isNotEmpty(authorByXpath)) {
                return authorByXpath.trim();
            }
        }

        String authorFromText = extractAuthorFromText(document);
        if (StringUtils.isNotEmpty(authorFromText)) {
            return authorFromText.trim();
        }

        String authorFroMeta = extractFromMeta(document);
        if (StringUtils.isNotEmpty(authorFroMeta)) {
            return authorFroMeta.trim();
        }

        return "";
    }


}
