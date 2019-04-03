package org.yasukusury.onlinedocument;

import com.google.common.base.Joiner;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.springframework.web.util.HtmlUtils;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.yasukusury.onlinedocument.biz.service.FileService;
import org.yasukusury.onlinedocument.commons.config.WebConf;
import org.yasukusury.onlinedocument.commons.utils.FileTool;
import org.yasukusury.onlinedocument.commons.utils.OfficeUtils;
import org.yasukusury.onlinedocument.commons.utils.XWPFUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.text.Image.*;

/**
 * @author 30254
 * creadtedate: 2019/3/15
 */
public class FileTest {

    private Process cmd(String cmd) throws IOException {
        return Runtime.getRuntime().exec(cmd);
    }

    @Test
    public void htmlTest() throws IOException {

        String prefix = WebConf.UPLOAD_PATH + "/book/1/";
        String htmlDir = prefix + "html/";
        new File(htmlDir).mkdirs();
        for (File file : new File(prefix + "md").listFiles()) {
            String from = file.getName().substring(0, file.getName().indexOf("."));
            cmd(String.format("pandoc %s -s -o %s.html", file.getAbsolutePath(), htmlDir + from));
        }
    }


    @Test
    public void pdfTest() throws Exception {

        String prefix = WebConf.UPLOAD_PATH + "/book/1/";
        String htmlDir = prefix + "pdf/";
        new File(htmlDir).mkdirs();

        OutputStream os = new FileOutputStream("upload/book/1/1.pdf");
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(new File("upload/book/1/1.html"));
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont("src\\main\\resources\\zip\\msyh.ttc",
                BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

        renderer.layout();
        renderer.createPDF(os);
        os.flush();
        os.close();


    }

    @Test
    public void docxTest() throws Exception {

        String prefix = WebConf.UPLOAD_PATH + "/book/1/";
        String docxDir = prefix + "docx/";
        new File(docxDir).mkdirs();
        StringBuilder sb = new StringBuilder();
        for (File file : new File(prefix + "md").listFiles()) {
            String from = file.getName().substring(0, file.getName().indexOf("."));
            cmd(String.format("pandoc %s -s -o %s.docx \n"
                    , file.getCanonicalPath(), docxDir + from));
        }

//        Process process = cmd(sb.toString());
//        InputStream inputStream = process.getInputStream();
//        while (process.isAlive()){
//            IOUtils.copy(inputStream, System.out);
//            Thread.sleep(3000);
//        }
//        System.out.println(process.exitValue());
    }

    @Test
    public void docxTest2() throws Exception {

        String content;
        try (FileInputStream in = new FileInputStream("upload/book/1/html/6.html")) {
            content = IOUtils.toString(in, "utf-8");
        }
        String path = "e:/wordFile";
        Map<String, Object> param = new HashMap<>();

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        content = HtmlUtils.htmlUnescape(content);
        List<Image> imgs = getImgStr(content);
        int count = 0;

//        for (Image img : imgs) {
//            count++;
//            //处理替换以“/>”结尾的img标签
//            content = content.replace(img.img, "${imgReplace" + count + "}");
//            //处理替换以“>”结尾的img标签
//            content = content.replace(img.img1, "${imgReplace" + count + "}");
//            Map<String, Object> header = new HashMap<>();
//
//            try {
////                File filePath = new File(ResourceUtils.getURL("classpath:").getPath());
////                String imagePath = filePath + "static";
//                String imagePath = "static";
//                //如果没有宽高属性，默认设置为400*300
//                if (img.width == null || img.height == null) {
//                    header.put("width", 400);
//                    header.put("height", 300);
//                } else {
//                    header.put("width", (int) (Double.parseDouble(img.width)));
//                    header.put("height", (int) (Double.parseDouble(img.height)));
//                }
//                header.put("type", "jpg");
//                header.put("content", OfficeUtils.inputStream2ByteArray(new FileInputStream(imagePath), true));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            param.put("${imgReplace" + count + "}", header);
//        }

        try {
            // 生成doc格式的word文档，需要手动改为docx
            byte by[] = content.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream bais = new ByteArrayInputStream(by);
            POIFSFileSystem poifs = new POIFSFileSystem();
            DirectoryEntry directory = poifs.getRoot();
            DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);
            FileOutputStream ostream = new FileOutputStream("e:\\wordFile\\temp.doc");
            poifs.writeFilesystem(ostream);
            bais.close();
            ostream.close();


            DocFmtConvert dfc = new DocFmtConvert();
            String srcDocPath = "e:\\wordFile\\temp.doc";//"upload/book/1/6.doc";
            String descDocPath = "e:\\\\wordFile\\\\temp.docx";//"upload/book/1/6.docx";
            try {
                dfc.convertDocFmt(srcDocPath, descDocPath, 12);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 临时文件（手动改好的docx文件）
            OfficeUtils.CustomXWPFDocument doc = OfficeUtils.generateWord(param, "e:\\wordFile\\temp.docx");
            //最终生成的带图片的word文件
            FileOutputStream fopts = new FileOutputStream("e:\\wordFile\\final.docx");
            doc.write(fopts);
            fopts.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void dccxTest3() throws FileNotFoundException {
        //创建 POIFSFileSystem 对象
        POIFSFileSystem poifs = new POIFSFileSystem();
        //获取DirectoryEntry
        DirectoryEntry directory = poifs.getRoot();
        //创建输出流
        OutputStream out = new FileOutputStream("upload/book/1/test1.docx");
        try {
            //创建文档,1.格式,2.HTML文件输入流
            directory.createDocument("WordDocument", new FileInputStream("upload/book/1/1.html"));
            //写入
            poifs.writeFilesystem(out);
            //释放资源
            out.close();
            System.out.println("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void docxTest4() throws Exception {
        try (FileInputStream in = new FileInputStream("upload/book/1/1.html");
             FileOutputStream out = new FileOutputStream("upload/book/1/1.doc");) {
            POIFSFileSystem poifs = new POIFSFileSystem();
            DirectoryEntry directory = poifs.getRoot();
            DocumentEntry documentEntry = directory.createDocument("WordDocument", in);
            poifs.writeFilesystem(out);

            DocFmtConvert dfc = new DocFmtConvert();
            String srcDocPath = "upload/book/1/1.doc";//"upload/book/1/6.doc";
            String descDocPath = "upload/book/1/1.docx";//"upload/book/1/6.docx";
            try {
                dfc.convertDocFmt(srcDocPath, descDocPath, 12);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void docxTese5() throws Exception {
        File inputFile = new File("upload/book/1/html/6.html"), outputFile = new File("upload/book/1/6.docx");
        // Get the template input stream from the application resources.
        URL resource = inputFile.toURI().toURL();

        // Instanciate the Docx4j objects.
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
        String s = IOUtils.toString(resource, "utf-8");
        s = s.replace("../static/css/markdown.min.css", "../../../markdown.min.css");
        List<Object> list = xhtmlImporter.convert(s, resource.toString());
//        wordMLPackage.getMainDocumentPart().getStyleDefinitionsPart().setCss();

        // Load the XHTML document.
        wordMLPackage.getMainDocumentPart().getContent().addAll(list);

        // Save it as a DOCX document on disc.
        wordMLPackage.save(outputFile);
        // Desktop.getDesktop().open(outputFile);
        inputFile = new File("upload/book/1/1.html");
        outputFile = new File("upload/book/1/1.docx");
        resource = inputFile.toURI().toURL();
        xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);

        list = xhtmlImporter.convert(resource);
        wordMLPackage.getMainDocumentPart().getContent().clear();
        wordMLPackage.getMainDocumentPart().getContent().addAll(list);
        wordMLPackage.save(outputFile);

    }

// 格式大全:前缀对应以下方法的fmt值
// 0:Microsoft Word 97 - 2003 文档 (.doc)
// 1:Microsoft Word 97 - 2003 模板 (.dot)
// 2:文本文档 (.txt)
// 3:文本文档 (.txt)
// 4:文本文档 (.txt)
// 5:文本文档 (.txt)
// 6:RTF 格式 (.rtf)
// 7:文本文档 (.txt)
// 8:HTML 文档 (.htm)(带文件夹)
// 9:MHTML 文档 (.mht)(单文件)
// 10:MHTML 文档 (.mht)(单文件)
// 11:XML 文档 (.xml)
// 12:Microsoft Word 文档 (.docx)
// 13:Microsoft Word 启用宏的文档 (.docm)
// 14:Microsoft Word 模板 (.dotx)
// 15:Microsoft Word 启用宏的模板 (.dotm)
// 16:Microsoft Word 文档 (.docx)
// 17:PDF 文件 (.pdf)
// 18:XPS 文档 (.xps)
// 19:XML 文档 (.xml)
// 20:XML 文档 (.xml)
// 21:XML 文档 (.xml)
// 22:XML 文档 (.xml)
// 23:OpenDocument 文本 (.odt)
// 24:WTF 文件 (.wtf)

    /**
     * 使用jacob进行Word文档格式互转(例:doc2docx、docx2doc)
     *
     * @author Harley Hong
     * @created 2017 /08/09 16:09:32
     */
    public static class DocFmtConvert {
        /**
         * doc格式
         */
        private static final int DOC_FMT = 0;
        /**
         * docx格式
         */
        private static final int DOCX_FMT = 12;

        /**
         * 描述 The entry point of application.
         *
         * @param args the input arguments
         * @author Harley Hong
         * @created 2017 /08/09 16:14:44
         */
        public static void main(String[] args) {
            DocFmtConvert dfc = new DocFmtConvert();
            String srcDocPath = "upload/book/1/6.doc";
            String descDocPath = "upload/book/1/6.docx";
            try {
                dfc.convertDocFmt(srcDocPath, descDocPath, DOCX_FMT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 根据格式类型转换doc文件
         *
         * @param srcPath  doc path 源文件
         * @param descPath the docx path 目标文件
         * @param fmt      fmt 所转格式
         * @return the file
         * @throws Exception the exception
         * @author Harley Hong
         * @created 2017 /08/09 16:14:07 Convert docx 2 doc file.
         */
        public File convertDocFmt(String srcPath, String descPath, int fmt) throws Exception {
            // 实例化ComThread线程与ActiveXComponent
            ComThread.InitSTA();
            ActiveXComponent app = new ActiveXComponent("Word.Application");
            try {
// 文档隐藏时进行应用操作
                app.setProperty("Visible", new Variant(true));
// 实例化模板Document对象
                Dispatch document = app.getProperty("Documents").toDispatch();
// 打开Document进行另存为操作

                File src = new File(srcPath);
                File dest = new File(descPath);
                Dispatch doc = Dispatch.invoke(document, "Open", Dispatch.Method,
                        new Object[]{src.getAbsolutePath(), new Variant(true), new Variant(true)}, new int[1]).toDispatch();
                Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[]{dest.getAbsolutePath(), new Variant(fmt)}, new int[1]);
                Dispatch.call(doc, "Close", new Variant(false));
//                Dispatch.call(doc, "Quit", new Variant(true));
//                Dispatch.invoke(doc, "Quit", Dispatch.Method, new Object[]{new Variant(true)}, new int[1]);
                return dest;
            } finally {
// 释放线程与ActiveXComponent
                app.invoke("Quit");
                app.safeRelease();
                ComThread.Release();
            }
        }
    }

    //获取html中的图片元素信息
    public static List<Image> getImgStr(String htmlStr) {
        List<Image> pics = new ArrayList<>();

        Document doc = Jsoup.parse(htmlStr);
        Elements imgs = doc.select("img");
        for (Element img : imgs) {
            Image map = new Image();
            if (!"".equals(img.attr("width"))) {
                map.width = img.attr("width").substring(0, img.attr("width").length() - 2);
            }
            if (!"".equals(img.attr("height"))) {
                map.height = img.attr("height").substring(0, img.attr("height").length() - 2);
            }
            map.img = img.toString().substring(0, img.toString().length() - 1) + "/>";
            map.img1 = img.toString();
            map.src = img.attr("src");
            pics.add(map);
        }
        return pics;
    }

    public static class Image {
        String img;
        String img1;
        String src;
        String width;
        String height;
    }

//    public static class AsianFontProvider extends XMLWorkerFontProvider {
//
//        public Font getFont(final String fontname, final String encoding,
//                            final boolean embedded, final float size, final int style,
//                            final BaseColor color) {
//            BaseFont bf = null;
//            try {
//                bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
//                        BaseFont.NOT_EMBEDDED);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            Font font = new Font(bf, size, style, color);
//            font.setColor(color);
//            return font;
//        }
//    }


    @Test
    public void generateHTML() throws IOException {
        // 从文件中读取markdown内容
        InputStream stream = new FileInputStream("upload/book/1/md/1.md");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));

        List<String> list = reader.lines().collect(Collectors.toList());
        String content = Joiner.on("\n").join(list);

        // markdown to image
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        options.set(Parser.EXTENSIONS, Collections.singletonList(TablesExtension.create()));
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(content);
        String html = renderer.render(document);

        FileOutputStream os = new FileOutputStream("upload/book/1/test1.html");
        IOUtils.write("<html>", os);
        IOUtils.write(FileService.CssHead, os);
        IOUtils.write(html, os);
        IOUtils.write("</html>", os);
        os.close();
    }

    @Test
    public void generatePDF() {
        String htmlFile = "upload/book/1/2.html";
        String pdfFile = "upload/book/1/2.pdf";
        try {
            html2pdf(htmlFile, pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void html2pdf(String htmlFile, String pdfFile) throws Exception {
        // step 1
        String url = new File(htmlFile).toURI().toURL().toString();
        System.out.println(url);
        // step 2
        OutputStream os = new FileOutputStream(pdfFile);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(url);

        // step 3 解决中文支持
        ITextFontResolver fontResolver = renderer.getFontResolver();
        org.w3c.dom.Document document = renderer.getDocument();

        fontResolver.addFont("src/main/resources/zip/msyh.ttc", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        fontResolver.addFont("C:\\Windows\\Fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.getSharedContext().setReplacedElementFactory(new Base64ImgReplacedElementFactory());
        renderer.getSharedContext().getTextRenderer().setSmoothingThreshold(0);

//        renderer.getWriter().setPageSize(PageSize.A4);
//        renderer.getSharedContext().setBaseURL("upload/book/1");
        renderer.layout();

        renderer.createPDF(os);
        os.close();

        System.out.println("create pdf done!!");

    }

    public static class Base64ImgReplacedElementFactory implements ReplacedElementFactory {
        private static int A4WidthInDots = (650) * 20;
        private static int A4HeightInDots = (978) * 20;

        public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac,
                                                     int cssWidth, int cssHeight) {
            org.w3c.dom.Element e = box.getElement();
            if (e == null) {
                return null;
            }
            String nodeName = e.getNodeName();
            // 找到img标签
            if (nodeName.equals("img")) {
                String attribute = e.getAttribute("src");
                FSImage fsImage;
                try {
                    // 生成itext图像
                    fsImage = buildImage(attribute, uac);
                } catch (BadElementException | IOException e1) {
                    fsImage = null;
                }
                if (fsImage != null) {
                    // 对图像进行缩放
                    if (cssWidth != -1 || cssHeight != -1) {
                        fsImage.scale(cssWidth, cssHeight);
                    }
                    if (fsImage.getWidth() > A4WidthInDots || fsImage.getHeight() > A4HeightInDots) {
                        int height = fsImage.getHeight() * A4WidthInDots / fsImage.getWidth();
                        int width = A4WidthInDots;
                        if (height > A4HeightInDots) {
                            width = fsImage.getWidth() * A4HeightInDots / height;
                            height = A4HeightInDots;
                        }
                        fsImage.scale(width, height);
                    }
                    return new ITextImageElement(fsImage);
                }
            }


            return null;
        }

        @Override
        public void reset() {
        }

        @Override
        public void remove(org.w3c.dom.Element e) {
        }

        @Override
        public void setFormSubmissionListener(FormSubmissionListener listener) {
        }


        protected FSImage buildImage(String srcAttr, UserAgentCallback uac) throws IOException,
                BadElementException {
            FSImage fiImg;
            if (srcAttr.toLowerCase().startsWith("data:image/")) {
                String base64Code = srcAttr.substring(srcAttr.indexOf("base64,") + "base64,".length()
                );
                // 解码
                byte[] decodedBytes = com.itextpdf.text.pdf.codec.Base64.decode(base64Code);


                fiImg = new ITextFSImage(getInstance(decodedBytes));
            } else {
                fiImg = uac.getImageResource(srcAttr).getImage();
            }
            return fiImg;
        }
    }

    @Test
    public void zipFiles() throws IOException {
        FileTool.zipFiles("src/main/resources/zip/static.zip"
                , "src/main/resources/static"
                , "src/main/resources/zip/document.html"
                , "src/main/resources/zip/left-nav-js.js"
                , "src/main/resources/zip/说明.txt"
                , "src/main/resources/zip/init.js");
    }

    @Test
    public void mergePDFs() throws IOException, DocumentException {
        PdfReader reader1 = new PdfReader("upload/book/1/1.pdf");
        PdfReader reader2 = new PdfReader("upload/book/1/2.pdf");
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        try (FileOutputStream out = new FileOutputStream("upload/book/1/merge.pdf")) {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            int totalPages = 0;
            totalPages += reader1.getNumberOfPages();
            totalPages += reader2.getNumberOfPages();

            List<PdfReader> readers = new ArrayList<>();
            readers.add(reader1);
            readers.add(reader2);


            PdfOutline root = writer.getRootOutline();

            // Loop through the PDF files and add to the output.
            for (PdfReader pdfReader : readers) {
                int curPage = 1, totalPage = pdfReader.getNumberOfPages();
                document.newPage();
                PdfImportedPage page = writer.getImportedPage(pdfReader, 0);
                cb.addTemplate(page, 0, 0);

                // Create a new page in the target for each source page.
                while (++curPage < totalPage) {
                    document.newPage();
                    page = writer.getImportedPage(pdfReader, curPage);
                    cb.addTemplate(page, 0, 0);
                }
            }
            out.flush();
            document.close();
        }
    }


    @Test
    public void readPDF() throws Exception {
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName("upload/book/1/merged.pdf");
        merger.addSource("upload/book/1/1.pdf");
        merger.addSource("upload/book/1/2.pdf");
        merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
        try (PDDocument document = PDDocument.load(new File("upload/book/1/merged.pdf"));
             PDDocument page1 = PDDocument.load(new File("upload/book/1/1.pdf"));
             PDDocument page2 = PDDocument.load(new File("upload/book/1/2.pdf"));) {
            PDDocumentOutline outline = new PDDocumentOutline();
            document.getDocumentCatalog().setDocumentOutline(outline);
            int page = 0;
            PDOutlineItem parent, child;
            child = new PDOutlineItem();
            outline.addLast(child);
            child.setTitle("1");
            child.setBold(true);
            child.setDestination(document.getPage(page));
            parent = child;
            page += page1.getNumberOfPages();
            child = new PDOutlineItem();
            child.setTitle("1");
            child.setBold(false);
            child.setDestination(document.getPage(page));
            parent.addLast(child);
            document.save(new File("upload/book/1/merged.pdf"));
            System.out.println();
        }
    }

    @Test
    public void mergeDOCXs() throws Exception {

        XWPFDocument destDocument;
        try(InputStream in0 = new FileInputStream("upload/book/1/1.docx");) {
            destDocument = new XWPFDocument(in0);
        }
        try (OutputStream dest = new FileOutputStream("upload/book/1/merged.docx");
             InputStream in1 = new FileInputStream("upload/book/1/1.docx");
             InputStream in2 = new FileInputStream("upload/book/1/6.docx");) {
            String srcString = destDocument.getDocument().xmlText();
            String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
            String sufix = srcString.substring(srcString.lastIndexOf("<"));
            destDocument.getDocument().getBody().set(CTBody.Factory.parse(prefix + sufix));
            XWPFDocument src1Document = new XWPFDocument(in1);
            XWPFDocument src2Document = new XWPFDocument(in2);
            appendBody(destDocument, src1Document, "1", 1L, false);
            appendBody(destDocument, src2Document, "6", 6L, true);
            destDocument.write(dest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void appendBody(XWPFDocument srcDoc, XWPFDocument appendDoc, String markName, Long id, boolean pageBreak) throws XmlException {
        XWPFParagraph p = srcDoc.createParagraph();
        p.setPageBreak(pageBreak);
        CTBookmark bookStart = p.getCTP().addNewBookmarkStart();
        BigInteger markId = new BigInteger(String.valueOf(id));
        bookStart.setId(markId);
        bookStart.setName(markName);

        XWPFRun pRun = XWPFUtils.getOrAddParagraphFirstRun(p, true, false);
        pRun.setText(markName);
        CTMarkupRange bookEnd = p.getCTP().addNewBookmarkEnd();
        bookEnd.setId(markId);

        CTBody src = srcDoc.getDocument().getBody();
        CTBody append = appendDoc.getDocument().getBody();
        XmlOptions optionsOuter = new XmlOptions().setSaveOuter();
        String appendString = append.xmlText(optionsOuter);
        String srcString = src.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        String sufix = srcString.substring(srcString.lastIndexOf("<"));
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);
        src.set(makeBody);
    }
}
