package org.example.tiktok.service;

import org.example.tiktok.pojo.dto.CommentDTO;
import org.example.tiktok.result.Result;

public interface CommentService {
    Result VideoComment(CommentDTO commentDTO);

    Result CommentReply(CommentDTO commentDTO);
}
