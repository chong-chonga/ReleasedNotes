# 													java.util.HashMap JDK 1.8 原理剖析

## 概述

HashMap 自 Jdk1.7 版本开始, 便使用 数组 + 链表 + 红黑树实现

当链表结点数大于 8 时, 将结点转化为红黑树

否则使用普通链表

HashMap 是**线程不安全**的, 同 **ArrayList**

现在来通过源码学习下它的特性

## Node<K,V> implements Map.Entry<K,V>

HashMap 的结点重写了 hashCode() 以及 equals() 方法

1. equals(), true 当 引用相同 / 实例类型为 Entry 且 Key 和 Value 相等时; 否则为 false
2. hashCode(), 调用 Object 的原生方法



## put(K k, V v) 方法

调用内部 putVal() 方法

![image-20210508145822575](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20210508145822575.png)

### 源码剖析

```java
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        // 判断 table 是否为空(null || length = 0), 否则进行 resize() 扩容
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        
        // 判断指定 hash 的索引i对应的结点是为空, 否则调用 newNode() 创建一个新的结点
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        
        
        else {
    	// i 下标对应的结点存在, 判断第一个结点是否和给定的 key 相同(引用相同, 或者 equals())
        // 满足上述情况, 则进行结点替换
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
        // 判断类型是否是为树结点, 否则进行类型转换后调用 putTreeVal() 方法
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            
            
        // 执行到这一步说明 Node 还是链表类型
            else {
                for (int binCount = 0; ; ++binCount) {
                    // 遍历到最后一个结点时, 构造新的结点, 并添加到末尾
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        // ------------------------------------------------
                        // 从0开始计数, 遍历到 TREEIFY_THRESHOLD-1则共遍历了TREEIFY_THRESHOLD次
                        // 如果 遍历次数大于等于 TREEIFY_THRESHOLD, 则转换为红黑树
                        // ------------------------------------------------
                        if (binCount >= TREEIFY_THRESHOLD - 1)
                            treeifyBin(tab, hash);
                        break;
                    }
                    // hash 相同且 引用/equals() 则替换结点
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```
为什么要采用头插法？因为可以这么认为：刚插入的数据更容易被访问到。Redis的解决哈希冲突的方式也是采用拉链法，也是选用头插法。
但Redis的哈希表扩容和HashMap的扩容不一样。Redis要考虑到扩容时的开销。为了降低扩容对处理请求时的影响，Redis使用了摊销的思想。
Redis的扩容是渐进式的，每当插入或查找元素时，都会检查是否正在扩容。如果正在扩容，则会进入`_dictRehashStep`函数，
这个函数会扫描至多10个bucket（一个哈希表中放了多个bucket）。 只要扫描到一个bucket有数据的话，就会将这个bucket的数据转移到`ht[1]`，
然后退出该函数。当所有的bucket都转移完毕的话，就会执行
```c
free(ht[0]);
ht[0] = ht[1];
```
这两行代码。那么这里会有一个问题，如果迟迟没有插入或查找请求，是不是rehash操作也不会完成？
Redis会定时执行`serverCron`函数，这个函数中会根据客户端的数量动态更改`hz`，同时也会进行RDB/AOF持久化，
如果当前正在扩容，则还会循环调用`dictRehash`函数；如果花费时间超过1ms，则退出循环。


### TREEIFY_THRESHOLD 常量说明

![image-20210508151126983](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20210508151126983.png)

### 结论

默认构造的 HashMap, 当单个单元格的结点数量大于 8 个时, 则调用 treeifyBin() 方法构造**红黑树**



## Get(Object o) 方法

### 源码剖析

![image-20210508144721955](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20210508144721955.png)

### java.util.HashMap.TreeNode

```java
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;
        boolean red;
        TreeNode(int hash, K key, V val, Node<K,V> next) {
            super(hash, key, val, next);
        }

        /**
         * Returns root of tree containing this node.
         */
        final TreeNode<K,V> root() {
            for (TreeNode<K,V> r = this, p;;) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }
        ...
        ...
        ...
    }

```



## final Node<K,V>[] resize() 方法

```java
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        // 确定扩容后的 capacity，threshold
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1;
        }
        else if (oldThr > 0) 
            newCap = oldThr;
        else {
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        // 创建新数组并赋值
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else {
                        // 这里使用了两个链表
                        // 一个链表记录不需要更换位置的结点：newIndex = oldIndex;
                        // 一个链表记录需要转移位置的结点：newIndex = oldIndex + oldCap;
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            // 计算最高位的 bit 是否为 1
                            // 如果为 1，说明扩容后 & (cap << 1)  运算会影响 index
                            // 如果为 0，说明扩容后，下标的 & 运算不会影响 index
                            // 这与 cap == 2^n 有关。
                            // 这也是为什么使用到两个链表的原因
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        // JDK 1.7 后，扩容是一整个链表转移位置。
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

```

## 总结
阅读`HashMap`源码之后，还有一些HashMap原理之外的感受：
- 编写代码方面：在HashMap中可以处处见到`if ((x = y.val) == m)`这样的代码，这是SAPP书中提到的一种优化：使用本地变量而不是内存访问。对于要进行内存访问的值，
会尽可能地使用本地变量将值缓存起来，以便后续使用，而不用需要值的时候，都进行内存访问。这是因为能用CPU寄存器和CPU高速缓存的访问速度要远大于内存访问。这样编写的代码
在指令周期上就胜过了一般的代码。
- 策略：HashMap也使用了懒加载，**只有在需要使用的时候才会分配**，才会调用`resize`初始化哈希表；另外，在操作系统级别上，为进程分配的内存也是懒分配的。这么一看，
其实很多设计原理是想通的。

# ConcurrentHashMap JDK.17 



