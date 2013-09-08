package org.mixer2.sample.dynacsssp.service;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mixer2.Mixer2Engine;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.Img;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class CssSpriteService {

    private static Log log = LogFactory.getLog(CssSpriteService.class);

    @Autowired
    private ServletContext servletContext;
    
    private BufferedImage bigImage;

    public BufferedImage getBigImage() {
        return this.bigImage;
    }

    public ConcurrentHashMap<String, String> classAndStyleMap = new ConcurrentHashMap<String, String>();

    private String bigImageUrl;
    
    public String getBigImageUrl() {
        return this.bigImageUrl;
    }
    
    @Autowired
    protected Mixer2Engine mixer2Engine;

    @Autowired
    protected ResourceLoader resourceLoader;

    @PostConstruct
    public void initialize() throws IOException {
        log.info("initializing...");
        String contextPath = this.servletContext.getContextPath();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = format.format(new Date());
        String bigImageUrl = contextPath + "/showBigImage/" + dateStr;
        this.bigImageUrl = bigImageUrl;

        // get target img tag as List
        List<Img> imgList = getTargetImgTagList();

        // create data for css sprite.
        int bigImageWidth = 0;
        int bigImageHeight = 0;
        LinkedHashMap<BufferedImage, Integer> readedImages = new LinkedHashMap<BufferedImage, Integer>();
        for (Img img : imgList) {

            // read image file
            String path = "classpath:m2mockup/m2template/" + img.getSrc();
            File file = resourceLoader.getResource(path).getFile();
            BufferedImage readedImage = ImageIO.read(file);

            // add image to map
            readedImages.put(readedImage, readedImage.getHeight());

            // className
            String className = createSpriteClassName(img.getSrc());

            // create style property
            StringBuilder sb = new StringBuilder();
            sb.append("width:" + readedImage.getWidth() + "px; ");
            sb.append("height:" + readedImage.getHeight() + "px; ");
            sb.append("background:url(" + bigImageUrl + ")");
            sb.append(" 0 -" + bigImageHeight + "px; ");

            // put className and style into map
            this.classAndStyleMap.put(className, sb.toString());

            // increment bigImage width and height
            if (readedImage.getWidth() > bigImageWidth) {
                bigImageWidth = readedImage.getWidth();
            }
            bigImageHeight += readedImage.getHeight();
        }

        // create big image
        this.bigImage = createBigImage(bigImageWidth, bigImageHeight,
                readedImages);
    }

    public String createSpriteClassName(String imgSrc) throws IOException {
        String path = "classpath:m2mockup/m2template/" + imgSrc;
        File file = resourceLoader.getResource(path).getFile();
        String tmp = file.getCanonicalPath();
        tmp = tmp.replace("/", "_");
        tmp = tmp.replace("\\", "_");
        tmp = tmp.replace(".", "_");
        String className = "sprite"
                + Arrays.asList(tmp.split("m2mockup_m2static", 2)).get(1);
        return className;
    }

    private List<Img> getTargetImgTagList() throws IOException {
        // load html template
        String mainTemplate = "classpath:m2mockup/m2template/index.html";
        Html html = mixer2Engine.loadHtmlTemplate(resourceLoader.getResource(
                mainTemplate).getInputStream());
        // get img tags for css sprite
        List<Img> imgList = html.getBody().getDescendants("sprite", Img.class);
        return imgList;
    }

    private BufferedImage createBigImage(int bigImageWidth, int bigImageHeight,
            LinkedHashMap<BufferedImage, Integer> readedImages) {
        BufferedImage bigImage = new BufferedImage(bigImageWidth,
                bigImageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bigImage.getGraphics();
        int nextY = 0;
        for (Entry<BufferedImage, Integer> e : readedImages.entrySet()) {
            g.drawImage(e.getKey(), 0, nextY, null);
            nextY += e.getValue();
        }
        return bigImage;
    }

}
