package lock;

import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: zhegong
 * @create: 2019-12-05 13:42
 **/
public class RedisLockUsage {

    /**
     * Redisson的分布式可重入锁RLock Java对象实现了java.util.concurrent.locks.Lock接口，同时还支持自动过期解锁。
     */
    public void testReentrantLock(RedissonClient redisson) {

        RLock lock = redisson.getLock("anyLock");
        try {
            // 1. 最常见的使用方法
            //lock.lock();

            // 2. 支持过期解锁功能,10秒钟以后自动解锁, 无需调用unlock方法手动解锁
            //lock.lock(10, TimeUnit.SECONDS);

            // 3. 尝试加锁，最多等待3秒，上锁以后10秒自动解锁
            boolean res = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (res) {    //成功
                // do your business

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    /**
     * 分布式锁提供了异步执行
     */
    public void testAsyncReentrantLock(RedissonClient redisson) {
        RLock lock = redisson.getLock("anyLock");
        try {
            lock.lockAsync();
            lock.lockAsync(10, TimeUnit.SECONDS);
            Future<Boolean> res = lock.tryLockAsync(3, 10, TimeUnit.SECONDS);

            if (res.get()) {
                // do your business

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    /**
     * Redisson分布式可重入公平锁也是实现了java.util.concurrent.locks.Lock接口的一种RLock对象。
     * 在提供了自动过期解锁功能的同时，保证了当多个Redisson客户端线程同时请求加锁时，
     * 优先分配给先发出请求的线程。
     */
    public void testFairLock(RedissonClient redisson) {

        RLock fairLock = redisson.getFairLock("anyLock");
        try {
            // 最常见的使用方法
            fairLock.lock();

            // 支持过期解锁功能, 10秒钟以后自动解锁,无需调用unlock方法手动解锁
            fairLock.lock(10, TimeUnit.SECONDS);

            // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
            boolean res = fairLock.tryLock(100, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            fairLock.unlock();
        }


        /**
         * 异步执行的相关方法
         */
//        RLock fairLock = redisson.getFairLock("anyLock");
//        fairLock.lockAsync();
//        fairLock.lockAsync(10, TimeUnit.SECONDS);
//        Future<Boolean> res = fairLock.tryLockAsync(100, 10, TimeUnit.SECONDS);

    }

    /**
     * Redisson的RedissonMultiLock对象可以将多个RLock对象关联为一个联锁，每个RLock对象实例可以来自于不同的Redisson实例,
     * 所有的锁都上锁成功才算成功
     *
     * @param redisson1
     * @param redisson2
     * @param redisson3
     */
    public void testMultiLock(RedissonClient redisson1,
                              RedissonClient redisson2, RedissonClient redisson3) {
        RLock lock1 = redisson1.getLock("lock1");
        RLock lock2 = redisson2.getLock("lock2");
        RLock lock3 = redisson3.getLock("lock3");

        RedissonMultiLock lock = new RedissonMultiLock(lock1, lock2, lock3);

        try {
            // 同时加锁：lock1 lock2 lock3, 所有的锁都上锁成功才算成功。
            lock.lock();

            // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
            boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    /**
     * Redisson的RedissonRedLock对象实现了Redlock介绍的加锁算法。该对象也可以用来将多个RLock
     对象关联为一个红锁，每个RLock对象实例可以来自于不同的Redisson实例。
     红锁在大部分节点上加锁成功就算成功
     * @param redisson1
     * @param redisson2
     * @param redisson3
     */
    public void testRedLock(RedissonClient redisson1,
                            RedissonClient redisson2, RedissonClient redisson3){

        RLock lock1 = redisson1.getLock("lock1");
        RLock lock2 = redisson2.getLock("lock2");
        RLock lock3 = redisson3.getLock("lock3");

        RedissonRedLock lock = new RedissonRedLock(lock1, lock2, lock3);
        try {
            // 同时加锁：lock1 lock2 lock3, 红锁在大部分节点上加锁成功就算成功。
            lock.lock();

            // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
            boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void testReadWriteLock(RedissonClient redisson) {
        RReadWriteLock rwlock = redisson.getReadWriteLock("anyRWLock");
        try {
// 最常见的使用方法
            rwlock.readLock().lock();
// 或
//        rwlock.writeLock().lock();

// 支持过期解锁功能
// 10秒钟以后自动解锁
// 无需调用unlock方法手动解锁
            rwlock.readLock().lock(10, TimeUnit.SECONDS);
// 或
//        rwlock.writeLock().lock(10, TimeUnit.SECONDS);

// 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
            boolean res = rwlock.readLock().tryLock(100, 10, TimeUnit.SECONDS);
// 或
//        boolean res = rwlock.writeLock().tryLock(100, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            rwlock.readLock().unlock();
//            rwlock.writeLock().unlock();
        }

    }

    public void testRSemaphore(RedissonClient redisson) throws InterruptedException {
        RSemaphore semaphore = redisson.getSemaphore("semaphore");
        semaphore.acquire();
//或
        semaphore.acquireAsync();
        semaphore.acquire(23);
        semaphore.tryAcquire();
//或
        semaphore.tryAcquireAsync();
        semaphore.tryAcquire(23, TimeUnit.SECONDS);
//或
        semaphore.tryAcquireAsync(23, TimeUnit.SECONDS);
        semaphore.release(10);
        semaphore.release();
//或
        semaphore.releaseAsync();
    }

    public void testPermitExpirableSemaphore(RedissonClient redisson) throws InterruptedException {
        RPermitExpirableSemaphore semaphore = redisson.getPermitExpirableSemaphore("mySemaphore");
//        String permitId = semaphore.acquire();
// 获取一个信号，有效期只有2秒钟。
        String permitId = semaphore.acquire(2, TimeUnit.SECONDS);
// ...
        semaphore.release(permitId);
    }

    public void testCountDownLatch(RedissonClient redisson) throws InterruptedException {
        RCountDownLatch latch = redisson.getCountDownLatch("anyCountDownLatch");
        latch.trySetCount(1);
        latch.await();

// 在其他线程或其他JVM里
        RCountDownLatch latch1 = redisson.getCountDownLatch("anyCountDownLatch");
        latch1.countDown();
    }
}

