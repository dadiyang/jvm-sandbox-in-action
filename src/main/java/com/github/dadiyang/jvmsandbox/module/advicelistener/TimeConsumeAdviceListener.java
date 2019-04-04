package com.github.dadiyang.jvmsandbox.module.advicelistener;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.github.dadiyang.jvmsandbox.module.util.SummaryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 耗时监控切片通知监听器
 * <p>
 * 打印被监控方法的耗时和参数返回值以及方法中的各个调用方法的耗时及占比
 *
 * @author dadiyang
 * date 2018/11/23
 */
public class TimeConsumeAdviceListener extends AdviceListener {
    private static final Logger log = LoggerFactory.getLogger("SANDBOX-FILE-APPENDER");
    private ThreadLocal<Long> startTime = new ThreadLocal<Long>();
    /**
     * 时间阈值，只监控耗时大于阈值的方法
     */
    private int threshold;

    public TimeConsumeAdviceListener(int threshold) {
        this.threshold = threshold;
    }

    @Override
    protected void afterThrowing(Advice advice) throws Throwable {
        if (startTime.get() != null) {
            long start = startTime.get();
            long time = System.currentTimeMillis() - start;
            if (time > threshold) {
                String param = SummaryUtil.summary(advice.getParameterArray(), false);
                log.info("{} 参数: {}, 抛出异常: {}, 耗时: {}", getName(advice), param, advice.getThrowable().getMessage(), time);
                startTime.remove();
            }
        }
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        startTime.set(System.currentTimeMillis());
    }

    @Override
    protected void afterReturning(Advice advice) throws Throwable {
        if (startTime.get() != null) {
            long start = startTime.get();
            long time = System.currentTimeMillis() - start;
            if (time > threshold) {
                String param = SummaryUtil.summary(advice.getParameterArray(), false);
                String returnObj = SummaryUtil.summary(advice.getReturnObj(), false);
                log.info("{} 参数: {} 返回值: {},  耗时: {}", getName(advice), param, returnObj, time);
                startTime.remove();
            }
        }
    }

    private String getName(Advice advice) {
        if (advice == null || advice.getTarget() == null || advice.getClass() == null) {
            return "";
        }
        return advice.getTarget().getClass().getSimpleName() + "." + (advice.getBehavior() == null ? "" : advice.getBehavior().getName());
    }
}