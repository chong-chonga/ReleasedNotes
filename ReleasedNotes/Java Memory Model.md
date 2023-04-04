# JVM-Java内存模型
准备 Java 面试绕不过去 JVM，Java内存模型经常问到。Java 内存模型前前后后看了好几次，但就是容易忘记，于是写篇笔记来记录下所思所想。

## Java 运行时数据区

### 堆

线程共享的。

堆是 Java 最大的一块内存，是 GC 回收的主要区域（另一块区域是方法区）。

几乎所有的对象都在这里被分配内存(JDK1.7开始默认启用了逃逸分析，如果只是在方法中被创建而没有被外界所引用，则直接在栈中分配内存)。

堆被划分为了 **年轻代、老年代**；**年轻代**又被分为了 **Eden区、Survivor0区、Survivor1区**。将堆内存这么细划分，好处在于：**可以针对不同分区内存采用不同的内存分配策略和GC回收策略，进行更细粒度的控制，提升内存分配、回收性能**。



### 栈

线程私有的。

FILO(先进后出）。栈分为虚拟机栈、本地方法栈。

进入一个新方法时，会在栈上划分一块内存作为该方法的**栈帧**；从一个方法中退出时（不管是正常 return 还是异常退出），该栈帧都会被释放掉。栈不需要 GC 进行回收。

tip: **C/C++ 进行方法调用时，call 指令会将 SP 寄存器保存在栈中；从方法返回时，return 指令会将 SP 指针从栈中弹出并赋值给 SP。这两条指令保证了栈帧的释放。**

```assembly
call method -> push SP
return -> pop SP
```

一个栈帧包含了：局部变量表、操作数栈（临时存储运算结果、变量值的地方）、动态链接（符号引用->方法区中方法的直接引用）、方法出口（方法调用的下一条指令的地址）。

虚拟机栈是为 Java 方法服务的，本地方法栈就是为 native 方法服务的。
![image.png](https://pic.leetcode-cn.com/1649063290-LYhgbZ-image.png)



### 方法区

线程共享的。

GC 回收的一块区域（回收频率低，只有在 **Full GC** 时回收）。

方法区是 Java 虚拟机规范中规定的一个区域，不是最终的实现，只是制定的一个规范。

它存储每个类的结构，例如**运行时常量池**、字段和方法数据，以及方法和构造函数的代码，包括类和接口初始化以及实例初始化中使用的特殊方法。

JDK 1.7 之前，HotSpot 方法区的实现是永久代。这里面包含了这么一些东西：类元信息、运行时常量池、JIT 代码缓存。

JDK 1.7 时，字符串常量、静态变量从永久代移动到了堆中。

JDK 1.8 时，永久代被废弃，转而使用元空间实现，元空间使用的本地内存实现的。

Java 内存中有相当大一块空间是用于存储字符串的，而很多字符串（toString() 产生的）都是只用一次，每次的字符串大概率不相同；如果字符串常量放在方法区中，很难及时回收。因此，放到堆中后，内存回收性能会更好。

#### 运行时常量池

Java 虚拟机为每个类和接口维护一个运行时常量池，这种数据结构和传统编程语言实现的符号表的目的基本一致。

运行时常量池中有两种条目：**符号引用**（稍后可能会被解析)，以及不需要进一步处理的**静态常量**。

##### 符号引用

运行时常量池中的**符号引用**是根据constant_pool表中每个条目的结构派生出来的。

##### 静态常量

运行时常量池中的**静态常量**也根据每个条目的结构从constant_pool表中的条目派生出来。

什么是 contant_pool 表呢？contant_pool 表在哪？

其实 constant_pool 就在 Java 代码编译后的字节码文件中。这里使用 IDEA 的插件-jclasslib 查看该文件：
![image.png](https://pic.leetcode-cn.com/1649752889-YdQzpY-image.png)
引用官方的介绍：**Java虚拟机指令不依赖于类、接口、类实例或数组的运行时布局。相反，指令引用的是constant_pool表中的符号信息。**
感兴趣的可以阅读[JDK13-类文件结构#常量池](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html#jvms-4.4)
根据该文档提供的信息，下面我自己实现了一个从 `ConstantLongInfo.class` 文件中读取该文件内容的程序。
**ConstantLongInfo**
```Java []
/**
 * The CONSTANT_Long_info and CONSTANT_Double_info represent 8-byte numeric (long and double) constants:
 * CONSTANT_Long_info {
 *     u1 tag;
 *     u4 high_bytes;
 *     u4 low_bytes;
 * }
 * All 8-byte constants take up two entries in the constant_pool table of the class file.
 * If a CONSTANT_Long_info or CONSTANT_Double_info structure is the entry at index n in the constant_pool table, then the next usable entry in the table is located at index n+2.
 * The constant_pool index n+1 must be valid but is considered unusable.
 * The tag item of the CONSTANT_Long_info structure has the value CONSTANT_Long (5).
 * @author Huang Lexin
 * @date 2022年04月06日 15:40
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class ConstantLongInfo extends ConstantPoolInfo {
	/**
	 * u4 + u4
	 */
	long val;

	public ConstantLongInfo(long val) {
		super(ConstantTag.LONG);
		this.val = val;
	}
}
```
**ClassFileRead**
```Java []
/**
 * @author Huang Lexin
 * @date 2022年04月06日 15:04
 */
@Slf4j
public class ClassFileRead {

	private static final int ACC_PUBLIC = 0x0001;

	private static final int ACC_FINAL = 0x0010;

	private static final int ACC_SUPER = 0x0020;

	private static final int ACC_INTERFACE = 0x0200;

	private static final int ACC_ABSTRACT = 0x0400;

	private static final int ACC_SYNTHETIC = 0x1000;

	private static final int ACC_ANNOTATION = 0x2000;

	private static final int ACC_ENUM = 0x4000;

	private static final int ACC_MODULE = 0x8000;


	/**
	 * In the Java SE Platform API, the class file format is supported by interfaces java.io.DataInput and java.io.DataOutput and classes such as java.io.DataInputStream and java.io.DataOutputStream.
	 * For example, values of the types u1, u2, and u4 may be read by methods such as
	 * readUnsignedByte, readUnsignedShort, and readInt of the interface java.io.DataInput.
	 */
	public static void main(String[] args) throws IOException {
		try (InputStream inputStream = ConstantLongInfo.class.getResourceAsStream("ConstantLongInfo.class");
		     DataInputStream dataInputStream = new DataInputStream(Objects.requireNonNull(inputStream))) {
			ClassFile classFile = new ClassFile();
			int magic = dataInputStream.readInt();

			int minorVersion = dataInputStream.readUnsignedShort();
			int majorVersion = dataInputStream.readUnsignedShort();

			int constantPoolCount = dataInputStream.readUnsignedShort();
			List<ConstantPoolInfo> constantPoolInfos = readConstantPool(dataInputStream, constantPoolCount);
			HashMap<Integer, ConstantUTF8Info> utf8ConstantsMap = getUTF8ConstantsMap(constantPoolInfos);

			int accessFlags = dataInputStream.readUnsignedShort();
			processAccessFlags(accessFlags);

			int thisClass = dataInputStream.readUnsignedShort();
			ConstantClassInfo thisClassInfo = (ConstantClassInfo) constantPoolInfos.get(thisClass - 1);
			System.out.println("thisClass = " + new String(utf8ConstantsMap.get(thisClassInfo.getNameIndex()).getBytes(),
					StandardCharsets.UTF_8));
			int superClass = dataInputStream.readUnsignedShort();
			ConstantClassInfo superClassInfo = (ConstantClassInfo) constantPoolInfos.get(superClass - 1);
			System.out.println("superClass = " + new String(utf8ConstantsMap.get(superClassInfo.getNameIndex()).getBytes(),
					StandardCharsets.UTF_8));

			int interfacesCount = dataInputStream.readUnsignedShort();
			classFile.setMagic(magic);
			classFile.setMinorVersion(minorVersion);
			classFile.setMajorVersion(majorVersion);
			classFile.setConstantPoolCount(constantPoolCount);
			classFile.setConstantPool(constantPoolInfos);
			classFile.setInterfacesCount(interfacesCount);

		} 

	}

	private static void processAccessFlags(int accessFlags) {
		log.info("Class 文件访问标志:{}", Integer.toHexString(accessFlags));
		if (ACC_PUBLIC == (accessFlags & ACC_PUBLIC)) {
			System.out.print("ACC_PUBLIC,");
		}
		if (ACC_SUPER == (accessFlags & ACC_SUPER)) {
			System.out.print("ACC_SUPER");
		}
		System.out.println();
		//...
	}

	private static HashMap<Integer, ConstantUTF8Info> getUTF8ConstantsMap(List<ConstantPoolInfo> constantInfos) {
		log.info("解析到{}个常量", constantInfos.size());
		HashMap<Integer, ConstantUTF8Info> utf8ConstantsMap = new HashMap<>();
		for (ConstantPoolInfo constantPoolInfo : constantInfos) {
			if (constantPoolInfo.getTag() == ConstantTag.UTF8.getVal()) {
				utf8ConstantsMap.put(constantPoolInfo.getIndexInPool(), (ConstantUTF8Info) constantPoolInfo);
			}
		}
		return utf8ConstantsMap;
	}

	private static List<ConstantPoolInfo> readConstantPool(DataInputStream dataInputStream, int constantPoolCount) throws IOException {
		List<ConstantPoolInfo> constantInfos = new ArrayList<>(constantPoolCount - 1);

		for (int i = 1; i < constantPoolCount; ++i) {
			int tag = dataInputStream.readUnsignedByte();
			int nameIndex;
			int descriptorIndex;
			int nameAndTypeIndex;
			int classIndex;
			switch (tag) {
				case 1:
					int length = dataInputStream.readUnsignedShort();
					byte[] bytes = new byte[length];
					int readBytes = dataInputStream.read(bytes);
					if (readBytes != bytes.length) {
						throw new RuntimeException("读取字节数与目标字节数不符!");
					}
					constantInfos.add(new ConstantUTF8Info(length, bytes));
					break;
				case 7:
					nameIndex = dataInputStream.readUnsignedShort();
					constantInfos.add(new ConstantClassInfo(nameIndex));
					break;
				case 8:
					nameIndex = dataInputStream.readUnsignedShort();
					constantInfos.add(new ConstantStringInfo(nameIndex));
					break;
				case 9:
					classIndex = dataInputStream.readUnsignedShort();
					nameAndTypeIndex = dataInputStream.readUnsignedShort();
					constantInfos.add(new ConstantFieldRefInfo(classIndex, nameAndTypeIndex));
					break;
				case 10:
					classIndex = dataInputStream.readUnsignedShort();
					nameAndTypeIndex = dataInputStream.readUnsignedShort();
					constantInfos.add(new ConstantMethodRefInfo(classIndex, nameAndTypeIndex));
					break;
				case 12:
					nameIndex = dataInputStream.readUnsignedShort();
					descriptorIndex = dataInputStream.readUnsignedShort();
					constantInfos.add(new ConstantNameAndTypeInfo(nameIndex, descriptorIndex));
					break;
				case 15:
					short referenceKind = (short) dataInputStream.readUnsignedByte();
					int referenceIndex = dataInputStream.readUnsignedShort();
					constantInfos.add(new ConstantMethodHandleInfo(referenceKind, referenceIndex));
					break;
				case 18:
					int bootstrapMethodAttrIndex = dataInputStream.readUnsignedShort();
					nameAndTypeIndex = dataInputStream.readUnsignedShort();
					constantInfos.add(new ConstantInvokeDynamicInfo(bootstrapMethodAttrIndex, nameAndTypeIndex));
					break;
				default:
					throw new RuntimeException("tag=" + tag + ", 编号为" + i + "的常量位置出错!");
			}
		}
		int i = 1;
		for (ConstantPoolInfo constantInfo : constantInfos) {
			constantInfo.setIndexInPool(i++);
		}
		return constantInfos;
	}
}
```
**运行结果**
![image.png](https://pic.leetcode-cn.com/1649753099-UazvVx-image.png)


### PC-程序计数器

线程私有的。

用于保存线程当前正在执行的 Java 虚拟机指令的地址。注意，只能保存执行 Java 虚拟机指令的地址，不能保存 native 指令的地址。
CPU 的寄存器组中有一个 PC 寄存器，硬件级别上正是用 PC 保存下一条要执行指令的地址。
我的疑问：**硬件级别上都有 PC 了，为什么还需要在内存中为这些线程分配一块空间用于模拟 PC 呢？ 为什么不直接使用硬件 PC？**
我的猜测：JVM是运行在物理机器上一台虚拟的机器，拥有自己的 ISA（指令集架构）。ISA 是能执行的指令的集合。JVM的指令集独立于机器的 ISA，从而在实际的物理机器上构建一台虚拟的机器。
我们都听说过：Java 的一个主要特点是跨平台。一次编译，多处运行。实际上，我们对 Java 代码进行编译后，产生的是 `.class` 后缀的字节码（byte code）文件。字节码文件中就包含了虚拟机 ISA 的指令，这些指令是交由**字节码执行引擎**执行的，而不是直接交由硬件处理的。JVM 的 ISA 与物理机器的 ISA 不同，假如我们完全依赖 CPU 的 PC，则会引起混乱。
因此构建出`内存级别上的 PC` 这种抽象。
这种虚拟性花费了额外的内存和额外的 ISA，达到的正是跨平台的特性。当然，不管是怎么模拟的，最终执行指令的都是物理机器的，而不是内存级别上的虚拟机。
#### 其他虚拟化的应用
- **CPU 虚拟性**：操作系统做的一件很棒的事情就是将 CPU 虚拟化了；即使是一块单核 CPU，也能够 ‘同时’ 运行多个应用。仿佛每个应用都在 CPU 上同时运行。这其中涉及到的进程调度算法以及如何进行进程上下文切换等...我们就不说了。
- **内存虚拟化**：操作系统做的另一件很棒的事情就是内存虚拟化，通过利用外存和虚拟机地址空间，在硬件的协助下，操作系统能为程序提供强大的内存支持，程序不用担心自己会被放在哪一块内存区域运行，也不用担心自己内存如何分配，这些事情都交由操作系统去管理了。

#### 参照：
[JDK13虚拟机规范-指令集](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-6.html)
[JDK13虚拟机规范-类文件格式](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html)

在看完以上的内容，我们画张图看下整体的结构。
![image.png](https://pic.leetcode-cn.com/1649063471-OTVyBL-image.png)


## 直接内存

直接内存不属于 JVM 的运行时数据区域，Java 的 NIO 使用到的内存是直接使用 native 方法分配的直接内存，避免了在 JVM 内存和 native 内存之间复制。

## 对象创建过程

一个简单的 new XXX 代码创建 Java 对象分为以下几个步骤。

### 类加载检查

创建一个类的实例，得先确定这个类是什么，所以，先要进行类的加载检查。

如果该类没有被加载过，则需要进行加载、解析、初始化。在初始化过程中，会执行类的static代码段。同一个类只会被加载一次，
因此static代码段也只会执行一次。

### 分配内存

确定了这个类是什么，我们就能确定这个对象会有多大。

接下来，就需要为该对象找到合适的存储空间。

分配内存有两种方式：指针碰撞、空闲列表。

**指针碰撞：**

适合内存规整的情况下使用。

使用一个指针，指针的一方是已分配内存，另一方是未分配内存。只需要将该指针往未分配内存的方向移动对象大小的地址即可。

**空闲列表**：

适合内存不规整的情况下使用。

操作系统为程序分配内存也有使用到空闲列表。

列表的结点存储空闲内存的起始地址。分配策略有：最差匹配、最优匹配等算法。

### 初始化零值

对象中的字段分为引用类型和原始类型。
引用类型赋 null，原始类型赋 0。（null 本质上来说也是0)。 这也就是为什么Java的对象的字段不需要赋值即可使用。

### 设置对象头

对象头分为 Mark Word、指向类元数据的指针、数组长度（数组对象特有）。

在这个过程中，设置其指向类元数据的指针、数组长度（如有必要）、分代年龄、hashcode 值、锁标志等。

### 执行构造方法

完成上述所有步骤后，才会来到构造方法，正式进入到 Java 代码执行中。


## 对象的访问方式

创建对象就是为了去使用它。我们通过引用去访问对象的方式取决于虚拟机。有两种方式：使用句柄、直接指针。

**句柄**

指向内存中的句柄，句柄包含了分别指向对象的实例数据、类型数据的两个指针，相当于套接了一个中间层。加入一个中间层的好处就是，当下层的数据地址发生更改时，
只需要更改中间层指向的地址。这个技术同样也在MySQL的辅助索引上用到（辅助索引指向的是主键，而非数据地址）。这样当MVCC修改了数据地址时，只需要修改主键指向的地址即可。
无需修改辅助索引指向的数据地址。由于主键索引一般只有1个，而辅助索引会有多个，这样就减少了要修改的地址的个数。
**优点：对象在内存中移动了，但是引用不需要改变，只需要改变句柄即可。**

**直接指针**

指向对象的地址，对象中包含了指向类型数据的指针。**优点：速度快，一次内存引用即可访问对象。**