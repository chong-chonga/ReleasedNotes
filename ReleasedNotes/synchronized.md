Java并发工具有三个：synchronized、volatile两个关键字和Lock。synchronized是最方便使用的线程同步工具。
那么`synchronized`关键字是怎么实现的呢？`synchronized`又是如何进行锁升级的？这篇文章主要是解决这两个问题。

## synchronized指令层面探究
要研究一个关键字是如何实现的，就只有通过反编译来查看。先写一个最简单的类`Test`：
```java
public class Test {

	int count = 0;

	private final Object lock = new Object();

	public void add() {
		synchronized (lock) {
			count++;
		}
	}

}
```
进入`Test`类目录，执行以下命令以生成反编译文件res（Test.java文件用什么编码的，就执行什么编码进行编译）：
```shell
javac -encoding UTF-8 Test.java
javap -c -v Test.class > res
```
我们只关注`add`方法的反编译内容：
```txt
 public void add();
    descriptor: ()V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=3, locals=3, args_size=1
         0: aload_0
         1: getfield      #13                 // Field lock:Ljava/lang/Object;
         4: dup
         5: astore_1
         6: monitorenter
         7: aload_0
         8: dup
         9: getfield      #7                  // Field count:I
        12: iconst_1
        13: iadd
        14: putfield      #7                  // Field count:I
        17: aload_1
        18: monitorexit
        19: goto          27
        22: astore_2
        23: aload_1
        24: monitorexit
        25: aload_2
        26: athrow
        27: return
```
可见，`synchronized`在进行自增操作前，分别加入了`monitorenter`和`monitorexit`两个指令。 通过第19号的`goto`指令我们可以知道，20-26的指令是发生异常时的处理流程。

## synchronized应该提供什么样的保证？
我们之所以使用`synchronized`，是因为我们希望同一时间只有一个线程能够执行同步代码块，而且我们还希望前一个线程修改的结果对后一个线程是可见的。
因此`synchronized`要保证**互斥性**和**内存可见性**。我们可以将上述的`monitorexit`指令看成是线程从同步代码块中退出。为了使这个线程的修改结果对后一个线程
是可见的，因此在真正退出前，应该将数据写回内存。`synchronized`并不负责**指令重排序**。

在搞清楚`synchronized`提供了什么样的保证之后，再来看看`synchronized`是如何实现的吧。

## 加锁原理
`synchronized`和`ReentrantLock`一样，都是可重入锁，即锁的持有者与加锁者一致的话，就允许加锁。
`synchronized`根据加锁等级由低到高分别为：偏向锁、轻量级锁、重量级锁。
`synchronized`会在给定的对象的**MarkWord**上记录持有锁的线程的id（OS级别）。如果线程想要进入同步代码块，就需要先将
**MarkWord**上的线程id指向自身。为了达到更轻量的锁控制，`synchronized`使用的CAS操作。也就是说，`synchronized`的实现原理和AQS是相似的。

### 偏向锁
首先说说为什么需要偏向锁。试想一下，假如有一把钥匙，每次来拿这个钥匙的都是同一个人。时间久了，这把钥匙干脆直接交给这个人更方便。

当有线程要进入`synchronized`代码块时，会做如下的操作：
1. 检查**MarkWord**的锁标志位是否为偏向锁状态；如果是，则会使用CAS操作修改MarkWord的标志位和线程ID。CAS成功时则退出，否则进入下一步。
2. 检查**MarkWord**的线程ID是否为当前线程；如果不是，则会去检查持有这个锁的线程是否存活，如果线程不存活了，则会尝试将**MarkWord**的线程ID修改为自己的。
如果线程ID一致或修改操作成功时，则退出，否则进入下一步。
3. 将偏向锁修改为轻量级锁，然后自旋CAS操作。这个自旋是带有适应性的。如果自旋一段次数就拿到锁了，则增大自旋阈值；否则降低自旋阈值。如果在达到阈值前成功抢到锁了，
则退出，否则进入下一步。
4. 锁升级为重量级锁，这时，抢锁的线程都会阻塞，因此会涉及线程的唤醒，这就需要操作系统的介入，因此会消耗更多的时间。




