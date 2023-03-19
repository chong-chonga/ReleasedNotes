# JVM-Garbage Collection



## 分代垃圾回收思想

创建的对象，有一小部分是会长期使用的，大部分是临时使用的。针对临时使用的对象的内存空间需要及时回收。

将堆划分为 young generation （年轻代）和 old generation （老年代）可以针对上述两种对象采取不同的回收策略，而**不用进行整堆扫描**。



### Minor gc

对于临时使用的对象，会更频繁的进行 GC （garbage collection），因此采用 Minor gc，只需扫描 young generation 即可。

进行 Minor gc 时，JVM 从所有 GC Roots 出发，标记所有可达的 young generation 的对象，被标记的对象不会被 gc （garbage collector）回收。一个问题是：**老年代的对象可能含有对年轻代对象的引用，这些年轻代对象也应当被标记**。



### Card Table

分代垃圾收集器应当能够知晓哪些老年代对象对年轻代对象有引用。

最简单的方法就是：扫描所有老年代对象，查看其是否包含对年轻代对象的引用。但是这个方法**耗费时间比较大**，不能利用到分代的优点。因此我们需要对其进行优化。

解决这个问题的一个方式是使用 `write barrier` 和 `card table`。

不管使用什么方式，都需要一个 `write barrier`（类似于一个进程） 来记录这些引用的修改。因此区别就在于**如何记录**。

可以维护一个列表，这个列表里存储了所有包含对年轻代对象引用的老年代对象。

可见，使用这个列表对**空间的开销比较大**。

card table 对时间和空间进行了权衡。`card table` 不是直接指明哪些老年代对象包含对年轻代对象的引用，而是将这些老年代对象分组放入固定大小的`buckets`中；并且跟踪哪些 bucket 含有对年轻代对象的引用。

每当程序修改引用的时候，都会将对应内存页标记为**脏页**。JVM 中的每个 512 字节大小的页都对应于 `card table` 上的 8 bit 大小的条目。正常情况下，搜集所有老年代指向年轻代的引用需要遍历老年代所有的对象。为此，我们就需要 `card table`；只需要遍历`card table` 标记为**脏页**中的老年代对象即可。

通过对 page （页）进行划分，既不用遍历所有的老年代对象，也不用存储所有这些对象的引用。



### Summary-总结

Minor gc 流程：

1. 从 `GC Roots` 出发，标记所有可达的 young generation 对象，将它们排除在本次 gc 回收范围之外。
2. 遍历 `card table` ，如果 `card_table[i] == 1`，说明对应的 page 含有指向 young generation 的 old generation 对象。找到相应的对象，并将 young generation 对象标记。
3. gc （garbage collector）进行回收。



### Full gc

整堆扫描采用 Full gc，比较耗时，发生次数越少越好。



