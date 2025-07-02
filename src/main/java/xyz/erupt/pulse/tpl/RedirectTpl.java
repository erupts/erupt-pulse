package xyz.erupt.pulse.tpl;

import xyz.erupt.pulse.prop.XProp;
import lombok.Setter;
import org.springframework.stereotype.Component;
import xyz.erupt.linq.lambda.LambdaSee;
import xyz.erupt.tpl.annotation.EruptTpl;
import xyz.erupt.tpl.annotation.TplAction;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@EruptTpl
@Component
public class RedirectTpl {

    @Resource
    private XProp xProp;

    @Resource
    @Setter
    private HttpServletRequest request;


    @TplAction(value = "redirect/**", path = "/tpl/redirect.ftl")
    public Map<String, Object> redirect() {
        Map<String, Object> map = new HashMap<>();
        map.put(LambdaSee.field(XProp::isDevMode), xProp.isDevMode());
        map.put("path", request.getServletPath().split("/redirect")[1]);
        return map;
    }

}
