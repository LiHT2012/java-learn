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

public class PdfService3 {

	// 利用模板生成pdf
	public static void fillTemplate() throws IllegalArgumentException, IllegalAccessException {
		// 模板路径
		String templatePath = "/home/liht/桌面/美的竖版0304.pdf";// 原PDF模板
		// 生成的新文件路径
		String newPDFPath = "/home/liht/桌面/美的测试0304.pdf";
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
					"saleName", "saleDate", "salesMan", "salesPhone", null, "1111",
					"2019-03-04", "deliveryWay--到付", "payWay", "看加粗了吗", null);
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
			int pageNo = form.getFieldPositions("productList").get(0).page;
			PdfContentByte pcb = stamper.getUnderContent(pageNo);
			Rectangle signRect = form.getFieldPositions("productList").get(0).position;
//			PdfContentByte pcb = stamper.getUnderContent(1);
//			Rectangle signRect = new Rectangle(55.65f,596f, 538f,596f);

			System.out.println(tem.getTop());
			System.out.println(tem.getBottom());
			System.out.println(tem.getLeft());
			System.out.println(tem.getRight());
			float totalWidth = signRect.getRight() - signRect.getLeft();
			t.setTotalWidth(totalWidth);
			float totalHeight = signRect.getTop() - signRect.getBottom();

			List<MideaProcModel> pdfList = new ArrayList<>();
			pdfList.add(
					new MideaProcModel("啊啊啊啊啊啊啊啊啊十", "KFR-120T2W/SY-TR(E4)AAAAA", 0, "unit", 100D, 200D, "remarks"));
			for (int i = 1; i <= 45; i++) {
				pdfList.add(new MideaProcModel("啊啊啊啊啊啊啊啊啊十", "KFR-120T2W/SY-TR(E4)AAAAA", i, "unit" + i, 100D, 200D,
						"remarks" + i));
			}

			// Seperate Page controller
			int recordPerPage = 15;
			MideaProcModel m;
			float avgHeight = totalHeight / recordPerPage;
			if (pdfList.size() < recordPerPage) {// 只有一页
				pcb = stamper.getUnderContent(pageNo);
				for (int j = 0; j < pdfList.size(); j++) {
					m = pdfList.get(j);
					PdfPCell c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
					c2.setFixedHeight(avgHeight);
//					c2.setCalculatedHeight(3f);
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
					c2.setFixedHeight(avgHeight);
//					c2.setCalculatedHeight(3f);
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
					c2.setFixedHeight(avgHeight);
//					c2.setCalculatedHeight(3f);
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph(String.valueOf(m.getNumber()), FontChinese));
					c2.setFixedHeight(avgHeight);
//					c2.setCalculatedHeight(3f);
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
					c2.setFixedHeight(avgHeight);
//					c2.setCalculatedHeight(3f);
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
					c2.setFixedHeight(avgHeight);
//					c2.setCalculatedHeight(3f);
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
					c2.setFixedHeight(avgHeight);
//					c2.setCalculatedHeight(3f);
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
					c2.setFixedHeight(avgHeight);
//					c2.setCalculatedHeight(3f);
					t.addCell(c2);
				}
				t.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), pcb);
			} else {
				recordPerPage = 25;
				int fullPageRequired = pdfList.size() / recordPerPage;
				int remainPage = pdfList.size() % recordPerPage > 1 ? 1 : 0;
				int totalPage = fullPageRequired + remainPage;

				int tempSize = 0;
				String page = null;
				Paragraph pageNumber = null;
				form.setField("defaultText", "<详见销货清单>");
				for (int i = 2; i <= totalPage + 1; i++) {
					stamper.insertPage(i, tem);
					page = "页码: " + (i - 1) + " / " + totalPage;
					System.out.println(page);
					pcb = stamper.getUnderContent(i);
					pageNumber = new Paragraph(page, FontChinese);
					wr.add(pageNumber);

					signRect.setTop(756f);// 696f整个页面表格开始的位置//756f
					t = MideaPdfModel.getTempTable("");
					t.setTotalWidth(totalWidth);
					PdfPCell c2 = new PdfPCell(new Paragraph("序号", FontChinese));
					c2.setFixedHeight(27f);
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph("名称", FontChinese));
					c2.setFixedHeight(27f);
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph("规格", FontChinese));
					c2.setFixedHeight(27f);
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph("数量", FontChinese));
					c2.setFixedHeight(27f);
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph("单位", FontChinese));
					c2.setFixedHeight(27f);
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph("单价（元）", FontChinese));
					c2.setFixedHeight(27f);
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph("合价（元）", FontChinese));
					c2.setFixedHeight(27f);
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);
					c2 = new PdfPCell(new Paragraph("备注", FontChinese));
					c2.setFixedHeight(27f);
//					c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);

					tempSize = 0;
					for (int j = (i - 2) * recordPerPage; tempSize < recordPerPage && j < pdfList.size(); j++) {
						m = pdfList.get(j);
						c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
						c2.setFixedHeight(avgHeight);
//						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
						c2.setFixedHeight(avgHeight);
//						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
						c2.setFixedHeight(avgHeight);
//						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getNumber()), FontChinese));
						c2.setFixedHeight(avgHeight);
//						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
						c2.setFixedHeight(avgHeight);
//						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
						c2.setFixedHeight(avgHeight);
//						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
						c2.setFixedHeight(avgHeight);
//						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
						c2.setFixedHeight(avgHeight);
//						c2.setCalculatedHeight(3f);
						t.addCell(c2);
						tempSize++;
					}
					t.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), pcb);
//					pcb = stamper.getOverContent(i);
					pcb.setFontAndSize(bf, 11f);
//					pcb.newlineShowText(page);//under会出现在最后一个字符串的上方，over会出现在最左下角
					pcb.showTextAligned(Element.ALIGN_CENTER, page, 505f, 775f, 0);
					System.out.println("the" + i + " page' height : " + t.calculateHeights());
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

	// 利用模板生成pdf
	public static void fillTemplate2() throws IllegalArgumentException, IllegalAccessException {
		// 模板路径
		String templatePath = "/home/liht/桌面/美的竖版0304.pdf";// 原PDF模板
		// 生成的新文件路径
		String newPDFPath = "/home/liht/桌面/美的测试0304.pdf";
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
					"saleName", "saleDate", "salesMan", "salesPhone", null, "1111",
					"2019-03-04", "deliveryWay--到付", "payWay", "看加粗了吗", null);
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
			int pageNo = form.getFieldPositions("productList").get(0).page;
			PdfContentByte pcb = stamper.getUnderContent(pageNo);
			Rectangle signRect = form.getFieldPositions("productList").get(0).position;
//			PdfContentByte pcb = stamper.getUnderContent(1);
//			Rectangle signRect = new Rectangle(55.65f,596f, 538f,596f);

			System.out.println(tem.getTop());
			System.out.println(tem.getBottom());
			System.out.println(tem.getLeft());
			System.out.println(tem.getRight());
			float totalWidth = signRect.getRight() - signRect.getLeft();
			t.setTotalWidth(totalWidth);
			float totalHeight = signRect.getTop() - signRect.getBottom();

			List<MideaProcModel> pdfList = new ArrayList<>();
//			pdfList.add(
//					new MideaProcModel("啊啊啊啊啊啊啊啊啊十", "KFR-120T2W/SY-TR(E4)AAAAA", 0, "unit", 100D, 200D, "remarks"));
			for (int i = 1; i <= 15; i++) {
				pdfList.add(new MideaProcModel("啊啊啊啊啊啊啊啊啊十", "KFR-120T2W/SY-TR(E4)AAAAA", i, "unit" + i, 100D, 200D,
						"remarks" + i));
			}

			// Seperate Page controller
			// Seperate Page controller
			int recordPerPage = 15;
			if (mid.getSalesPhone() != null) {
				recordPerPage = 14;
			}
			MideaProcModel m;
			float avgHeight = totalHeight / recordPerPage;
			if (pdfList.size() <= recordPerPage) {// 只有一页
				pcb = stamper.getUnderContent(pageNo);
				if (mid.getSalesPhone() != null) {// 暂时当做安装费
					avgHeight = totalHeight / (recordPerPage + 1);
					for (int j = 0; j <= recordPerPage; j++) {
						if (j >= pdfList.size()) {// 多于data数据
							if (pdfList.size() == recordPerPage) {
								PdfPCell c2 = new PdfPCell(new Paragraph("安装费：", FontChinese));
								c2.setFixedHeight(avgHeight);
								c2.setColspan(2);
								t.addCell(c2);

								c2 = new PdfPCell(new Paragraph(mid.getSalesPhone(), FontChinese));
								c2.setFixedHeight(avgHeight);
								c2.setColspan(6);
								t.addCell(c2);
							} else {
								PdfPCell c2 = new PdfPCell(new Paragraph("", FontChinese));
								c2.setFixedHeight(avgHeight * (recordPerPage - pdfList.size()));
								c2.setColspan(8);
								t.addCell(c2);

								c2 = new PdfPCell(new Paragraph(mid.getSalesPhone(), FontChinese));
								c2.setFixedHeight(avgHeight);
								c2.setColspan(6);
								t.addCell(c2);
							}
							break;
						}
						m = pdfList.get(j);
						PdfPCell c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getNumber()), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);
					}
				} else {
					for (int j = 0; j < pdfList.size(); j++) {
						m = pdfList.get(j);
						PdfPCell c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getNumber()), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
						c2.setFixedHeight(avgHeight);
						t.addCell(c2);
					}
				}
				t.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), pcb);
			} else {
				recordPerPage = 25;
				int fullPageRequired = pdfList.size() / recordPerPage;
				int remainPage = pdfList.size() % recordPerPage > 1 ? 1 : 0;
				int totalPage = fullPageRequired + remainPage;

				int tempSize = 0;
				String page = null;
				form.setField("defaultText", "<详见销货清单>");
				for (int i = 2; i <= totalPage + 1; i++) {
					stamper.insertPage(i, tem);
					page = "页码: " + (i - 1) + " / " + totalPage;

					pcb = stamper.getUnderContent(i);
					signRect.setTop(756f);// 696f整个页面表格开始的位置//756f

					t = MideaPdfModel.getTempTable("");
					t.setTotalWidth(totalWidth);
					PdfPCell c2 = new PdfPCell(new Paragraph("序号", FontChinese));
					c2.setFixedHeight(27f);
//										c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);

					c2 = new PdfPCell(new Paragraph("名称", FontChinese));
					c2.setFixedHeight(27f);
//										c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);

					c2 = new PdfPCell(new Paragraph("规格", FontChinese));
					c2.setFixedHeight(27f);
//										c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);

					c2 = new PdfPCell(new Paragraph("数量", FontChinese));
					c2.setFixedHeight(27f);
//										c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);

					c2 = new PdfPCell(new Paragraph("单位", FontChinese));
					c2.setFixedHeight(27f);
//										c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);

					c2 = new PdfPCell(new Paragraph("单价（元）", FontChinese));
					c2.setFixedHeight(27f);
//										c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);

					c2 = new PdfPCell(new Paragraph("合价（元）", FontChinese));
					c2.setFixedHeight(27f);
//										c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);

					c2 = new PdfPCell(new Paragraph("备注", FontChinese));
					c2.setFixedHeight(27f);
//										c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
					t.addCell(c2);

					tempSize = 0;
					for (int j = (i - 2) * recordPerPage; tempSize < recordPerPage && j < pdfList.size(); j++) {
						m = pdfList.get(j);
						MideaTable(FontChinese, t, m, avgHeight, j);
						tempSize++;
					}
					t.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), pcb);
					pcb.setFontAndSize(bf, 11f);
//										pcb.newlineShowText(page);//under会出现在最后一个字符串的上方，over会出现在最左下角
					pcb.showTextAligned(Element.ALIGN_CENTER, page, 505f, 775f, 0);
					System.out.println("the" + i + " page' height : " + t.calculateHeights());
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

	private static void MideaTable(Font FontChinese, PdfPTable t, MideaProcModel m, float avgHeight, int j) {
		PdfPCell c2;
		c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
		c2.setFixedHeight(avgHeight);
//								c2.setCalculatedHeight(3f);
		t.addCell(c2);
		c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
		c2.setFixedHeight(avgHeight);
//								c2.setCalculatedHeight(3f);
		t.addCell(c2);
		c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
		c2.setFixedHeight(avgHeight);
//								c2.setCalculatedHeight(3f);
		t.addCell(c2);
		c2 = new PdfPCell(new Paragraph(String.valueOf(m.getNumber()), FontChinese));
		c2.setFixedHeight(avgHeight);
//								c2.setCalculatedHeight(3f);
		t.addCell(c2);
		c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
		c2.setFixedHeight(avgHeight);
//								c2.setCalculatedHeight(3f);
		t.addCell(c2);
		c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
		c2.setFixedHeight(avgHeight);
//								c2.setCalculatedHeight(3f);
		t.addCell(c2);
		c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
		c2.setFixedHeight(avgHeight);
//								c2.setCalculatedHeight(3f);
		t.addCell(c2);
		c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
		c2.setFixedHeight(avgHeight);
//								c2.setCalculatedHeight(3f);
		t.addCell(c2);
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
		fillTemplate2();
	}
}
