## 字节跳动-LeetCode题库总结



| 标志 |                             题目                             | 通过率 | 难度 |                             备注                             |
| :--: | :----------------------------------------------------------: | :----: | :--: | :----------------------------------------------------------: |
|  √   |     [1. 两数之和](https://leetcode.cn/problems/two-sum/)     | 52.5%  | 简单 |                   双指针 + 排序 \| 哈希表                    |
|  √   | [2. 两数相加](https://leetcode.cn/problems/add-two-numbers/) | 41.7%  | 中等 |                   模拟，迭代加下去就可以了                   |
|  √   | [42. 接雨水](https://leetcode.cn/problems/trapping-rain-water/) | 60.8%  | 困难 |                  双指针 \| 左右高度动态规划                  |
|  √   | [3. 无重复字符的最长子串](https://leetcode.cn/problems/longest-substring-without-repeating-characters/) | 38.8%  | 中等 |                       哈希表 + 双指针                        |
|  √   | [5. 最长回文子串](https://leetcode.cn/problems/longest-palindromic-substring/) | 36.6%  | 中等 |                     中心扩散 \| 动态规划                     |
|  √   |   [146. LRU 缓存](https://leetcode.cn/problems/lru-cache/)   | 52.8%  | 中等 | 设计双向链表、linkFirst(E e), unlink(Node<E> x), unlinkLast() 等方法，再加上 Map 即可完成；put操作已经存在的 key 被视为使用了 key |
|  √   | [25. K 个一组翻转链表](https://leetcode.cn/problems/reverse-nodes-in-k-group/) | 67.3%  | 困难 |             头插法翻转，处理好前置节点和翻转恢复             |
|  √   | [4. 寻找两个正序数组的中位数](https://leetcode.cn/problems/median-of-two-sorted-arrays/) | 41.4%  | 困难 | 关键：offset = k / 2 - 1，比较两个数组的右边界来排除其他元素 |
|  √   |      [15. 三数之和](https://leetcode.cn/problems/3sum/)      | 35.4%  | 中等 | 排序，每个循环枚举的数不重复，按照一定的顺序地枚举第一、二、三个数 |
|  √   | [206. 反转链表](https://leetcode.cn/problems/reverse-linked-list/) | 72.9%  | 简单 |                          链表头插法                          |
|  √   | [440. 字典序的第K小数字](https://leetcode.cn/problems/k-th-smallest-in-lexicographical-order/) | 42.7%  | 困难 | 1 ~ 9 建立字典树，确定子树每层的节点数量,判断是否在子树中 (注意数据大小) |
|  √   | [7. 整数反转](https://leetcode.cn/problems/reverse-integer/) | 35.3%  | 中等 |     和头插法类似，取最后一位数，依次附加在乘10后的结果上     |
|  √   |   [46. 全排列](https://leetcode.cn/problems/permutations/)   | 78.5%  | 中等 |          回溯， 在回溯中使用哈希表记录哪个数被使用           |
|  √   | [21. 合并两个有序链表](https://leetcode.cn/problems/merge-two-sorted-lists/) | 66.6%  | 简单 |                      pHead 虚节点的应用                      |
|  √   | [121. 买卖股票的最佳时机](https://leetcode.cn/problems/best-time-to-buy-and-sell-stock/) | 57.8%  | 简单 | 记录最小买入价格，当前价格比最小价格小，更新买入价格；否则更新最大收益 |
|  √   | [124. 二叉树中的最大路径和](https://leetcode.cn/problems/binary-tree-maximum-path-sum/) | 45.0%  | 困难 | left -> root -> right 这条路径对其父节点计算结果贡献为0；关键是理解什么路径对父节点有影响 |
|  √   | [33. 搜索旋转排序数组](https://leetcode.cn/problems/search-in-rotated-sorted-array/) | 43.6%  | 中等 | mid 的一侧必定是有序的，另一侧无序；通过比较中点和左右边界值大小来确定；同时确定要找的数是否在有序数组里 |
|  √   | [56. 合并区间](https://leetcode.cn/problems/merge-intervals/) | 48.5%  | 中等 | 排序，一个区间 **tail** 大于后一个区间的 **begin** 时,这两个区间合并 |
|  √   | [11. 盛最多水的容器](https://leetcode.cn/problems/container-with-most-water/) | 61.4%  | 中等 | 和接雨水有点类似，但是这道题只需要考虑左右边界即可；双指针解决，哪个指针对应的高度小，就移动哪个指针 |
|  √   | [143. 重排链表](https://leetcode.cn/problems/reorder-list/)  | 63.7%  | 中等 |          快慢指针找链表中点+翻转一侧链表 + 合并链表          |
|  √   | [53. 最大子数组和](https://leetcode.cn/problems/maximum-subarray/) | 54.9%  | 简单 | 当前元素要么与前面的序列一起，要么单独；取决于前面的连续子数组和是否大于 0 \| 高级做法：线段树 |
|  √   | [23. 合并K个升序链表](https://leetcode.cn/problems/merge-k-sorted-lists/) | 56.9%  | 困难 |            K 个节点迭代 \| 分治，两个链表合并一次            |
|  √   | [54. 螺旋矩阵](https://leetcode.cn/problems/spiral-matrix/)  | 48.7%  | 中等 |            四个边界，注意左右、上下边界重合的情况            |
|  √   | [31. 下一个排列](https://leetcode.cn/problems/next-permutation/) | 37.6%  | 中等 | 数字的下一个排列，就是找比当前数大的数；将左侧的较小的数与右侧较大的数交换来实现 |
|  √   |     [135. 分发糖果](https://leetcode.cn/problems/candy/)     | 49.0%  | 困难 | 每个元素对答案的贡献取决于左右的递增、递减序列长度；出现1个上升点时，递增长度为2，出现1个下降点时，递减长度为1 |
|  √   | [215. 数组中的第K个最大元素](https://leetcode.cn/problems/kth-largest-element-in-an-array/) | 64.7%  | 中等 |    最小堆，堆大小为 k；操作完所有元素后，堆顶元素即为答案    |
|  √   | [200. 岛屿数量](https://leetcode.cn/problems/number-of-islands/) | 57.7%  | 中等 |                         BFS + 哈希表                         |
|  √   | [22. 括号生成](https://leetcode.cn/problems/generate-parentheses/) | 77.5%  | 中等 |       回溯，可用的左括号和右括号都为0时，存储一种答案        |
|  √   | [103. 二叉树的锯齿形层序遍历](https://leetcode.cn/problems/binary-tree-zigzag-level-order-traversal/) | 57.3%  | 中等 |                        BFS + reverse                         |
|  √   |   [148. 排序链表](https://leetcode.cn/problems/sort-list/)   | 66.5%  | 中等 |                  快慢指针找中点 + 归并排序                   |
|  √   | [199. 二叉树的右视图](https://leetcode.cn/problems/binary-tree-right-side-view/) | 65.5%  | 中等 |                     DFS + 二叉树深度标志                     |
|  √   | [70. 爬楼梯](https://leetcode.cn/problems/climbing-stairs/)  | 53.7%  | 简单 |        经典 dp，dp[i] = dp[i - 1] +dp[i - 2];(i > 1)         |
|  √   | [92. 反转链表 II](https://leetcode.cn/problems/reverse-linked-list-ii/) | 55.3%  | 中等 | 找到 left 节点的前置节点，right 的后置节点；处理 `left == 1` 的特殊情况 |
|  √   | [93. 复原 IP 地址](https://leetcode.cn/problems/restore-ip-addresses/) | 56.2%  | 中等 |   条件较为复杂，如前导0、三位数的值小于255、剩余的 IP 段数   |
|  √   | [20. 有效的括号](https://leetcode.cn/problems/valid-parentheses/) | 44.6%  | 简单 |                 自己使用数组实现的栈效率更高                 |
|  √   | [105. 从前序与中序遍历序列构造二叉树](https://leetcode.cn/problems/construct-binary-tree-from-preorder-and-inorder-traversal/) | 71.0%  | 中等 | 根据前序遍历的根节点，在中序遍历中找到对应的下标，将其分割为左子树和右子树，递归处理即可 |
|  √   | [剑指 Offer 09. 用两个栈实现队列](https://leetcode.cn/problems/yong-liang-ge-zhan-shi-xian-dui-lie-lcof/) | 70.9%  | 简单 | 自己用数组实现栈会实现得更快；栈的压入和弹出操作两次，即可得到和队列一样的效果 |
|  √   | [32. 最长有效括号](https://leetcode.cn/problems/longest-valid-parentheses/) | 36.4%  | 困难 | 以 ) 字符结尾的才是有效括号，使用栈顶存储最近未匹配的括号下标，左括号入栈，右括号弹出 |
|  √   | [41. 缺失的第一个正数](https://leetcode.cn/problems/first-missing-positive/) | 42.6%  | 困难 | 只考虑 1 ~ len 范围内的值，要实现 O(1) 的空间吗，就必须原地置换；正数！; 因此可以将其修改为负数，使用时取绝对值，修改后不会影响原来的值使用！ |
|  √   | [10. 正则表达式匹配](https://leetcode.cn/problems/regular-expression-matching/) | 31.6%  | 困难 | 两个字符串，二维动态规划，范围 0 ~ 字符串长度，`dp[i][j]` 表示 i 长度的字符串 s 是否匹配 j 长度的字符串 r |
|  √   | [14. 最长公共前缀](https://leetcode.cn/problems/longest-common-prefix/) | 42.4%  | 简单 | 找所有字符串的公共前缀，查找顺序不影响最终答案，就可以利用分治小问题，合并为一个整体答案 \| n 个字符串依次扫描 \| 排序 + 首尾字符串扫描 |
|  √   | [88. 合并两个有序数组](https://leetcode.cn/problems/merge-sorted-array/) | 52.3%  | 简单 | 开辟临时数组 \| 从后向前放置较大的数，因为从前向后放置较小的数，会覆盖 num1 元素的使用 |
|  √   | [175. 组合两个表](https://leetcode.cn/problems/combine-two-tables/) | 73.6%  | 简单 |                   保留左表信息，左连接查询                   |
|  √   | [72. 编辑距离](https://leetcode.cn/problems/edit-distance/)  | 62.2%  | 困难 | 两个字符串，和正则表达式匹配一样，解决方法还是二维 dp；关键在于三种不同类型的操作：A添加字符、B添加字符、A替换字符。对应于三种递推公式 |
|  √   | [102. 二叉树的层序遍历](https://leetcode.cn/problems/binary-tree-level-order-traversal/) | 64.8%  | 中等 |                         节点队列迭代                         |
|  √   | [198. 打家劫舍](https://leetcode.cn/problems/house-robber/)  | 53.1%  | 中等 | 一个元素只有拿与不拿两种状态，相邻的元素不能拿，拿与不拿当前元素，可以取这两种情况的最大值；知道这道题是 **动态规划** 就会很简单 |
|  √   | [300. 最长递增子序列](https://leetcode.cn/problems/longest-increasing-subsequence/) | 53.4%  | 中等 | 当前元素只有大于前面的元素时，才可以组成一个递增序列；很难想到 **动态规划**，在所有递增序列中取最长的即可 |
|  √   | [剑指 Offer 24. 反转链表](https://leetcode.cn/problems/fan-zhuan-lian-biao-lcof/) | 74.3%  | 简单 |                         链表的头插法                         |
|  √   | [24. 两两交换链表中的节点](https://leetcode.cn/problems/swap-nodes-in-pairs/) | 70.8%  | 中等 |                 模拟，按照要求处理好条件即可                 |
|  √   | [剑指 Offer 03. 数组中重复的数字](https://leetcode.cn/problems/shu-zu-zhong-zhong-fu-de-shu-zi-lcof/) | 67.9%  | 简单 | 和 [41. 缺失的第一个正数](https://leetcode.cn/problems/first-missing-positive/) 问题类似，本题指明元素值在 0 ~ n-1 范围内，可以使用原地交换达到 O(1) 的空间复杂度 \| 最简单是使用哈希表 \| 节省空间进一步， 用 bitMap |
|  √   |      [78. 子集](https://leetcode.cn/problems/subsets/)       | 80.5%  | 中等 |        回溯法解决，关键在于优化性能，减少不必要的判断        |
|  √   | [128. 最长连续序列](https://leetcode.cn/problems/longest-consecutive-sequence/) | 54.9%  | 中等 | 哈希表，连续序列，因此当前数字需要找到其相邻的数字形成一个序列，为了避免重复查找，可以从小查到大，也可以从大查到小 |
|  √   |  [79. 单词搜索](https://leetcode.cn/problems/word-search/)   | 46.3%  | 中等 |                 简单的回溯 + 哈希表防止重复                  |
|  √   | [176. 第二高的薪水](https://leetcode.cn/problems/second-highest-salary/) | 35.7%  | 中等 | 关键在于 MySQL 中的 ifNull 函数以及 limit count, offet 的使用 |
|  √   | [76. 最小覆盖子串](https://leetcode.cn/problems/minimum-window-substring/) | 44.1%  | 困难 | 一个字符串统计得出一个哈希表，另一个字符串通过修改哈希表对应的值来得到答案 |
|  √   | [剑指 Offer 51. 数组中的逆序对](https://leetcode.cn/problems/shu-zu-zhong-de-ni-xu-dui-lcof/) | 49.1%  | 困难 | 利用归并排序，左右数组有序，统计右子数组对左子数组元素的答案贡献 |
|  √   | [221. 最大正方形](https://leetcode.cn/problems/maximal-square/) | 48.8%  | 中等 | 动态规划经典题，关键在于了解三个点决定是否能得出更大的正方形 |
|  √   | [136. 只出现一次的数字](https://leetcode.cn/problems/single-number/) | 72.2%  | 简单 | 题目的特殊条件：其他数字都出现两次，异或规律：num ^ num = 0, num ^ 0 = num |
|  √   | [13. 罗马数字转整数](https://leetcode.cn/problems/roman-to-integer/) | 62.5%  | 简单 | 模拟，给定了组合的规则，只需要超前扫描一个字符并添加判断即可 |
|  √   | [9. 回文数](https://leetcode.cn/problems/palindrome-number/) | 57.3%  | 简单 | 类似于链表的头插法使得链表反转，整数的反转也是依次将数 “插到前面”，先取后面的数字，然后乘以10，将数字往前移 |
|  √   | [45. 跳跃游戏 II](https://leetcode.cn/problems/jump-game-ii/) | 44.6%  | 中等 |   贪心策略，还是一样，只是走到目的地时将步数 + 1 统计答案    |
|  √   |   [55. 跳跃游戏](https://leetcode.cn/problems/jump-game/)    | 50.0%  | 中等 | 贪心策略，从当前点出发，遍历所有能到达的点，选择那个能走到最远的点作为终点 |
|  √   | [面试题 16.25. LRU 缓存](https://leetcode.cn/problems/lru-cache-lcci/) | 54.8%  | 中等 | 和上面的 [146. LRU 缓存](https://leetcode.cn/problems/lru-cache/) 一样 |
|  √   | [415. 字符串相加](https://leetcode.cn/problems/add-strings/) | 54.8%  | 简单 | 模拟，从后面的字符往前加就可以了，关键在于不用 %, / 这两种运算符来计算当前位的值, 目的是为了提高运算性能 |
|  √   | [101. 对称二叉树](https://leetcode.cn/problems/symmetric-tree/) | 57.6%  | 简单 |                     简单左右子树递归即可                     |
|  √   | [剑指 Offer 22. 链表中倒数第k个节点](https://leetcode.cn/problems/lian-biao-zhong-dao-shu-di-kge-jie-dian-lcof/) | 80.2%  | 简单 |              简单思路，简单思路，简单思路即可！              |
|  √   |  [322. 零钱兑换](https://leetcode.cn/problems/coin-change/)  | 45.6%  | 中等 | 动态规划经典题，和 [70. 爬楼梯](https://leetcode.cn/problems/climbing-stairs/) 类似，从起始值遍历到需要的值。当前状态根据币值和前面的状态进行转移 |
|  √   | [983. 最低票价](https://leetcode.cn/problems/minimum-cost-for-tickets/) | 相似题 | 中等 | 零钱兑换的相似题；不同的地方：出行的时间可能不连续；还是可以从起始日期遍历到终止日期。需要出行时，就进行状态转移；否则，状态和前一天一样 |
|  √   | [43. 字符串相乘](https://leetcode.cn/problems/multiply-strings/) | 44.8%  | 中等 | 模拟题，理清乘法的思路即可，使用一个数的所有数字乘以另一个数的所有数字，不断求余数和进位即可 |
|  √   | [剑指 Offer 29. 顺时针打印矩阵](https://leetcode.cn/problems/shun-shi-zhen-da-yin-ju-zhen-lcof/) | 43.7%  | 简单 | 与 [54. 螺旋矩阵](https://leetcode.cn/problems/spiral-matrix/) 题目一样 |
|  √   | [122. 买卖股票的最佳时机 II](https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-ii/) | 70.3%  | 中等 | 贪心算法，这一天比前一天的价格高，就选择 "卖出"，利润就会增加；这一天比前一天价格低，卖出最好在其前面，因此利润和前一天一样 |
|  √   | [6. Z 字形变换](https://leetcode.cn/problems/zigzag-conversion/) | 51.9%  | 中等 |                          字符串处理                          |
|  √   | [347. 前 K 个高频元素](https://leetcode.cn/problems/top-k-frequent-elements/) | 63.0%  | 中等 | 使用哈希表统计数字出现次数，根据出现次数进行排序，关于排序，最好自己写快排 |
|  √   | [98. 验证二叉搜索树](https://leetcode.cn/problems/validate-binary-search-tree/) | 36.0%  | 中等 | 左子树中的节点必须小于根节点的值（最大值为根节点传递），右子树的节点必须大于根节点的值（最小值为根节点传递） |
|  √   |     [51. N 皇后](https://leetcode.cn/problems/n-queens/)     | 74.0%  | 困难 | 经典的一道递归题，关键在于优化判断条件，开辟空间存储已经放置过的皇后位置，减少额外的判断 |
|  √   | [8. 字符串转换整数 (atoi)](https://leetcode.cn/problems/string-to-integer-atoi/) | 21.6%  | 中等 |                比较复杂的是太多的条件需要考虑                |
|  √   | [96. 不同的二叉搜索树](https://leetcode.cn/problems/unique-binary-search-trees/) | 70.2%  | 中等 | 递归选择根节点，将创建树的问题转化为更小的子问题；使用 map 存储结果 |
|  √   | [剑指 Offer 11. 旋转数组的最小数字](https://leetcode.cn/problems/xuan-zhuan-shu-zu-de-zui-xiao-shu-zi-lcof/) | 49.2%  | 简单 | 重复、最小；搜索最小值的要点是比较 nums[mid] 与 nums[right] 的大小；搜索重复值的要点是 --right |
|  √   | [33. 搜索旋转排序数组](https://leetcode.cn/problems/search-in-rotated-sorted-array/) |        |      | 无重复；通过 nums[right] 与 nums[mid] 比较可以确定 mid 必定位于两段序列中的一个；通过判断 target 是否在这段递增序列中，变更 left、right |
|  √   | [搜索旋转排序数组2](https://leetcode.cn/problems/search-in-rotated-sorted-array-ii/) |        |      | 含有重复元素版本；需要多考虑一种情况，即 midVal 和边界值相同，可以直接缩短边界 |
|  √   | [寻找旋转排序数组中的最小值](https://leetcode.cn/problems/find-minimum-in-rotated-sorted-array/) |        |      |       只需要确定 midVal 所在序列即可，找对递减方向即可       |
|  √   | [寻找旋转排序数组中的最小值2](https://leetcode.cn/problems/find-minimum-in-rotated-sorted-array-ii/) |        |      |             含有重复元素版本，同上，直接缩短边界             |
|  √   | [面试题 10.03. 搜索旋转数组](https://leetcode.cn/problems/search-rotate-array-lcci/) |        |      | 和[搜索旋转排序数组2](https://leetcode.cn/problems/search-in-rotated-sorted-array-ii/)基本一样，更进一步要求最小下标 |
|  √   | [739. 每日温度](https://leetcode.cn/problems/daily-temperatures/) | 69.1%  | 中等 |                   动态规划，复用子问题答案                   |
|  √   | [695. 岛屿的最大面积](https://leetcode.cn/problems/max-area-of-island/) | 67.5%  | 中等 |                      经典回溯+哈希表题                       |
|  √   | [394. 字符串解码](https://leetcode.cn/problems/decode-string/) | 56.1%  | 中等 |            使用迭代 + 栈体会递归下降和回退的过程             |
|  √   |   [134. 加油站](https://leetcode.cn/problems/gas-station/)   | 54.1%  | 中等 | 因为剩余的油量不小于0，才会地点 i 走到地点 i+1；假如从 i 不能到达 j，由于从 i 开始，到达下一个地点的剩余油量必定大于等于0，如果从 i 都不能到达 j，则其他点没有剩余油量更不可能到达 j |
|  √   | [236. 二叉树的最近公共祖先](https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-tree/) | 69.0%  | 中等 |                 DFS 在更新过程中更新最深结点                 |
|  √   | [543. 二叉树的直径](https://leetcode.cn/problems/diameter-of-binary-tree/) | 56.9%  | 简单 |              DFS 过程中返回最长的那条路径的长度              |
|  √   | [84. 柱状图中最大的矩形](https://leetcode.cn/problems/largest-rectangle-in-histogram/) | 44.2%  | 困难 | 和 [739. 每日温度](https://leetcode.cn/problems/daily-temperatures/) 有异曲同工之妙；一个矩形和其他矩形形成的面积，取决于这个矩形左右边界的位置，而寻找左右边界的位置可以依据相邻矩形的高度关系，决定是否复用相邻矩形的边界 |
|  √   |    [69. x 的平方根](https://leetcode.cn/problems/sqrtx/)     | 38.8%  | 简单 |                           二分查找                           |
|      | [162. 寻找峰值](https://leetcode.cn/problems/find-peak-element/) | 49.5%  | 中等 |                                                              |
|      | [113. 路径总和 II](https://leetcode.cn/problems/path-sum-ii/) | 63.1%  | 中等 |                                                              |
|      | [19. 删除链表的倒数第 N 个结点](https://leetcode.cn/problems/remove-nth-node-from-end-of-list/) | 44.0%  | 中等 |                                                              |
|      | [239. 滑动窗口最大值](https://leetcode.cn/problems/sliding-window-maximum/) | 49.9%  | 困难 |                                                              |
|      | [224. 基本计算器](https://leetcode.cn/problems/basic-calculator/) | 41.9%  | 困难 |                                                              |
|      | [1114. 按序打印](https://leetcode.cn/problems/print-in-order/) | 65.3%  | 简单 |                                                              |
|      | [160. 相交链表](https://leetcode.cn/problems/intersection-of-two-linked-lists/) | 62.8%  | 简单 |                                                              |
|      |   [55. 跳跃游戏](https://leetcode.cn/problems/jump-game/)    | 43.5%  | 中等 |                                                              |
|      | [169. 多数元素](https://leetcode.cn/problems/majority-element/) | 66.8%  | 简单 |                                                              |
|      |  [178. 分数排名](https://leetcode.cn/problems/rank-scores/)  | 60.5%  | 中等 |                                                              |
|      | [82. 删除排序链表中的重复元素 II](https://leetcode.cn/problems/remove-duplicates-from-sorted-list-ii/) | 53.3%  | 中等 |                                                              |
|      |  [139. 单词拆分](https://leetcode.cn/problems/word-break/)   | 53.0%  | 中等 |                                                              |
|      | [剑指 Offer 38. 字符串的排列](https://leetcode.cn/problems/zi-fu-chuan-de-pai-lie-lcof/) | 58.0%  | 中等 |                                                              |
|      | [104. 二叉树的最大深度](https://leetcode.cn/problems/maximum-depth-of-binary-tree/) | 77.0%  | 简单 |                                                              |
|      | [剑指 Offer 42. 连续子数组的最大和](https://leetcode.cn/problems/lian-xu-zi-shu-zu-de-zui-da-he-lcof/) | 60.7%  | 简单 |                                                              |
|      | [329. 矩阵中的最长递增路径](https://leetcode.cn/problems/longest-increasing-path-in-a-matrix/) | 50.2%  | 困难 |                                                              |



|      | [1143. 最长公共子序列](https://leetcode.cn/problems/longest-common-subsequence/) | 64.3% | 中等 |      |
| :--: | :----------------------------------------------------------: | ----- | :--: | ---- |
|      | [剑指 Offer 38. 字符串的排列](https://leetcode.cn/problems/zi-fu-chuan-de-pai-lie-lcof/) | 58.0% | 中等 |      |
|      | [64. 最小路径和](https://leetcode.cn/problems/minimum-path-sum/) | 69.2% | 中等 |      |
|      | [704. 二分查找](https://leetcode.cn/problems/binary-search/) | 54.4% | 简单 |      |
|      | [240. 搜索二维矩阵 II](https://leetcode.cn/problems/search-a-2d-matrix-ii/) | 51.1% | 中等 |      |
|      | [剑指 Offer 07. 重建二叉树](https://leetcode.cn/problems/zhong-jian-er-cha-shu-lcof/) | 70.2% | 中等 |      |
|      | [面试题 02.05. 链表求和](https://leetcode.cn/problems/sum-lists-lcci/) | 46.8% | 中等 |      |
|      | [71. 简化路径](https://leetcode.cn/problems/simplify-path/)  | 44.2% | 中等 |      |
|      | [445. 两数相加 II](https://leetcode.cn/problems/add-two-numbers-ii/) | 59.6% | 中等 |      |
|      | [424. 替换后的最长重复字符](https://leetcode.cn/problems/longest-repeating-character-replacement/) | 53.8% | 中等 |      |
|      | [560. 和为 K 的子数组](https://leetcode.cn/problems/subarray-sum-equals-k/) | 45.1% | 中等 |      |
|      | [26. 删除有序数组中的重复项](https://leetcode.cn/problems/remove-duplicates-from-sorted-array/) | 54.0% | 简单 |      |
|      | [剑指 Offer 48. 最长不含重复字符的子字符串](https://leetcode.cn/problems/zui-chang-bu-han-zhong-fu-zi-fu-de-zi-zi-fu-chuan-lcof/) | 46.4% | 中等 |      |
|      | [152. 乘积最大子数组](https://leetcode.cn/problems/maximum-product-subarray/) | 42.6% | 中等 |      |
|      |   [112. 路径总和](https://leetcode.cn/problems/path-sum/)    | 53.2% | 简单 |      |
|      | [85. 最大矩形](https://leetcode.cn/problems/maximal-rectangle/) | 53.0% | 困难 |      |
|      | [309. 最佳买卖股票时机含冷冻期](https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | 62.9% | 中等 |      |
|      | [402. 移掉 K 位数字](https://leetcode.cn/problems/remove-k-digits/) | 32.4% | 中等 |      |
|      | [131. 分割回文串](https://leetcode.cn/problems/palindrome-partitioning/) | 72.9% | 中等 |      |
|      | [297. 二叉树的序列化与反序列化](https://leetcode.cn/problems/serialize-and-deserialize-binary-tree/) | 57.6% | 困难 |      |
|      | [面试题 17.21. 直方图的水量](https://leetcode.cn/problems/volume-of-histogram-lcci/) | 63.6% | 困难 |      |
|      | [165. 比较版本号](https://leetcode.cn/problems/compare-version-numbers/) | 52.0% | 中等 |      |
|      | [726. 原子的数量](https://leetcode.cn/problems/number-of-atoms/) | 55.2% | 困难 |      |
|      | [185. 部门工资前三高的所有员工](https://leetcode.cn/problems/department-top-three-salaries/) | 51.3% | 困难 |      |
|      | [83. 删除排序链表中的重复元素](https://leetcode.cn/problems/remove-duplicates-from-sorted-list/) | 53.5% | 简单 |      |
|      | [253. 会议室 II](https://leetcode.cn/problems/meeting-rooms-ii/)![plus](https://static.leetcode.cn/cn-mono-assets/production/assets/plus.31398c34.svg) | 51.4% | 中等 |      |
|      | [234. 回文链表](https://leetcode.cn/problems/palindrome-linked-list/) | 51.5% | 简单 |      |
|      |  [61. 旋转链表](https://leetcode.cn/problems/rotate-list/)   | 41.7% | 中等 |      |
|      |  [48. 旋转图像](https://leetcode.cn/problems/rotate-image/)  | 74.1% | 中等 |      |
|      | [34. 在排序数组中查找元素的第一个和最后一个位置](https://leetcode.cn/problems/find-first-and-last-position-of-element-in-sorted-array/) | 42.2% | 中等 |      |
|      |  [91. 解码方法](https://leetcode.cn/problems/decode-ways/)   | 32.1% | 中等 |      |
|      | [213. 打家劫舍 II](https://leetcode.cn/problems/house-robber-ii/) | 43.7% | 中等 |      |
|      | [28. 实现 strStr()](https://leetcode.cn/problems/implement-strstr/) | 40.5% | 简单 |      |
|      | [207. 课程表](https://leetcode.cn/problems/course-schedule/) | 53.9% | 中等 |      |
|      | [470. 用 Rand7() 实现 Rand10()](https://leetcode.cn/problems/implement-rand10-using-rand7/) | 55.1% | 中等 |      |
|      | [138. 复制带随机指针的链表](https://leetcode.cn/problems/copy-list-with-random-pointer/) | 66.9% | 中等 |      |
|      | [887. 鸡蛋掉落](https://leetcode.cn/problems/super-egg-drop/) | 29.7% | 困难 |      |
|      | [226. 翻转二叉树](https://leetcode.cn/problems/invert-binary-tree/) | 79.1% | 简单 |      |
|      | [179. 最大数](https://leetcode.cn/problems/largest-number/)  | 41.1% | 中等 |      |
|      | [718. 最长重复子数组](https://leetcode.cn/problems/maximum-length-of-repeated-subarray/) | 56.7% | 中等 |      |
|      | [59. 螺旋矩阵 II](https://leetcode.cn/problems/spiral-matrix-ii/) | 76.4% | 中等 |      |
|      | [39. 组合总和](https://leetcode.cn/problems/combination-sum/) | 72.8% | 中等 |      |
|      |   [460. LFU 缓存](https://leetcode.cn/problems/lfu-cache/)   | 44.0% | 困难 |      |
|      | [剑指 Offer 36. 二叉搜索树与双向链表](https://leetcode.cn/problems/er-cha-sou-suo-shu-yu-shuang-xiang-lian-biao-lcof/) | 65.3% | 中等 |      |
|      | [剑指 Offer 25. 合并两个排序的链表](https://leetcode.cn/problems/he-bing-liang-ge-pai-xu-de-lian-biao-lcof/) | 72.6% | 简单 |      |
|      | [141. 环形链表](https://leetcode.cn/problems/linked-list-cycle/) | 51.4% | 简单 |      |
|      | [94. 二叉树的中序遍历](https://leetcode.cn/problems/binary-tree-inorder-traversal/) | 75.9% | 简单 |      |
|      | [287. 寻找重复数](https://leetcode.cn/problems/find-the-duplicate-number/) | 64.9% | 中等 |      |
|      | [17. 电话号码的字母组合](https://leetcode.cn/problems/letter-combinations-of-a-phone-number/) | 57.8% | 中等 |      |
|      | [354. 俄罗斯套娃信封问题](https://leetcode.cn/problems/russian-doll-envelopes/) | 42.3% | 困难 |      |
|      | [108. 将有序数组转换为二叉搜索树](https://leetcode.cn/problems/convert-sorted-array-to-binary-search-tree/) | 76.7% | 简单 |      |
|      | [328. 奇偶链表](https://leetcode.cn/problems/odd-even-linked-list/) | 65.4% | 中等 |      |
|      |  [62. 不同路径](https://leetcode.cn/problems/unique-paths/)  | 67.0% | 中等 |      |
|      | [349. 两个数组的交集](https://leetcode.cn/problems/intersection-of-two-arrays/) | 74.1% | 简单 |      |
|      |  [456. 132 模式](https://leetcode.cn/problems/132-pattern/)  | 36.3% | 中等 |      |
|      | [44. 通配符匹配](https://leetcode.cn/problems/wildcard-matching/) | 33.2% | 困难 |      |
|      | [剑指 Offer 26. 树的子结构](https://leetcode.cn/problems/shu-de-zi-jie-gou-lcof/) | 46.7% | 中等 |      |
|      | [315. 计算右侧小于当前元素的个数](https://leetcode.cn/problems/count-of-smaller-numbers-after-self/) | 42.3% | 困难 |      |
|      | [670. 最大交换](https://leetcode.cn/problems/maximum-swap/)  | 46.1% | 中等 |      |
|      | [剑指 Offer 13. 机器人的运动范围](https://leetcode.cn/problems/ji-qi-ren-de-yun-dong-fan-wei-lcof/) | 53.3% | 中等 |      |
|      | [343. 整数拆分](https://leetcode.cn/problems/integer-break/) | 61.7% | 中等 |      |
|      | [209. 长度最小的子数组](https://leetcode.cn/problems/minimum-size-subarray-sum/) | 48.7% | 中等 |      |
|      | [剑指 Offer 34. 二叉树中和为某一值的路径](https://leetcode.cn/problems/er-cha-shu-zhong-he-wei-mou-yi-zhi-de-lu-jing-lcof/) | 58.6% | 中等 |      |
|      | [862. 和至少为 K 的最短子数组](https://leetcode.cn/problems/shortest-subarray-with-sum-at-least-k/) | 21.0% | 困难 |      |
|      | [295. 数据流的中位数](https://leetcode.cn/problems/find-median-from-data-stream/) | 52.7% | 困难 |      |
|      | [177. 第N高的薪水](https://leetcode.cn/problems/nth-highest-salary/) | 46.5% | 中等 |      |
|      |    [155. 最小栈](https://leetcode.cn/problems/min-stack/)    | 58.1% | 简单 |      |
|      | [912. 排序数组](https://leetcode.cn/problems/sort-an-array/) | 55.6% | 中等 |      |
|      | [181. 超过经理收入的员工](https://leetcode.cn/problems/employees-earning-more-than-their-managers/) | 69.2% | 简单 |      |
|      | [437. 路径总和 III](https://leetcode.cn/problems/path-sum-iii/) | 56.8% | 中等 |      |
|      | [38. 外观数列](https://leetcode.cn/problems/count-and-say/)  | 60.0% | 中等 |      |
|      | [337. 打家劫舍 III](https://leetcode.cn/problems/house-robber-iii/) | 60.7% | 中等 |      |
|      | [35. 搜索插入位置](https://leetcode.cn/problems/search-insert-position/) | 45.3% | 简单 |      |
|      | [剑指 Offer 10- II. 青蛙跳台阶问题](https://leetcode.cn/problems/qing-wa-tiao-tai-jie-wen-ti-lcof/) | 45.4% | 简单 |      |
|      |    [50. Pow(x, n)](https://leetcode.cn/problems/powx-n/)     | 37.8% | 中等 |      |
|      | [剑指 Offer 40. 最小的k个数](https://leetcode.cn/problems/zui-xiao-de-kge-shu-lcof/) | 57.3% | 简单 |      |
|      |      [66. 加一](https://leetcode.cn/problems/plus-one/)      | 45.9% | 简单 |      |
|      | [123. 买卖股票的最佳时机 III](https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-iii/) | 56.0% | 困难 |      |
|      |      [18. 四数之和](https://leetcode.cn/problems/4sum/)      | 39.3% | 中等 |      |
|      | [110. 平衡二叉树](https://leetcode.cn/problems/balanced-binary-tree/) | 57.0% | 简单 |      |
|      | [217. 存在重复元素](https://leetcode.cn/problems/contains-duplicate/) | 55.6% | 简单 |      |
|      | [47. 全排列 II](https://leetcode.cn/problems/permutations-ii/) | 64.7% | 中等 |      |
|      | [279. 完全平方数](https://leetcode.cn/problems/perfect-squares/) | 64.9% | 中等 |      |
|      | [189. 轮转数组](https://leetcode.cn/problems/rotate-array/)  | 44.3% | 中等 |      |
|      | [188. 买卖股票的最佳时机 IV](https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-iv/) | 41.4% | 困难 |      |
|      |   [283. 移动零](https://leetcode.cn/problems/move-zeroes/)   | 64.0% | 简单 |      |
|      | [剑指 Offer 62. 圆圈中最后剩下的数字](https://leetcode.cn/problems/yuan-quan-zhong-zui-hou-sheng-xia-de-shu-zi-lcof/) | 65.8% | 简单 |      |
|      | [剑指 Offer 04. 二维数组中的查找](https://leetcode.cn/problems/er-wei-shu-zu-zhong-de-cha-zhao-lcof/) | 40.1% | 中等 |      |
|      | [153. 寻找旋转排序数组中的最小值](https://leetcode.cn/problems/find-minimum-in-rotated-sorted-array/) | 56.9% | 中等 |      |
|      | [63. 不同路径 II](https://leetcode.cn/problems/unique-paths-ii/) | 40.2% | 中等 |      |
|      |  [67. 二进制求和](https://leetcode.cn/problems/add-binary/)  | 53.8% | 简单 |      |
|      |    [77. 组合](https://leetcode.cn/problems/combinations/)    | 77.1% | 中等 |      |
|      | [180. 连续出现的数字](https://leetcode.cn/problems/consecutive-numbers/) | 48.3% | 中等 |      |
|      | [剑指 Offer 37. 序列化二叉树](https://leetcode.cn/problems/xu-lie-hua-er-cha-shu-lcof/) | 57.0% | 困难 |      |
|      | [184. 部门工资最高的员工](https://leetcode.cn/problems/department-highest-salary/) | 49.4% | 中等 |      |
|      | [剑指 Offer 57 - II. 和为s的连续正数序列](https://leetcode.cn/problems/he-wei-sde-lian-xu-zheng-shu-xu-lie-lcof/) | 71.1% | 简单 |      |
|      | [40. 组合总和 II](https://leetcode.cn/problems/combination-sum-ii/) | 60.8% | 中等 |      |
|      | [167. 两数之和 II - 输入有序数组](https://leetcode.cn/problems/two-sum-ii-input-array-is-sorted/) | 58.5% | 中等 |      |
|      | [142. 环形链表 II](https://leetcode.cn/problems/linked-list-cycle-ii/) | 56.1% | 中等 |      |
|      | [556. 下一个更大元素 III](https://leetcode.cn/problems/next-greater-element-iii/) | 33.3% | 中等 |      |



