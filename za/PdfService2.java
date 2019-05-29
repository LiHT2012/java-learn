package com.pim.pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfService2 {

	// 利用模板生成pdf
	public static void fillTemplate() throws IllegalArgumentException, IllegalAccessException {
		// 模板路径
		String templatePath = "/home/liht/桌面/美的竖版文档表格0306.pdf";// 原PDF模板
		// 生成的新文件路径
		String newPDFPath = "/home/liht/桌面/美的测试0306.pdf";
		PdfReader reader;
		FileOutputStream out;
		ByteArrayOutputStream bos;
		PdfStamper stamper;
		try { // ↓↓↓↓↓这个是字体文件
			BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);// 字体类型
			Font FontChinese = new Font(bf, 10f, Font.NORMAL);
			out = new FileOutputStream(newPDFPath);// 输出流
			reader = new PdfReader(templatePath);// 读取pdf模板
			reader.getCropBox(1);

			bos = new ByteArrayOutputStream();
			stamper = new PdfStamper(reader, bos);
			PdfWriter wr = stamper.getWriter();
			Rectangle tem = wr.getPageSize();

			AcroFields form = stamper.getAcroFields();

			MideaPdfModel mid = new MideaPdfModel("啊啊啊啊啊啊啊啊啊啊啊二", "a啊啊啊啊啊啊啊吧", "customerName", "contact",
					"saleName", "saleDate", "salesMan", "salesPhone", null, "194150126320",
					"2019-03-04", "deliveryWay--到付", "payWay", "看加粗了吗aaaaaaaaaaa"
							+ "aaaaaaaaaaaaaaaaaaaaaaaaaa啊啊啊啊啊啊啊啊啊啊啊二啊啊啊啊啊啊啊啊啊啊啊二啊啊啊" + "啊啊啊啊啊啊啊啊二啊啊啊啊啊啊啊啊啊啊啊二",
					null);
			Map<String, String> map = MideaPdfModel.getMap(mid);
			ArrayList<BaseFont> list = new ArrayList<>();
			list.add(bf);
			form.setSubstitutionFonts(list);
			for (String key : map.keySet()) {
				System.out.println(key);
				System.out.println(map.get(key));
				form.setField(key, map.get(key));
			}

			PdfPTable t = MideaPdfModel.getTempTable("");
//			t.setSplitLate(true);//当前页能放多少放多少
			// 表格位置
//			int pageNo = form.getFieldPositions("productList").get(0).page;
//			PdfContentByte pcb = stamper.getUnderContent(pageNo);
//			Rectangle signRect = form.getFieldPositions("productList").get(0).position;
			PdfContentByte pcb = stamper.getUnderContent(1);
			Rectangle signRect = new Rectangle(55.65f, 596f, 538f, 596f);

			float totalWidth = signRect.getRight() - signRect.getLeft();
			t.setTotalWidth(totalWidth);
//			float totalHeight = signRect.getTop() - signRect.getBottom();

			List<MideaProcModel> pdfList = new ArrayList<>();
//			pdfList.add(
//					new MideaProcModel("啊啊啊啊啊啊啊啊啊十", "KFR-120T2W/SY-TR(E4)AAAAA", 0, "unit", 100D, 200D, "remarks"));
			for (int i = 1; i <= 18; i++) {
				pdfList.add(new MideaProcModel("啊啊啊啊啊啊啊啊啊十", "KFR-120T2W/SY-TR(E4)AAAAA", i, "unit" + i, 100D, 200D,
						"remarks" + i));
			}

			// Seperate Page controller
			int firstPage = 20;
			int recordPerPage = 25;
			int dataSize = pdfList.size();
			int totalSize = dataSize + 6;// 把最后的合计表格算进去,安装费一行待定
			int totalPage = 1;
			boolean newPage = false;
			if (totalSize > firstPage + 2) {// 多余一页
				totalPage = totalPage + (totalSize - firstPage) / recordPerPage;
				int remain = (totalSize - firstPage) % recordPerPage;
				if (remain > 0) {// 若只因为合计表格需开新页
					if (remain <= 6) {
						newPage = true;
					}
					totalPage++;
				}
			}

//			float avgHeight = totalHeight / recordPerPage;

			int size = firstPage;
			MideaProcModel m;
			int total = 0;
			int tempSize = 0;
			String page = null;
			PdfPCell c2 = null;
			if (newPage) {// 合计部分需开新页
				for (int i = 1; i <= totalPage; i++) {
					pcb = stamper.getUnderContent(i);
					tempSize = 0;
					for (int j = total; tempSize < size && j < totalSize; j++) {
						if (j >= dataSize) {
							break;
						}
						m = pdfList.get(j);
						c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
//							c2.setFixedHeight(avgHeight);
						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
//							c2.setFixedHeight(avgHeight);
						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
//							c2.setFixedHeight(avgHeight);
						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getNumber()), FontChinese));
//							c2.setFixedHeight(avgHeight);
						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
//							c2.setFixedHeight(avgHeight);
						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
//							c2.setFixedHeight(avgHeight);
						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
//							c2.setFixedHeight(avgHeight);
						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
//							c2.setFixedHeight(avgHeight);
						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						total++;
						tempSize++;
					}
					t.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), pcb);
					pcb.setFontAndSize(bf, 11f);
//					pcb.newlineShowText(page);//under会出现在最后一个字符串的上方，over会出现在最左下角
					page = "页码: " + i + " / " + totalPage;
					pcb.showTextAligned(Element.ALIGN_CENTER, page, 291f, 40f, 0);
					System.out.println("the" + i + " page' height : " + t.calculateHeights());

					if (i + 1 < totalPage) {
						size = recordPerPage;
						stamper.insertPage(i + 1, tem);
						signRect.setTop(756f);// 整个页面表格开始的位置
						t = MideaPdfModel.getTempTable("");
//						t.setSplitLate(true);//当前页能放多少放多少
						t.setTotalWidth(totalWidth);
						c2 = new PdfPCell(new Paragraph("序号", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("名称", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("规格", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("数量", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("单位", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("单价（元）", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("合价（元）", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("备注", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
					}
					if (i + 1 == totalPage) {
						size = recordPerPage;
						stamper.insertPage(i + 1, tem);
						signRect.setTop(756f);// 整个页面表格开始的位置
						t = MideaPdfModel.getTempTable("");
//						t.setSplitLate(true);//当前页能放多少放多少
						t.setTotalWidth(totalWidth);

						if (mid.getSetupCost() != null) {// 后边会添加安装费一项
							c2 = new PdfPCell(new Paragraph("安装费:", FontChinese));
							c2.setColspan(2);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							c2.setFixedHeight(25f);
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph(mid.getTotal(), FontChinese));
							c2.setColspan(6);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							c2.setFixedHeight(25f);
							t.addCell(c2);
						}

						c2 = new PdfPCell(new Paragraph("合计:", FontChinese));
						c2.setColspan(2);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						c2.setFixedHeight(25f);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(mid.getTotal(), FontChinese));
						c2.setColspan(6);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						c2.setFixedHeight(25f);
						t.addCell(c2);
//						FontChinese.setStyle(Font.BOLD);
						c2 = new PdfPCell(new Paragraph("合计(大写):", FontChinese));
						c2.setColspan(2);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						c2.setFixedHeight(25f);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(
								NumberFormat.numberToChineseLimitLenth12(Double.valueOf(mid.getTotal())), FontChinese));
						c2.setColspan(6);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						c2.setFixedHeight(25f);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph("报价说明", FontChinese));
						c2.setRowspan(4);
//						c2.setFixedHeight(100f);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph("报价有效期", FontChinese));
						c2.setFixedHeight(25f);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(mid.getValidTime(), FontChinese));
						c2.setColspan(2);
						c2.setFixedHeight(25f);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph("交货方式", FontChinese));
						c2.setColspan(2);
						c2.setFixedHeight(25f);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(mid.getDeliveryWay(), FontChinese));
						c2.setColspan(2);
						c2.setFixedHeight(25f);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph("付款方式", FontChinese));
						c2.setFixedHeight(25f);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(mid.getPayWay(), FontChinese));
						c2.setColspan(6);
						c2.setFixedHeight(25f);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(mid.getSaleRemarks(), FontChinese));
						c2.setColspan(7);
						c2.setFixedHeight(50f);
//						c2.setRowspan(2);
						t.addCell(c2);
					}
				}
			} else {
				for (int i = 1; i <= totalPage; i++) {
					pcb = stamper.getUnderContent(i);
					tempSize = 0;
					for (int j = total; tempSize < size && j < totalSize; j++) {
						if (j >= pdfList.size()) {// 处理到表尾合计部分
							if (mid.getSetupCost() != null) {// 后边会添加安装费一项
								c2 = new PdfPCell(new Paragraph("安装费:", FontChinese));
								c2.setColspan(2);
								c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
								c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
								c2.setFixedHeight(25f);
								t.addCell(c2);

								c2 = new PdfPCell(new Paragraph(mid.getTotal(), FontChinese));
								c2.setColspan(6);
								c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
								c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
								c2.setFixedHeight(25f);
								t.addCell(c2);
							}
							c2 = new PdfPCell(new Paragraph("合计:", FontChinese));
							c2.setColspan(2);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							c2.setFixedHeight(25f);
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph(mid.getTotal(), FontChinese));
							c2.setColspan(6);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							c2.setFixedHeight(25f);
							t.addCell(c2);
//							FontChinese.setStyle(Font.BOLD);
							c2 = new PdfPCell(new Paragraph("合计(大写):", FontChinese));
							c2.setColspan(2);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							c2.setFixedHeight(25f);
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph(
									NumberFormat.numberToChineseLimitLenth12(Double.valueOf(mid.getTotal())),
									FontChinese));
							c2.setColspan(6);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							c2.setFixedHeight(25f);
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph("报价说明", FontChinese));
							c2.setRowspan(4);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph("报价有效期", FontChinese));
							c2.setFixedHeight(25f);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph(mid.getValidTime(), FontChinese));
							c2.setColspan(2);
							c2.setFixedHeight(25f);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph("交货方式", FontChinese));
							c2.setColspan(2);
							c2.setFixedHeight(25f);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph(mid.getDeliveryWay(), FontChinese));
							c2.setColspan(2);
							c2.setFixedHeight(25f);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph("付款方式", FontChinese));
							c2.setFixedHeight(25f);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph(mid.getPayWay(), FontChinese));
							c2.setColspan(6);
							c2.setFixedHeight(25f);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph(mid.getSaleRemarks(), FontChinese));
							c2.setColspan(7);
							c2.setFixedHeight(50f);
//							c2.setRowspan(2);
							t.addCell(c2);

							break;
						} else {
							m = pdfList.get(j);
							c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
//							c2.setFixedHeight(avgHeight);
							c2.setCalculatedHeight(3f);
							t.addCell(c2);
							c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
//							c2.setFixedHeight(avgHeight);
							c2.setCalculatedHeight(3f);
							t.addCell(c2);
							c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
//							c2.setFixedHeight(avgHeight);
							c2.setCalculatedHeight(3f);
							t.addCell(c2);
							c2 = new PdfPCell(new Paragraph(String.valueOf(m.getNumber()), FontChinese));
//							c2.setFixedHeight(avgHeight);
							c2.setCalculatedHeight(3f);
							t.addCell(c2);
							c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
//							c2.setFixedHeight(avgHeight);
							c2.setCalculatedHeight(3f);
							t.addCell(c2);
							c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
//							c2.setFixedHeight(avgHeight);
							c2.setCalculatedHeight(3f);
							t.addCell(c2);
							c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
//							c2.setFixedHeight(avgHeight);
							c2.setCalculatedHeight(3f);
							t.addCell(c2);
							c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
//							c2.setFixedHeight(avgHeight);
							c2.setCalculatedHeight(3f);
							t.addCell(c2);
						}
						total++;
						tempSize++;
					}
					t.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), pcb);
					pcb.setFontAndSize(bf, 11f);
//					pcb.newlineShowText(page);//under会出现在最后一个字符串的上方，over会出现在最左下角
					page = "页码: " + i + " / " + totalPage;
					pcb.showTextAligned(Element.ALIGN_CENTER, page, 291f, 40f, 0);
					System.out.println("the" + i + " page' height : " + t.calculateHeights());

					if (i + 1 <= totalPage) {
						size = recordPerPage;
						stamper.insertPage(i + 1, tem);
						signRect.setTop(756f);// 整个页面表格开始的位置
						t = MideaPdfModel.getTempTable("");
//						t.setSplitLate(true);//当前页能放多少放多少
						t.setTotalWidth(totalWidth);
						c2 = new PdfPCell(new Paragraph("序号", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("名称", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("规格", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("数量", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("单位", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("单价（元）", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("合价（元）", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph("备注", FontChinese));
						c2.setFixedHeight(27f);
//						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);
					}
				}
			}

			stamper.setFormFlattening(false);// 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
			stamper.close();

			Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
			PdfCopy copy = new PdfCopy(doc, out);
			doc.open();

			PdfImportedPage importPage = null;
			/// 循环是处理成品只显示一页的问题
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), i);
				copy.addPage(importPage);
			}
			doc.close();
			System.out.println("生成pdf文件完成！");
		} catch (IOException e) {
			System.out.println(e);
		} catch (DocumentException e) {
			System.out.println(e);
		}
	}

	public static String leftPad(String str, int i) {
		int addSpaceNo = i - str.length();
		String space = "";
		for (int k = 0; k < addSpaceNo; k++) {
			space = " " + space;
		}
		;
		String result = space + str;
		return result;
	}

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		fillTemplate();
	}
}
