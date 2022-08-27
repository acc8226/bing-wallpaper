package com.wdbyte.bing;

import java.nio.file.Path;

public interface IRegion {

    // BING API format 指定返回 js，idx表示起始页，n表示条数（目前最多一次性返回 8 条）
    String BASE_BING_API = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=10&nc=1612409408851&pid=hp&FORM=BEHPTB&uhd=1&uhdwidth=3840&uhdheight=2160";

    String getURL();

    Path getBingPath();

    Path getReadmePath();

    String getMonthPathString();

    Path getMonthPath();

    String getMarkdownHeadText();

    String getCurrentDayFormat();

    String getTitleFormat();

    String getCopyrightFormat();

    String getCurrentMonthText();

    String getArchivesText();

}
