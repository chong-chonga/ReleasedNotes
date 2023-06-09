# 剑指 Offer II 003. 前 n 个数字二进制中 1 的个数



## 解题思路

这道题让我们得出 0 ~ n 的位模式中的 1 的个数, 可以看到位模式之间肯定存在着一种规律, 找到这个规律是解题的关键。



## 规律

1. 我们发现 1 的个数会定时的变成 1 (x = 2^k时)
2. 我们发现 100 ~ 111 这个区间的 1 的个数等于先前的 00 ~ 11 区间段基础上 + 1
3. (其实这个规律也是所有 X 进制共有的规律, 只是每个区间划分不同)

```tex
0
1
10
11
100---------00 ~ 11 的重复+1
101
110
111
1000--------2^3, 1000 ~ 1111 区间的1的个数 = 位模式 1000 前的基础上+1
1001
1010
1011
1100
1101
1110
1111
10000 		0000 ~ 1111 的重复 + 1
...
...
...
```



## 推导递推关系式

根据我们发现的规律, 可以知道存在一种递推关系



设答案数组为 arr , 设 i , 如果 i 是 2 的 k 次幂, 即 i == 2^k时

```tex
i 的位模式为（000...10000...)
```

则有: 

arr[i+0] = 1+arr[0];

arr[i+1] = 1+arr[1];

arr[i+2] = 1+arr[2];

...

arr[i^2-1]=1+arr[i-1];

用伪代码来描述, 则有

```java
if i == 2^k
    for j = 0 to i -1
        arr[i+j] = 1 + arr[j];
```



## 代码实现

```java
class Solution {
		public int[] countBits(int n) {
			if (n == 0) {
				return new int[]{0};
			}
			if (n == 1) {
				return new int[]{0, 1};
			}
			int[] result = new int[n + 1];
			result[0] = 0;
			result[1] = 1;
			int count = 2;
			for (int i = 2; i <= n; i++) {
				if (count == 0) {
					count = i;
				}
				result[i] = 1 + result[i - count];
				--count;
			}
			return result;
		}
	}
```



## 复杂度分析

- 时间复杂度 O(N): 只需要一次扫描即可得到所有答案。
- 空间复杂度 O(N): 创建的数组大小为 n+1, 故空间复杂度为 O(N);
