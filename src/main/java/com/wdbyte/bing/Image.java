package com.wdbyte.bing;

import java.util.Objects;

/**
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class Image implements Comparable<Image> {
    private final String desc;
    private final String date;
    private final String url;

    /**
     * 用于写入记录文件
     * @return
     */
    public String formatMarkdown() {
        return String.format("%s | [%s](%s) " + System.lineSeparator(), date, desc, url);
    }

    /**
     * 大图
     */
    public String toLarge() {
        String smallUrl = url + "&w=1000";
        return String.format("![](%s)" + System.lineSeparator() + System.lineSeparator() + "今日 %s | [%s](%s)" + System.lineSeparator() + System.lineSeparator(), smallUrl, date, desc, url);
    }

    /**
     * 小图
     */
    public String smallImg() {
        String smallUrl = url + "&pid=hp&w=384&h=216&rs=1&c=4";
        return String.format("![](%s) %s [download 4k](%s)", smallUrl, date, url);
    }

    public String getDesc() {
        return desc;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public Image() {
        this.desc = null;
        this.date = null;
        this.url = null;
    }

    public Image(String desc, String date, String url) {
        this.desc = desc;
        this.date = date;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Image images = (Image)o;
        return Objects.equals(date, images.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return "Images{" +
                "desc='" + desc + '\'' +
                ", date='" + date + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int compareTo(Image image) {
        return image.getDate().compareTo(this.getDate());
    }
}
