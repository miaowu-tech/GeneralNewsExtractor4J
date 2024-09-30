package tech.miaowu.spider.extractor.news;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 *
 * @author anselwang
 * @since v0.1.0
 */
public class GeneralNewsExtractor {
    public NewsInfo extract(String rawHtml) {
        Document document = Jsoup.parse(rawHtml);

        TitleExtractor titleExtractor = new TitleExtractor();
        String title = titleExtractor.extract(document, "");

        ContentExtractor contentExtractor = new ContentExtractor();
        NodeInfo nodeInfo = contentExtractor.extract(document, "", "");

        PublishTimeExtractor publishTimeExtractor = new PublishTimeExtractor();
        String publishTime = publishTimeExtractor.extract(document, "");

        AuthorExtractor authorExtractor = new AuthorExtractor();
        String author = authorExtractor.extract(document, "");

        MetaExtractor metaExtractor = new MetaExtractor();
        Map<String, String> metaMap = metaExtractor.extract(document);

        NewsInfo newsInfo = new NewsInfo();
        newsInfo.setTitle(title);
        newsInfo.setAuthor(author);
        newsInfo.setMetaMap(metaMap);
        newsInfo.setPublishTime(publishTime);
        if (nodeInfo != null) {
            newsInfo.setContent(nodeInfo.getDensityInfo().getTiText());
            newsInfo.setImages(nodeInfo.getImageList());
        }

        return newsInfo;
    }
}
