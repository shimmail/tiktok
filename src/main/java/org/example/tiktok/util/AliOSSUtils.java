package org.example.tiktok.util;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectResult;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 阿里云 OSS 工具类
 */
@Component
public class AliOSSUtils {
    @Autowired
    private static OSS ossClient;

    @Value("${aliyun.oss.bucketName}")
    private static String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private static String endpoint;

    @Value("${aliyun.oss.urlPrefix}")
    private static String urlPrefix;

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?)://[\\w-]+(\\.[\\w-]+)+(/[\\w-./?%&=]*)?$"
    );
    /**
     * 实现上传图片到OSS
     */
    public static String upload(MultipartFile file) throws IOException {
        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        // 创建文件的uuid
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        // 上传文件到 OSS
        ossClient.putObject(bucketName, fileName, inputStream);

        // 文件访问路径
        String url = urlPrefix + fileName;

        // 校验图片格式
        if (!URL_PATTERN.matcher(url).matches()) {
            throw new FileUploadException("URL格式不正确: " + url);
        }

        return url;
    }
}

