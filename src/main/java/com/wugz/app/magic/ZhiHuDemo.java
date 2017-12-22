package com.wugz.app.magic;

import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.JsonPathSelector;

public class ZhiHuDemo implements PageProcessor{
	
	// 设置编码 ，超时时间，重试次数，
	private Site site = Site.me().setRetryTimes(10).setSleepTime(5000).setTimeOut(5000)
			.addCookie("Domain", "zhihu.com")
			//本地知乎cookie
			.addCookie("z_c0", "Mi4xSTUtZUF3QUFBQUFBTU1MTk5DMFdEQmNBQUFCaEFsVk5aQk1GV3dEZVZfMjZOeFdqSDRUWWV2d21BQnJSTUthYWJB|1511507300|7640262a3bd364d7d6fb4d9697cad41f5faa7fd2")
			.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
	
	//问题的索引
	//https://www.zhihu.com/question/20902967
	private static final String URL_question = "^https://www\\.zhihu\\.com/question/\\d+$";
	//https://www.zhihu.com/question/19647535/answer/110944270
	private  static  final String URL_answer = "https://www\\.zhihu\\.com/question/\\d+/answer/\\d+";
	
	private static   String questionId ="" ;
	
	private static final JSONObject RESULT = new JSONObject();
	
	@Override
	public void process(Page page) {
		System.out.println(page.toString());
		//页面为问题页，则将答案链接循环加入Downloader
		if(page.getUrl().regex(URL_question).match()){
			int total = 20;
			int time = total/20;
			page.setCharset("UTF-8");
			for(int i=0;i<=time;i++){
				int  offset = i*20;
				int limit= total<(i+1)*20?total:((i+1)*20-1);
				String url  ="https://www.zhihu.com/api/v4/questions/"+questionId+"/answers?include=data%5B*%5D.is_normal%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Cmark_infos%2Ccreated_time%2Cupdated_time%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%2Cupvoted_followees%3Bdata%5B*%5D.author.badge%5B%3F(type%3Dbest_answerer)%5D.topics&offset="+offset+"&limit="+limit+"&sort_by=default";
				System.out.println(url);
				page.addTargetRequest(url);
				page.getRequest().setCharset("UTF-8");
			}
			//某个具体答案详情页面，则获取详情信息 。
		}else if(page.getUrl().regex(URL_answer).match()){
			  List<String> badgeText = page.getHtml().$("div.AuthorInfo-badgeText","text").all();
		        page.putField("badgeText", badgeText);
		        
		        List<String> name = page.getHtml().$("div.AuthorInfo-content a","text").all();
		        page.putField("name", name);
		        RESULT.put(name.get(0), page.getResultItems().getAll());
		} else {
			System.out.println(page.getUrl());
			List<String> id = new JsonPathSelector("$.data[*].id").selectList(page.getRawText());
			for(int i=0;i<id.size();i++){
				String answerUrl = "https://www.zhihu.com/question/"+questionId+"/answer/"+id.get(i);
				page.addTargetRequest(answerUrl);
			}
		}
	}

	@Override
	public Site getSite() {
		return site;
	}
	
	public static void main(String[] args) throws InterruptedException {
		questionId = "30692237";
	    Spider.create(new ZhiHuDemo())
	            //从"https://github.com/code4craft"开始抓
	            .addUrl("https://www.zhihu.com/question/30692237")
	            //.addPipeline(new JsonFilePipeline("F:\\webmagic\\"))
	            //开启5个线程抓取
	            .thread(5)
	            //启动爬虫
	            .run();
	
		   System.out.println(RESULT.toJSONString());
	  
	
	}

}
