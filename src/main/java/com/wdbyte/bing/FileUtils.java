package com.wdbyte.bing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    private static final Path README_PATH = Paths.get("README.md");

    private static final Path BING_PATH = Paths.get("sources.txt");

    private static final Path MONTH_PATH = Paths.get("archives/");

    /**
     * 读取 sources.txt
     *
     * @return
     * @throws IOException
     */
    public static List<Image> readBing() throws IOException {
        if (!Files.exists(BING_PATH)) {
            Files.createFile(BING_PATH);
        }
        List<String> allLines = Files.readAllLines(BING_PATH)
                .stream()
                .filter(s -> !s.isEmpty())
                .filter(s -> !s.startsWith("#"))
                .collect(Collectors.toList());

        List<Image> imageList = new ArrayList<>(allLines.size() + 1);
        for (String allLine : allLines) {
            String s = allLine.trim();
            int descEnd = s.indexOf("]");
            String desc = s.substring(14, descEnd);

            String date = s.substring(0, 10);

            int urlStart = s.lastIndexOf("(") + 1;
            String url = s.substring(urlStart, s.length() - 1);

            imageList.add(new Image(desc, date, url));
        }
        return imageList;
    }

    /**
     * 写入 sources.txt
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeBing(List<Image> imgList) throws IOException {
        Files.deleteIfExists(BING_PATH);
        Files.createFile(BING_PATH);

        for (Image images : imgList) {
            Files.write(BING_PATH, images.formatMarkdown().getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * 写入 README.md
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeReadme(List<Image> imgList) throws IOException {
        Files.deleteIfExists(README_PATH);
        Files.createFile(README_PATH);

        // 取 30 张
        List<Image> currentMonthImageList = new ArrayList<>(31);
        for (int i = 0; i < 31; i++) {
            Image current = imgList.get(i);
            currentMonthImageList.add(current);
            if (i + 1 >= imgList.size()) {
                break;
            }
            String currentDateStr = current.getDate().substring(0, 7);
            String nextDateStr = imgList.get(i + 1).getDate().substring(0, 7);
            if (!Objects.equals(currentDateStr, nextDateStr)) {
                break;
            }
        }
        Files.write(README_PATH, (currentMonthImageList.get(0).toLarge()).getBytes(), StandardOpenOption.APPEND);

        writeFile(README_PATH, currentMonthImageList.subList(1, currentMonthImageList.size()));

        // 归档
        Files.write(README_PATH, ("## 历史归档" + System.lineSeparator() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);

        List<String> dateList = imgList.stream()
                .map(Image::getDate)
                .map(date -> date.substring(0, 7))
                .distinct()
                .collect(Collectors.toList());
        int i = 0;
        for (String date : dateList) {
            String link = String.format("[%s](https://github.com/acc8226/bing-wallpaper/tree/main/archives/%s.md) | ", date, date);
            Files.write(README_PATH, link.getBytes(), StandardOpenOption.APPEND);
            i++;
            if (i % 8 == 0) {
                Files.write(README_PATH, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            }
        }
    }

    /**
     * 按月份写入图片信息
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeMonthInfo(List<Image> imgList) throws IOException {
        Map<String, List<Image>> monthMap = new HashMap<>();
        for (Image images : imgList) {
            String key = images.getDate().substring(0, 7);
            List<Image> list;
            if (monthMap.containsKey(key)) {
                list = monthMap.get(key);
            } else {
                list = new ArrayList<>(31);
                monthMap.put(key, list);
            }
            list.add(images);
        }

        if (!Files.exists(MONTH_PATH)) {
            Files.createDirectories(MONTH_PATH);
        }

        for (String monthName : monthMap.keySet()) {
            Path path = MONTH_PATH.resolve(monthName +".md");

            Files.deleteIfExists(path);
            Files.createFile(path);

            writeFile(path, monthMap.get(monthName), monthName);
        }
    }

    private static void writeFile(Path path, List<Image> imagesList, String name) throws IOException {
        if (!imagesList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(200);
            stringBuilder.append("## Bing Wallpaper");
            if (name != null) {
                stringBuilder.append("(");
                stringBuilder.append(name);
                stringBuilder.append(")");
            }
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(System.lineSeparator());
            Files.write(path, stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);

            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append("|  |  |  |");
            stringBuilder.append(System.lineSeparator());

            stringBuilder.append("| :----: | :----: | :----: |");
            stringBuilder.append(System.lineSeparator());
            Files.write(path, stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);

            int i = 0;
            for (Image images : imagesList) {
                Files.write(path, ("|" + images.smallImg()).getBytes(), StandardOpenOption.APPEND);
                // 每 3 张图换行
                if (i % 3 == 2) {
                    Files.write(path, ("|" + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
                }
                i++;
            }
            // 最后一张图如果不是最后一列则进行封底
            if (i % 3 != 0) {
                Files.write(path, "|".getBytes(), StandardOpenOption.APPEND);
            }
            Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        }
    }

    private static void writeFile(Path path, List<Image> imagesList) throws IOException {
        writeFile(path, imagesList, "当月");
    }
}
