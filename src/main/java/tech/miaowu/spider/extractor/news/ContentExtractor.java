package tech.miaowu.spider.extractor.news;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @author anselwang
 * @since v0.1.0
 */
public class ContentExtractor {
    private final static String HIGH_WEIGHT_ARRT_KEYWORD = "content|article|news_txt|pages_content|post_text";

    private final static Pattern HIGH_WEIGHT_KEYWORD_PATTERN = Pattern.compile(HIGH_WEIGHT_ARRT_KEYWORD);

    private final Map<String, List<String>> elementCache;

    public ContentExtractor() {
        elementCache = new HashMap<>();
    }

    /**
     * 常见的中英文标点符号
     */
    private final static Set<Character> punctuation = new HashSet<>(Arrays.asList('！', '，', '。', '？', '、', '；', '：',
            '“', '”', '‘', '’', '《', '》', '%','（','）',
            ',','.','?',':',';','\'','"','!','%','(',')'));

    public NodeInfo extract(Document document, String host, String bodyXpath) {
        Element body = bodyXpath.isEmpty() ? document.body() : document.selectFirst(bodyXpath);

        if (body == null) {
            return null;
        }

        List<NodeInfo> nodeInfoList = new ArrayList<>();
        List<Element> nodes = expandNode(body);
        int i = 0;
        for (Element node : nodes) {
            DensityInfo textDensityInfo = calculateTextDensity(node);
            int ti = textDensityInfo.getTi();
            int lti = textDensityInfo.getLti();
            String tiText = textDensityInfo.getTiText();
/*            int textTagCount = countTextTags(node, "p");*/
            int textTagCount = textDensityInfo.getPi();
            double sbdi = calculateSbdi(tiText, ti, lti);

            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo.setDensityInfo(textDensityInfo);
            nodeInfo.setElement(node);
            nodeInfo.setImageList(getImages(node, host));
            nodeInfo.setTextTagCount(textTagCount);
            nodeInfo.setSbdi(sbdi);
            nodeInfo.setBodyHtml(node.outerHtml());
            nodeInfoList.add(nodeInfo);
        }

        calculateScore(nodeInfoList);
        nodeInfoList.sort(Comparator.comparingDouble(NodeInfo::getScore).reversed());

        if (!nodeInfoList.isEmpty()) {
            return nodeInfoList.get(0);
        }

        return null;
    }

    /**
     * 根据公式：
     *
     *          Ti - LTi
     *  TDi = -----------
     *         TGi - LTGi
     *
     * Ti:节点 i 的字符串字数
     * LTi：节点 i 的带链接的字符串字数
     * TGi：节点 i 的标签数
     * LTGi：节点 i 的带连接的标签数
     *
     * @param element
     * @return
     */
    private DensityInfo calculateTextDensity(Element element) {
        List<String> allTextList = getAllTextOfElement(Collections.singletonList(element));
        String tiText = StringUtils.join(allTextList, "\n");
        int ti = tiText.length();
        ti = increaseTagWeight(ti, element);

        Elements linkTagList = element.getElementsByTag("a");
        List<String> allLinkTagTextList = getAllTextOfElement(linkTagList);
        String ltiText = StringUtils.join(allLinkTagTextList, "\n");
        int lti = ltiText.length();

        Elements pTagList = element.getElementsByTag("p");
        int pi = exceptEmptyTextTag(pTagList, true);

        Elements elements = Xsoup.select(element, "//*").getElements();
        int tgi = exceptEmptyTextTag(elements, true);
        int ltgi = linkTagList.size();

        DensityInfo densityInfo = new DensityInfo();
        densityInfo.setTiText(tiText);
        densityInfo.setTi(ti - lti);
        densityInfo.setTgi(tgi);
        densityInfo.setLti(lti);
        densityInfo.setPi(pi);
        densityInfo.setLtgi(ltgi);
        if (tgi - ltgi == 0) {
            if (needSkipLtgi(ti, pi)) {
                densityInfo.setDensity(0);
            } else {
                ltgi = 0;
            }
        }

        double density = (ti - lti) / (double) (tgi - ltgi);
        if (Double.isInfinite(density)) {
            density = 0;
        }
        densityInfo.setDensity(density);

        return densityInfo;
    }

    private List<String> getImages(Element node, String host) {
        Elements images = node.select("img");
        List<String> imageUrls = new ArrayList<>();
        for (Element img : images) {
            String src = img.attr("src");
            if (host != null && !src.startsWith("http")) {
                src = host + src;
            }
            imageUrls.add(src);
        }
        return imageUrls;
    }

    /**
     * 去除p和span无文字内容的标签
     * @param elements
     * @return
     */
    private int exceptEmptyTextTag(Elements elements, boolean needRemoveEmpty) {
        int tagCount = 0;
        if (needRemoveEmpty) {
            for (Element element1 : elements) {
                if (element1.tagName().equals("p") || element1.tagName().equals("span")) {
                    if (element1.text().isEmpty()) {
                        continue;
                    }
                }
                tagCount++;
            }
        } else {
            tagCount = elements.size();
        }
        return tagCount;
    }

    /**
     *            Ti - LTi
     *  SbDi = --------------
     *            Sbi + 1
     *
     *  SbDi: 符号密度
     *  Sbi：符号数量
     *
     * @return
     * @param text
     * @param ti
     * @param lti
     * @return
     */
    private double calculateSbdi(String text, int ti, int lti) {
        int sbi = countPunctuation(text);
        double sbdi = (ti - lti) / (double) (sbi + 1);
        if (sbdi == 0) {
            return 1;
        }
        return sbdi;
    }

    private int countPunctuation(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (punctuation.contains(c)) {
                count++;
            }
        }
        return count;
    }

    private static List<Element> expandNode(Element element) {
        List<Element> elements = new ArrayList<>();
        expandNodes(element, elements);
        return elements;
    }

    private static void expandNodes(Element element, List<Element> elements) {
        if (!element.tag().getName().equals("script") && !element.tag().getName().equals("style")) {
            elements.add(element);
        }
        for (Element child : element.children()) {
            expandNodes(child, elements);
        }
    }

    private List<String> getAllTextOfElement(List<Element> elements) {
        List<String> textList = new ArrayList<>();
        for (Element element : elements) {
            String hash = EncryptUtils.md5(element.outerHtml());
            if (elementCache.containsKey(hash)) {
                textList.addAll(elementCache.get(hash));
            } else {
                List<String> oneElementTextList = new ArrayList<>();
                getTextOfElement(element, oneElementTextList);
                elementCache.put(hash, oneElementTextList);
                textList.addAll(oneElementTextList);
            }
        }
        return textList;
    }

    private void getTextOfElement(Element element, List<String> elementTextList) {
        List<String> currentElementTextList = new ArrayList<>();
        List<TextNode> texts = element.textNodes();
        for (TextNode textElement : texts) {
            String text = textElement.text().trim();
            if (!text.isEmpty()) {
                text = text.replaceAll("\\s", "")
                           .replaceAll("\\u3000", "");
                text = text.replace('\n', ' ');
                currentElementTextList.add(text);
            }
        }
        if (!currentElementTextList.isEmpty()) {
            elementTextList.addAll(currentElementTextList);
        }

        Elements subElements = element.children();
        if (!subElements.isEmpty()) {
            for (Element subElement : subElements) {
                getTextOfElement(subElement, elementTextList);
            }
        }
    }

    private int increaseTagWeight(int ti, Element element) {
        String tagClass = element.attr("class");
        if (HIGH_WEIGHT_KEYWORD_PATTERN.matcher(tagClass).find()) {
            return 2 * ti;
        }
        return ti;
    }

    /**
     * 有时候，会出现像维基百科一样，在文字里面加a 标签关键词的情况，例如：
     * <div>
     * 我是正文我是正文我是正文<a href="xxx">关键词1</a>我是正文我是正文我是正文我是正文
     * 我是正文我是正文我是正文我是正文我是正文<a href="xxx">关键词2</a>我是正文我是正文
     * 我是正文
     * </div>
     *
     * 在这种情况下，tgi = ltgi = 2，计算公式的分母为0. 为了把这种情况和列表页全是链接的
     * 情况区分出来，所以要做一下判断。检查节点下面所有 a 标签的超链接中的文本数量与本节点
     * 下面所有文本数量的比值。如果超链接的文本数量占比极少，那么此时，ltgi 应该忽略
     * @param ti: 节点 i 的字符串字数
     * @param lti: 节点 i 的带链接的字符串字数
     * @return boolean
     */
    private boolean needSkipLtgi(int ti, int lti) {
         if (lti == 0) {
             return false;
         }

        //文的字符数量是链接字符数量的十倍以上
        return ti / lti > 10;
    }

    /**
     *  score = 1 * ndi * log10(text_tag_count + 2) * log(sbdi)
     *
     *  1：在论文里面，这里使用的是 log(std)，但是每一个密度都乘以相同的对数，他们的相对大小是不会改变的，所以我们没有必要计算
     *  ndi：节点 i 的文本密度
     *  textTagCount: 正文所在标签数。例如正文在<p></p>标签里面，这里就是 p 标签数，如果正文在<div></div>标签，这里就是 div 标签数
     *  sbdi：节点 i 的符号密度
     *  @param nodeInfoList 所以节点列表
     *  @return:
    **/
    private void calculateScore(List<NodeInfo> nodeInfoList) {
        for (NodeInfo nodeInfo : nodeInfoList) {
            double density = nodeInfo.getDensityInfo().getDensity();
            int textTagCount = nodeInfo.getTextTagCount();
            double sbdi = nodeInfo.getSbdi();
            double score = density * Math.log10(textTagCount + 2) * Math.log(sbdi);
            if (Double.isNaN(score) || Double.isInfinite(score) ) {
                score = 0;
            }
            nodeInfo.setScore(score);
        }
    }
}
