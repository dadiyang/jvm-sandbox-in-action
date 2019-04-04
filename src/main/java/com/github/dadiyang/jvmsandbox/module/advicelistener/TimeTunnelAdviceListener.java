package com.github.dadiyang.jvmsandbox.module.advicelistener;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.github.dadiyang.jvmsandbox.module.util.LruLinkedHashMap;
import com.github.dadiyang.jvmsandbox.module.util.SummaryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 耗时监控切片通知监听器
 * <p>
 * 打印被监控方法的耗时和参数返回值以及方法中的各个调用方法的耗时及占比
 *
 * @author dadiyang
 * date 2018/11/23
 */
public class TimeTunnelAdviceListener extends AdviceListener {
    private static final Logger log = LoggerFactory.getLogger("SANDBOX-FILE-APPENDER");
    /**
     * 使用实现 Lru 算法的 hashMap，只保存最活跃的时间点，防止内存泄露
     */
    private Map<Integer, Advice> timePoint = Collections.synchronizedMap(new LruLinkedHashMap<Integer, Advice>(256));
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1000);

    @Override
    protected void before(Advice advice) throws Throwable {
        int id = ID_GENERATOR.incrementAndGet();
        log.info("记录，id: " + id + ", 方法：" + getName(advice) + ", 参数：" + SummaryUtil.summary(advice.getParameterArray(), false));
        timePoint.put(id, advice);
    }

    public Advice getTimePoint(int id) {
        return timePoint.get(id);
    }

    private String getName(Advice advice) {
        return advice.getTarget().getClass().getSimpleName() + "." + advice.getBehavior().getName();
    }

    public void listAllRecords() {
        for (Map.Entry<Integer, Advice> entry : timePoint.entrySet()) {
            Advice advice = entry.getValue();
            log.info("记录，id: " + entry.getKey() + ", 方法：" + getName(advice) + ", 参数：" + SummaryUtil.summary(advice.getParameterArray(), false));
        }
    }
}