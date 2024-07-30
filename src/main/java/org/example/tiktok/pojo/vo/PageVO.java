package org.example.tiktok.pojo.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class PageVO<T> {
        protected List<T> items;
        protected long total;

    public PageVO(List<T> items, long total) {
        this.items = items;
        this.total = total;
    }

    public PageVO() {
    }
}

