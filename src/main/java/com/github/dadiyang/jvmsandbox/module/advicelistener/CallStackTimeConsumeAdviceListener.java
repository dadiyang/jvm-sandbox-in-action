package com.github.dadiyang.jvmsandbox.module.advicelistener;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.github.dadiyang.jvmsandbox.module.util.SummaryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.*;

/**
 * 耗时监控切片通知监听器
 * <p>
 * 打印被监控方法的耗时和参数返回值以及方法中的各个调用方法的耗时及占比
 *
 * @author dadiyang
 * date 2018/11/23
 */
public class CallStackTimeConsumeAdviceListener extends AdviceListener {
    private static final Logger log = LoggerFactory.getLogger("SANDBOX-FILE-APPENDER");
    private ThreadLocal<Stack<Long>> startTime = new ThreadLocal<Stack<Long>>();
    private ThreadLocal<Map<String, Long>> methodMsg = new ThreadLocal<Map<String, Long>>();

    @Override
    protected void afterThrowing(Advice advice) throws Throwable {
        addMsg(getName(advice), advice.getParameterArray(), advice.getReturnObj(), "方法: ");
    }

    @Override
    protected void before(Advice advice) throws Throwable {
        recordStart();
    }

    @Override
    protected void afterReturning(Advice advice) throws Throwable {
        addMsg(getName(advice), advice.getParameterArray(), advice.getReturnObj(), "方法: ");
    }

    private String getName(Advice advice) {
        if (advice == null || advice.getTarget() == null || advice.getClass() == null) {
            return "";
        }
        return advice.getTarget().getClass().getSimpleName() + "." + (advice.getBehavior() == null ? "" : advice.getBehavior().getName());
    }

    @Override
    protected void beforeCall(Advice advice, int callLineNum, String callJavaClassName, String callJavaMethodName, String callJavaMethodDesc) {
        recordStart();
    }

    @Override
    protected void afterCallReturning(Advice advice, int callLineNum, String callJavaClassName, String callJavaMethodName, String callJavaMethodDesc) {
        addMsg(callJavaMethodName + "." + callJavaMethodName, null, null, "在第 " + callLineNum + " 行调用 ");
    }

    @Override
    protected void afterCallThrowing(Advice advice, int callLineNum, String callJavaClassName, String callJavaMethodName, String callJavaMethodDesc, String callThrowJavaClassName) {
        addMsg(callJavaMethodName + "." + callJavaMethodName, null, null, "在第 " + callLineNum + " 行调用 ");
    }

    /**
     * 记录开始时间
     */
    private void recordStart() {
        if (startTime.get() == null) {
            startTime.set(new Stack<Long>());
        }
        if (methodMsg.get() == null) {
            methodMsg.set(new LinkedHashMap<String, Long>());
        }
        startTime.get().push(System.currentTimeMillis());
    }

    private void addMsg(String methodName, Object[] params, Object returnVal, String prefix) {
        StringBuilder msg = new StringBuilder(prefix);
        long time = System.currentTimeMillis() - startTime.get().pop();
        msg.append(methodName);
        if (params != null) {
            msg.append(", 参数: ").append(SummaryUtil.summary(params, false));
        }
        if (returnVal != null) {
            msg.append(", 返回值: ").append(SummaryUtil.summary(returnVal, false));
        }
        msg.append(", 耗时: ").append(time);
        Map<String, Long> msgs = methodMsg.get();
        if (startTime.get().isEmpty()) {
            msgs.put(msg.toString(), time);
            printMethodMsg();
            startTime.remove();
            methodMsg.remove();
        } else {
            // startTime 的大小代表调用栈的深度，目前只有一层，所以size永远是1
            msg.insert(0, "-- ");
            msgs.put(msg.toString(), time);
        }
    }

    /**
     * 打印方法信息
     */
    private void printMethodMsg() {
        Map<String, Long> msgs = methodMsg.get();
        // 第一个为总耗时
        Iterator<Long> iterator = msgs.values().iterator();
        long allTime = 0;
        // 最后一个为总时间
        while (iterator.hasNext()) {
            allTime = iterator.next();
        }
        NumberFormat nf = getPercentFormat();
        List<String> rs = new ArrayList<String>();
        for (Map.Entry<String, Long> entry : msgs.entrySet()) {
            double percent = (double) entry.getValue() / allTime;
            rs.add(entry.getKey() + ", 占比: " + nf.format(percent));
        }
        // 最后一个为被监控的方法，其他的是其调用栈
        String theMethodMsg = rs.remove(rs.size() - 1);
        StringBuilder sb = new StringBuilder(theMethodMsg).append("\n");
        for (String r : rs) {
            sb.append(r).append("\n");
        }
        log.info(sb.toString());
    }

    @Override
    protected void beforeLine(Advice advice, int lineNum) {
        super.beforeLine(advice, lineNum);
    }

    private static NumberFormat getPercentFormat() {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        return nf;
    }
}