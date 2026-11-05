package org.practice.spring_observability.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final Counter ordersPlaced;
    private final Counter ordersFailed;
    private final AtomicInteger ordersInProgess = new AtomicInteger(0);

    public OrderController(MeterRegistry registry) {
        this.ordersPlaced = Counter.builder("orders.placed").register(registry);
        this.ordersFailed = Counter.builder("orders.failed").register(registry);
        Gauge.builder("orders.in.progress", ordersInProgess, AtomicInteger::get).register(registry);
    }

    @PostMapping("/place")
    @Timed(value = "order.time")
    public void placeOrder() {
        ordersInProgess.incrementAndGet();
        try {
            sleep((long) (Math.random() * 300)); // pretend work
            if (Math.random() < 0.1) {
                ordersFailed.increment();
                throw new RuntimeException("payment failed");
            }
            ordersPlaced.increment();
        } finally {
            ordersInProgess.decrementAndGet();
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) { }
    }
}
