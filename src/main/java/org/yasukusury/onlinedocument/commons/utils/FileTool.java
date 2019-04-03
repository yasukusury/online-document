package org.yasukusury.onlinedocument.commons.utils;

import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author 30254
 * creadtedate: 2019/3/13
 */
public class FileTool {


    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        try (
                // 新建文件输入流并对它进行缓冲
                FileInputStream input = new FileInputStream(sourceFile);
                BufferedInputStream inBuff = new BufferedInputStream(input);

                // 新建文件输出流并对它进行缓冲
                FileOutputStream output = new FileOutputStream(targetFile);
                BufferedOutputStream outBuff = new BufferedOutputStream(output)) {

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        }

    }

    // 复制文件夹
    public static void copyDirectory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        File file = new File(targetDir);
        file.mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] files = new File(sourceDir).listFiles();
        for (File aFile : files) {
            if (aFile.isFile()) {
                // 源文件
                // 目标文件
                File targetFile = new
                        File(file.getAbsolutePath()
                        + File.separator + aFile.getName());
                copyFile(aFile, targetFile);
            }
            if (aFile.isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + aFile.getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + aFile.getName();
                copyDirectory(dir1, dir2);
            }
        }
    }


    public static File copyDirectoryStructure(String srcDir, String desDir) throws IOException {
        copyDirectoryStructure structure = new copyDirectoryStructure(srcDir, desDir);
        structure.copy(structure.srcDir);
        return new File(srcDir);
    }

    public static File writeAsJsFile(String jsName, String content) throws IOException {
        try (FileWriter writer = new FileWriter(jsName)) {
            writer.write(content);
            writer.flush();
        }
        return new File(jsName);
    }

    private static class copyDirectoryStructure {

        private String srcDir;
        private String desDir;

        public copyDirectoryStructure(String srcDir, String desDir) throws IOException {
            this.srcDir = new File(srcDir).getCanonicalPath();
            this.desDir = new File(desDir).getCanonicalPath();
            srcRoot = new File(srcDir).getCanonicalPath().split("\\\\").length - 1;
        }

        private int srcRoot;
        private FileInputStream in;
        private FileOutputStream out;

        private void copy(String sourcePath) {

            File[] sourceFiles = new File(sourcePath).listFiles();

            if (sourceFiles == null) {
                return;
            }

            Arrays.stream(sourceFiles).forEach(sourceFile -> {
                try {
                    if (sourceFile.isDirectory()) {
                        copy(sourceFile.getCanonicalPath());
                    } else {
                        in = new FileInputStream(sourceFile);

                        //拼接目标文件路径
                        String[] sourceFilePath = sourceFile.getCanonicalPath().split("\\\\");
                        sourceFilePath[srcRoot - 1] = desDir;
                        File newFile = new File(StringTool.join("\\", sourceFilePath, srcRoot - 1, sourceFilePath.length));
                        if (!newFile.exists()) {
                            newFile.getParentFile().mkdirs();
                        }
                        out = new FileOutputStream(newFile);

                        byte[] temp = new byte[4096];
                        int c;
                        while ((c = in.read(temp)) != -1) {
                            out.write(temp, 0, c);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    public static File mergeZipFile(String targetZipFile, String... sourceZipFiles) throws IOException {
        try (FileOutputStream fout = new FileOutputStream(targetZipFile);
             ZipOutputStream out = new ZipOutputStream(fout);) {
            HashSet<String> names = new HashSet<>();
            for (String sourceZipFile : sourceZipFiles) {
                try (ZipFile zipFile = new ZipFile(sourceZipFile, Charset.forName("UTF-8"));
                     FileInputStream fin = new FileInputStream(sourceZipFile);
                     ZipInputStream zipInputStream = new ZipInputStream(fin);) {
                    Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                    while (enumeration.hasMoreElements()) {
                        ZipEntry ze = enumeration.nextElement();
                        if (ze.isDirectory() || names.contains(ze.getName())) {
                            continue;
                        }
                        ZipEntry oze = new ZipEntry(ze.getName());
                        out.putNextEntry(oze);
                        if (ze.getSize() > 0) {
                            IOUtils.copy(zipFile.getInputStream(ze), out);
                            out.closeEntry();
                            out.flush();
                        }
                        names.add(oze.getName());
                    }
                    zipInputStream.closeEntry();
                }
            }
        }
        return new File(targetZipFile);
    }

    public static File zipFiles(String desFileName, String... srcFiles) throws IOException {
        File zipFile = new File(desFileName);
        try(FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);) {
            for (String path : srcFiles) {
                File src = new File(path);
                compress0(src, zos, "");
            }
        }
        return zipFile;
    }

    public static void compress(String srcFilePath, String destFilePath) throws IOException {
        File src = new File(srcFilePath);
        File zipFile = new File(destFilePath);
        try(FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);) {
            compress0(src, zos, "");
        }
    }

    private static void compress0(File src, ZipOutputStream zos, String baseDir) throws IOException {
        if (!src.exists())
            return;
        if (src.isFile()) {
            try (FileInputStream fis = new FileInputStream(src)) {
                ZipEntry entry = new ZipEntry(baseDir + src.getName());
                zos.putNextEntry(entry);
                int count;
                byte[] buf = new byte[1024];
                while ((count = fis.read(buf)) != -1) {
                    zos.write(buf, 0, count);
                }
            }

        } else if (src.isDirectory()) {
            File[] files = src.listFiles();
            String base = baseDir + src.getName() + File.separator;
            if (files.length == 0) {
                zos.putNextEntry(new ZipEntry(base));
            }
            for (File file : files) {
                compress0(file, zos, base);
            }

        }

    }

}
