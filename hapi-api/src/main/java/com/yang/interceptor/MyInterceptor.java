package com.yang.interceptor;

import com.yang.utils.IMoocJSONResult;
import com.yang.utils.JsonUtils;
import com.yang.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author yg
 * @date 2020/8/14 16:08
 */
public class MyInterceptor implements HandlerInterceptor {
    
    @Autowired
    public RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(userToken)){
            String uniqueToken = redisOperator.get(USER_REDIS_SESSION + ":" + userId);

            if (StringUtils.isEmpty(uniqueToken) || StringUtils.isBlank(uniqueToken)) {
                returnErrorResponse(response, new IMoocJSONResult().errorTokenMsg("请登录"));
                return false;
            } else  {
                if (!uniqueToken.equals(userToken)) {
                    // 账号不在当前设备登录
                    returnErrorResponse(response, new IMoocJSONResult().errorTokenMsg("请重新登录"));
                    return false;
                }
            }

        } else {
            returnErrorResponse(response, new IMoocJSONResult().errorTokenMsg("请登录"));
            return false;
        }

        return  true;
    }

    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }
}
