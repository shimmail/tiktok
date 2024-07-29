package org.example.tiktok.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tiktok.pojo.dto.VideoDTO;
import org.example.tiktok.pojo.dto.VideoSearch;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.pojo.vo.PageVO;
import org.example.tiktok.result.Result;

public interface VideoService {
    Result publish(VideoDTO videoDTO);

    Result ListPopular(Page<Video> page);

    Result<PageVO<Video>> search(VideoSearch videoSearch, Page<Video> page);
}
