package com.pinyougou.sellergoods.controller;

import com.pinyougou.utils.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController

public class UploadController {

@Value("${FILE_SERVER_URL}")
 private String FILE_SERVER_URL;//文件服务器地址

        @RequestMapping("upload")
    public Result upload(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();//获取上传文件名

        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);//获取后缀名



        try {
            //创建FASTdfs客户端

            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");

            //执行上传处理
            String path = fastDFSClient.uploadFile(multipartFile.getBytes(), extName);

            //拼接返回给前端的url和ip地址
            String url=FILE_SERVER_URL+path;
            System.out.println(url);
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }

}
