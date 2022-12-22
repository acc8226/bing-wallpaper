package com.wdbyte.bing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author niujinpeng
 * @date 2021/02/08
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image implements Comparable<Image> {

    private static final String BASE_URL_PREFIX = "https://cn.bing.com";

    @JsonProperty("startdate")
    private String endDateStr;
    private String url;
    private String title;
    private String copyright;

    public static Image fromSourceByLine(String line) {
        String[] split = line.split("\\|");
        return new Image(split[0], split[1], split[2], split[3]);
    }

    public void appendPath(String path) {
        this.url = BASE_URL_PREFIX + path;
    }

    /**
     * 用于写入 sources_zh-CN.txt 的单行格式
     *
     * @return
     */
    public String sourceFormat() {
        return String.format("%s|%s|%s|%s" + System.lineSeparator(), endDateStr, url, title, copyright);
    }

    /**
     * README 首页大图的排版
     */
    public String largeImg(String todayFormat, String titleFormat, String copyrightFormat) {
        String smallUrl = url + "&w=1000";
        return String.format("![%s](%s)"
                        + System.lineSeparator() + System.lineSeparator()
                        + "%s%s | %s%s | %s%s [download 4k](%s)"
                        + System.lineSeparator() + System.lineSeparator(),
                title, smallUrl,
                todayFormat, endDateStr, titleFormat, title, copyrightFormat, copyright, url);
    }

    /**
     * README 和 归档文件 小图的排版
     */
    public String smallImg() {
        String smallUrl = url + "&pid=hp&w=384&h=216&rs=1&c=4";
        return String.format("| ![%s](%s) <br/>%s [download 4k](%s)", title, smallUrl, endDateStr, url);
    }

    public String getCopyright() {
        return copyright;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Image() {
    }

    public Image(String endDate, String url, String title, String copyright) {
        this.endDateStr = endDate;
        this.url = url;
        this.title = title;
        this.copyright = copyright;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Image image = (Image) o;
        return Objects.equals(endDateStr, image.endDateStr);
    }

    @Override
    public int hashCode() {
        return endDateStr != null ? endDateStr.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", copyright='" + copyright + '\'' +
                '}';
    }

    @Override
    public int compareTo(Image image) {
        return image.getEndDateStr().compareTo(this.getEndDateStr());
    }

}
