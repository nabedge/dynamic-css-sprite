package org.mixer2.sample.dynacsssp.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mixer2.sample.dynacsssp.service.CssSpriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    private static Log log = LogFactory.getLog(IndexController.class);

    @Autowired
    CssSpriteService cssSpriteService;

    @RequestMapping(value = "/")
    public String index(Model model) {
        log.info("going index()");
        String message = "Hello World !";
        model.addAttribute("helloMessage", message);
        return "indexView";
    }

    @RequestMapping(value = "/showBigImage/*", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] showBigImage(HttpServletResponse response) throws IOException {
        log.info("going showBigImage()");
        response.setHeader("Cache-Control","max-age=3600");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(cssSpriteService.getBigImage(), "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

}
