# JVM-类生命周期

Java 虚拟机从类或接口的二进制中派生出符号引用（class file 中的 constant_pool 参照 [Java SE13-JVM-The Constant Pool](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html#jvms-4.4)）



## JVM 如何启动

JVM 使用 `BoostrapClassLoader` 或用户定义的 `ClassLoader` 创建初始类或接口来启动。JVM 链接、初始化该类或接口，并调用 `public static void main(String[])` 方法。main 方法的执行，可以驱动后续的行为。比如对要使用到的类进行加载、链接、

初始类或接口可以指定，比如通过命令行参数。

### 参照

[Java SE13-JVM-Java Virtual Machine Startup](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-5.html#jvms-5.2)



## Creation and Loading-创建和加载

类/接口 **C** 的创建是由另一个类/接口 **D** 触发的，可以通过以下两种方式触发。

记住，我们用 **D** 表示触发 **C** 创建的类/接口，下面会用到。

- D 通过其运行时常量池引用 C
- D 调用某些 Java SE 平台类库中的方法来触发，比如反射`Reflection`

数组类型是由 JVM 创建的，而不是类加载器创建的。

创建其他类型是通过使用类加载器加载二进制表示的 Class。

class 的内容可以参照 [Class File Format](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html)。



### 类加载器类型

有两种类加载器：**JVM 提供的启动类加载器、用户自定义的类加载器**。

用户自定义的类加载器是从 `abstract class ClassLoader` 派生而来的。可以从用户定义的源中创建类。

在运行时，一个类/接口是由其二进制名称和定义它的类加载器共同决定的。每个这样的类/接口都属于一个单独的`package`包，这个 package 是由其名称和定义它的类加载器决定的。

也就是说：**JVM 运行时判断两个类是否相同，不仅要看其名称（可能是全限定名）是否相同，还要看定义这个类的类加载器是否相同**。这个正是**双亲委派模型**作用的原理。

JVM 使用下列三种方式之一来创建类。

1. 如果该类是非数组类型，则下列两种方法之一会用于加载并创建该类。
   - 如果 D 是由启动类加载器定义的，则使用启动类加载器加载 C。
   - 如果 D 是由用户自定的类加载器定义的，则使用用户自定的类加载器加载 C。
2. 否则由 JVM 直接创建，而不是类加载器。然而，D 的类加载器会在创建数组类型 C 的时候用到。

上面这些方式，可以看出：由 D 触发了 C 的创建，那么就使用 D 的类加载器去创建 C。

### 具体加载步骤

尝试解析具体的 class 文件，这其中就包括了对 class 文件的结构验证、解析对父类的符号引用...。
[JVM13规范-Creation And Loading](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-5.html#jvms-5.3)



## Linking-链接

链接类或接口包括了 **验证、准备**。如有必要，链接也会**解析**对该类/接口的符号引用。

JVM 可以选择“惰性”链接策略，在这种策略中，类或接口中的每个符号引用在使用时都被单独解析。另外，实现也可以选择“即时”链接策略，在这种策略中，当验证类或接口时，所有的符号引用都会被立即解析。

[Java SE13-JVM-Linking](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-5.html#jvms-5.4)

### Verification-验证

验证是用于确保二进制表示的类/接口的结构正确。这个过程可能会加载其他的类/接口。

### Preparation-准备

创建类/接口的**静态字段**并设为默认值。这不需要执行 Java 代码。

### Resolution-解析

许多 JVM 指令依赖于**运行时常量池的符号引用**，比如 `new`指令 。而这些指令的执行需要对这些**符号引用**进行解析。

**因此，在进行符号引用的解析时，可能会触发对类或接口创建和加载**。

参见[JVM13规范-Resolution](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-5.html#jvms-5.3)

```tex
symbolic reference in run-time constant pool.
```

解析就是动态确定符号引用一个或多个具体的值的过程。开始的时候，运行时常量池的所有符号引用都是未解析的。



## Initialization-初始化

在讲初始化前，我们先规定好 `字段` （field）的含义。

给定一个类 Demo，其含有两个字段，分别为 field1、field2；在这里，我们称其为字段（field），而不是属性（property），因为 property 在 JVM 中有其他含义。

```java
public class Demo {
    // 静态字段
    private static int field1;
    
    // 实例字段
    private int field2;
}
```

类或接口 C 只可能在以下情况下被初始化：

- 被 new、getstatic（获取 static 字段的值）、putstatic（设置 static 字段的值）、invokestatic（调用static方法） 中任意一条虚拟机指令所引用。
- 类库中某些**反射方法**的调用。
- C 是一个类，其子类被初始化时。
- C 是一个接口，且声明了非 abstract，static 的方法，其实现类被初始化时。
- ...

[JavaSE-13-Initialization](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-5.html#jvms-5.5)

[JavaSE-13-JVM指令手册](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-6.html#jvms-6.5.getstatic)



## 从 main 方法执行看整个流程

为了说明整个方法执行流程，我们需要以下代码。

**DemoObject**

```java
/**
 * @author Huang Lexin
 * @date 2022年04月13日 21:11
 */
public class DemoObject {

	private String name;

	public DemoObject(String name) {
		this.name = name;
	}

	public void method1() {
		System.out.println(1);
	}
}
```

**MainProcedure**

```java
/**
 * @author Huang Lexin
 * @date 2022年04月13日 21:13
 */
public class MainProcedure {
    
    private static int num = 1;
    
	public static void main(String[] args) {
		DemoObject demoObject = new DemoObject("hello");
		demoObject.method1();
	}
}
```

1. `MainProcedure.java` 仍然是一个文本文件，在被 `javac` 命令编译后变为 `MainProcedure.class` 字节码文件。

2. JVM 启动一个进程，并从 `classpath` 下找到 `MainProcedure` 二进制文件，使用类加载器创建并加载 `MainProcedure`，随后链接、初始化。
在**Linking**的第二个过程-**Preparation** 中，MainProcedure 的静态 field num 初始化为零值 [Java SE13-JVM-Primitive Types and Values]
   (https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-2.html#jvms-2.3)。
接着在**Initialization**时，会执行所有的赋值操作，这个时候，`private static int num = 1`被执行。num 的最终值是 1。

3. JVM 调用 `MainProcedure` 的 main 方法。

4. main 方法执行到 `DemoObject demoObject = new DemoObject("hello");`。由于 `DemoObject`还未被加载，因此触发会对 `DemoObject`进行加载。加载完成后， 
   `DemoObject`，这块的大小就被确定了。然后在堆中划分一块内存区域存放该对象。

5. 对该实例的字段（field）初始化为零值，并设置对象头，最后执行构造方法。这个实例有指向方法区中 `DemoObject` 类元信息的指针（对象头中）。

6. 最后将该引用指向该实例

7. main 方法指向到 `demoObject.method1();` 。JVM 根据 demoObject 引用找到实例，并通过实例的对象头上指向 `DemoObject` 类元信息的指针找到对应的方法表，获得 method1() 的字节码地址。

8. 执行 method1()。


## 双亲委派机制

和双亲委派有关的 Java 代码在 `ClassLoader#loadClass` 中体现。

另外，以下是 ClassLoader 的一段注释

```java
/*
 * The ClassLoader uses a delegation model to search for
 * classes and resources.  Each instance of ClassLoader has an
 * associated parent ClassLoader. When requested to find a class or
 * resource, a ClassLoader instance will usually delegate the search
 * for the class or resource to its parent ClassLoader before attempting to
 * find the class or resource itself.
 */
```



### ClassLoader#loadClass

```java
    protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // 通过 native 方法判断该类是否被加载
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    // 如果父类加载器不是 null，则委派给 parent 去加载
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        // 父类加载器是 null，说明父类是 BootstrapClassLoader，委派给 native 方法
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // parent 找不到则会抛出异常
                    
                }

                if (c == null) {
                    // parent 找不到了，当前类加载器来加载
                    long t1 = System.nanoTime();
                    c = findClass(name);
					
                    PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
```

loadClass 方法的内部逻辑可以看出：**类加载器首先会将加载类的工作委派给 parent 加载，而这是一个递归的过程；只有 parent 找不到时，才会自己尝试去加载。**

推论：**当类加载器含有共同的 parent 时，parent 加载的类是被所有子类所共享的-依赖于 loadClass 逻辑。**

**Java 核心类库应当被共享**，因此，parent 加载的自然而然就是 `Java` 的核心类库。

比如 Object 类（因为要被其他很多类所共享）。 被`BootStapClassLoader` 所加载的类，都是 Java 核心的类库。

现在再对比这张图，结合源码的流程，我们就更能理解了。

![image-20220411170124611](C:\Users\悠一木碧\AppData\Roaming\Typora\typora-user-images\image-20220411170124611.png)

parent 是 `ClassLoder`中的一个 `final` 字段，在 `ClassLoader` 创建的时候被赋值并固定了。ClassLoader 提供了两个 protected 的构造方法，其他都是 private 构造方法。因此，子类能使用到的只有以下两个构造方法。

```java
public abstract class ClassLoader {
    
    private final ClassLoader parent;
    
	protected ClassLoader() {
        this(checkCreateClassLoader(), null, getSystemClassLoader());
    }
	
    protected ClassLoader(String name, ClassLoader parent) {
        this(checkCreateClassLoader(name), name, parent);
    }
}
	
```

根据上面的描述，我们可以知道：

如果我们自定义类加载器，则必须从 `ClassLoader`中派生；委派机制在 `loadClass` 方法中，因此不要轻易覆盖该方法。