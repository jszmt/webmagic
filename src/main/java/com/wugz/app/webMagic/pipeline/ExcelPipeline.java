package com.wugz.app.webMagic.pipeline;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wugz.app.utils.HttpAccessServletUtil;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

/**
 * Store results in files.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class ExcelPipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public ExcelPipeline() {
        setPath("F:\\国家数据\\");
    }

    public ExcelPipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
     	String data = (String) resultItems.getAll().get("data");
    	JSONObject json = JSONObject.parseObject(data);
    	String returncode = json.getString("returncode");
    	if("200".equals(returncode)) {
    		JSONObject returndata = json.getJSONObject("returndata");
    		JSONArray titleList = returndata.getJSONArray("wdnodes");
    		JSONArray contentList = returndata.getJSONArray("datanodes");
    		JSONArray rowTitleList = null;
    		JSONArray cellTitleList = null;
    		for (Object object : titleList) {
				JSONObject j  = (JSONObject) object;
				if("zb".equals(j.getString("wdcode"))) { //指标为第一列的每一行的属性
					rowTitleList = j.getJSONArray("nodes");
				}else if("sj".equals(j.getString("wdcode"))) {
					cellTitleList = j.getJSONArray("nodes");  //时间为第一行的每一列
				}
			}
    		 //第一步，创建一个workbook对应一个excel文件
            XSSFWorkbook workbook = new XSSFWorkbook();
            //第二部，在workbook中创建一个sheet对应excel中的sheet
            XSSFSheet sheet = workbook.createSheet();
            setTitle(rowTitleList,cellTitleList,sheet);
            setContent(rowTitleList,cellTitleList,sheet,contentList);
            
            writeExcel(workbook,"法人单位数-三次产业法人单位数");
    	}
    }
    
    private void setContent(JSONArray rowTitleList, JSONArray cellTitleList, XSSFSheet sheet, JSONArray contentList) {
    	XSSFRow row = null;
    	XSSFCell cell = null;
    	for(int i=1;i<=rowTitleList.size();i++) {
    		row = sheet.getRow(i);
    		for(int j=1;j<=cellTitleList.size();j++) {
    			cell = row.createCell(j);
    			String value = searchContentValue(contentList,(JSONObject)rowTitleList.get(i-1),(JSONObject)cellTitleList.get(j-1));
    			cell.setCellValue(value);
    		}
    	}
	}

	private String searchContentValue(JSONArray contentList, JSONObject rowTitle, JSONObject cellTitle) {
		String value = "";
		for (Object obj : contentList) {
			JSONObject dataDetail = (JSONObject) obj;
			JSONArray wds = dataDetail.getJSONArray("wds");
			boolean flag = true;
			for (Object wd : wds) {
				JSONObject wdJson = (JSONObject) wd;
				String valuecode = wdJson.getString("valuecode");
				String rowCode = rowTitle.getString("code");
				String cellCode = cellTitle.getString("code");
				if("zb".equals(wdJson.get("wdcode")) && !valuecode.equals(rowCode)) {
					flag = false;
				}
				if("sj".equals(wdJson.get("wdcode")) && !valuecode.equals(cellCode)) {
					flag = false;
				}
			}
			if(flag) {
				value = dataDetail.getJSONObject("data").getString("data");
			}
		}
		
		return value;
	}

	/***
     * 写第一行和第一列的title
     * @param rowTitleList
     * @param cellTitleList
     * @param sheet
     */
	private void setTitle(JSONArray rowTitleList, JSONArray cellTitleList, XSSFSheet sheet) {
		XSSFRow firstRow = sheet.createRow(0);
		XSSFCell cell = null;
		for(int i=0;i<=cellTitleList.size();i++) {
			cell = firstRow.getCell(i);
			if(cell == null) {
				cell = firstRow.createCell(i);
			}
			if(i==0) {
				cell.setCellValue("指标");
			}else {
				String cellValue = cellTitleList.getJSONObject(i-1).getString("cname");
				cell.setCellValue(cellValue);
			}
		}
		
		XSSFRow row = null;
		for(int i = 1;i<=rowTitleList.size();i++) {
			row = sheet.createRow(i);
			String value = rowTitleList.getJSONObject(i-1).getString("cname") + "（" +rowTitleList.getJSONObject(i-1).getString("unit") + "）";
			row.createCell(0).setCellValue(value);
		}
	
	}
	
	/***
	 * 输出一个excel
	 * @param workbook
	 * @param fileName
	 */
	private void writeExcel(XSSFWorkbook workbook, String fileName) {
		 FileOutputStream fos = null; 
		 	try {
	            fos = new FileOutputStream(this.getPath() + fileName+".xlsx");
	            workbook.write(fos);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	        	if(workbook != null) {
	        	 	try {
						workbook.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	        	}
	        	if(fos != null) {
	        		try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        	}
	        }		
		}
}
