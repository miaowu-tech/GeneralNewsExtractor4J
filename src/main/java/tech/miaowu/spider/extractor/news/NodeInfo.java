package tech.miaowu.spider.extractor.news;

import org.jsoup.nodes.Element;

import java.util.List;

/**
 *
 * @author anselwang
 * @since v0.1.0
 */
public class NodeInfo {
    private DensityInfo densityInfo;
    private Element element;
    private List<String> imageList;
    private int textTagCount;
    private double sbdi;
    private String bodyHtml;
    private double score;

    public DensityInfo getDensityInfo() {
        return densityInfo;
    }

    public void setDensityInfo(DensityInfo densityInfo) {
        this.densityInfo = densityInfo;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public int getTextTagCount() {
        return textTagCount;
    }

    public void setTextTagCount(int textTagCount) {
        this.textTagCount = textTagCount;
    }

    public double getSbdi() {
        return sbdi;
    }

    public void setSbdi(double sbdi) {
        this.sbdi = sbdi;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
