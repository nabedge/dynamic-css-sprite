package org.mixer2.sample.dynacsssp.view;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mixer2.jaxb.xhtml.A;
import org.mixer2.jaxb.xhtml.H1;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.Img;
import org.mixer2.sample.dynacsssp.service.CssSpriteService;
import org.mixer2.springmvc.AbstractMixer2XhtmlView;
import org.mixer2.xhtml.PathAjuster;
import org.mixer2.xhtml.TagCreator;
import org.mixer2.xhtml.exception.TagTypeUnmatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IndexView extends AbstractMixer2XhtmlView {

    @Autowired
    protected ResourceLoader resourceLoader;

    @Autowired
    protected CssSpriteService cssSpriteService;

    @Override
    protected Html createHtml(Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, TagTypeUnmatchException {

        String helloMessage = (String) model.get("helloMessage");

        // load html template
        String mainTemplate = "classpath:m2mockup/m2template/index.html";
        Html html = getMixer2Engine().loadHtmlTemplate(
                resourceLoader.getResource(mainTemplate).getInputStream());

        H1 h1 = html.getById("helloMessage", H1.class);
        h1.unsetContent();
        h1.getContent().add(helloMessage);

        // css sprite !
        cssSprite(request.getContextPath(), html);
        
        // add direct link to sprite image at the last of body tag.
        A a = TagCreator.a();
        a.setHref(cssSpriteService.getBigImageUrl());
        a.getContent().add("show big image");
        html.getBody().getContent().add(a);

        // replace static file path
        Pattern pattern = Pattern.compile("^\\.+/.*m2static/(.*)$");
        String ctx = request.getContextPath();
        PathAjuster.replacePath(html, pattern, ctx + "/m2static/$1");

        return html;
    }

    private void cssSprite(String contextPath, Html html) throws IOException {
        for (Img img : html.getBody().getDescendants("sprite", Img.class)) {
            String className = cssSpriteService.createSpriteClassName(img
                    .getSrc());
            String style = cssSpriteService.classAndStyleMap.get(className);
            if (style != null) {
                img.setStyle(style);
                // img.getCssClass().add(className);
                img.setSrc(contextPath + "/m2static/img/spacer.gif");
            }
        }
    }

}
