package com.github.dadiyang.jvmsandbox.module;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.github.dadiyang.jvmsandbox.module.advicelistener.TimeTunnelAdviceListener;
import com.github.dadiyang.jvmsandbox.module.util.SummaryUtil;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 时间隧道，记录方法的调用，支持根据 id 进行方法调用回放
 *
 * @author dadiyang
 * date 2018/11/23
 */
@MetaInfServices(Module.class)
@Information(id = "time-tunnel", author = "dadiyang", version = "0.0.1")
public class TimeTunnelModule implements Module {
    private static final Logger log = LoggerFactory.getLogger(TimeConsumeModule.class);
    @Resource
    private ModuleEventWatcher moduleEventWatcher;
    private TimeTunnelAdviceListener timeTunnelAdviceListener = new TimeTunnelAdviceListener();

    /**
     * 开启记录
     */
    @Command("record")
    public void record(Map<String, String> reqParams) {
        try {
            String classPattern = reqParams.get("classPattern");
            String behaviorPattern = reqParams.get("behaviorPattern");
            if (classPattern == null || classPattern.trim().isEmpty() || behaviorPattern == null || behaviorPattern.trim().isEmpty()) {
                log.warn("类匹配模式和方法匹配模式都不能为空");
                return;
            }
            log.info("print time tunnel msg：classPattern:" + classPattern + ", behaviorPattern:" + behaviorPattern);
            new EventWatchBuilder(moduleEventWatcher)
                    .onClass(classPattern)
                    .onBehavior(behaviorPattern)
                    .onWatching().withCall().withLine()
                    .onWatch(timeTunnelAdviceListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command("reinvoke")
    public void reinvoke(Map<String, String> reqParams) {
        String idStr = reqParams.get("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                Advice advice = timeTunnelAdviceListener.getTimePoint(id);
                Object obj = advice.getTarget();
                Object[] params = advice.getParameterArray();
                advice.getBehavior().invoke(obj, params);
                log.info("以相同的参数触发 " + ", id: " + id + " " + advice.getBehavior().getName() + ", 参数: " + SummaryUtil.summary(params, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("请指定需要触发的id");
        }
    }

    @Command("listAllRecords")
    public void listAllRecords() {
        timeTunnelAdviceListener.listAllRecords();
    }


}
