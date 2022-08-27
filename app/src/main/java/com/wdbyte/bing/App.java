package com.wdbyte.bing;

import java.io.IOException;
import java.util.Collection;

/**
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class App {

    public static void main(String[] args) throws IOException {
        write2File(new LocalRegion());
    }

    private static void write2File(IRegion region) throws IOException {
        FileUtils.updateRegion(region);

        Collection<Image> imageCollection = FileUtils.readFromNet();
        imageCollection.addAll(FileUtils.readFromSource());

        FileUtils.write2Source(imageCollection);
        FileUtils.writeReadme(imageCollection);
        FileUtils.writeMonthInfo(imageCollection);
    }

}
