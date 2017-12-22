package com.wugz.app.govdata;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wugz.app.utils.HttpAccessServletUtil;

public class GovDataSearch {
	
	private final String url = "http://data.stats.gov.cn/easyquery.htm";
	
	public static void main(String[] args) {
		GovDataSearch g = new GovDataSearch();
		g.search();
	}
	
	public void search() {
		long start = System.currentTimeMillis();
		String id = "zb";
		String param = "id="+id+"&dbcode=hgnd&wdcode=zb&m=getTree";
		String rootData = HttpAccessServletUtil.sendGet(url, param);
		if(rootData != null && rootData != "") {
			JSONArray rootList = JSONArray.parseArray(rootData);
			for (Object obj : rootList) {
				String foldersName = "/"; //文件夹名称
				JSONObject root = (JSONObject) obj;
				foldersName += root.get("name");
				serachDetail(root,foldersName);
			}
			System.out.println("执行完毕,耗时："+(System.currentTimeMillis()-start));
		}
	}
	
	/***
	 * 使用递归，遍历每个节点，找到最底层的子节点，进行详情查询（父节点没有详情查询）
	 * @param secJson
	 * @param foldersName
	 * @return
	 */
	private JSONObject serachDetail(JSONObject secJson, String foldersName) {
		System.out.println("开始执行serachDetail方法，执行节点："+secJson.getString("name")+",id="+secJson.getString("id"));
		boolean isParent = secJson.getBooleanValue("isParent");
		if(!isParent) {
			return secJson;
		}
		String id = secJson.getString("id");
		String param = "id="+id+"&dbcode=hgnd&wdcode=zb&m=getTree";
		String data_str = HttpAccessServletUtil.sendGet(url, param);
		JSONArray dataList = JSONArray.parseArray(data_str);
		for (Object obj : dataList) {
			JSONObject data = (JSONObject) obj;
			JSONObject detail = serachDetail(data,foldersName+"/"+data.getString("name"));
			//返回为null 则视为最底层的父节点执行完毕，本次for循环执行与其同级的其他父节点
			if(detail != null) {
				write(detail,foldersName+"/"+data.getString("name"));
			}
		}
		return null;
	}
	
	/***
	 * 
	 * @param detail  子节点详情
	 * @param foldersName  子节点所属目录路径
	 */
	private void write(JSONObject detail, String foldersName) {
		String id = detail.getString("id");
		String detailUrl = "http://data.stats.gov.cn/easyquery.htm";
		String param = "m=QueryData&dbcode=hgnd&rowcode=zb&colcode=sj&wds=[]&dfwds=";
		String msg = "[{\"wdcode\":\"zb\",\"valuecode\":\""+id+"\"}]";
		try {
			msg = URLEncoder.encode(msg, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = HttpAccessServletUtil.sendGet(detailUrl, param+msg);
		DownLoadExcel d = new DownLoadExcel(foldersName);
		d.process(result, detail.getString("name"));
	}
}
