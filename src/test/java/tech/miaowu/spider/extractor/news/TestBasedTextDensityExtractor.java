package tech.miaowu.spider.extractor.news;

import cn.hutool.json.JSONUtil;

/**
 *
 * @author anselwang
 * @since v0.1.0
 */
public class TestBasedTextDensityExtractor {

    public static void main(String[] args) {
        String rawText = "<htm><head><title>Hello GNE</title></head><body><div id=\"content\">Hello, GNE! This is a paragraph.</div></body></html>";
        GeneralNewsExtractor generalNewsExtractor = new GeneralNewsExtractor();
        NewsInfo newsInfo = generalNewsExtractor.extract(rawText);
        System.out.println(JSONUtil.parse(newsInfo));
    }

}
