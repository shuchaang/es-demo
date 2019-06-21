package com.example.es.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author shuchang
 * Created on  2019-06-18
 */
@Component
@Slf4j
public class ElasticSearchUtil {

    @Autowired
    private RestHighLevelClient highLevelClient;

    private static RestHighLevelClient client;

    private static ObjectMapper objectMapper=new ObjectMapper();

    private  String type="fh";
    private  String index="cmft";

    @PostConstruct
    public void init(){
        client=this.highLevelClient;
    }


    /**
     * 创建索引 index
     * @return
     */
    public  boolean createIndex(){
        CreateIndexRequest request = new CreateIndexRequest(index);
        try {
            CreateIndexResponse response = client.indices().create(request);
            if(response.isAcknowledged()){
                log.info("创建索引成功");
            }else{
                log.error("创建索引失败");
            }
            return response.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 添加数据
     * @param id
     * @param object
     * @return
     */
    public  IndexResponse addData(String id, JSONObject object){
        IndexRequest request = new IndexRequest(index,type,id);
        try {
            request.source(objectMapper.writeValueAsString(object), XContentType.JSON);
            return client.index(request);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }


    public  SearchResponse search(String keyword) throws IOException {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.multiMatchQuery(keyword,"title","content"));
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span class=\"red\">");
        highlightBuilder.postTags("</span>");
        highlightBuilder.field("content");
        builder.highlighter(highlightBuilder);
        builder.from(0);
        builder.size(2);
        SearchRequest source = request.source(builder);
        return client.search(source);
    }

    public  RestClient getLowLevelClient(){
        return client.getLowLevelClient();
    }
}
