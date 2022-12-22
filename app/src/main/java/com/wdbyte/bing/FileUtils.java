package com.wdbyte.bing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件操作工具类
 *
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class FileUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String ARCHIVE_GITHUB_URL = "./%s%s.md";

    private static IRegion region;

    public static void updateRegion(IRegion region) {
        FileUtils.region = region;
    }

    public static Set<Image> readFromNet() throws IOException {
        JsonNode jsonNode = MAPPER.readTree(new URL(region.getURL()));
        String imagesNode = jsonNode.get("images").toString();
        TypeReference<Image[]> typeReference = new TypeReference<Image[]>() {
        };
        Image[] images = MAPPER.readValue(imagesNode, typeReference);

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
        return imageSet;
    }

    /**
     * 读取 sources_zh-CN.txt
     *
     * @return
     * @throws IOException
     */
    public static Collection<Image> readFromSource() throws IOException {
        Path bingPath = region.getBingPath();
        if (!Files.exists(bingPath)) {
            Files.createFile(bingPath);
            return Collections.emptySet();
        }
        return Files.readAllLines(bingPath)
                .stream()
                .filter(s -> !s.isEmpty())
                .filter(s -> !s.startsWith("#"))
                .map(Image::fromSourceByLine).collect(Collectors.toSet());
    }

    /**
     * 写入 sources_zh-CN.txt
     *
     * @param imgList
     * @throws IOException
     */
    public static void write2Source(Collection<Image> imgList) throws IOException {
        Path bingPath = region.getBingPath();
        Files.deleteIfExists(bingPath);
        Files.createFile(bingPath);

        for (Image images : imgList) {
            Files.write(bingPath, images.sourceFormat().getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * 写入 README.md
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeReadme(Collection<Image> imgList) throws IOException {
        Path readmePath = region.getReadmePath();
        Files.deleteIfExists(readmePath);
        Files.createFile(readmePath);

        Iterator<Image> iterator = imgList.iterator();
        Image current = iterator.next();

        // 写入当天
        Files.write(readmePath, current.largeImg(region.getCurrentDayFormat(), region.getTitleFormat(), region.getCopyrightFormat()).getBytes(), StandardOpenOption.APPEND);

        List<Image> currentMonthImageList = new ArrayList<>(31);
        String firstImageEndDate = current.getEndDateStr().substring(0, 7);
        while (iterator.hasNext()) {
            Image next = iterator.next();
            currentMonthImageList.add(current);
            if (!Objects.equals(firstImageEndDate, next.getEndDateStr().substring(0, 7))) {
                break;
            }
            current = next;
        }
        // 写入当月
        writeFile(readmePath, currentMonthImageList);

        // 归档
        Files.write(readmePath, (System.lineSeparator() + region.getArchivesText() + System.lineSeparator() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);

        List<String> dateList = imgList.stream()
                .map(Image::getEndDateStr)
                .map(date -> date.substring(0, 7))
                .distinct()
                .collect(Collectors.toList());

        int i = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (String date : dateList) {
            if (i % 8 != 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append("[");
            stringBuilder.append(date);
            stringBuilder.append("](");
            stringBuilder.append(String.format(ARCHIVE_GITHUB_URL, region.getMonthPathString(), date));
            stringBuilder.append(") |");
            if (i % 8 == 7) {
                stringBuilder.append(System.lineSeparator());
            }
            i++;
        }
        if (i % 8 != 0) {
            stringBuilder.append(System.lineSeparator());
        }
        Files.write(readmePath, stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    }

    /**
     * 按月份写入图片信息
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeMonthInfo(Collection<Image> imgList) throws IOException {
        Path monthPath = region.getMonthPath();
        Map<String, List<Image>> monthMap = new HashMap<>();
        for (Image images : imgList) {
            String key = images.getEndDateStr().substring(0, 7);
            List<Image> list;
            if (monthMap.containsKey(key)) {
                list = monthMap.get(key);
            } else {
                list = new ArrayList<>(31);
                monthMap.put(key, list);
            }
            list.add(images);
        }

        if (!Files.exists(monthPath)) {
            Files.createDirectories(monthPath);
        }

        for (String monthName : monthMap.keySet()) {
            Path path = monthPath.resolve(monthName + ".md");

            Files.deleteIfExists(path);
            Files.createFile(path);

            writeFile(path, monthMap.get(monthName), monthName);
        }
    }

    private static void writeFile(Path path, List<Image> imagesList, String name) throws IOException {
        if (!imagesList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(300);
            stringBuilder.append(region.getMarkdownHeadText());
            if (name != null) {
                stringBuilder.append("(");
                stringBuilder.append(name);
                stringBuilder.append(")");
            }
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(System.lineSeparator());

            // 需要处理 1、 2 和 多张图的情况
            stringBuilder.append("|");
            int i;
            int j = Math.min(3, imagesList.size());
            for (i = 1; i <= j; i++) {
                stringBuilder.append("  |");
            }
            stringBuilder.append(System.lineSeparator());

            // 需要处理 1、 2 和 多张图的情况
            stringBuilder.append("|");
            for (i = 1; i <= j; i++) {
                stringBuilder.append(" :----: |");
            }
            stringBuilder.append(System.lineSeparator());

            i = 0;
            for (Image images : imagesList) {
                stringBuilder.append(images.smallImg());
                // 每 3 张图进行封底+换行
                if (i % 3 == 2) {
                    stringBuilder.append("|").append(System.lineSeparator());
                }
                i++;
            }
            // 最后一张图如果不是最后一列表示从来没有进行过封底+换行则 换之
            if (i % 3 != 0) {
                stringBuilder.append("|").append(System.lineSeparator());
            }
            Files.write(path, stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
        }
    }
    
    private static void writeFile(Path path, List<Image> imagesList) throws IOException {
        writeFile(path, imagesList, region.getCurrentMonthText());
    }

}
