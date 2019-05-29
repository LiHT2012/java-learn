package com.pim.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.pim.dao.OrgUserRoleDao;
import com.pim.dao.ProductDao;
import com.pim.exception.PermissionException;
import com.pim.field.JsonData;
import com.pim.thirdparty.aliyun.ALiYunComponent;

@Service
public class PdfServiceImpl implements PdfService {

	@Autowired
	private ALiYunComponent aliyunComponent;
	@Autowired
	private OrgUserRoleDao roleDao;
	@Autowired
	private ProductDao productDao;

	@Override
	public String generatePdf(String pdfType, Float bfSize, MideaPdfModel mid, List<MideaProcModel> pdfList)
			throws IllegalArgumentException, IllegalAccessException {
		// 生成的新文件路径
		String newPDFPath = "testaaaaa.pdf";
		File file = new File(newPDFPath);
		PdfReader reader;
		FileOutputStream out;
		ByteArrayOutputStream bos;
		PdfStamper stamper;
		String url = null;
		try {
			BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);// 字体类型
			Font FontChinese = new Font(bf, bfSize, Font.NORMAL);
			out = new FileOutputStream(file);// 输出流
			reader = new PdfReader(pdfType);// 读取pdf模板

			bos = new ByteArrayOutputStream();
			stamper = new PdfStamper(reader, bos);
			PdfWriter wr = stamper.getWriter();
			Rectangle tem = wr.getPageSize();

			AcroFields form = stamper.getAcroFields();

			Map<String, String> map = MideaPdfModel.getMap(mid);
			// 设置form字体兼容中文
			ArrayList<BaseFont> list = new ArrayList<>();
			list.add(bf);
			form.setSubstitutionFonts(list);

			for (String key : map.keySet()) {
				form.setField(key, map.get(key));
			}

			PdfPTable t = MideaPdfModel.getTempTable(pdfType);

			PdfContentByte pcb = stamper.getUnderContent(1);
			Rectangle signRect = new Rectangle(55.65f, 596f, 538f, 596f);

			float totalWidth = signRect.getRight() - signRect.getLeft();
			t.setTotalWidth(totalWidth);

			/**
			 * mid.getTotalAmount---前端直接传取整的还是后台来取整 double Math.rint(double
			 * a)：四舍五入函数，返回与a的值最相近的整数（但是以浮点数形式存储）
			 */

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
						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getQuantity()), FontChinese));
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
						c2 = new PdfPCell(new Paragraph(m.getProcRemarks(), FontChinese));
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

							c2 = new PdfPCell(new Paragraph(mid.getTotalAmount(), FontChinese));
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

						c2 = new PdfPCell(new Paragraph(mid.getTotalAmount(), FontChinese));
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
								NumberFormat.numberToChineseLimitLenth12(Double.valueOf(mid.getTotalAmount())), FontChinese));
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

						c2 = new PdfPCell(new Paragraph(mid.getValidityPeriod(), FontChinese));
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

						c2 = new PdfPCell(new Paragraph(mid.getDeliveryMethod(), FontChinese));
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

						c2 = new PdfPCell(new Paragraph(mid.getPaymentMethod(), FontChinese));
						c2.setColspan(6);
						c2.setFixedHeight(25f);
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
						c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
						t.addCell(c2);

						c2 = new PdfPCell(new Paragraph(mid.getRemarks(), FontChinese));
						c2.setColspan(7);
						c2.setFixedHeight(50f);
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
								c2.setFixedHeight(25f);
								t.addCell(c2);

								c2 = new PdfPCell(new Paragraph(mid.getTotalAmount(), FontChinese));
								c2.setColspan(6);
								c2.setFixedHeight(25f);
								t.addCell(c2);
							}
							c2 = new PdfPCell(new Paragraph("合计:", FontChinese));
							c2.setColspan(2);
							c2.setFixedHeight(25f);
							t.addCell(c2);
							c2 = new PdfPCell(new Paragraph(mid.getTotalAmount(), FontChinese));
							c2.setColspan(6);
							c2.setFixedHeight(25f);
							t.addCell(c2);
//							FontChinese.setStyle(Font.BOLD);
							c2 = new PdfPCell(new Paragraph("合计(大写):", FontChinese));
							c2.setColspan(2);
							c2.setFixedHeight(25f);
							t.addCell(c2);
							c2 = new PdfPCell(new Paragraph(
									NumberFormat.numberToChineseLimitLenth12(Double.valueOf(mid.getTotalAmount())),
									FontChinese));
							c2.setColspan(6);
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

							c2 = new PdfPCell(new Paragraph(mid.getValidityPeriod(), FontChinese));
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

							c2 = new PdfPCell(new Paragraph(mid.getDeliveryMethod(), FontChinese));
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

							c2 = new PdfPCell(new Paragraph(mid.getPaymentMethod(), FontChinese));
							c2.setColspan(6);
							c2.setFixedHeight(25f);
							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); // 水平居中
							c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
							t.addCell(c2);

							c2 = new PdfPCell(new Paragraph(mid.getRemarks(), FontChinese));
							c2.setColspan(7);
							c2.setFixedHeight(50f);
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
							c2 = new PdfPCell(new Paragraph(String.valueOf(m.getQuantity()), FontChinese));
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
							c2 = new PdfPCell(new Paragraph(m.getProcRemarks(), FontChinese));
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

			FileInputStream input = new FileInputStream(file);
			url = aliyunComponent.uploadPic("testssss", "test.pdf", input, "pdf");
			file.delete();
			System.out.println("生成pdf文件完成！");
		} catch (IOException e) {
			System.out.println(e);
		} catch (DocumentException e) {
			System.out.println(e);
		}
		return url;
	}

	@Override
	public String generateMideaPdf(MideaPdfModel midea, String userId)
			throws IllegalArgumentException, IllegalAccessException, PermissionException {
		String companyId = midea.getCompanyId();
		Integer orgIndex = midea.getOrgIndex();
		//验权：哪种？
		roleDao.checkAdminOrEditorOfOrganization(companyId, orgIndex, userId);
		//根据companyId，orgIndex，userId获取 生成pdf的下一个pdf_no
		
		//拼接文件名：人名+no+date.pdf
		
		return null;
	}
	
	public void getProductList(MideaPdfModel midea) {
		List<MideaProcModel> list = midea.getProductList();
		List<String> productIds = new ArrayList<>(list.size());
		List<MideaProcResp> procList = new ArrayList<>(list.size());
		for(MideaProcModel procM : list) {
			procList.add(new MideaProcResp(procM.getProductId(), procM.getQuantity(), procM.getPrice()));
			productIds.add(procM.getProductId());
		}
		//根据productIds查询详情
		Map<String, JsonData> map = productDao.batchGetProductData(0, productIds);
		for(MideaProcResp resp : procList) {
			resp.setData(map.get(resp.getProductId()));
		}
		
	}

//	@Override
//	public String generatePdf(String pdfType, Float bfSize, MideaPdfModel mid, List<MideaProcModel> pdfList)
//			throws IllegalArgumentException, IllegalAccessException {
//
//		// 生成的新文件路径
//		String newPDFPath = "testaaaaa.pdf";
//		File file = new File(newPDFPath);
//		PdfReader reader;
//		FileOutputStream out;
//		ByteArrayOutputStream bos;
//		PdfStamper stamper;
//		String url = null;
//		try {
//			BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);// 字体类型
//			Font FontChinese = new Font(bf, bfSize, Font.NORMAL);
//			out = new FileOutputStream(file);// 输出流
//			reader = new PdfReader(pdfType);// 读取pdf模板
//
//			bos = new ByteArrayOutputStream();
//			stamper = new PdfStamper(reader, bos);
//			PdfWriter wr = stamper.getWriter();
//			Rectangle tem = wr.getPageSize();
//
//			AcroFields form = stamper.getAcroFields();
//
////			mid = new MideaPdfModel("报价单", "啊啊啊啊啊啊啊啊啊啊啊二", "a啊啊啊啊啊啊啊吧", "customerName", "contact",
////					"saleName", "saleDate", "salesMan", "salesPhone", Arrays.asList(new MideaReq()), "1111",
////					"2019-03-04", "deliveryWay--到付", "payWay", "看加粗了吗");
//			Map<String, String> map = MideaPdfModel.getMap(mid);
//			// 设置form字体兼容中文
//			ArrayList<BaseFont> list = new ArrayList<>();
//			list.add(bf);
//			form.setSubstitutionFonts(list);
//
//			for (String key : map.keySet()) {
//				form.setField(key, map.get(key));
//			}
//
//			PdfPTable t = MideaPdfModel.getTempTable(pdfType);
//
//			// 表格位置
//			int pageNo = form.getFieldPositions("productList").get(0).page;
//			PdfContentByte pcb = stamper.getUnderContent(pageNo);
//			Rectangle signRect = form.getFieldPositions("productList").get(0).position;
////					PdfContentByte pcb = stamper.getUnderContent(1);
////					Rectangle signRect = new Rectangle(55.65f,596f, 538f,596f);
//
//			float totalWidth = signRect.getRight() - signRect.getLeft();
//			t.setTotalWidth(totalWidth);
//			float totalHeight = signRect.getTop() - signRect.getBottom();
//
////			pdfList = new ArrayList<>();
////			pdfList.add(
////					new MideaProcModel("啊啊啊啊啊啊啊啊啊十", "KFR-120T2W/SY-TR(E4)AAAAA", 0, "unit", 100D, 200D, "remarks"));
////			for (int i = 1; i <= 45; i++) {
////				pdfList.add(new MideaProcModel("啊啊啊啊啊啊啊啊啊十", "KFR-120T2W/SY-TR(E4)AAAAA", i, "unit" + i, 100D, 200D,
////						"remarks" + i));
////			}
//
//			// Seperate Page controller
//			int recordPerPage = 15;
//			MideaProcModel m;
//			float avgHeight = totalHeight / recordPerPage;
//			if (pdfList.size() < recordPerPage) {// 只有一页
//				pcb = stamper.getUnderContent(pageNo);
//				if(mid.getSetupCost() != null) {//安装费
//					avgHeight = totalHeight / (recordPerPage+1);
//					for (int j = 0; j <= recordPerPage; j++) {
//						if(j >= pdfList.size()) {//多于data数据
//							if(pdfList.size() == recordPerPage) {
//								PdfPCell c2 = new PdfPCell(new Paragraph("安装费：", FontChinese));
//								c2.setFixedHeight(avgHeight);
//								c2.setColspan(2);
//								t.addCell(c2);
//								
//								c2 = new PdfPCell(new Paragraph(mid.getSetupCost(), FontChinese));
//								c2.setFixedHeight(avgHeight);
//								c2.setColspan(6);
//								t.addCell(c2);
//							} else {
//								PdfPCell c2 = new PdfPCell(new Paragraph("", FontChinese));
//								c2.setFixedHeight(avgHeight*(recordPerPage - pdfList.size()));
//								c2.setColspan(8);
//								t.addCell(c2);
//								
//								c2 = new PdfPCell(new Paragraph(mid.getSetupCost(), FontChinese));
//								c2.setFixedHeight(avgHeight);
//								c2.setColspan(6);
//								t.addCell(c2);
//							}
//						}
//						m = pdfList.get(j);
//						PdfPCell c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getQuantity()), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//					}
//				} else {
//					for (int j = 0; j < pdfList.size(); j++) {
//						m = pdfList.get(j);
//						PdfPCell c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getQuantity()), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//
//						c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
//						c2.setFixedHeight(avgHeight);
//						t.addCell(c2);
//					}
//				}
//				t.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), pcb);
//			} else {
//				recordPerPage = 25;
//				int fullPageRequired = pdfList.size() / recordPerPage;
//				int remainPage = pdfList.size() % recordPerPage > 1 ? 1 : 0;
//				int totalPage = fullPageRequired + remainPage;
//
//				int tempSize = 0;
//				String page = null;
//				form.setField("defaultText", "<详见销货清单>");
//				for (int i = 2; i <= totalPage + 1; i++) {
//					stamper.insertPage(i, tem);
//					page = "页码: " + (i - 1) + " / " + totalPage;
//
//					pcb = stamper.getUnderContent(i);
//					signRect.setTop(756f);// 696f整个页面表格开始的位置//756f
//
//					t = MideaPdfModel.getTempTable(pdfType);
//					t.setTotalWidth(totalWidth);
//					PdfPCell c2 = new PdfPCell(new Paragraph("序号", FontChinese));
//					c2.setFixedHeight(27f);
////							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
//					t.addCell(c2);
//
//					c2 = new PdfPCell(new Paragraph("名称", FontChinese));
//					c2.setFixedHeight(27f);
////							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
//					t.addCell(c2);
//
//					c2 = new PdfPCell(new Paragraph("规格", FontChinese));
//					c2.setFixedHeight(27f);
////							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
//					t.addCell(c2);
//
//					c2 = new PdfPCell(new Paragraph("数量", FontChinese));
//					c2.setFixedHeight(27f);
////							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
//					t.addCell(c2);
//
//					c2 = new PdfPCell(new Paragraph("单位", FontChinese));
//					c2.setFixedHeight(27f);
////							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
//					t.addCell(c2);
//
//					c2 = new PdfPCell(new Paragraph("单价（元）", FontChinese));
//					c2.setFixedHeight(27f);
////							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
//					t.addCell(c2);
//
//					c2 = new PdfPCell(new Paragraph("合价（元）", FontChinese));
//					c2.setFixedHeight(27f);
////							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
//					t.addCell(c2);
//
//					c2 = new PdfPCell(new Paragraph("备注", FontChinese));
//					c2.setFixedHeight(27f);
////							c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER); //水平居中
//					c2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE); // 垂直居中
//					t.addCell(c2);
//
//					tempSize = 0;
//					for (int j = (i - 2) * recordPerPage; tempSize < recordPerPage && j < pdfList.size(); j++) {
//						m = pdfList.get(j);
//						MideaTable(FontChinese, t, m, avgHeight, j);
//						tempSize++;
//					}
//					t.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), pcb);
//					pcb.setFontAndSize(bf, bfSize);
////							pcb.newlineShowText(page);//under会出现在最后一个字符串的上方，over会出现在最左下角
//					pcb.showTextAligned(Element.ALIGN_CENTER, page, 505f, 775f, 0);
//					System.out.println("the" + i + " page' height : " + t.calculateHeights());
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
//			for (int i = 1; i <= reader.getQuantityOfPages(); i++) {
//				importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), i);
//				copy.addPage(importPage);
//			}
//			doc.close();
//			FileInputStream input = new FileInputStream(file);
//			url = aliyunComponent.uploadPic("testssss", "test.pdf", input, "pdf");
//			file.delete();
//			System.out.println("生成pdf文件完成！");
//		} catch (IOException e) {
//			System.out.println(e);
//		} catch (DocumentException e) {
//			System.out.println(e);
//		}
//		return url;
//	}
//
//	private void MideaTable(Font FontChinese, PdfPTable t, MideaProcModel m, float avgHeight, int j) {
//		PdfPCell c2;
//		c2 = new PdfPCell(new Paragraph(String.valueOf(j + 1), FontChinese));
//		c2.setFixedHeight(avgHeight);
////								c2.setCalculatedHeight(3f);
//		t.addCell(c2);
//		c2 = new PdfPCell(new Paragraph(m.getProName(), FontChinese));
//		c2.setFixedHeight(avgHeight);
////								c2.setCalculatedHeight(3f);
//		t.addCell(c2);
//		c2 = new PdfPCell(new Paragraph(m.getModel(), FontChinese));
//		c2.setFixedHeight(avgHeight);
////								c2.setCalculatedHeight(3f);
//		t.addCell(c2);
//		c2 = new PdfPCell(new Paragraph(String.valueOf(m.getQuantity()), FontChinese));
//		c2.setFixedHeight(avgHeight);
////								c2.setCalculatedHeight(3f);
//		t.addCell(c2);
//		c2 = new PdfPCell(new Paragraph(m.getUnit(), FontChinese));
//		c2.setFixedHeight(avgHeight);
////								c2.setCalculatedHeight(3f);
//		t.addCell(c2);
//		c2 = new PdfPCell(new Paragraph(String.valueOf(m.getPrice()), FontChinese));
//		c2.setFixedHeight(avgHeight);
////								c2.setCalculatedHeight(3f);
//		t.addCell(c2);
//		c2 = new PdfPCell(new Paragraph(String.valueOf(m.getSum()), FontChinese));
//		c2.setFixedHeight(avgHeight);
////								c2.setCalculatedHeight(3f);
//		t.addCell(c2);
//		c2 = new PdfPCell(new Paragraph(m.getRemarks(), FontChinese));
//		c2.setFixedHeight(avgHeight);
////								c2.setCalculatedHeight(3f);
//		t.addCell(c2);
//	}
}
