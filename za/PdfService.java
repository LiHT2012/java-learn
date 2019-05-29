package com.pim.pdf;

import java.util.List;

import com.pim.exception.PermissionException;

//import java.io.ByteArrayOutputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Rectangle;
//import com.itextpdf.text.pdf.AcroFields;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfContentByte;
//import com.itextpdf.text.pdf.PdfCopy;
//import com.itextpdf.text.pdf.PdfImportedPage;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.PdfStamper;
//import com.itextpdf.text.pdf.PdfWriter;

public interface PdfService {

	public String generatePdf(String pdfType, Float bfSize, MideaPdfModel mid, List<MideaProcModel> pdfList)
			throws IllegalArgumentException, IllegalAccessException;

	public String generateMideaPdf(MideaPdfModel midea, String userId) throws IllegalArgumentException, IllegalAccessException, PermissionException;
	
//	// 利用模板生成pdf
//	public static void fillTemplate() throws IllegalArgumentException, IllegalAccessException {
//		// 模板路径
//		String templatePath = "/home/liht/桌面/美的竖版2.pdf";// 原PDF模板
//		// 生成的新文件路径
//		String newPDFPath = "/home/liht/桌面/美的测试2.pdf";
//		PdfReader reader;
//		FileOutputStream out;
//		ByteArrayOutputStream bos;
//		PdfStamper stamper;
//		try { // ↓↓↓↓↓这个是字体文件
//			BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);// 字体类型
//			Font FontChinese = new Font(bf, 12f, Font.BOLD);
//			out = new FileOutputStream(newPDFPath);// 输出流
//			reader = new PdfReader(templatePath);// 读取pdf模板
//			bos = new ByteArrayOutputStream();
//			stamper = new PdfStamper(reader, bos);
//			PdfWriter wr = stamper.getWriter();
//			Rectangle tem = wr.getPageSize();
//			AcroFields form = stamper.getAcroFields();
//
//			MideaPdfModel mid = new MideaPdfModel("formNameaaaa", "projectName--美的", "projectLocation-北京",
//					"customerName", "contact", "saleName", "saleDate", "salesMan", "salesPhone",
//					Arrays.asList(new MideaReq()), "1111", "", "deliveryWay--到付", "payWay", null);
//			Map<String, String> map = MideaPdfModel.getMap(mid);
//
//			for (String key : map.keySet()) {
//				form.setField(key, map.get(key));
//			}
//			PdfPTable t = MideaPdfModel.getTempTable("");
//			t.setSplitLate(false);//当前页能放多少放多少
//			// 表格位置
//			int pageNo = form.getFieldPositions("productList").get(0).page;
//			PdfContentByte pcb = stamper.getUnderContent(pageNo);
//			Rectangle signRect = form.getFieldPositions("productList").get(0).position;
//			
//			float totalWidth = signRect.getRight() - signRect.getLeft();
//			t.setTotalWidth(totalWidth);
//			float totalHeight = signRect.getTop() - signRect.getBottom();
//			
//			List<MideaProcModel> pdfList = new ArrayList<>();
//			pdfList.add(new MideaProcModel("proName", "KFR-120T2W/SY-TR(E4)", 0, "unit", 100D, 200D, "remarks"));
//			for (int i = 1; i <=25; i++) {
//				pdfList.add(new MideaProcModel("proName" + i, "KFR-120T2W/SY-TR(E4)" + i, i, "unit" + i, 100D, 200D, "remarks" + i));
//			}
//			
//
//			// Seperate Page controller
//			int recordPerPage = 10;
//			int fullPageRequired = pdfList.size() / recordPerPage;
//			int remainPage = pdfList.size() % recordPerPage > 1 ? 1 : 0;
//			int totalPage = fullPageRequired + remainPage;
//			float avgHeight = totalHeight/recordPerPage;
//
//			MideaProcModel m;
//			int total = 0;
//			for (int i = 1; i <= totalPage; i++) {
////						doc.newPage();
//				pcb = stamper.getUnderContent(i);
//				for (int j = (i - 1) * recordPerPage; (total - (i - 1) * recordPerPage < recordPerPage)
//						&& j < pdfList.size(); j++) {
//					m = pdfList.get(j);
//					PdfPCell c2 = new PdfPCell(new Paragraph(String.valueOf(j+1), FontChinese));
//					c2.setFixedHeight(avgHeight);
////					c2.setCalculatedHeight(3f);
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
//					c2.setFixedHeight(avgHeight);
////					c2.setCalculatedHeight(3f);
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
//					c2.setFixedHeight(avgHeight);
////					c2.setCalculatedHeight(3f);
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph(String.valueOf(m.getNumber()), FontChinese));
//					c2.setFixedHeight(avgHeight);
////					c2.setCalculatedHeight(3f);
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
//					c2.setFixedHeight(avgHeight);
////					c2.setCalculatedHeight(3f);
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
//					c2.setFixedHeight(avgHeight);
////					c2.setCalculatedHeight(3f);
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
//					c2.setFixedHeight(avgHeight);
////					c2.setCalculatedHeight(3f);
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
//					c2.setFixedHeight(avgHeight);
////					c2.setCalculatedHeight(3f);
//					t.addCell(c2);
//					total++;
//				}
//				t.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), pcb);
//				if (i + 1 <= totalPage) {
//					stamper.insertPage(i + 1, tem);
//					t = MideaPdfModel.getTempTable("");
//					t.setSplitLate(false);//当前页能放多少放多少
//					t.setTotalWidth(totalWidth);
//					PdfPCell c2 = new PdfPCell(new Paragraph("序号", FontChinese));
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); //垂直居中
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph("名称", FontChinese));
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); //垂直居中
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph("规格", FontChinese));
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); //垂直居中
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph("数量", FontChinese));
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); //垂直居中
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph("单位", FontChinese));
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); //垂直居中
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph("单价（元）", FontChinese));
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); //垂直居中
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph("合价（元）", FontChinese));
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); //垂直居中
//					t.addCell(c2);
//					c2 = new PdfPCell(new Paragraph("备注", FontChinese));
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); //垂直居中
//					t.addCell(c2);
//				}
//			}
//			stamper.setFormFlattening(false);// 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
//			stamper.close();
//
//			Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
//			PdfCopy copy = new PdfCopy(doc, out);
//			doc.open();
//
//			PdfImportedPage importPage = null;
//			/// 循环是处理成品只显示一页的问题
//			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
//				importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), i);
//				copy.addPage(importPage);
//			}
//			doc.close();
//			System.out.println("生成pdf文件完成！");
//		} catch (IOException e) {
//			System.out.println(e);
//		} catch (DocumentException e) {
//			System.out.println(e);
//		}
//	}
//
//	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
//		fillTemplate();
//	}
}
