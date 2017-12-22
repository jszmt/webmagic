package com.wugz.app.govdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DownLoadExcel {
	
	private final String folders = "F:\\国家数据\\";
	
	private String path;

	 public DownLoadExcel(String foldersName) {
		 this.path = folders + foldersName ;
		 
		 File dirFile = new File(this.path);
		 // 如果dir对应的文件不存在，或者不是一个目录，则退出
		 if (!dirFile.exists() || !dirFile.isDirectory()) {
			 dirFile.mkdirs();
		 }
	 }
	 /***
	  * 
	  * 先获取第一行和第一列  title 的所属list （list中保存title的相关信息）
	  * 
	  * 
	  * 
	  * @param data
	  * @param fileName
	  */
	public void process(String data,String fileName) {
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
	            
	            initCellStyle(sheet, workbook);
	            
	            writeExcel(workbook,fileName);
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
			//循环详情list 找到一个 与行列title  id都一样的一个  就是他  把他的值写到excel中
			for (Object obj : contentList) {
				JSONObject dataDetail = (JSONObject) obj;
				JSONArray wds = dataDetail.getJSONArray("wds");
				boolean flag = true;
				for (Object wd : wds) {
					JSONObject wdJson = (JSONObject) wd;
					String valuecode = wdJson.getString("valuecode");
					String rowCode = rowTitle.getString("code"); //行title的 id
					String cellCode = cellTitle.getString("code"); //列title的id
					if("zb".equals(wdJson.get("wdcode")) && !valuecode.equals(rowCode)) {
						flag = false;
					}
					if("sj".equals(wdJson.get("wdcode")) && !valuecode.equals(cellCode)) {
						flag = false;
					}
				}
				//存在
				if(flag) {
					value = dataDetail.getJSONObject("data").getString("data");
//					try {
//						//有小数的值 保留一位小数
//						if(value.contains(".")) {
//							value = String.valueOf(new BigDecimal(value).setScale(1, BigDecimal.ROUND_DOWN));
//						}
//					}catch (Exception e) {
//						value = dataDetail.getJSONObject("data").getString("data");
//					}
				}
			}
			return value;
		}

		/***
	     * 写第一行和第一列的title
	     * @param rowTitleList
	     * @param cellTitleList
	     * @param sheet
		 * @param workbook 
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
	            fos = new FileOutputStream(this.getPath() +"\\"+ fileName+".xlsx");
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
		
	    private void initCellStyle(XSSFSheet sheet, XSSFWorkbook workbook) {
	        int lastRowNum = sheet.getLastRowNum();
	        sheet.autoSizeColumn(1, true);
        	//创建样式对象
    		CellStyle style = workbook.createCellStyle();
//			 //上下左右边线的框的颜色
//			if(CellStyle.BORDER_THIN != style.getBorderBottom()){
//				style.setBorderBottom(CellStyle.BORDER_THIN);//下
//				style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//			}
//			if(CellStyle.BORDER_THIN != style.getBorderLeft()){
//				style.setBorderLeft(CellStyle.BORDER_THIN);//左
//				style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//			}
//			if(CellStyle.BORDER_THIN !=  style.getBorderRight()){
//				style.setBorderRight(CellStyle.BORDER_THIN);//右
//				style.setRightBorderColor(IndexedColors.BLACK.getIndex());
//			}
//			if(IndexedColors.BLACK.getIndex() != style.getRightBorderColor()){
//				style.setBorderTop(CellStyle.BORDER_MEDIUM_DASHED);//上
//				style.setTopBorderColor(IndexedColors.BLACK.getIndex());
//			}
	        //字体居中
	        style.setAlignment(CellStyle.ALIGN_CENTER);
	        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	        for (int rowNum = 0; rowNum <= lastRowNum; rowNum++) {
	    		XSSFRow row = sheet.getRow(rowNum);
	    		if(row == null){
	    			continue;
	    		}
    			XSSFCell cell = null;
    	        for(int i=0;i<=row.getLastCellNum();i++){
            		cell = row.getCell(i);
            		if(cell != null){
	            		if(i==0||rowNum==0) {
	            			if(!(i==0&&rowNum==0)) {
	            				if(i == 0) {
	            					//设置单元格背景颜色
//	            					style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());//中间那个属性就是设置颜色（天蓝色）
//	            					style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	            				}else {
	            					//设置单元格背景颜色
//	            					style.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());//中间那个属性就是设置颜色（天蓝色）
//	            					style.setFillPattern(CellStyle.SOLID_FOREGROUND);
	            				}
	            			}
	            			sheet.setColumnWidth(i, cell.getStringCellValue().length()*512);//设置自动宽度
	            		}else {
	            			sheet.autoSizeColumn(i,true);
	            		}
	            		cell.setCellStyle(style);
            		}
    	        }
	        }
	        
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
		
		public static void main(String[] args) {
			GovDataSearch g = new GovDataSearch();
			g.search();
			//System.out.println(String.valueOf(new BigDecimal("219438.6").intValue()));
		}

}
