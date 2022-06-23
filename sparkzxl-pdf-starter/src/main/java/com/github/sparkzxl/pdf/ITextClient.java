package com.github.sparkzxl.pdf;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.util.Base64Utils;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * description: itext api client
 *
 * @author zhouxinlei
 */
@Slf4j
public class ITextClient {


    public float top = 785;
    /**
     * 总宽度
     */
    public float totalWidth = 550;
    /**
     * 左边空白宽度
     */
    public float left = 55;
    /**
     * 当前左边距，随着文本的追加而增长
     */
    public float currLeft = 55;

    public float bindLeft = 35;

    public String fontPath = "";

    public String fileName = "";

    public String filePath = "";

    public PdfWriter pdfWriter;

    public PdfDocument pdfDocument;

    public PdfDocumentInfo documentInfo;

    public PdfPage pdfPage;

    public Document document;

    public PdfCanvas pdfCanvas;

    public Canvas canvas;

    public PdfFont font;

    public int pageNum = 0;

    public PageSize pageSize = PageSize.A4;

    /**
     * html转pdf
     *
     * @param html html元素
     * @return byte[] byte数组
     * @throws IOException IO异常
     */
    public static byte[] html2Pdf(String html) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ConverterProperties props = new ConverterProperties();
        // 提供解析用的字体
        FontProvider fp = new FontProvider();
        // 添加标准字体库、无中文
        fp.addStandardPdfFonts();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // 自定义字体路径、解决中文,可先用绝对路径测试。
        fp.addDirectory(classLoader.getResource("font").getPath());
        props.setFontProvider(fp);
        // props.setBaseUri(baseResource); // 设置html资源的相对路径
        HtmlConverter.convertToPdf(html, outputStream, props);
        byte[] result = outputStream.toByteArray();
        outputStream.close();
        return result;
    }

    public void initPdfData() {
        try {
            String osName = System.getProperty("os.name");
            String window = "Window";
            if (osName.contains(window)) {
                fontPath = "C:/Windows/Fonts/simfang.ttf";
            } else {
                fontPath = "/usr/share/fonts/chinese/simfang.ttf";
            }
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            File documentDirs = new File(path.getAbsolutePath(), "static/document/");
            if (!documentDirs.exists()) {
                documentDirs.mkdirs();
            }
            filePath = documentDirs.getPath() + "/";
            File file = new File(filePath + fileName);
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            // 1、创建流对象
            pdfWriter = new PdfWriter(file);
            // 2、创建文档对象
            pdfDocument = new PdfDocument(pdfWriter);
            document = new Document(pdfDocument);
            pdfDocument.setTagged();
            pdfDocument.getCatalog().setLang(new PdfString("zh-cn"));
            pdfDocument.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(false));
            font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
            documentInfo = pdfDocument.getDocumentInfo();
            addNewPage();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 新建页面
     */
    public void addNewPage() {
        pdfDocument.addNewPage();
        pageNum++;
        pdfPage = pdfDocument.getPage(pageNum);
        pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNum));
        canvas = new Canvas(pdfCanvas, pdfDocument, pdfPage.getPageSize());
    }

    /**
     * 页眉
     *
     * @param data @
     */
    public void addHead(String data) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(data);
        paragraph.setFont(font).setFontSize(20);
        paragraph.setBold();
        paragraph.setMarginTop(50);
        paragraph.setTextAlignment(TextAlignment.CENTER);
        canvas.add(paragraph);
        pdfCanvas.setStrokeColor(DeviceCmyk.BLACK).setLineWidth(0.7f).moveTo(left, top - 40).lineTo(totalWidth, top - 40)
                .stroke();
        pdfCanvas.moveTo(left, top - 43).lineTo(totalWidth, top - 43).stroke();
    }

    /**
     * 页尾
     *
     * @param data 字符串
     * @param top  上边距
     */
    public void addFooter(String data, float top) {
        pdfCanvas.setStrokeColor(DeviceCmyk.BLACK).setLineWidth(1.5f).moveTo(left, top).lineTo(totalWidth, top).stroke();
    }

    /**
     * 整横线
     *
     * @param lineWidth 线条粗细
     * @param left      x坐标
     * @param top       y坐标
     */
    public void addFullLine(float lineWidth, float left, float top) {
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNum));
        canvas.setStrokeColor(DeviceCmyk.BLACK).setLineWidth(lineWidth).moveTo(left, top).lineTo(totalWidth, top).stroke();
    }

    /**
     * 装订线
     */
    public void addBindingLine() {
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNum));
        canvas.setStrokeColor(DeviceCmyk.BLACK);
        canvas.setLineDash(3, 3, 10);
        canvas.setLineWidth(0.5f).moveTo(bindLeft, 680).lineTo(bindLeft, 552).stroke();
        addPrintText(537, bindLeft - 6, 13, "装");
        canvas.setLineWidth(0.5f).moveTo(bindLeft, 532).lineTo(bindLeft, 404).stroke();
        addPrintText(389, bindLeft - 6, 13, "订");
        canvas.setLineWidth(0.5f).moveTo(bindLeft, 384).lineTo(bindLeft, 256).stroke();
        addPrintText(241, bindLeft - 6, 13, "线");
        canvas.setLineWidth(0.5f).moveTo(bindLeft, 236).lineTo(bindLeft, 100).stroke();
    }

    /**
     * 指定竖线长度
     *
     * @param lineWidth 线条粗细
     * @param left      x坐标
     * @param topStart  y坐标
     * @param topEnd    结束坐标
     */
    public void addVerticalLine(float lineWidth, float left, float topStart, float topEnd) {
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNum));
        canvas.setStrokeColor(DeviceCmyk.BLACK);
        canvas.setLineWidth(lineWidth).moveTo(left, topStart).lineTo(left, topEnd).stroke();
    }

    /**
     * 指定横线长度
     *
     * @param lineWidth 线条粗细
     * @param left      x坐标
     * @param right     y坐标
     * @param top       结束坐标
     */
    public void addLine(float lineWidth, float left, float right, float top) {
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNum));
        canvas.setStrokeColor(DeviceCmyk.BLACK).setLineWidth(lineWidth).moveTo(left, top).lineTo(right, top).stroke();
    }

    /**
     * 增加矩形线
     *
     * @param lineWidth 线条粗细
     * @param left      左边距
     * @param right     右边距
     * @param top       上边距
     * @param height    高度
     */
    public void addRectLine(float lineWidth, float left, float right, float top, float height) {
        float topEnd = top - height;
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(pageNum));
        canvas.setStrokeColor(DeviceCmyk.BLACK);
        canvas.setLineWidth(lineWidth);

        canvas.moveTo(left, top).lineTo(right, top).stroke();

        canvas.moveTo(left, topEnd).lineTo(right, topEnd).stroke();

        canvas.setLineWidth(lineWidth).moveTo(left, top).lineTo(left, topEnd).stroke();

        canvas.setLineWidth(lineWidth).moveTo(right, top).lineTo(right, topEnd).stroke();

    }

    /**
     * 添加一个段落 exemple 比如一段文字或者少于一行的可以使用
     *
     * @param data       文本
     * @param fontSize   字体大小
     * @param left       左边距
     * @param top        上边距
     * @param isBlod     是否加粗
     * @param isCenter   是否居中
     * @param lineIndent @
     */
    public void addParagraphText(String data, float fontSize, float left, float top, boolean isBlod, boolean isCenter,
                                 boolean lineIndent) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(data);
        paragraph.setWidth(510);
        paragraph.setFont(font).setFontSize(fontSize);
        paragraph.setMarginTop(top);
        setParagraphStyle(left, isBlod, isCenter, lineIndent, paragraph);
    }

    private void setParagraphStyle(float left, boolean isBlod, boolean isCenter, boolean lineIndent, Paragraph paragraph) {
        if (isBlod) {
            paragraph.setBold();
        }
        if (left == 0) {
            paragraph.setMarginLeft(45);
        } else {
            paragraph.setMarginLeft(left);
        }
        if (isCenter) {
            paragraph.setTextAlignment(TextAlignment.CENTER);
        }
        if (lineIndent) {
            paragraph.setFirstLineIndent(50);
        }
        canvas.add(paragraph);
    }

    /**
     * 指定位置添加文本内容
     *
     * @param data       文本
     * @param fontSize   字体大小
     * @param left       左边距
     * @param top        上边距
     * @param isBlod     是否加粗
     * @param isCenter   是否居中
     * @param lineIndent @
     */
    public void addFixedPositionText(String data, float fontSize, float left, float top, boolean isBlod,
                                     boolean isCenter, boolean lineIndent) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(data);
        paragraph.setWidth(500);
        paragraph.setFont(font).setFontSize(fontSize);
        paragraph.setFixedPosition(left, top, 500);
        setParagraphStyle(left, isBlod, isCenter, lineIndent, paragraph);
    }

    /**
     * 可控文本位置填充（适合短文本）
     *
     * @param top      上边距
     * @param left     左边距
     * @param fontSize 字体大小
     * @param str      文本
     */
    public void addPrintText(float top, float left, float fontSize, Object str) {
        pdfCanvas.beginText().setFontAndSize(font, fontSize).moveText(left, top).showText(String.valueOf(str))
                .endText();
    }

    /**
     * 垂直文本添加
     *
     * @param top      上边距
     * @param left     左边距
     * @param fontSize 字体大小
     * @param str      文本
     */
    public void addVerticalText(float top, float left, float fontSize, String str) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(str);
        paragraph.setWidth(15);
        paragraph.setFont(font).setFontSize(fontSize);
        paragraph.setFixedPosition(left, top, 15);
        canvas.add(paragraph);
        canvas.flush();
    }

    /**
     * 添加图片
     *
     * @param left      左边距
     * @param top       上边距
     * @param width     宽度
     * @param imagePath 图片路径
     * @throws MalformedURLException 异常
     */
    public void addImage(float left, float top, float width, String imagePath) throws MalformedURLException {
        Image fox = new Image(ImageDataFactory.create(imagePath));
        fox.setFixedPosition(left, top, width);
        document.add(fox);
    }

    public void addImageByBase64(float left, float top, float width, String base64) throws MalformedURLException {
        String imgBase64 = base64.replaceAll("data:image/png;base64,", "");
        byte[] data = Base64Utils.decodeFromString(imgBase64);
        Image fox = new Image(ImageDataFactory.create(data));
        fox.setFixedPosition(left, top, width);
        canvas.add(fox);
    }

    public String pdfToImg(String pdfPath) {
        File file = new File(pdfPath);
        PDDocument pdDocument;
        try {
            String orderPath = file.getParent();
            String fileName = file.getName();
            int index = fileName.indexOf(".");
            fileName = fileName.substring(0, index);
            String imgPath = orderPath + File.separator + fileName + ".png";
            List<BufferedImage> pnglist = new ArrayList<>();
            pdDocument = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 100);
                pnglist.add(image);
            }
            imageMerging(pnglist, imgPath);
            pdDocument.close();
            return imgPath;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void imageMerging(List<BufferedImage> pngList, String outPath) throws IOException {
        int height = 0,
                extWidth = 0,
                maxWidth = 0,
                extHeight,
                extHeight1,
                picNum = pngList.size();
        int[] heightArray = new int[picNum];
        int[] widthArray = new int[picNum];
        BufferedImage buffer;
        List<int[]> imgRgb = new ArrayList<>();
        int[] extImgRgb;
        for (BufferedImage bufferedImage : pngList) {
            buffer = bufferedImage;
            if (buffer.getWidth() > maxWidth) {
                maxWidth = buffer.getWidth();
            }
        }
        for (int i = 0; i < picNum; i++) {
            buffer = pngList.get(i);
            heightArray[i] = extHeight = buffer.getHeight();
            widthArray[i] = extWidth = buffer.getWidth();
            height += extHeight;
            extImgRgb = new int[extWidth * extHeight];
            extImgRgb = buffer.getRGB(0, 0, extWidth, extHeight, extImgRgb, 0, extWidth);
            imgRgb.add(extImgRgb);
        }
        extHeight = 0;
        BufferedImage imageResult = new BufferedImage(extWidth, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < picNum; i++) {
            extHeight1 = heightArray[i];
            extWidth = widthArray[i];
            if (i != 0) {
                extHeight += heightArray[i - 1];
            }
            imageResult.setRGB((maxWidth - extWidth) / 2, extHeight, extWidth, extHeight1, imgRgb.get(i), 0, extWidth);
        }
        File outFile = new File(outPath);
        ImageIO.write(imageResult, "png", outFile);
    }
}
