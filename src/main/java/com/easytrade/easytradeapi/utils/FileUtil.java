package com.easytrade.easytradeapi.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件工具类
 *
 * @author Mo Xu
 * @date 2022/04/11
 */
public class FileUtil {
    /**
     * 上传文件
     *
     * @param file     文件
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return {@link String} 返回输出文件的目的路径
     * @throws IOException IOException
     */
    public static String uploadFile(byte[] file, String filePath, String fileName)
            throws IOException {
        // 检测当前路径是否存在，不存在则创建
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }

        // 输出文件到对应路径
        String outputPath = filePath + fileName;
        FileOutputStream out = new FileOutputStream(outputPath);
        out.write(file);
        out.flush();
        out.close();

        return outputPath;
    }


    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return {@link Boolean} 返回是否成功删除文件
     */
    public static Boolean removeFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }
}
