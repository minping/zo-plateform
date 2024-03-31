package com.jfinal.kit;

import cn.hutool.core.io.FileUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileKit {

    public static final String UTF8 = "utf-8";


    /**
     * 获取临时目录
     *
     * @return
     */
    public static String tempPath() {
        String tempPath = System.getProperty("java.io.tmpdir");
        return tempPath;
    }

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean exists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 获取文件相对于class根目录路径
     *
     * @param filePath
     * @return
     */
    public static String getClassPath(String filePath) {
        String path = FileKit.class.getResource("/").getPath();
        if (StrKit.isNotBlank(filePath)) {
            path = path + File.separator + filePath;
        }
        if (isWinOS()) {
            path = path.substring(1);
        }
        return path;
    }

    public static boolean isWinOS() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 读取文件内容
     *
     * @param classFilePath 相对于classes根目录的文件路径
     * @return null则代表没有该文件
     */
    public static String readInClass(String classFilePath) {
        return read(getClassPath(classFilePath));
    }

    /**
     * 读取文件内容
     *
     * @param filePath 文件完整路径
     * @return
     */
    public static String read(String filePath) {
        return read(filePath, UTF8);
    }

    /**
     * 读取文件
     *
     * @param filePath 文件完整路径
     * @param encoding 文件编码
     * @return
     */
    public static String read(String filePath, String encoding) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        BufferedReader br = null;
        StringBuffer sb = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            sb = new StringBuffer();
            String buffer;
            while ((buffer = br.readLine()) != null) {
                sb.append(buffer + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


    /**
     * 写入文件
     *
     * @param content  待写入内容
     * @param filePath 文件完整路径
     */
    public static void write(String content, String filePath) {
        write(content, filePath, UTF8);
    }

    /**
     * 写入文件
     *
     * @param content  文件内容
     * @param filePath 文件完整路径
     * @param encoding UTF-8
     */
    public static void write(String content, String filePath, String encoding) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), encoding));
            writer.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 字符串写入文件
     *
     * @param content       字符内容
     * @param classFilePath 文件路径 相对于classes根目录
     */
    public static void writeInClass(String content, String classFilePath) {
        write(content, getClassPath(classFilePath));
    }


    /**
     * 得到文件头
     *
     * @return 文件头
     * @throws IOException
     */
    public static String getFileHead(InputStream is) throws IOException {
        byte[] b = new byte[28];
        try {
            is.read(b, 0, 28);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return bytesToHexString(b);
    }

    /**
     * 将文件头转换成16进制字符串
     *
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static void output(HttpServletResponse response, HttpServletRequest request, File file) {
        output(response, request, file, file.getName());
    }

    /**
     * 下载文件
     *
     * @param response
     * @param file
     */
    public static void output(HttpServletResponse response, HttpServletRequest request, File file, String fileName) {
        if (StrKit.isBlank(fileName)) {
            fileName = file.getName();
        }
        response.setCharacterEncoding(StrKit.UTF8);
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        String agent = request.getHeader("USER-AGENT");
        //火狐浏览器下采用base64编码
        if (agent != null && agent.toLowerCase().indexOf("firefox") >= 0) {
            fileName = "=?UTF-8?B?" + (new String(StrKit.base64Encoder(fileName))) + "?=";
        } else {
            //处理空格转+号问题
            fileName = StrKit.urlEncode(fileName);
            fileName = fileName.replaceAll("\\+", "%20");
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            while (bis.read(buff) > 0) {
                os.write(buff, 0, buff.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String md5(String filePath) throws IOException, NoSuchAlgorithmException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer, 0, 1024)) != -1) {
            md.update(buffer, 0, length);
        }
        BigInteger bigInt = new BigInteger(1, md.digest());
        return bigInt.toString(16);
    }

    public static byte[] file2Byte(String filePath) {
        return file2Byte(new File(filePath));
    }

    public static byte[] file2Byte(File file) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream in = null;
        try {
            if (!file.exists()) {
                throw new FileNotFoundException("file not exists");
            }
            bos = new ByteArrayOutputStream((int) file.length());
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static File copy(File src, File dest, Boolean isOverride) {
        return FileUtil.copy(src, dest, isOverride);
    }


    public static Boolean delete(String destPath) {
        return FileUtil.del(destPath);
    }

}
