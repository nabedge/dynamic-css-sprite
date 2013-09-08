package org.mixer2.sample.dynacsssp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {

	public static String getContextPath() {
		HttpServletRequest request = getRequest();
		if (request != null) {
			return request.getContextPath();
		} else {
	        return "";
		}
	}
	
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = null;
        if (sra != null) {
        	request = sra.getRequest();
        }
        return request;
    }

}
