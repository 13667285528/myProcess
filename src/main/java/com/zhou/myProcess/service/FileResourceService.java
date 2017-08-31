package com.zhou.myProcess.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class FileResourceService {

    private String fileSavePath;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public FileResourceService(String fileSavePath){
        this.fileSavePath = fileSavePath;
    }

    /**
     * 文件上传工具方法
     * @param file
     * @return 文件上传所保存的地址路径
     */
    public String upload(FileInputStream file, String fileName) throws IOException {
        String url = this.fileSavePath + "/" + this.sdf.format(new Date()) + "/" + System.currentTimeMillis()
                + fileName.substring(fileName.lastIndexOf(""));
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(file);
            bos = new BufferedOutputStream(new FileOutputStream("b.txt"));
            byte[] b = new byte[1024];
            while ((bis.read(b)) != -1) {
                bos.write(b);
            }
        }finally {
            if(bis != null)
                bis.close();
            if(bos != null)
                bos.close();
        }
        return url;
    }

    /**
     * 文件下载工具方法 该方法没有返回值，通过对请求和响应的操作来实现对文件的下
     *
     * @param request
     *            请求
     * @param response
     *            响应
     * @param fileUrl
     *            文件下载的绝对路径
     */
    public void download(HttpServletRequest request, HttpServletResponse response, String fileUrl,
                         String fileName) throws Exception {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        // 获取文件的文件名
        if(fileName == null){
            fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }
        try {
            long fileLength = new File(fileUrl).length();

            // 设置响应头为下载,并设置响应的文件名
            response.setContentType("text/html;charset=utf-8");
            request.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition",
                    "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
            response.setHeader("Content-Length", String.valueOf(fileLength));

            bis = new BufferedInputStream(new FileInputStream(fileUrl));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
    }

}
