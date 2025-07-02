package xyz.erupt.pulse.service;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
public class RequestService {

    public static final String LANG = "lang";

    @Resource
    private HttpServletRequest request;

    public String getLang() {
        return request.getHeader(LANG);
    }

}
