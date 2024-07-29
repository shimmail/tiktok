package org.example.tiktok.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtils {

    private static final String UPLOAD_DIR = "D:\\code\\java\\2\\files"; // 指定文件上传的目录

    /**
     * 保存文件到指定目录，使用UUID作为文件名的一部分
     *
     * @param file 要保存的文件
     * @return 保存后的文件路径
     * @throws IOException 如果保存文件时发生IO异常
     */
    public static String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("文件为空");
        }

        String extension = null;
        // 获取文件原始名
        String originalFilename = file.getOriginalFilename();
        // 获取文件扩展名
        int lastIndexOfDot = originalFilename.lastIndexOf('.');
        if (lastIndexOfDot > -1 && lastIndexOfDot < originalFilename.length() - 1) {
            extension = originalFilename.substring(lastIndexOfDot + 1);
            System.out.println("文件扩展名: " + extension); // 输出: 文件扩展名: jpg
        } else {
            throw new IOException("文件名中没有扩展名");
        }


        // 使用UUID生成新文件名
        String uuid = UUID.randomUUID().toString();
        String newFilename = uuid + "." + extension;

        // 构造文件保存路径
        Path targetLocation = Paths.get(UPLOAD_DIR).resolve(newFilename).normalize();

        // 尝试保存文件
        Files.copy(file.getInputStream(), targetLocation);

        // 返回保存后的文件路径
        return targetLocation.toString();
    }

}