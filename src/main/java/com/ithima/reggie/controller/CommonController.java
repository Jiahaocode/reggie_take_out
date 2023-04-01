package com.ithima.reggie.controller;

import com.ithima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/common")
@Slf4j

public class CommonController {

    //springBoot注入
    @Value("${reggie.path}")
    private String basePath;


    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String>uplpad(MultipartFile file){
        //获取原文件名
        String filename = file.getOriginalFilename();
        //获取后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //新文件名
        filename = UUID.randomUUID() + suffix;

        //获取服务器中 backend/images/dish 的路径
//        ServletContext context = session.getServletContext();
//        String basePath = context.getRealPath("backend/images/dish/");

        //判断当前目录是否存在
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //将临时文件转存到指定位置
        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(filename);
    }


    /**
     * //使用流的方法向浏览器写二进制数据,文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void downLoad(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件的内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，通过输出流将文件写会浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");


            int len=0;
            byte[] bytes = new byte[1024];
            while ((len=fileInputStream.read(bytes)) !=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
