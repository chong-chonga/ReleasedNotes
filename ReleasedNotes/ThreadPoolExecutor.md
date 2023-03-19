# ThreadPoolExecutor 线程池执行器原理



## 前言

许多地方都用到了池化技术，本质上是对资源进行复用。

除了线程池，还有数据库连接池、对象池等。

复用，复用；创建和销毁一次资源，但是可以多次使用，将创建、销毁的性能消耗均摊到每次使用上，从而降低了性能的损耗。

## 属性

线程池使用 ctl 一个原子类型的变量存储线程池状态和 worker 数量。

并使用静态常量用于表示有效位的掩码：

```java
	// ctl 的初始值是 -1 << 29
	private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
	
	// COUNT_BITS = 29, 在第 29 位截止
    private static final int COUNT_BITS = Integer.SIZE - 3;

	// 掩码 MASK = 1 << 29 -1, 说明有效位为低 29 位
    private static final int COUNT_MASK = (1 << COUNT_BITS) - 1;
```

线程池还有表示线程池状态的常量：

```java
	// 高三位: 111
    private static final int RUNNING    = -1 << COUNT_BITS;
	// 高三位: 000
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
	// 高三位: 001
    private static final int STOP       =  1 << COUNT_BITS;
	// 高三位: 010
    private static final int TIDYING    =  2 << COUNT_BITS;
	// 高三位: 011
    private static final int TERMINATED =  3 << COUNT_BITS;
```

可以看到，是使用高三位表示线程池状态的。RUNNING 是所有状态里唯一的负数，其他状态都是正数且依次递增。

另外，在下面将要看到，很多方法要判断 worker 的数量和线程池的状态，因此用到了以下的一些方法：

```java
	// 取 ctl 的高 3 位
	private static int runStateOf(int c)     { return c & ~COUNT_MASK; }
	// 取 ctl 的低 29 位，得到的值表示 worker 数量
    private static int workerCountOf(int c)  { return c & COUNT_MASK; }
	// 将给定两个参数进行或运算
    private static int ctlOf(int rs, int wc) { return rs | wc; }
```

我们再看看判断线程池状态的便捷方法：**isRunning**

```java
// SHUTDOWN 是所有正数状态里最小的那个，如果 ctl < SHUTDOWN，则只会是 RUNNING 状态的 ctl
private static boolean isRunning(int c) {
    return c < SHUTDOWN;
}
```



## Worker

该类继承自 AQS，同时也实现了 Runnable。

```java
    private final class Worker
        extends AbstractQueuedSynchronizer
        implements Runnable
    {

        private static final long serialVersionUID = 6138294804551838833L;

        /** Thread this worker is running in.  Null if factory fails. */
        final Thread thread;
        /** Initial task to run.  Possibly null. */
        Runnable firstTask;
        /** Per-thread task counter */
        volatile long completedTasks;

        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        // Worker 实现的 run 方法将会在线程 start 后被调用
        // 而该方法实际上执行的是 runWorker() 方法。
        public void run() {
            runWorker(this);
        }

		// 省略 Lock 相关的方法
        // ...
    }

```

我们可以看到，Worker 本身实现了 Runnable，并包含一个 Thread， `this.thread = getThreadFactory().newThread(this);`

因此，该 Thread 执行的是 Worker 的 run() 方法，而 Worker 的 run 方法是直接调用 runWorker() 方法。





#### 线程池创建过程

#### execute(Runnable command)

```java
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();

        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
            // worker数量小于 corePoolSize, 则使用 corePoolSize 调用 addWorker
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        // 尝试将 command 加入到工作队列中
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            // 如果当前线程池非运行状态, 则移除之前添加的 command, 并调用拒绝策略
            if (! isRunning(recheck) && remove(command))
                reject(command);  
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        // 如果工作队列加入失败,则尝试使用 maximumPoolSize 调用 addWorker
        // 如果失败, 则采用拒绝策略
        else if (!addWorker(command, false))
            reject(command);
    }

```



#### submit(Callable<?> command)

```java
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        // 调用了 execute() 方法，所以流程基本一样。
        execute(ftask);
        return ftask;
    }
```



#### addWorker(Runnable firstTask, boolean core)

```java
    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        // 1. 判断当前线程池是否已经 shutdown | stop, 如果是, 则拒绝任务
        for (int c = ctl.get();;) {
            // Check if queue empty only if necessary.
            if (runStateAtLeast(c, SHUTDOWN)
                && (runStateAtLeast(c, STOP)
                    || firstTask != null
                    || workQueue.isEmpty()))
                return false;
            
            for (;;) {
                		// 2.判断当前 worker 是否已经大于 corePoolSize | maximumPoolSize, 如果是, 则不添加 worker
                if (workerCountOf(c)
                    >= ((core ? corePoolSize : maximumPoolSize) & COUNT_MASK))
                    return false;
                // 尝试增加 worker 并原子性增加 ctl 的值
                // 增加成功, 则跳出 retry 循环
                if (compareAndIncrementWorkerCount(c))
                    break retry;
               	// 如果线程池 shutdown 则回到第一步
                c = ctl.get(); 
                if (runStateAtLeast(c, SHUTDOWN))
                    continue retry;
                // 否则重读 c 的值并重试
            }
        }
		
        // worker 数量增加成功时, 进入此代码块
        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            // 创建 Worker, Worker 包含一个从 ThreadFactory 创建的一个 Thread
            w = new Worker(firstTask);
            final Thread t = w.thread;
            // 判断 ThreadFactory 创建的 Thread 是否成功
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on Thr
                    
                  	eadFactory failure or if
                    // shut down before lock acquired.
                    int c = ctl.get();
					
                    // 判断线程池状态是否为 Running | STOP 状态
                    if (isRunning(c) ||
                        (runStateLessThan(c, STOP) && firstTask == null)) {
                        // 判断该线程是否为新创建状态, 如果不是, 则抛出异常
                        if (t.getState() != Thread.State.NEW)
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        workerAdded = true;
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                    }
                    // 后续即释放 ReentranLock, 并尝试启动线程
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    // 启动线程，JVM 会调用 Thread 的 run 方法，而 Worker 的 run 方法调用了 runWorker()
                    // 因此，JVM 使该线程实际执行 runWorker() 方法。
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            // worker 启动失败, 进入失败处理
            // 1. 如果 worker 创建了, 则从 workers 中移除该 worker
            // 2. 减少 worker 数量
            // 3. 尝试检查是否因为该 worker 导致线程池 Ternimate 失败
            if (! workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }

```

上述方法在成功添加 Worker 后，会启动 Worker 中的 Thread 对象。

我们知道，该 Thread 执行的正是 Worker 的 run() 方法（因为该 Thread 是以 Worker 创建的）；而 Worker 的 run() 方法只是简单调用了 runWorker 方法，因此**线程复用的关键就在 runWorker 方法中**。

#### runWorker()

```java
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                // 如果线程池正在停止，确保当前线程被中断
                // 否则，确保其不会被中断
                if ((runStateAtLeast(ctl.get(), STOP) ||
                     (Thread.interrupted() &&
                      runStateAtLeast(ctl.get(), STOP))) &&
                    !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    try {
                        task.run();
                        afterExecute(task, null);
                    } catch (Throwable ex) {
                        afterExecute(task, ex);
                        throw ex;
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

```

Worker 如果当前的 task 为空，则会从任务队列中取任务执行，这个过程可能被阻塞，而且根据情况分为超时等待、永久等待。获取到任务的线程会在当前线程中执行 task。如果获取到的 task 为 null，则会退出 while 循环，转而执行 processWorkerExit，这个方法的作用是销毁线程。

Worker 是继承了 AQS 且实现了 Lock 方法的。在 Worker 没有加锁时，该线程是可以被中断的；在 Worker 加锁时，线程是不可以被中断的。

我们大概就能知道线程的执行流程了：**Worker 里的线程尝试获取 task，如果创建该线程的时候，没有分配 task，则从任务队列中获取；如果线程是超时获取任务的，且没有获取到任务，则会被释放。**



![image-20220325103204271](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220325103204271.png)



#### 线程池总结

创建线程池的关键参数是

- **corePoolSize**：线程池核心大小，在execute（）方法中，首先使用这个条件进行判断，并调用 `addWorker(command, true)`，表示优先创建核心线程，`Thread` 对象由 `ThreadFactory`创建，封装在 `Worker`对象中。
- **maximumPoolSize**：线程池最大大小，只有在任务队列满时，才会采用 `addWorker(command, false)`。
- **BlockingQueue**：阻塞队列，用于保存待执行的任务，可以使用参数指定最大大小，当 `worker` 数量大于 `corePoolSize` 时，会将任务优先保存在这里。
- **RejectedExecutionHandler**：拒绝策略处理器，当线程池被关闭，或者队列已满，则会交由该处理器处理任务。
- **ThreadFactory**：创建`Worker`对象中的`Thread`对象的工厂类，通过 `Worker `构造方法内部完成属性的赋值。

线程池关键的属性是

- **AtomicInteger ctl**：该属性是原子类型，用于保存线程池的状态和`worker`数量，在 `addWorker(Runnable command, boolean core)` 方法中，如果条件满足，创建`Worker`，则会调用 CAS 方法，尝试原子性增加 `ctl`的值；初始值是 `Running` 状态值。
- **HashSet<Worker> workers**：用于保存创建的 `Worker`对象，使用的 Set 集合保存。

通过以上的内容，我们可以自行创建一个线程池，然后不断地调用 `execute()`方法提交所需要执行的任务，线程池将会根据 `corePoolSize` 和线程池状态判断是否可以创建 `Worker`, 如果创建成功，则会启动 Worker 中的线程对象，开始执行任务；如果 `Worker`数量达到 `corePoolSize`，则会将任务放置于任务队列中。只有当队列放不下时，才会尝试超越 `corePoolSize`，达到 `maximumPoolSize`。



## 应用场景

### 1. 响应用户访问请求

一个商城的页面会有大量的信息：优惠活动，品牌特卖，打折商品等。需要查询商品价格、图片、相关活动等信息。这些信息如果使用串行请求，IO等待时间会很长；如果使用并行处理，则可以在 IO 之间穿插查询，提高响应速度。

### 2. 执行计算

对相关订单数据进行导出等，需要查询并统筹计算金额，销量，分类热度等；通过线程池可以完成并行处理。



## 难点

使用线程池最大的问题在于配置线程池的参数。要根据实际业务需求调整参数，如果只是利用 JDK 提供的线程池，则修改参数的成本较高（服务重启）。

解决思路：修改并封装现有的线程池，并添加监控和通知修改参数功能，以便在不重启服务的情况下动态调整线程池参数，并能够查看线程池运行状况。

