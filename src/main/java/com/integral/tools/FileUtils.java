package com.integral.tools;

import java.io.File;

/**
 * Created by xiongzicheng on 2016/11/28.
 */
public class FileUtils {

    static String tmpDir = System.getProperty("java.io.tmpdir");
    static String separator = System.getProperty("file.separator");

    public static File getTmpFile(String fileName) {
        return new File(String.format("%s%s%s", tmpDir, separator, fileName));
    }
}
