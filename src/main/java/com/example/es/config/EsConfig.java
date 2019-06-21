package com.example.es.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;

import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shuchang
 * Created on  2019-06-17
 */
@Configuration
@Slf4j
public class EsConfig {
    private String host="127.0.0.1";
    private Integer port=9200;
    private String schema="http";
    private int connectTimeOut=1000;
    private int socketTimeOut=3000;
    private int connectionRequestTimeOut=500;
    private int maxConnectNum=100;
    private int maxConnectPerRoute=100;
    private HttpHost httpHost;
    private boolean uniqueConnectTimeConfig=true;
    private boolean uniqueConnectNumConfig=true;
    private RestClientBuilder builder;
    private RestHighLevelClient client;

    @Bean
    public RestHighLevelClient highLevelClient(){
        httpHost=new HttpHost(host,port,schema);
        builder=RestClient.builder(httpHost);
        if(uniqueConnectNumConfig){
            setNumConfig();
        }
        if(uniqueConnectTimeConfig){
            setTimeConfig();
        }
        client=new RestHighLevelClient(builder);
        return client;
    }

    private void setTimeConfig() {
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                requestConfigBuilder.setConnectTimeout(connectTimeOut);
                requestConfigBuilder.setSocketTimeout(socketTimeOut);
                requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
                return requestConfigBuilder;
            }
        });
    }

    private void setNumConfig() {
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                httpClientBuilder.setMaxConnTotal(maxConnectNum);
                httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
                return httpClientBuilder;
            }
        });
    }


}
