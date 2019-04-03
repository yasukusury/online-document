package org.yasukusury.onlinedocument.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import org.yasukusury.onlinedocument.biz.dto.BookDto;
import org.yasukusury.onlinedocument.biz.dto.ChapterDto;
import org.yasukusury.onlinedocument.biz.entity.Chapter;
import org.yasukusury.onlinedocument.commons.config.WebConf;
import org.yasukusury.onlinedocument.commons.utils.FileTool;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.itextpdf.text.Image.getInstance;

/**
 * @author 30254
 * creadtedate: 2019/1/4
 */
@Service
public class FileService {

    @Autowired
    RedisService redisService;

    private static final String DWNL_HTML_PATTREN = "dwnl-html";
    private static final String DWNL_PDF_PATTREN = "dwnl-pdf";
    private static final String DWNL_DOCX_PATTREN = "dwnl-docx";

    public static final String CssHead = "<head>\n" +
            "<meta charset=\"utf-8\" />\n" +
            "<link rel=\"stylesheet\" href=\"../static/css/markdown.min.css\"/>\n" +
            "</head>\n";

    private static final Parser parser;
    private static final HtmlRenderer htmlRenderer;
    private static final ITextRenderer pdfRenderer;
    private static final WordprocessingMLPackage wordMLPackage;

    static {
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        options.set(Parser.EXTENSIONS, Collections.singletonList(TablesExtension.create()));
        parser = Parser.builder(options).build();
        htmlRenderer = HtmlRenderer.builder(options).build();

        pdfRenderer = new ITextRenderer();
        ITextFontResolver fontResolver = pdfRenderer.getFontResolver();
        WordprocessingMLPackage aPackage;
        try {
            fontResolver.addFont("src/main/resources/zip/msyh.ttc", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            aPackage = WordprocessingMLPackage.createPackage();
        } catch (DocumentException | IOException | InvalidFormatException e) {
            e.printStackTrace();
            aPackage = new WordprocessingMLPackage();
        }
        wordMLPackage = aPackage;
        pdfRenderer.getSharedContext().setReplacedElementFactory(new Base64ImgReplacedElementFactory());
        pdfRenderer.getSharedContext().getTextRenderer().setSmoothingThreshold(0);

    }

    public String image(MultipartFile multipartFile, String url) throws IOException {

        File file = new File(url);
        boolean b = file.createNewFile();
        if (!b) {
            throw new IOException();
        }
        multipartFile.transferTo(file);
        return '/' + url.substring(url.indexOf("upload"));
    }

    public void mdFile(String md, String html, Chapter chapter) throws IOException {
        String prefix = WebConf.UPLOAD_PATH + "/book/" + chapter.getBook();
        String mdPrefix = prefix + "/md/";
        String htmlPrefix = prefix + "/md-html/";
        ensurePath(mdPrefix);
        ensurePath(htmlPrefix);
        FileWriter out1 = new FileWriter(mdPrefix + chapter.getId() + ".md");
        FileWriter out2 = new FileWriter(htmlPrefix + chapter.getId() + ".html");
        out1.write(md);
        out1.flush();
        out1.close();
        out2.write(html);
        out2.flush();
        out2.close();
    }

    public String mdFile(Chapter chapter) throws IOException {
        File file = new File(WebConf.UPLOAD_PATH + "/book/" + chapter.getBook() + "/md/" + chapter.getId() + ".md");
        return getFileString(file);
    }

    public String htmlFile(Chapter chapter) throws IOException {
        File file = new File(WebConf.UPLOAD_PATH + "/book/" + chapter.getBook() + "/md-html/" + chapter.getId() + ".html");
        return getFileString(file);
    }

    private String getFileString(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (FileReader fin = new FileReader(file);
             BufferedReader in = new BufferedReader(fin)) {
            List<String> strings = in.lines().collect(Collectors.toList());
            strings.forEach(s -> sb.append(StringEscapeUtils.escapeJson(s)).append("\\n"));
        }

        return sb.toString();
    }

    private void ensurePath(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public synchronized File packBook2HTMLs(BookDto book) throws IOException {
        String prefix = WebConf.UPLOAD_PATH + "/book/" + book.getId() + "/";
        if (redisService.hasKey(DWNL_HTML_PATTREN + ':' + book.getId())) {
            return new File(prefix + book.getName() + "-html.zip");
        }
        String htmlDir = prefix + "html/";
        generateHTMLs(book, prefix, htmlDir);
        String jsonString = JSON.toJSONString(book);
        String js = "var json = " + jsonString + ";";
        String jsPath = prefix + "json.js";
        FileTool.writeAsJsFile(jsPath, js);
        FileTool.zipFiles(prefix + "temp.zip"
                , prefix + "html"
                , prefix + "upload"
                , jsPath);
        return FileTool.mergeZipFile(prefix + book.getName() + "-html.zip"
                , prefix + "temp.zip"
                , "src/main/resources/zip/static.zip");
    }

    public synchronized File packBook2PDF(BookDto book) throws Exception {
        String prefix = WebConf.UPLOAD_PATH + "/book/" + book.getId() + "/";
        File mainFile = new File(prefix + book.getName() + ".pdf");
        if (redisService.hasKey(DWNL_PDF_PATTREN + ':' + book.getId())) {
            return mainFile;
        }
        String pdfDir = prefix + "pdf/";
        generatePDFs(book, prefix, pdfDir);
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(mainFile.getPath());
        List<ChapterDto> dtoList = sortChapter(book.getSub());
        HashMap<Long, File> fileMap = new HashMap<>();
        for (ChapterDto dto : dtoList) {
            if (dto.getUrl()) {
                File file = new File(pdfDir + dto.getId() + ".pdf");
                fileMap.put(dto.getId(), file);
                merger.addSource(file);
            }
        }
        merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
        try (PDDocument document = PDDocument.load(mainFile);) {
            PDDocumentOutline outline = new PDDocumentOutline();
            document.getDocumentCatalog().setDocumentOutline(outline);
            int page = 0;
            int i = 0;
            HashMap<Long, PDOutlineItem> itemMap = new LinkedHashMap<>();
            while (i < dtoList.size()) {
                ChapterDto dto = dtoList.get(i++);
                PDOutlineItem item = new PDOutlineItem();
                itemMap.put(dto.getId(), item);
                item.setTitle(dto.getName());
                item.setDestination(document.getPage(page));
                if (dto.getPid() > 0) {
                    PDOutlineItem parent = itemMap.get(dto.getPid());
                    parent.setBold(true);
                    parent.addLast(item);
                } else {
                    outline.addLast(item);
                    item.setBold(true);
                    item.openNode();
                }
                if (dto.getUrl()) {
                    try (PDDocument curDoc = PDDocument.load(fileMap.get(dto.getId()));) {
                        page += curDoc.getNumberOfPages();
                    }
                }
            }
            document.save(mainFile);
        }
        return mainFile;
    }

    public synchronized File packBook2DOCX(BookDto book) throws Exception {
        String prefix = WebConf.UPLOAD_PATH + "/book/" + book.getId() + "/";
        File mainFile = new File(prefix + book.getName() + ".docx");
        if (redisService.hasKey(DWNL_DOCX_PATTREN + ':' + book.getId())) {
            return mainFile;
        }
        String docxDir = prefix + "docx/";
        generateDOCXs(book, prefix, docxDir);

        List<ChapterDto> dtoList = sortChapter(book.getSub());

        XWPFDocument destDocument;
        try (OutputStream dest = new FileOutputStream(mainFile);) {
            ChapterDto dto = dtoList.get(0);
            String src = prefix + dto.getId() + ".docx";
            try (InputStream in0 = new FileInputStream(src);) {
                destDocument = new XWPFDocument(in0);
                String srcString = destDocument.getDocument().xmlText();
                String pre = srcString.substring(0, srcString.indexOf(">") + 1);
                String sub = srcString.substring(srcString.lastIndexOf("<"));
                destDocument.getDocument().getBody().set(CTBody.Factory.parse(pre + sub));
            }
            try (InputStream in = new FileInputStream(src);
                 XWPFDocument srcDoc = new XWPFDocument(in);) {
                appendBody(destDocument, srcDoc, book, false);

            }
            for (int i = 1; i < dtoList.size(); i++) {
                dto = dtoList.get(i);
                src = prefix + dto.getId() + ".docx";
                try (InputStream in = new FileInputStream(src);
                     XWPFDocument srcDoc = new XWPFDocument(in);) {
                    appendBody(destDocument, srcDoc, book, true);
                }
            }
            destDocument.write(dest);
            destDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mainFile;
    }

    private List<ChapterDto> sortChapter(List<ChapterDto> dtoList) {
        List<ChapterDto> chapterList = new LinkedList<>();
        LinkedList<ChapterDto> queue = new LinkedList<>(dtoList);
        queue.sort((a, b) -> (int) (!a.getPid().equals(b.getPid()) ? a.getPid() - b.getPid() : a.getSeq() - b.getSeq()));
        while (queue.size() > 0) {
            long pid = 0;
            int i = 0;
            do {
                boolean s = false;
                for (; i < queue.size(); i++) {
                    ChapterDto dto = queue.get(i);
                    if (dto.getPid() == pid) {
                        queue.remove(i);
                        chapterList.add(dto);
                        pid = dto.getId();
                        s = true;
                        break;
                    }
                }
                if (!s) {
                    pid = 0;
                }
            } while (pid > 0);

        }
        return chapterList;
    }

    private synchronized void generateHTMLs(BookDto book, String prefix, String htmlDir) throws IOException {
        if (!redisService.setLock(DWNL_HTML_PATTREN + ':' + book.getId(), 30, TimeUnit.MINUTES)) {
            return;
        }
        String temp = prefix + "upload/book/" + book.getId() + "/";
        File tempFile = new File(temp);
        tempFile.mkdirs();
        FileTool.copyDirectoryStructure(prefix + "images", temp);
        new File(htmlDir).mkdirs();
        for (File file : new File(prefix + "md").listFiles()) {
//            String from = file.getName().substring(0,file.getName().indexOf("."));
//            cmd(String.format("pandoc %s -s -o %s.html", file.getAbsolutePath(), htmlDir + from));

            String fileName = file.getName().split("\\.")[0];
            try (BufferedReader reader = new BufferedReader(new FileReader(file));
                 FileOutputStream os = new FileOutputStream(htmlDir + fileName + ".html");) {

                List<String> list = reader.lines().collect(Collectors.toList());
                String content = Joiner.on("\n").join(list);
                Node document = parser.parse(content);
                String html = htmlRenderer.render(document).replaceAll("<img src=\"/upload/book", "<img src=\"upload/book");

                IOUtils.write("<html>", os);
                IOUtils.write(FileService.CssHead, os);
                IOUtils.write(html, os);
                IOUtils.write("</html>", os);
            }
        }
    }

    private synchronized void generatePDFs(BookDto book, String prefix, String pdfDir) throws Exception {
        generateHTMLs(book, prefix, prefix + "html/");
        if (!redisService.setLock(DWNL_PDF_PATTREN + ':' + book.getId(), 30, TimeUnit.MINUTES)) {
            return;
        }
        new File(pdfDir).mkdirs();
        for (File file : new File(prefix + "html").listFiles()) {

            String fileName = file.getName().split("\\.")[0];
            try (FileInputStream in = new FileInputStream(file);
                 FileOutputStream os = new FileOutputStream(pdfDir + fileName + ".pdf");) {
                String content = IOUtils.toString(in, "utf-8");
                content = content.replace("../static/css/markdown.min.css", "../../../markdown.min.css");
                pdfRenderer.setDocumentFromString(content, file.toURI().toURL().toString());
                pdfRenderer.layout();
                pdfRenderer.createPDF(os);
            }
        }
    }

    private synchronized void generateDOCXs(BookDto book, String prefix, String docxDir) throws Exception {
        generateHTMLs(book, prefix, prefix + "html/");
        if (!redisService.setLock(DWNL_DOCX_PATTREN + ':' + book.getId(), 30, TimeUnit.MINUTES)) {
            return;
        }
        new File(docxDir).mkdirs();
        for (File inputFile : new File(prefix + "html").listFiles()) {
            String fileName = inputFile.getName().split("\\.")[0];
            File outputFile = new File(prefix + fileName + ".docx");
            URL url = inputFile.toURI().toURL();
            String content = IOUtils.toString(url, "utf-8");
            content = content.replace("../static/css/markdown.min.css", "../../../markdown.min.css");
            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
            List<Object> list = xhtmlImporter.convert(content, url.toString());

            wordMLPackage.getMainDocumentPart().getContent().clear();
            wordMLPackage.getMainDocumentPart().getContent().addAll(list);
            wordMLPackage.save(outputFile);
        }
    }

    private static void appendBody(XWPFDocument srcDoc, XWPFDocument appendDoc, BookDto book, boolean pageBreak) throws XmlException {
        XWPFParagraph p = srcDoc.createParagraph();
        p.setPageBreak(pageBreak);
        CTBookmark bookStart = p.getCTP().addNewBookmarkStart();
        BigInteger markId = new BigInteger(String.valueOf(book.getId()));
        bookStart.setId(markId);
        bookStart.setName(book.getName());
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

    public static class Base64ImgReplacedElementFactory implements ReplacedElementFactory {
        private static int A4WidthInDots = (650) * 20;
        private static int A4HeightInDots = (978) * 20;

        public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac,
                                                     int cssWidth, int cssHeight) {
            org.w3c.dom.Element element = box.getElement();
            if (element == null) {
                return null;
            }
            String nodeName = element.getNodeName();
            // 找到img标签
            if (nodeName.equals("img")) {
                String attribute = element.getAttribute("src");
                FSImage fsImage;
                try {
                    // 生成itext图像
                    fsImage = buildImage(attribute, uac);
                } catch (BadElementException | IOException e) {
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

}
