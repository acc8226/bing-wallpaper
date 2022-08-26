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

    private static final Path BING_PATH = Paths.get("../sources.txt");
    private static final Path README_PATH = Paths.get("../README.md");
    private static final Path MONTH_PATH = Paths.get("../archives/");

    private static final String ARCHIVE_LOCAL_URL = "https://gitee.com/kaiLee/bing-wallpaper/blob/main/archives/%s.md";
    private static final String ARCHIVE_GITHUB_URL = "https://github.com/acc8226/bing-wallpaper/tree/main/archives/%s.md";

    /**
     * 读取 sources.txt
     *
     * @return
     * @throws IOException
     */
    public static Collection<Image> readFromSource() throws IOException {
        if (!Files.exists(BING_PATH)) {
            Files.createFile(BING_PATH);
        }
        return Files.readAllLines(BING_PATH)
                .stream()
                .filter(s -> !s.isEmpty())
                .filter(s -> !s.startsWith("#"))
                .map(Image::fromSourceByLine).collect(Collectors.toSet());
    }

    /**
     * 写入 sources.txt
     *
     * @param imgList
     * @throws IOException
     */
    public static void write2Source(Collection<Image> imgList) throws IOException {
        Files.deleteIfExists(BING_PATH);
        Files.createFile(BING_PATH);

        for (Image images : imgList) {
            Files.write(BING_PATH, images.sourceFormat().getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * 写入 README.md
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeReadme(Collection<Image> imgList) throws IOException {
        Files.deleteIfExists(README_PATH);
        Files.createFile(README_PATH);

        Iterator<Image> iterator = imgList.iterator();
        Image current = iterator.next();
        Files.write(README_PATH, (current.largeImg()).getBytes(), StandardOpenOption.APPEND);

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
        writeFile(README_PATH, currentMonthImageList.subList(1, currentMonthImageList.size()));

        // 归档
        Files.write(README_PATH, (System.lineSeparator() + "## 历史归档" + System.lineSeparator() + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);

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
            stringBuilder.append(String.format(ARCHIVE_GITHUB_URL, date));
            stringBuilder.append(") |");
            if (i % 8 == 7) {
                stringBuilder.append(System.lineSeparator());
            }
            i++;
        }
        if (i % 8 != 0) {
            stringBuilder.append(System.lineSeparator());
        }
        Files.write(README_PATH, stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    }

    /**
     * 按月份写入图片信息
     *
     * @param imgList
     * @throws IOException
     */
    public static void writeMonthInfo(Collection<Image> imgList) throws IOException {
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

        if (!Files.exists(MONTH_PATH)) {
            Files.createDirectories(MONTH_PATH);
        }

        for (String monthName : monthMap.keySet()) {
            Path path = MONTH_PATH.resolve(monthName + ".md");

            Files.deleteIfExists(path);
            Files.createFile(path);

            writeFile(path, monthMap.get(monthName), monthName);
        }
    }

    private static void writeFile(Path path, List<Image> imagesList, String name) throws IOException {
        if (!imagesList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(500);
            stringBuilder.append("## Bing Wallpaper");
            if (name != null) {
                stringBuilder.append("(");
                stringBuilder.append(name);
                stringBuilder.append(")");
            }
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(System.lineSeparator());

            stringBuilder.append("|  |  |  |");
            stringBuilder.append(System.lineSeparator());

            stringBuilder.append("| :----: | :----: | :----: |");
            stringBuilder.append(System.lineSeparator());

            int i = 0;
            for (Image images : imagesList) {
                stringBuilder.append(images.smallImg());
                // 每 3 张图换行
                if (i % 3 == 2) {
                    stringBuilder.append("|");
                    stringBuilder.append(System.lineSeparator());
                }
                i++;
            }
            // 最后一张图如果不是最后一列则进行封底
            if (i % 3 != 0) {
                stringBuilder.append("|");
                stringBuilder.append(System.lineSeparator());
            }
            Files.write(path, stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
        }
    }

    private static void writeFile(Path path, List<Image> imagesList) throws IOException {
        writeFile(path, imagesList, "当月");
    }

}
