package com.example.es.config;

import lombok.Data;

import java.util.Date;

/**
 * @author shuchang
 * Created on  2019-06-17
 */
@Data
public class DocIndexTemplate {
    private String id;
    private String title;
    private String content;
    private String batchNo;
    private Date uploadTime;

}
