# Java面试-MySQL

## 基础知识

MySQL 中，数据是以 page 为单位，page 的默认大小为 16 KB；不管是数据还是索引，都是以 page 为单位存储的。

MySQL 查询、更新操作都是先查找 **Buffer Pool** 中找，如果没有再从硬盘中加载到 **Buffer Pool**；然后查询、更新都是先操作 **Buffer Pool** 的内容。**Buffer Pool** 就是内存的一块区域，作为硬盘的一块缓存区域，利用内存减少磁盘的 IO，提高性能。
可见，MySQL的数据不会全部驻留在内存。

是 8 bytes）和对应的 page pointer （6 bytes）；索引节点是比较小的，单个 page 是可以存储比较多的索引的，基于性能考虑，索引节点是常驻内存的。

下面以主键索引为例。

在 B+Tree结构中，节点就是上述的 **page**。节点分为 **索引节点（非叶子节点）**和 **数据节点（叶子节点）**。索引节点包含了对应的索引列（这里是 8 bytes）和对应的 `page pointer` （6 bytes）；索引节点是比较小的，单个 page 是可以存储比较多的索引的，基于性能考虑，索引节点是常驻内存的。

数据节点，因为存储了具体的数据，常驻内存是不可能的。因此，IO 基本是发生在读取具体的数据节点。

## 索引

### 索引的数据结构类型

MySQL 中的索引结构分为 **BTREE** 、**HASH**。

**HASH**

**HASH** 和 **HashMap** 类似，对索引列进行 hash 处理，将索引存储在每个 **tab** 中。说到 **HASH**，那就必然会有 **哈希冲突**，MySQL 也是使用拉链法处理的。

**HASH** 类型索引的最大问题是：不支持范围查找。基本上所有的业务都需要支持范围查询，因此很少使用 **HASH** 类型的索引结构。

在 HASH 索引结构下，索引命中与否与哈希值有关，与查询索引列的顺序无关。

### 聚簇索引和非聚簇索引

- 聚簇索引：叶子节点存储了索引和数据，找到了索引也就找到了数据。（InnDB 的主键索引）
- 非聚簇索引：叶子节点不存储数据，存储的是数据对应的地址。（Mylsam 存储引擎的索引）

聚簇索引在插入数据导致需要分页的时候，会有额外的性能消耗。

在聚簇索引之上建立的索引都是辅助索引，辅助索引查找数据一般需要两次。
**为什么辅助索引不直接指向数据的地址，而要指向主键呢？**这是因为MySQL使用了MVCC，MySQL会保存同一数据行的多个版本，
当数据被修改时，数据的地址会发生变化，因此也要修改指向这个数据的索引。因此，为了减少在修改数据时要修改索引的开销，将辅助索引指向主键索引， 时，可以只修改主键索引。

非聚簇索引都是辅助索引，像联合索引、唯一索引等。

### 覆盖索引和非覆盖索引

- 覆盖索引：查询的字段都包含在索引中，无需获取对应的数据，直接从索引中取。
- 非覆盖索引：查询的字段包含非索引字段，需要获取对应的数据。

### 联合索引与最左前缀原则

假设有表 tab1 和索引 idx_p123：

```sql
create table tmp_idx_test.tab1
(
    id        bigint auto_increment
        primary key,
    property1 int                     not null,
    property2 int                     not null,
    property3 int                     not null,
    property4 varchar(30) default '1' not null
);

create index idx_p123
    on tmp_idx_test.tab1 (property1, property2, property3);
```

现在建立了以 property1、property2、property3 的**联合索引**。 

根据 B+-Tree 的特性，是先保证 property1 有序，再保证 property2 有序，最后保证 property3 有序。

为了能够走索引，我们查询条件应按照上述规则进行查询，这也就是我们常说的 **最左前缀原则**。

**满足最左前缀原则的**

```sql
# 使用联合索引
explain select * from tab1 where property2 = 1 and property1 = 3 and property3 = 2;

explain select * from tab1 where property1 = 3 and property2 = 1 and property3 = 3;

explain select * from tab1 where property1 = 1 and property2 = 1;

explain select * from tab1 where property1 = 1;
```
![image-20220421160225526](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220421160225526.png)
只要`where`条件出现的索引字段能够组合成索引的左前缀时，查询就可以走索引，和`where`的先后没有关系。

**不满足最左前缀原则的**

```sql
explain select * from tab1 where property2 = 2;

explain select * from tab1 where property3 = 1;

# 没有遵循最左前缀原则，则不走索引，进行全表扫描
explain select * from tab1 where property2 = 1 and property3 = 1;
```

![image-20220421160153636](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220421160153636.png)

不使用 property1 条件查询，则 property2 和 property3 都是无序的，只能进行全表扫描。

**跳跃式条件字段查询**

上面的查询条件都是严格的从左到右顺序，那如果是 ` where property1 = ? and property3 = ?` 呢

```sql
# 跳跃式条件查询
explain select * from tab1 where property1 = 1 and property3 = 2;
```

![image-20220421160449908](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220421160449908.png)

这个查询为什么也走了索引，不是缺失了 property2 字段吗？

我们分析下查询流程：在这个联合索引的 **B+-Tree**  结构中，property1 是保证有序的，上述查询提供了 property1 ，因此能利用 **B+-Tree** 的索引进行查找满足条件的记录。由于没有提供 property2，因此**索引退化为 property1**（剩余的 property3 只是单纯地对记录进行筛选）。

其实仔细观察 explain 执行结果：key_len  = 4，正是 int 类型字段的大小，说明只使用了 property1 索引字段。印证了我们上述的猜测。

## 日志-(redo log、undo log、bin log)

`redo log`也相当于`write-ahead log`，和`two-phase commit`一样，经常被用于分布式系统中。如Google的Spanner、Microsoft的FaRM， Frangipani等等。
使用这种日志是基于这样的思想：**所有的机器和程序本身都不是 100%稳定的，都有可能出现意外停止运行的情况**。

### redo log （InnoDB 引擎独有）

**redo log** 保证了事务的**持久性**。

当一个事务开启时，对于更新操作，是先操作 **Buffer Pool** 中的数据，也就是内存中的数据。磁盘上的数据此时并没有真正更改。
。这样就会有一个问题：**修改后的内存数据如果没有及时的写入到磁盘，就会丢失修改**。

OS （操作系统）在这方面也是采用了日志来保证自身文件系统元数据（inode、bitmap等）。

**redo log** 就是为了解决这一问题而出现的。在更新缓存中的数据后，记录修改信息，写入到 **redo log buffer** 中。是的，**redo log** 也有对应的 **buffer** （缓存）。

当然，**redo log buffer** 也会出现丢失的情况， 丢失的数据量取决于 **redo log buffer** 的写入**策略**。

- 事务 commit 后，等待后台线程写入磁盘。（MySQL自己的后台线程fsync）
- 事务 commit 时写入磁盘。（每次都调用fsync）
- 事务 commit 时写入操作系统 page cache（操作系统针对dirty page的fsync）。

在写日志时，要在性能和可靠性这两个方面之间做权衡，因此通常会采用以上三种策略。顺达一提，Redis的AOF持久化也是上面这三种策略（大型软件系统之间的设计是相互借鉴的）。

### bin log （主从、主备等策略相关）

不管使用什么存储引擎，只要修改了数据，就会产生**bin log**。主从、主备依赖于 **bin log** 来同步数据。

**bin log** 会直接修改数据的 SQL。

**bin log** 存储的内容有三种格式：

- statement：sql 原来长什么样子就存什么样子。
- row：sql 中使用了 now() 这样会因时间而影响执行结果的函数时，会使用实际产生的值替换该函数。比如 sql 执行时调用 now() 的返回值是 36231，sql 会记录 35231 而不是 now()。
- mixed：上述两种的混合策略，根据 SQL 是否含有 now() 这样的影响执行结果的函数来决定使用何种格式。

写入 **redo log** 时，是先写入 **redo log buffer** 的；那写入 **bin log**  也是先写入对应的 **bin log buffer**。

对应的， **bin log buffer** 的刷盘策略和`redo log buffer`一样。

### undo log （实现事务回滚）

开启事务后，如果事务 **rollback**，怎么恢复以前的数据？

我们当然可以先记录以前的数据，在发生 **rollback** 后，用原数据去覆盖当前数据。但是这样做代价太大了。

更轻量级的做法是记录对应的逆向 SQL，在执行修改数据的 SQL 前，先记录好对应的逆向 SQL。一旦事务 **rollback**，就执行逆向 SQL 即可。

比如

```sql
update tab1 set user_nickname = 'K' where user_open_id = 1;
```

对应的逆向 SQL 为

```sql
update tab1 set user_nickname = 'K'
```
### 小结
**redo log** 让 InnoDB 存储引擎有了崩溃恢复功能。

**bin log** 让 MySQL 集群的数据保持一致。

**undo log** 让 MySQL 的事务提交具有原子性。

说完了MySQL的日志，可以思考一下log与其他领域的关联。

log具有让单机系统恢复的功能，因此很多共识算法也是基于log的，如Raft共识算法、Paxos算法都是保证log的一致性。如果log能保证是一致的，
那么这些机器以相同的状态启动，并按照相同顺序执行这些log必然会得到相同的结果，这也就是状态机（state machine）的定义。
log可以看作是Command的包装。当然，这里会有一个特例：生成时间戳、随机数、UUID这样的log，不同机器执行的结果就会不同。因此有必要将这些结果包含在log中，以便维持分布式系统的一致性。
这也就是为什么`bin log`会有`row`格式。如此看来，MySQL使用的bin log其实和分布式系统中的log的本质是一样的，都是为分布式的情景而设计的。
这就得提一下VMWare在2010年发布的一篇名为[The design of a practical system for fault-tolerant virtual machines](https://dl.acm. org/doi/10.1145/1899928.1899932)论文了，VMWare设计的一个在单核CPU条件下的具有容错的主从（primary-backup）虚拟机系统，primary 和 backup 之间通过
`logging channel`来发送日志和ack，从而保证主从的一致性。这个容错系统有一个特别之处：机器级别上的复制。传统的主从备份是软件层面上的复制，
这样做自然有一个缺点：每个软件都需要独立实现一套复制方案。而VMWare的这个系统做到的机器级别上的复制。
在复制策略上，VMWare采用的复制方案也是`state machine`方案。为了保证两台机器的CPU、内存、IO设备状态是一致的，就需要有机器的全部控制权，而VMM（Virtual Machine Monitor）可以实现这一点。
VMWare是如何检测故障、主从之间通过日志同步等细节还请参阅论文，这里只是做个简单的引伸。


## 锁

MySQL 中的锁有共享锁、排他锁、意向共享锁、意向排他锁、自增锁五种锁类型。

纸上得来终觉浅，还是实践测试一下这些知识的真伪吧。

为了方便测试，我们建立以下的表 `tab1`：

```sql
create table tmp_idx_test.tab1
(
    id        bigint auto_increment
        primary key,
    property1 int                     not null,
    property2 int                     not null,
    property3 int                     not null,
    property4 varchar(30) default '1' not null
);

create index idx_p123
    on tmp_idx_test.tab1 (property1, property2, property3);

```

### 共享锁（S锁、行锁）

测试内容：**共享锁只锁对应的数据行，其他事务可以读取这些行的数据，但是不能修改。对于没有加共享锁的数据行是可以修改的。**

下面按照执行顺序进行叙述。

#### 单行数据加锁

**事务1 对 id = 1 的数据行加 S 锁**

```sql
# 手动开启事务，防止事务自动 commit/rollback 导致锁直接释放
start transaction;

# 尝试获取共享锁 (S锁,行锁); 其他事务只能对该行进行读取，任何修改操作将会阻塞，直至该事务释放。
select * from tab1 where id = 1 lock in share mode;
```

![image-20220423205936073](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220423205936073.png)

- 事务 1 对 `id = 1` 的数据行加了共享锁。

**事务2修改 id = 1 的数据行失败**

事务2 在 事务1 之后执行以下 sql

```sql
start transaction;

# 尝试读取 id = 1 的数据行
select * from tab1 where id = 1;
# 尝试修改 id = 2 的数据行
update tab1 set property4 = 2 where id = 2;
# 尝试修改 id = 1 的数据行
update tab1 set property4 = 2 where id = 1;
```

![image-20220423210056122](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220423210056122.png)

- 事务2 可以读取 ` id = 1` 的数据行，也可以修改 `id = 2` 的数据行。

- 事务2在对 `id = 1` 的数据行进行 **update** 时被阻塞，一直得不到执行。



**事务1修改 id = 1 的数据行成功**

既然事务2不能修改 `id = 1` 的数据行，那么持有 S 锁的事务1能修改吧？

```sql
# 和事务 2 执行相同的 sql，单变量对照嘛
update tab1 set property4 = 2 where id = 1;
```

![image-20220423211011839](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220423211011839.png)

- 事务1持有 id = 1 的数据行的 S 锁，因此可以进行修改。

**事务1 rollback 释放 S 锁**

```sql
rollback;
```

**事务2 修改 id = 1 的数据行成功**

```sql
update tab1 set property4 = 2 where id = 1;
```

![image-20220423211411924](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220423211411924.png)

- 当事务1释放了 id = 1 的数据行的 S 锁时，事务2对 id = 1 的数据行写入成功。



#### 多行数据加锁

我们指定了 id = 1 的数据行，那么只对 id = 1 的数据行加了 S 锁。

现在我们修改 SQL，使其影响的行数变多，再看看效果。

**表中记录**

![image-20220424150211262](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220424150211262.png)

可以看到，property3 和 property4 在当前记录中的值是相同的：`id = 1 ~ 5` 的记录对应的值为 1，`id = 6` 的记录对应的值为 4。

为了对比一下索引的影响，这里 **property3 建立了索引，property4 没有建立索引**。

![image-20220424132908339](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220424132908339.png)

##### 普通索引测试

**事务1 对 property3 = 1 的行记录加锁**

```sql
start transaction;

select * from tab1 where property3 = 1 lock in share mode;
```

![image-20220424134105766](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220424134105766.png)

- 猜想：事务1 对 property3 = 1 的记录加锁， property3 = 4 的记录没有加 S 锁。事务2 可以写 property3 = 4 的记录，但不能写 property3 = 1 的记录。



**事务2 对所有数据行修改失败**

```sql
start transaction;

update tab1 set property1 = 1 where property3 = 4;

update tab1 set property1 = 1 where property3 = 1;
```

![image-20220424134231017](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220424134231017.png)

![image-20220424134256750](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220424134256750.png)

- 事实上，事务2 对所有的记录都不能修改（也可以换成 property4 试试，也是同样的情况）。

做完以上的实验，说明事务1对 表 `tab1` 加的不是行锁，而是表锁了。

为什么会出现这种情况？不是应该只对 property3 = 1 的记录加 S 锁吗？

我们使用 `explain` 语句来查明事务1是怎么做的。

```sql
explain select * from tab1 where property3 = 1 lock in share mode;
```

![image-20220424144716154](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220424144716154.png)

猜测：**全表扫描导致对整张表加锁**。



##### 主键索引测试

上面使用的是普通索引，且查询方式是**全表扫描**，下面我们来试试主键索引 id 的多行数据加锁。

**事务1**

##### 唯一索引测试

上面的测试 property3 是普通索引，可以再对 property2 建立一个 **unqiue** 索引。

```sql
create unique index tab1_property2_uindex
    on tab1 (property2);
```

#### 结论

- 一个事务对数据行加上了 S 锁后，其他事务无法只能读不能写。
- S 锁可以被多个事务持有（可以自行进行实验）。
- 当一个数据行被多个事务加上了 S 锁，任何一个事务都不能对该行数据进行写（可自行实验）。
- 当一个数据行只被一个事务加上了 S 锁，持有该锁的事务可以对该行数据进行写（可自行实验）。



### 排他锁（X锁、行锁）


### 自增锁

使用以下 SQL 可以查询自增锁的模式（mode），默认为1。

```sql
show variables like 'innodb_autoinc_lock_mode';
```

![image-20220424010032834](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220424010032834.png)

#### 测试

连续执行下面的语句，会发现自增主键 `id` 的值会持续递增下去，即使使用该主键的记录没有真正插入到表中。使得中间 **空缺** 了一部分自增主键。

```sql
start transaction;

insert into tab1 (`property1`,`property2`,`property3`,`property4`) value (1,2,3,4);
select * from tab1;

rollback;
```

![image-20220424011845654](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220424011845654.png)

#### 结论

- `innodb_autoinc_lock_mode = 1` 时，则表中自增主键将会连续递增下去，即使执行 insert 的事务没有 commit。在这种情况下，会丢失未提交事务中的主键。

## MylSAM 和 InnoDB 区别

### InnoDB

InnoDB 支持事务。

支持表锁、行锁。

InnoDB 一定有主键，且主键一定是聚簇索引。如果没有主键索引，则会尝试使用 unqiue 索引，如果都没有，则 InnoDB 使用数据库内行隐藏 id 作为主键索引。

表数据和索引数据一起存放的。

InnoDB 辅索引的数据域存储的是主键，一般需要回表。不建议使用过长的主键，也不建议主键是无序的，否则会导致数据 page 频繁分裂。

### MylSAM

MylSAM 不支持事务。

只支持表锁。

使用非聚簇索引。不管是主键索引还是辅助索引，叶子节点存储的都是对应 record （记录）的地址。

表数据和索引数据是独立存放的。有三个文件：表结构文件、索引文件、数据文件。

## 事务

### 事务的四大特性-ACID

- Atomicity（原子性：一个事务的动作要么全部执行，要么都不执行）。依赖于 **undo log**（记录 sql 对应的反向操作）实现。
- Consistence （一致性：像账户余额在交易后不能为负数这样的逻辑，应当在程序代码实现，依赖于其他三个特性的保证）。
- Isolation （隔离性：一个事务不能影响另一个事务的执行）。依赖于 **MVCC** 实现。
- Duration （持久性：事务提交后，对数据库的修改是持续的）。依赖于 **redo log** （记录 sql 对应的操作）实现。


### 事务的实现原理

MySQL 的所有存储引擎里，只有 **InnoDB** 支持事务。

事务的实现原理，也就是事务的四大特性是如何实现的。

**InnoDB** 通过 **Buffer Pool、LogBuffer、Redo log、Undo log** 来实现事务。

1. 在执行更新时，InnoDB 会先找到要修改的数据行所在 **page**，并将其加载到 **Buffer Pool** 中。
2. 执行更新语句，此时修改的是内存中的数据。
3. 针对更新语句生成对应的 **Redo log** 对象，存入到 **Log Buffer** 中（确保事务的持久性）。
4. 针对更新语句生成对应的 **Undo log** 日志，用于后续的事务回滚（确保事务的原子性）。
   - 如果事务 commit，则将 **Redo log** 对象持久化到磁盘中（MySQL的其他进程会在合适的时候将 **Buffer Pool** 中的数据持久化到磁盘。
   - 如果事务 rollback，则利用 **Undo log** 日志进行回滚。

为了提高读写性能，MySQL 采用的是 MVCC（多版本并发控制）来实现`Read Committed`和`Repeatable Read`两个隔离级别下的读写不冲突。
（允许多个读，单个写）。事务的隔离级别只影响当前事务本身，不影响其他事务。
- 什么情况下写入会阻塞：只讨论事务的读取对其他事务的写入的影响（修改相同数据行时，事务的写入必定会使其他事务阻塞）。
当隔离级别为`Serializable`（其他隔离级别不会）的事务tx1查询了数据行row1，那么tx1就会持有tx1的读锁直到事务tx1提交或被撤销。
则后续有其他事务（不管是什么隔离级别）对这个数据行进行写入就会阻塞，直到tx1事务提交或回滚。
- 什么情况下读取会阻塞：只讨论事务的写入对其他事务的读取的影响（事务的读取不影响）。如果有事务tx1对数据行row1进行了写入，
则后续隔离级别为`Serializable`（其他隔离级别不会）的事务tx2查询数据行row1时就会阻塞，直到tx1事务提交或回滚。

总结一下，在MySQL中，隔离级别为`Serializable`的事务的`read`会阻塞其他事务对这个数据的`write`；反之。其他事务对这个数据的`write`
会阻塞隔离级别为`Serializable`的事务的`write`。还是那句话，MVCC对事务隔离级别为`Read Committed`和`Repeatable Read`的事务有益，
因为它的快照读可以使这些事务在有写入的情况下也能读取数据，而不是像`Serializable`那样被阻塞。至于`Read Uncommitted`这种脏读，
已经不需要考虑锁了，直接读取数据即可。

### 事务的隔离级别

事务有四种隔离级别，MySQL 的默认事务级别是 **可重复读（Repeatable Read）**。

不可重复读在于 `update`，幻读在于`insert、delete`。

- 读未提交（Read Uncommitted）：会读到其他事务未 commit 的数据。
- 读已提交（Read Committed）：会读到其他事务 commit 的数据，也就是在当前事务内，两次读取的数据可能不一样。
- 可重复读（Repeatable Read）：读取到的结果只会被其他事务的 `insert、delete` SQL 和当前事务影响。
- 串行化（Serializable）：所有事务顺序执行，不会有任何问题，但是性能会很差。

## 面试题

尽量使用 bigint 类型的字段作为主键；如果使用 UUID 等其他字段作为主键，一是比较费空间；二是在索引查找中难以比较大小。

为什么推荐使用自增主键？不使用自增的话，新增数据可能会插入一个已经满了的 `page`，导致将多余的数据插入到下一个`page`，可能会导致**多次 IO**。

Java 的 web 应用做扩容是非常方便的，应用的瓶颈一般是 DB （数据库）。DB 和 Java 同样可实现的情况下，优先选择 Java 实现该逻辑。

### B-Tree 和 B+-Tree 的区别？

**B-Tree**

B-Tree 的节点存储索引和数据，且节点的索引没有重复。叶子节点之间没有相互指向。

**B+-Tree**

B+-Tree 的非叶子节点只存储索引，不存储数据。因此，非叶子节点能存储更多的索引。

B+-Tree 的叶子节点之间是相互指向的，可以很好地支持范围查找。

