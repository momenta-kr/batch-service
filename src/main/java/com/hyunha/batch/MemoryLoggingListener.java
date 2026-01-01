package com.hyunha.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@Slf4j
@Component
public class MemoryLoggingListener {

    @EventListener(ApplicationReadyEvent.class)
    public void logMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();

        log.info("🚀 Application started - Memory usage");
        log.info("🧠 Heap     | used={}MB, committed={}MB, max={}MB",
                toMB(heap.getUsed()),
                toMB(heap.getCommitted()),
                toMB(heap.getMax()));

        log.info("📦 NonHeap  | used={}MB, committed={}MB, max={}MB",
                toMB(nonHeap.getUsed()),
                toMB(nonHeap.getCommitted()),
                toMB(nonHeap.getMax()));
    }

    private long toMB(long bytes) {
        return bytes / (1024 * 1024);
    }
}
