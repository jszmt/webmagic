package com.wugz.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wugz.app.magic.ZhiHuDemo;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.example.ZhihuPageProcessor;

@SpringBootApplication
@RestController
public class WebMagicDemoApplication {
	

	public static void main(String[] args) {
		SpringApplication.run(WebMagicDemoApplication.class, args);
	}
	
	@RequestMapping
	public Object test() {
	    Spider.create(new ZhiHuDemo())
	            //从"https://github.com/code4craft"开始抓
	            .addUrl("https://www.zhihu.com/question/30692237")
	            .addPipeline(new JsonFilePipeline("F:\\webmagic\\"))
	            //开启5个线程抓取
	            .thread(5)
	            //启动爬虫
	            .run();
		return "success";
	}
	
	
}
