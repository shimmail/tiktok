package org.example.tiktok.util;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 阿里云 OSS 工具类
 */
@Component
public class AliOSSUtil {
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?)://[\\w-]+(\\.[\\w-]+)+(/[\\w-./?%&=]*)?$"
    );
    private String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
    private String accessKeyId = "LTAI5tRvYdjnRvUKbpkDQqwt";
    private String accessKeySecret = "S8zAjVmpXZgGAGvn9OgtdEzms6lj9z";
    private String bucketName = "west-2";
    /**
     * 实现上传图片到OSS
     */
    public String uploadAvatar (MultipartFile file) throws IOException {

        // 允许的文件类型
        String[] allowedTypes = {"image/jpeg", "image/png", "application/pdf"};

        // 最大文件大小
        long maxSize = 5 * 1024 * 1024; // 5MB

        // 校验文件类型
        String contentType = file.getContentType();
        if (!Arrays.asList(allowedTypes).contains(contentType)) {
            throw new FileUploadException("不支持的图片类型！");
        }

        // 校验文件大小
        if (file.getSize() > maxSize) {
            throw new FileUploadException("图片太大，不能超过5MB！");
        }


        //获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        //创建文件的uuid
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //上传文件到 OSS
        /*OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);
        ossClient.shutdown();
*/
        //文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;


        return url;
    }


    //根据OSS图片url得到缩略图url
    public static String getThumbnailUrl(String imgUrl){
        imgUrl = imgUrl + "?x-oss-process=image/resize,m_fill,w_400,quality,q_60";
        System.out.println(">>>>>>>>>>>>>>>>> 缩略图url:"+imgUrl);
        return imgUrl;
    }

    //根据OSS视频url得到视频封面图url
    public static String getVideoCoverlUrl(String videoUrl){
        videoUrl = videoUrl + "?x-oss-process=video/snapshot,t_7000,f_jpg,w_800,h_600,m_fast";
        System.out.println(">>>>>>>>>>>>>>>>> 视频封面图url:"+videoUrl);
        return videoUrl;
    }
}

