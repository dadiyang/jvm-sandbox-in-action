package com.github.dadiyang.jvmsandbox.module.advicelistener;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.github.dadiyang.jvmsandbox.module.util.InterfaceProxyUtils;
import com.github.dadiyang.jvmsandbox.module.util.RequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截 javax.servlet.http.HttpServlet.service 方法获取当前 request 和 response 对象，放到 context 中
 * <p>
 * 应用中需要当前请求对象时可以通过 RequestContextHolder.get 静态方法获取
 *
 * @author dadiyang
 * @since 2019/4/7
 */
public class HttpServletAdviceListener extends AdviceListener {
    private static final Logger log = LoggerFactory.getLogger(HttpServletAdviceListener.class);

    @Override
    protected void before(Advice advice) {
        // 只关心顶层调用
        if (!advice.isProcessTop()) {
            return;
        }
        // jvm-sandbox 是在独立的 ClassLoader 中运行的，因此需要做一层代理
        HttpServletRequest request = InterfaceProxyUtils.puppet(HttpServletRequest.class, advice.getParameterArray()[0]);
        HttpServletResponse response = InterfaceProxyUtils.puppet(HttpServletResponse.class, advice.getParameterArray()[1]);
        // 保存 request 对象
        log.debug("设置 request 上下文");
        RequestContextHolder.set(new RequestContextHolder.Context(request, response));
    }

    @Override
    protected void afterReturning(Advice advice) {
        // 只关心顶层调用
        if (!advice.isProcessTop()) {
            return;
        }
        clearRequest();
    }

    private void clearRequest() {
        log.debug("移除 request 上下文");
        RequestContextHolder.remove();
    }

    @Override
    protected void afterThrowing(Advice advice) {
        // 只关心顶层调用
        if (!advice.isProcessTop()) {
            return;
        }
        clearRequest();
    }
}
