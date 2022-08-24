package com.wdbyte.bing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class Wallpaper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // BING API
    private static final String BING_API = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=10&nc=1612409408851&pid=hp&FORM=BEHPTB&uhd=1&uhdwidth=3840&uhdheight=2160";

    public static void main(String[] args) throws IOException {
        JsonNode jsonNode = MAPPER.readTree(new URL(BING_API));
        String imagesNode = jsonNode.get("images").toString();
        List<Image> images = MAPPER.readValue(imagesNode, new TypeReference<List<Image>>() {});
        Set<Image> imageSet = new TreeSet<>();
        for (Image image : images) {
            // 图片时间
            String endDate = image.getEndDateStr();
            LocalDate localDate = LocalDate.parse(endDate, DateTimeFormatter.BASIC_ISO_DATE);
            image.setEndDateStr(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

            // 图片地址
            String path = image.getUrl();
            if (path.contains("&")) {
                path = path.substring(0, path.indexOf("&"));
            }
            image.appendPath(path);

            imageSet.add(image);
        }
        imageSet.addAll(FileUtils.readFromSource());

        FileUtils.write2Source(imageSet);
        FileUtils.writeReadme(imageSet);
        FileUtils.writeMonthInfo(imageSet);
    }

}
