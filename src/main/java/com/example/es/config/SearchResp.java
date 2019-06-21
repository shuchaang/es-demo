package com.example.es.config;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author shuchang
 * Created on  2019-06-18
 */
@Data
public class SearchResp {
    private String docId;
    private String content;
}
