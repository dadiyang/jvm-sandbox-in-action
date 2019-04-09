package com.github.dadiyang.jvmsandbox.module.modules;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatcher;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.github.dadiyang.jvmsandbox.module.advicelistener.CallStackTimeConsumeAdviceListener;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 调用栈中每个方法调用的耗时
 *
 * @author dadiyang
 * date 2018/11/23
 */
@MetaInfServices(Module.class)
@Information(id = "call-stack-time-consume", author = "dadiyang", version = "0.0.1")
public class CallStackTimeConsumeModule implements Module {
    private static final Logger log = LoggerFactory.getLogger(CallStackTimeConsumeModule.class);
    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Command("print")
    public void print(Map<String, String> req) {
        try {
            String classPattern = req.get("classPattern");
            String behaviorPattern = req.get("behaviorPattern");
            if (classPattern == null || classPattern.trim().isEmpty() || behaviorPattern == null || behaviorPattern.trim().isEmpty()) {
                log.warn("类匹配模式和方法匹配模式都不能为空");
                return;
            }
            log.info("print call-stack-time-consume msg：classPattern:" + classPattern + ", behaviorPattern:" + behaviorPattern);
            EventWatcher watcher = new EventWatchBuilder(moduleEventWatcher)
                    .onClass(classPattern)
                    .onBehavior(behaviorPattern)
                    .onWatching().withCall().withLine()
                    .onWatch(new CallStackTimeConsumeAdviceListener());
        } catch (Exception e) {
            log.error("发生异常", e);
        }
    }
}
