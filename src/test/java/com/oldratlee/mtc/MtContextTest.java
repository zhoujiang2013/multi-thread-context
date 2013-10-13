package com.oldratlee.mtc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

/**
 * @author ding.lid
 */
public class MtContextTest {

    static class Task implements Runnable {
        final String value;

        Task(String value) {
            this.value = value;
        }

        MtContext context;

        @Override
        public void run() {
            context = MtContext.getContext();
            context.set("key", value);
            context.set("p", context.get("p") + value);
        }
    }

    @Test
    public void test_thread_independent() throws Exception {
        MtContext.getContext().set("parent", "parent");
        MtContext.getContext().set("p", "p0");

        Task task1 = new Task("1");
        Thread thread1 = new Thread(task1);

        Task task2 = new Task("2");
        Thread thread2 = new Thread(task2);

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Child independent & Inheritable
        assertEquals("1", task1.context.get("key"));
        assertEquals("parent", task1.context.get("parent"));
        assertEquals("p01", task1.context.get("p"));
        assertNull(task1.context.get("NotExisted"));
        assertEquals(3, task1.context.get().size());

        assertEquals("2", task2.context.get("key"));
        assertEquals("p02", task2.context.get("p"));
        assertNull(task2.context.get("NotExisted"));
        assertEquals(3, task2.context.get().size());

        // Context is not same
        assertNotSame(task1.context, task2.context);

        assertNotSame(MtContext.getContext(), task1.context);
        assertNotSame(MtContext.getContext(), task2.context);

        // children do not effect parent
        assertEquals(2, MtContext.getContext().get().size());
        assertEquals("parent", MtContext.getContext().get("parent"));
        assertEquals("p0", MtContext.getContext().get("p"));
    }
}