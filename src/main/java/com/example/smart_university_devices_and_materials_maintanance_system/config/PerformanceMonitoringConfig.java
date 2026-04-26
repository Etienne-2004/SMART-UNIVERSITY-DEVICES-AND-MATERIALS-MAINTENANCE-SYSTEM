package com.example.smart_university_devices_and_materials_maintanance_system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class PerformanceMonitoringConfig implements WebMvcConfigurer, HealthIndicator {

    private final ConcurrentHashMap<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> responseTimes = new ConcurrentHashMap<>();
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PerformanceInterceptor());
    }

    @Override
    public Health health() {
        long avgResponseTime = totalRequests.get() > 0 ? totalResponseTime.get() / totalRequests.get() : 0;
        
        return Health.up()
                .withDetail("totalRequests", totalRequests.get())
                .withDetail("averageResponseTime", avgResponseTime + "ms")
                .withDetail("endpoints", requestCounts.size())
                .withDetail("status", "Performance monitoring active")
                .build();
    }

    public class PerformanceInterceptor implements HandlerInterceptor {
        private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            startTime.set(System.currentTimeMillis());
            totalRequests.incrementAndGet();
            
            String endpoint = request.getRequestURI();
            requestCounts.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
            
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler, ModelAndView modelAndView) {
            Long start = startTime.get();
            if (start != null) {
                long duration = System.currentTimeMillis() - start;
                totalResponseTime.addAndGet(duration);
                
                String endpoint = request.getRequestURI();
                responseTimes.computeIfAbsent(endpoint, k -> new AtomicLong(0)).addAndGet(duration);
                
                // Log slow requests (> 1000ms)
                if (duration > 1000) {
                    log.warn("Slow request detected: {} took {}ms", endpoint, duration);
                }
                
                startTime.remove();
            }
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) {
            if (ex != null) {
                log.error("Request completed with error: {}", request.getRequestURI(), ex);
            }
        }
    }

    // Performance statistics methods
    public long getRequestCount(String endpoint) {
        return requestCounts.getOrDefault(endpoint, new AtomicLong(0)).get();
    }

    public long getAverageResponseTime(String endpoint) {
        AtomicLong totalTime = responseTimes.get(endpoint);
        AtomicLong count = requestCounts.get(endpoint);
        
        if (count == null || count.get() == 0) return 0;
        return totalTime.get() / count.get();
    }

    public void resetMetrics() {
        requestCounts.clear();
        responseTimes.clear();
        totalRequests.set(0);
        totalResponseTime.set(0);
        log.info("Performance metrics reset");
    }
}
