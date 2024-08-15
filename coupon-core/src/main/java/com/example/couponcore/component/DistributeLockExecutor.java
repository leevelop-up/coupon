package com.example.couponcore.component;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class DistributeLockExecutor {

    private final RedissonClient redissonClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void execute(String lockName, long waitMilliSecond, long leaseMilliSecond, Runnable logic) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean isLocked = lock.tryLock(waitMilliSecond, leaseMilliSecond, TimeUnit.MILLISECONDS);
            if (!isLocked) {
                throw new IllegalStateException("[" + lockName + "] lock 획득 실패");
            }
            logic.run();
        } catch (InterruptedException e) {
            log.error("락을 획득하는 동안 스레드가 인터럽트됨: {}, lockName: {}, waitMilliSecond: {}, leaseMilliSecond: {}",
                    Thread.currentThread().getName(), lockName, waitMilliSecond, leaseMilliSecond, e);
            // 인터럽트 상태 복원
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("락과 함께 로직을 실행하는 동안 예외 발생: {}", lockName, e);
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
