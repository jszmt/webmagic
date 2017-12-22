package com.wugz.app.webMagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/***
 * 
 * @author ASUS
 *查询老赖黑名单（百度搜索  老赖黑名单）
 *https://www.baidu.com/s?wd=%E8%80%81%E8%B5%96%E9%BB%91%E5%90%8D%E5%8D%95&rsv_spt=1
 &rsv_iqid=0xee82ad70000075da&issp=1&f=3&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn
 &tn=62095104_2_oem_dg&rsv_enter=1&oq=%25E9%25BB%2591%25E5%2590%258D%25E5%258D%2595&inputT=9486&
 rsv_t=57c0MpEREnNEkk%2BWUunbRw5%2BcH1HKAAh1KAdL6Eo9K33miqcyIt4cIauLf37tqxHGdS28vT3yoA&rsv_pq=c680679d000089bd&
 rsv_sug3=11&rsv_sug1=12&rsv_sug7=101&rsv_sug2=0&prefixsug=%25E8%2580%2581%25E8%25B5%2596&rsp=0&rsv_sug4=9487
 *
 */
public class LaoLaiHeiMingDan implements PageProcessor {
	private Site site = Site.me().setRetryTimes(10).setSleepTime(5000).setTimeOut(5000);
			
	@Override
	public void process(Page page) {
		System.out.println(page.getHtml().toString());
	}

	@Override
	public Site getSite() {
		return site;
	}
	
	
	public static void main(String[] args) {
		int a = 10;
		int b = 3;
		for(int i=0;i<10;i++) {
			//分析url不同这样加
			a = a+10;
			b = b+1;
			String url = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=6899&query=%E8%80%81%E8%B5%96%E9%BB%91%E5%90%8D%E5%8D%95&pn="+a+"&rn=10&ie=utf-8&oe=utf-8&format=json&t=1513246068032&cb=jQuery1102046237519948208927_1513246011771&_=151324601177"+b;
			Spider.create(new LaoLaiHeiMingDan())
			.addUrl(url)
			.addPipeline(new JsonFilePipeline("F:\\webmagic\\laolai\\"))
			.thread(10)
			.run();
		}

	}

}
