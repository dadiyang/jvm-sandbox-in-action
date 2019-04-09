package com.github.dadiyang.jvmsandbox.module.advicelistener;

import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 调用链追踪切面监听器，
 * <p>
 * 通过两个 ThreadLocal 分别保存 id 和 栈深度
 * <p>
 * id 相同则为同一个调用链，入口方法栈深度为 0
 *
 * @author huangxuyang
 * @since 2019/4/9
 */
public class CallChainAdviceListener extends AdviceListener {
    private static final Logger log = LoggerFactory.getLogger(CallChainAdviceListener.class);
    private static final ThreadLocal<Long> ID_THREAD_LOCAL = new ThreadLocal<Long>();
    private static final ThreadLocal<Integer> STACK_THREAD_LOCAL = new ThreadLocal<Integer>();
    private static AtomicLong idGen = new AtomicLong();

    @Override
    protected void before(Advice advice) throws Throwable {
        // 存放id用于链路追踪
        if (ID_THREAD_LOCAL.get() == null) {
            // 这里直接使用自增id，分布式环境下，建议使用分布式id生成器
            ID_THREAD_LOCAL.set(idGen.getAndIncrement());
        }
        // 模拟线程栈
        if (STACK_THREAD_LOCAL.get() == null) {
            STACK_THREAD_LOCAL.set(0);
        }
        // 任何方法执行时，栈深度加1
        int depth = STACK_THREAD_LOCAL.get() + 1;
        STACK_THREAD_LOCAL.set(depth);
    }

    @Override
    protected void afterReturning(Advice advice) throws Throwable {
        afterAdvice(advice);
    }

    @Override
    protected void afterThrowing(Advice advice) throws Throwable {
        afterAdvice(advice);
    }

    private void afterAdvice(Advice advice) throws Throwable {
        int depth = STACK_THREAD_LOCAL.get();
        try {
            long id = ID_THREAD_LOCAL.get();
            log.info("方法执行完毕, id: {}, 栈深度: {}, 类名: {}, 方法名: {}",
                    id, (depth - 1), advice.getTarget().getClass().toString(), advice.getBehavior().getName());

            // 在这里添加其他需要的操作

        } finally {
            // 方法执行结束后栈深度减1
            int curDepth = depth - 1;
            STACK_THREAD_LOCAL.set(curDepth);
            // 栈深度为 0 表示调用链已结束，移除本线程的 ThreadLocal 变量
            if (curDepth == 0) {
                STACK_THREAD_LOCAL.remove();
                ID_THREAD_LOCAL.remove();
            }
        }
    }

}
