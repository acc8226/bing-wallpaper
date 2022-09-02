package com.wdbyte.bing;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalRegion implements IRegion {

    private static final String BING_API = BASE_BING_API;
    private static final Path BING_PATH = Paths.get("../sources/sources.txt");
    private static final Path README_PATH = Paths.get("../README.md");
    private static final String MONTH_PATH_STRING = "archives/";
    private static final Path MONTH_PATH = Paths.get( "../" + MONTH_PATH_STRING);

    @Override
    public String getURL() {
        return BING_API;
    }

    @Override
    public Path getBingPath() {
        return BING_PATH;
    }

    @Override
    public Path getReadmePath() {
        return README_PATH;
    }

    @Override
    public String getMonthPathString() {
        return MONTH_PATH_STRING;
    }

    @Override
    public Path getMonthPath() {
        return MONTH_PATH;
    }

    @Override
    public String getMarkdownHeadText() {
        return "## bing wallpaper";
    }

    @Override
    public String getCurrentDayFormat() {
        return "Today: ";
    }

    @Override
    public String getTitleFormat() {
        return "title: ";
    }

    @Override
    public String getCopyrightFormat() {
        return "copyright：";
    }

    @Override
    public String getCurrentMonthText() {
        return "current month";
    }

    @Override
    public String getArchivesText() {
        return "## archives";
    }

}
