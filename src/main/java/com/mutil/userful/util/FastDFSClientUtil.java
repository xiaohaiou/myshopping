package com.mutil.userful.util;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FastDFSClientUtil {

    private static final Logger log = LoggerFactory.getLogger(FastDFSClientUtil.class);

    @Value("${fdfs.reqHost}")
    private String reqHost;
    @Value("${fdfs.reqPort}")
    private String reqPort;
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig; //创建缩略图   ， 缩略图访问有问题，暂未解决

    public String uploadFile(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadFile((InputStream)file.getInputStream(),
                                                        file.getSize(),
                                                        FilenameUtils.getExtension(file.getOriginalFilename()),
                                                        null);
        String path = thumbImageConfig.getThumbImagePath(storePath.getPath()) ;
        log.info("thumbImage :" + path);  //   缩略图访问有问题，暂未解决
        return getResAccessUrl(storePath);
    }

    public String uploadFile(File file) throws IOException {
        StorePath storePath = storageClient.uploadFile(new FileInputStream(file),
                file.length(),
                FilenameUtils.getExtension(file.getName()),
                null);
        String path = thumbImageConfig.getThumbImagePath(storePath.getPath()) ;
        log.info("thumbImage :" + path);  //   缩略图访问有问题，暂未解决
        return getResAccessUrl(storePath);
    }

    public void delFile(String groupName , String filePath) {
        storageClient.deleteFile(filePath);
    }

    public InputStream download(String groupName, String path ) {
        InputStream ins =  storageClient.downloadFile(groupName, path, new DownloadCallback<InputStream>(){
            @Override
            public InputStream recv(InputStream ins) throws IOException {
                // 将此ins返回给上面的ins
                return ins;
            }}) ;
        return ins ;
    }

    /**
     * 封装文件完整URL地址
     * @param storePath
     * @return
     */
    private String getResAccessUrl(StorePath storePath) {
        String fileUrl = "http://" + reqHost + ":" + reqPort + "/" + storePath.getFullPath();
        return fileUrl;
    }



}
