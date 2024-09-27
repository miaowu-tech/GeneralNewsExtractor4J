package tech.miaowu.spider.extractor.news;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author anselwang
 * @since v0.1.0
 */
public class MetaExtractor {
    public Map<String, String> extract(Document element) {
        Map<String, String> metaContent = new HashMap<>();
        Elements metaList = Xsoup.select(element, "//meta").getElements();
        for (Element meta : metaList) {
            String name = Xsoup.select(meta, "@name").get();
            if (name.isEmpty()) {
                String property = Xsoup.select(meta, "@property").get();
                if (property.isEmpty()) {
                    continue;
                } else {
                    name = property;
                }
            }

            String content = Xsoup.compile("@content").evaluate(meta).get();
            if (content.isEmpty()) {
                continue;
            }
            metaContent.put(name, content);
        }
        return metaContent;
    }

    public static void main(String[] args) {
        String html = "<html><head><meta name='description' content='Example description'><meta property='og:title' content='Example title'/></head><body></body></html>";
        Document document = Jsoup.parse(html);
        MetaExtractor metaExtractor = new MetaExtractor();
        Map<String, String> metaContent = metaExtractor.extract(document);
        System.out.println(metaContent);
    }
}
