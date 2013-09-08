package org.mixer2.sample.dynacsssp.view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mixer2.Mixer2Engine;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.Html;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
        "classpath:mvc-dispatcher-servlet.xml" })
public class IndexViewTest {

    @Autowired
    protected Mixer2Engine mixer2Engine;

    @Autowired
    protected IndexView indexView;

    @Test
    public void testIndexView() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(
                request));

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("helloMessage", "Hello World !");

        indexView.setMixer2Engine(mixer2Engine);
        Html html = indexView.createHtml(model, request, response);

        // assert
        Div div = html.getById("message", Div.class);
        // item name
        assertThat(div.getContent().get(0)
                .toString(), is("Hello World !"));

    }

}
