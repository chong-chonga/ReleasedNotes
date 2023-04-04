package com.huanglexin;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.VarHandle;
import java.util.*;

/**
 * @author Huang Lexin
 * @date 2023年03月20日 20:08
 */
public class Test {


	public class TreeNode {
		int val;
		TreeNode left;
		TreeNode right;

		TreeNode() {
		}

		TreeNode(int val) {
			this.val = val;
		}

		TreeNode(int val, TreeNode left, TreeNode right) {
			this.val = val;
			this.left = left;
			this.right = right;
		}
	}


	class ListNode {
		int val;
		ListNode next;

		ListNode(int x) {
			val = x;
			next = null;
		}
	}



}
