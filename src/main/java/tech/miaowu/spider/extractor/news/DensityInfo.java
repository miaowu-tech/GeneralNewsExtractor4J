package tech.miaowu.spider.extractor.news;

/**
 *
 * @author anselwang
 * @since v0.1.0
 */
public class DensityInfo {
    private double density;
    /**
     * 节点 i 的字符串字数
     */
    private int ti;

    /**
     * 节点 i 的带链接的字符串字数
     */
    private int lti;

    /**
     * 节点 i 的标签数
     */
    private int tgi;

    /**
     * 节点 i 的带连接的标签数
     */
    private int ltgi;

    /**
     * 节点 i 的 p 标签数
     */
    private int pi;

    private String tiText;

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public int getTi() {
        return ti;
    }

    public void setTi(int ti) {
        this.ti = ti;
    }

    public int getLti() {
        return lti;
    }

    public void setLti(int lti) {
        this.lti = lti;
    }

    public int getTgi() {
        return tgi;
    }

    public void setTgi(int tgi) {
        this.tgi = tgi;
    }

    public int getLtgi() {
        return ltgi;
    }

    public void setLtgi(int ltgi) {
        this.ltgi = ltgi;
    }

    public int getPi() {
        return pi;
    }

    public void setPi(int pi) {
        this.pi = pi;
    }

    public String getTiText() {
        return tiText;
    }

    public void setTiText(String tiText) {
        this.tiText = tiText;
    }
}
