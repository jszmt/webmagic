package com.wugz.app.magic;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

/***
 * 
 * @author 获取知乎发现的详情信信息
 *
 */
public class ZhiHuList implements PageProcessor{

	// 设置编码 ，超时时间，重试次数，
	private Site site = Site.me().setRetryTimes(10).setSleepTime(5000).setTimeOut(5000)
			.addCookie("Domain", "zhihu.com")
			//本地知乎cookie
			.addCookie("z_c0", "Mi4xSTUtZUF3QUFBQUFBTU1MTk5DMFdEQmNBQUFCaEFsVk5aQk1GV3dEZVZfMjZOeFdqSDRUWWV2d21BQnJSTUthYWJB|1511507300|7640262a3bd364d7d6fb4d9697cad41f5faa7fd2")
			.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
	
	//https://www.zhihu.com/question/20902967
	private static final String URL_question = "^https://www\\.zhihu\\.com/question/\\d+$";
	//https://www.zhihu.com/question/19647535/answer/110944270
	private  static  final String URL_answer = "https://www\\.zhihu\\.com/question/\\d+/answer/\\d+";
	
	private static final String ZHIHU_URL = "^https://www\\.zhihu\\.com/explore+$";
	
	private static final JSONObject JSON = new JSONObject();
	
	
	private static volatile Integer i = 0;
	
	private static volatile Integer index = 5;//初始获取记录数目
	
	@Override
	public void process(Page page) {
		if(index>36) {
			return;
		}
		System.out.println(page.getHtml().links());
		if(page.getUrl().regex(URL_answer).match()) {
			Html html = page.getHtml();
			String name = html.xpath("//meta[@itemprop='name']").css("meta","content").get();
			String keywords = html.xpath("//meta[@itemprop='keywords']").css("meta","content").get();
			String answerName = html.css("div.AuthorInfo-content").css("a.UserLink-link","text").get();
			String badgeText = html.css("div.AuthorInfo-detail").css("div.AuthorInfo-badgeText","text").get();
			List<String> message = html.xpath("//div[@class=RichContent-inner]").$("span","text").all();
			if(message.size() == 0) {
				message = html.xpath("//div[@class=RichContent-inner]").$("p","text").all();
			}
			JSONObject json_0 = new JSONObject();
			json_0.put("name", name);
			json_0.put("keywords", keywords);
			json_0.put("answerName", answerName);
			json_0.put("badgeText", badgeText);
			json_0.put("message", message);
			synchronized (i) {
				JSON.put(String.valueOf(i++), json_0);
			}
		}else {
			List<String> list = page.getHtml().xpath("//div[@class=\"explore-feed feed-item\"]").links().regex(URL_answer).all();
			page.addTargetRequests(list);
			index+=5;
			String url = "https://www.zhihu.com/node/ExploreAnswerListV2?params=%7B%22offset%22%3A"+index+"%2C%22type%22%3A%22day%22%7D";
			page.addTargetRequest(url);
			page.getRequest().setCharset("UTF-8");
		
		}
	}
//
	@Override
	public Site getSite() {
		return site;
	}
	
	public static void main(String[] args) {
	    Spider.create(new ZhiHuList())
	            //从"https://github.com/code4craft"开始抓
	            .addUrl("https://www.zhihu.com/explore")
	            .addPipeline(new JsonFilePipeline("F:\\webmagic\\"))
	            //开启5个线程抓取
	            .thread(5)
	            //启动爬虫
	            .run();
	    System.out.println(JSON.toJSONString());
		
	}
}
