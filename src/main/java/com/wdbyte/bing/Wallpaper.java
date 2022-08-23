package com.wdbyte.bing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class Wallpaper {

    // BING API
    private static final String BING_API = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=10&nc=1612409408851&pid=hp&FORM=BEHPTB&uhd=1&uhdwidth=3840&uhdheight=2160";

    private static final String BING_URL = "https://cn.bing.com";

    public static void main(String[] args) throws IOException {
        String httpContent = HttpUtils.getHttpContent(BING_API);
        JSONObject jsonObject = JSON.parseObject(httpContent);
        JSONArray jsonArray = jsonObject.getJSONArray("images");

        jsonObject = (JSONObject)jsonArray.get(0);

        // 图片版权
        String copyright = (String)jsonObject.get("copyright");

        // 图片时间
        String endDate = (String)jsonObject.get("enddate");
        LocalDate localDate = LocalDate.parse(endDate, DateTimeFormatter.BASIC_ISO_DATE);
        endDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 图片地址
        String url = BING_URL + jsonObject.get("url");
        if (url.contains("&")) {
            url = url.substring(0, url.indexOf("&"));
        }

        List<Image> imagesList = FileUtils.readBing();
        if (imagesList.isEmpty()) {
            imagesList = Collections.singletonList(new Image(copyright, endDate, url));
        } else {
            imagesList.set(0, new Image(copyright, endDate, url));
            // 去重和排序
            imagesList = imagesList.stream().distinct().sorted().collect(Collectors.toList());
        }

        FileUtils.writeBing(imagesList);
        FileUtils.writeReadme(imagesList);
        FileUtils.writeMonthInfo(imagesList);
    }

}
