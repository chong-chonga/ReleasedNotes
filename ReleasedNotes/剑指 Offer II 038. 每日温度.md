# 剑指 Offer II 038. 每日温度



## 原题

#### [剑指 Offer II 038. 每日温度](https://leetcode-cn.com/problems/iIQa4I/)

请根据每日 `气温` 列表 `temperatures` ，重新生成一个列表，要求其对应位置的输出为：要想观测到更高的气温，至少需要等待的天数。如果气温在这之后都不会升高，请在该位置用 `0` 来代替。

**示例 1:**

```
输入: temperatures = [73,74,75,71,69,72,76,73]
输出: [1,1,4,2,1,1,0,0]
```

**示例 2:**

```
输入: temperatures = [30,40,50,60]
输出: [1,1,1,0]
```

**示例 3:**

```
输入: temperatures = [30,60,90]
输出: [1,1,0]
```

**提示：**

- `1 <= temperatures.length <= 10^5`
- `30 <= temperatures[i] <= 100`



## 解题思路

解决这道题目最直观的思路就是：将当天的温度与过去的温度进行比较，求出至少需要等待的天数。

所以，我们可以遍历每天的温度，将其与前面日子的温度进行比较，然后得到答案。

还需要弄明白以下几点：

- 题目要求的是**至少等待天数**， 一个日子等待天数已经求出后，在后续日子的遍历中， **不需要再考虑它了**。
- 当天的答案必须要留到后续循环中才能算出, 所以需要**保存当天的数据**留给后续处理
- 最后一天的等待天数必定是 0

下面我们设当天温度为 `curTemperture`， 答案用数组 `res` 表示，`preTemperture` 表示前面某天的温度。

```java
    int len = temperatures.length;
    ...
    for (int i = ..; i < len; ++i) {
        if (curTemperture > preTemperture) {
        // 为了确定答案, 所以我们还需要记录 preTemperture 是第几天的数据
        res[?] = i - ?
        ...
        ...
   		 }
	}

```

根据上面的描述, 我们很自然的想到用栈来处理（栈顶的数据正是最近的数据），由于栈只能保存一个对象，但是我们必须保存以下数据：

- **温度多少?**
- **第几天的数据?**

```java
class Temperture{
    // 第几天的数据
    int day;
    // 当天的温度
    int val;
}
...
    
// Java 里的 Stack 是线程同步的, 考虑到性能, 所以这里用 Deque
Deque<Temperature> stack = new ArrayDeque<>();
```

有了以上的铺垫，我们就有思路了。

**用栈存储过去的温度数据，并将当天的温度与栈中保存的温度进行比较，如果当天温度高，则弹出过去的数据，如此循环；结束当天的遍历时，要将当天的数据保存在栈中，以备后续使用。**



## 完整代码

```java
	class Solution {

		class Temperature {
			int day;
			int val;

			public Temperature(int day, int val) {
				this.day = day;
				this.val = val;
			}
		}
        
		public int[] dailyTemperatures(int[] temperatures) {
			Deque<Temperature> stack = new ArrayDeque<>();
			int len = temperatures.length;
			int[] result = new int[len];

			int preTemperature = temperatures[0];
			int startTime = 0;
			stack.push(new Temperature(startTime, preTemperature));
			int curTempreature;
			Temperature preTem;

			for (int i = 1; i < len; ++i) {
				curTempreature = temperatures[i];
				// 当前温度比前一天温度高时, 开始回推过去的温度
				while (curTempreature > preTemperature) {
					stack.pop();
					result[startTime] = i - startTime;
					if (stack.isEmpty()) {
						break;
					}
					preTem = stack.peek();
					startTime = preTem.day;
					preTemperature = preTem.val;
				}
				preTemperature = curTempreature;
				startTime = i;
				stack.push(new Temperature(i, curTempreature));
			}

			return result;
		}
	}

```



## 其他细节

计算机的**寄存器个数是有限的**, 像上面代码使用到的 stack 能保存的数据远远大于寄存器所能保存的数据；因此栈是在内存中的，而内存引用一般是性能的瓶颈，为了减**少读写内存的次数**；我们使用 `preTemperature` 和 `startTime` 这样的本地变量 (一般存储在寄存器中) 来保存上一次读栈的数据。而不是将其直接与 `stack.peek().val` 或 `stack.peek().day` 比较。