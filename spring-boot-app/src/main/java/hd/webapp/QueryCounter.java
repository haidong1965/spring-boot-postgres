package hd.webapp;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.concurrent.atomic.AtomicInteger;

public class QueryCounter implements StatementInspector {

    private static final ThreadLocal<AtomicInteger> COUNTER =
            ThreadLocal.withInitial(AtomicInteger::new);

    @Override
    public String inspect(String sql) {
        COUNTER.get().incrementAndGet();
        return sql;
    }

    public static void reset() {
        COUNTER.get().set(0);
    }

    public static int getCount() {
        return COUNTER.get().get();
    }

    public static void clear() {
        COUNTER.remove();
    }
}
