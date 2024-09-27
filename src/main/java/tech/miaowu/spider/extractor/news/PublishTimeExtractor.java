package tech.miaowu.spider.extractor.news;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anselwang
 * @since v0.1.0
 */
public class PublishTimeExtractor {
    private final static List<String> PUBLISH_TIME_META = new ArrayList<>();

    static {
        PUBLISH_TIME_META.addAll(List.of(
        "//meta[starts-with(@property, \"rnews:datePublished\")]/@content",
                "//meta[starts-with(@property, \"article:published_time\")]/@content",
                "//meta[starts-with(@property, \"og:published_time\")]/@content",
                "//meta[starts-with(@property, \"og:release_date\")]/@content",
                "//meta[starts-with(@itemprop, \"datePublished\")]/@content",
                "//meta[starts-with(@itemprop, \"dateUpdate\")]/@content",
                "//meta[starts-with(@name, \"OriginalPublicationDate\")]/@content",
                "//meta[starts-with(@name, \"article_date_original\")]/@content",
                "//meta[starts-with(@name, \"og:time\")]/@content",
                "//meta[starts-with(@name, \"apub:time\")]/@content",
                "//meta[starts-with(@name, \"publication_date\")]/@content",
                "//meta[starts-with(@name, \"sailthru.date\")]/@content",
                "//meta[starts-with(@name, \"PublishDate\")]/@content",
                "//meta[starts-with(@name, \"publishdate\")]/@content",
                "//meta[starts-with(@name, \"PubDate\")]/@content",
                "//meta[starts-with(@name, \"pubtime\")]/@content",
                "//meta[starts-with(@name, \"_pubtime\")]/@content",
                "//meta[starts-with(@name, \"weibo: article:create_at\")]/@content",
                "//meta[starts-with(@pubdate, \"pubdate\")]/@content"
        ));
    }

    private String extractFromUserXpath(Document document, String publishTimeXpath) {
        if (publishTimeXpath != null && !publishTimeXpath.isEmpty()) {
            Elements elements = document.select(publishTimeXpath);
            if (!elements.isEmpty()) {
                return elements.first().text();
            }
        }
        return "";
    }

    private String extractFromMeta(Document document) {
        for (String xpath : PUBLISH_TIME_META) {
            String publishTimeFromMeta = Xsoup.compile(xpath).evaluate(document).get();
            if (StringUtils.isNotEmpty(publishTimeFromMeta)) {
                return publishTimeFromMeta;
            }
        }
        return "";
    }

    public String extract(Document document, String publishTimeXpath) {
        String publishTimeFromXpath = extractFromUserXpath(document, publishTimeXpath);
        if (StringUtils.isNotEmpty(publishTimeFromXpath)) {
            return publishTimeFromXpath.trim();
        }

        String publishTimeFromMeta = extractFromMeta(document);
        if (StringUtils.isNotEmpty(publishTimeFromMeta)) {
            return publishTimeFromMeta.trim();
        }

        return "";
    }
}
