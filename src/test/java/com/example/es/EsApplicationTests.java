package com.example.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.es.config.DocIndexTemplate;
import com.example.es.config.ElasticSearchUtil;
import com.example.es.config.SearchResp;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;


import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.fit.pdfdom.PDFDomTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private ElasticSearchUtil util;




    @Test
    public void readPDF() throws IOException, InterruptedException {
        File file = new File("/Users/sun7ay/Desktop/4.pdf");
        String text="";
        String name="";
        if(file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("pdf")) {
            PDDocument document = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
            name = file.getName();
        }else if(file.getName().substring(file.getName().lastIndexOf(".") + 1).
                equalsIgnoreCase("docx")){
            FileInputStream fis = new FileInputStream(file);
            XWPFDocument xdoc = new XWPFDocument(fis);
            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
            text = extractor.getText();
            fis.close();
        }
        DocIndexTemplate template = new DocIndexTemplate();
        template.setContent(text);
        template.setTitle(name);
        template.setId(UUID.randomUUID().toString());
        template.setUploadTime(new Date());


//        Request request = new Request("PUT","/cmft/fh/"+template.getId());
//        request.addParameter("pretty","true");
//
//        Gson gson = new Gson();
//        String js = gson.toJson(template);
//
//        NStringEntity entity = new NStringEntity(js, ContentType.APPLICATION_JSON);
//
//        request.setEntity(entity);
//
//
//        Thread.sleep(100000);
    }

    @Test
    public void search() throws InterruptedException {
        try {
            SearchResponse response = util.search("招商");
            List<SearchResp> list = new ArrayList<>();
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                String id=hit.getId();
                Text[] contents = hit.getHighlightFields().get("content").getFragments();
                for (Text content : contents) {
                    SearchResp resp = new SearchResp();
                    resp.setContent(content.string());
                    resp.setDocId(id);
                    list.add(resp);
                }
            }
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void pdfToHTML() throws IOException, ParserConfigurationException {

            PDDocument pdf = PDDocument.load(new File("/Users/sun7ay/Desktop/1.pdf"));
            Writer output = new PrintWriter("/Users/sun7ay/Desktop/2.html", "utf-8");
            new PDFDomTree().writeText(pdf, output);
            output.close();

    }
}
