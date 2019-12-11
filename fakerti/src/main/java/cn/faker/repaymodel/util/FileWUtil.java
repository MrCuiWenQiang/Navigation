package cn.faker.repaymodel.util;

/**
 * 文本读写工具
 */

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.faker.repaymodel.util.error.ErrorUtil;

public class FileWUtil {
    //路径为 /sd卡路径/你的App名称/log.txt ,如有需要，自行修改代码
    private static final String appName = "testNavigation";
    private static File file;

    static {
        file = new File(Environment.getExternalStorageDirectory(), appName);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file, "log.txt");
    }

    /**
     * 将文本追加写入到文件
     */
    public static void setAppendFile(String value) {
        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter printWriter = new PrintWriter(bw);
            printWriter.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ":\n");
            printWriter.println(value);
            printWriter.close();
        } catch (Exception e) {
            ErrorUtil.showError(e);
        }
    }

    /**
     * 将异常信息写入到文件
     */
    public static void setAppendFile(Throwable ex) {//Throwable ex
        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter printWriter = new PrintWriter(bw);
            printWriter.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ":\n");
            ex.printStackTrace(printWriter);
            printWriter.close();
        } catch (Exception e) {
            ErrorUtil.showError(e);
        }
    }

}
