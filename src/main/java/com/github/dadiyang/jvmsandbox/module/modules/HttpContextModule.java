package com.github.dadiyang.jvmsandbox.module.modules;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.LoadCompleted;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.github.dadiyang.jvmsandbox.module.advicelistener.HttpServletAdviceListener;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * 请求上下文模块
 * <p>
 * 拦截 javax.servlet.http.HttpServlet.service 方法获取当前 request 和 response 对象，放到 context 中
 * <p>
 * 应用中需要当前请求对象时可以通过 RequestContextHolder.get 静态方法获取
 *
 * @author dadiyang
 * @since 2019/4/9
 */
@MetaInfServices(Module.class)
@Information(id = "http-servlet-context", author = "dadiyang", version = "0.0.1")
public class HttpContextModule implements Module, LoadCompleted {
    private static final Logger log = LoggerFactory.getLogger(CallStackTimeConsumeModule.class);
    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Override
    public void loadCompleted() {
        log.debug("启动拦截 javax.servlet.http.HttpServlet 的 service 方法获取");
        new EventWatchBuilder(moduleEventWatcher)
                .onClass("javax.servlet.http.HttpServlet")
                .includeSubClasses()
                .onBehavior("service")
                .withParameterTypes(
                        "javax.servlet.http.HttpServletRequest",
                        "javax.servlet.http.HttpServletResponse"
                )
                .onWatch(new HttpServletAdviceListener());
    }
}
