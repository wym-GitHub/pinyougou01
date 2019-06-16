package com.itheima;

import org.csource.fastdfs.*;

public class FastDFS {
    public static void main(String[] args) throws Exception {
        //加载配置文件,配置文件中的内容就是tracker服务地址
        ClientGlobal.init("D:\\IDEA-workspace\\pinyougou-parent\\fastDFSdemo\\src\\main\\resources\\fdfs_client.conf");

        //2.创建TrackerClient的对象,直接new一个
        TrackerClient trackerClient = new TrackerClient();

        //3使用trackerClient创建一个连接,获得trackerserver对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //4.创建一个StorageServer的引用
        StorageServer storageServer=null;
        //5.创建一个strorageClient对象,需要两个参数trackerServer和storageServer
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        //6使用storageclient上传图片
        //扩展名不带"."
        //String[] jpgs = storageClient.upload_file("D:/4.jpg", "jpg", null);
//        int i = storageClient.delete_file(, );

//        for (String jpg : jpgs) {
//            System.out.println(jpg);
//        }
    }

}
