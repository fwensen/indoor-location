package cn.edu.uestc.indoorlocation.algorithm.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MathUtils {

	/**
	 * 求均值
	 * @param rssis
	 * @return
	 */
	public static int mean(int[] rssis) {
		
		int ret = 0;
		for (int i = 0; i < rssis.length; i++)
			ret += rssis[i];
		return ret/rssis.length;
	}
	
	/**
	 * 均值
	 * @param rssis
	 * @return
	 */
	public static int mean(List<Integer> rssis) {
		
		int ret = 0;
		for (int num : rssis)
			ret+=num;
		return ret/rssis.size();
	}
	
	/**
	 * 得到中值
	 * @param rssis
	 * @return
	 */
	public static int mid(int[] rssis) {
		
		int [] out = new int[rssis.length];
		System.arraycopy(rssis, 0, out, 0, rssis.length);
		Arrays.sort(out);
		return out[out.length/2];
	}
	
	/**
	 * 得到中值
	 * @param rssis
	 * @return
	 */
	public static int mid(List<Integer> rssis) {
		
		List<Integer> out = new ArrayList<Integer>();
		for (int i = 0; i < rssis.size(); i++)
			out.add(rssis.get(i));
		Collections.sort(out);
		return out.get(out.size()/2);
	}
	
	
	/**
	 * 去掉最小的N个数，最大的M个数，返回已排序的数
	 */
	public static int[] removeNminMmaxSorted(int[] rssis, int N, int M) {
	
		if (N+M > rssis.length) return null;
		int[] newRssis = new int[rssis.length];
		System.arraycopy(rssis, 0, newRssis, 0, rssis.length);
		Arrays.sort(newRssis);
		int [] out = new int[rssis.length - N - M];
		System.arraycopy(newRssis, N, out, 0, newRssis.length-N-M);
		newRssis = null;
		return out;
	}
	
	/**
	 * 去掉最小的N个数，最大的M个数，返回非排序的
	 * @param rssis
	 * @param N
	 * @param M
	 * @return
	 */
	public static int[] removeNminMmaxNotSort(int[] rssis, int N, int M) {
		
		int [] out = removeNminMmaxSorted(rssis, N, M);
		List<Integer> outList = new ArrayList<Integer>();
		for (int i = 0; i < out.length; i++)
			outList.add(out[i]);
		
		int[] ret = new int[out.length];
		int n = 0;
		for (int i = 0; i < rssis.length; i++) {
			
			for (int j = 0; j < outList.size(); j++) {
				
				if (rssis[i] == outList.get(j)) {
					outList.remove(j);
//					if (n < ret.length)
					ret[n++] = rssis[i];
//					else
//						return ret;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 * 协方差
	 * @param nums1
	 * @param nums2
	 * @return
	 */
	public static double cov(final List<Integer> nums1, final List<Integer> nums2) {
		
		double ret = 0.0;
		if (nums1.size() != nums2.size()) return -1;
		if (nums1.size() == 0 || nums2.size() == 0) return 0;
		
		double mean1 = mean(nums1);
		double mean2 = mean(nums2);
		
		for (int i = 0; i < nums1.size(); i++) {
			ret += (nums1.get(i) - mean1)*(nums2.get(i) - mean2);
		}
		return ret / nums1.size();
	}
	
	/**
	 * 方差
	 * @param nums
	 * @return
	 */
	public static double var(final List<Integer> nums) {
		
		double ret = 0.0;
		double mean = mean(nums);
		for (double num : nums) 
			ret += Math.pow((num - mean), 2);
		return ret / nums.size();
	}
	
	/**
	 * 点积计算
	 * 注意，当两个数列长度不等时，以最短长度计算
	 * @param nums1
	 * @param nums2
	 * @return
	 */
	public static double dotProduct(final List<Integer> nums1, final List<Integer> nums2) {
		
		double ret = 0.0;
		int len = Math.min(nums1.size(), nums2.size());
		for (int i = 0; i < len; i++)
			ret += (nums1.get(i) * nums2.get(i));
		return ret;
	}
	
	/**
	 * 明氏距离
	 * @param nums1
	 * @param nums2
	 * @param n
	 * @return
	 */
	public static double minkowsky(final List<Integer> nums1, final List<Integer> nums2, double n) {
		
		if (nums1.size() != nums2.size()) return 0;
		double sum = 0.0;
		for (int i = 0; i < nums1.size(); i++)
			sum += Math.pow(Math.abs(nums1.get(i) - nums2.get(i)), n);
		return Math.pow(sum, 1.0/n);
	}
	
	/**
	 * 欧氏距离
	 * @param nums1
	 * @param nums2
	 * @return
	 */
	public static double euclidean(final List<Integer> nums1, final List<Integer> nums2) {
		
		double sum = 0.0001;
		assert nums1.size() == nums2.size();
		for (int i = 0; i < nums1.size(); i++) {
			sum += Math.pow(Math.abs(nums1.get(i) - nums2.get(i)), 2.0);
		}
		
		return Math.sqrt(sum);
//		return minkowsky(nums1, nums2, 2.0);
	}
	
	/**
	 * 
	 * @param nums
	 * @return
	 */
	public static double module(final List<Integer> nums) {
		
		double sum = 0;
		for (double num : nums) sum += (num*num);
		return Math.sqrt(sum);
	}
	
	/**
	 * 夹角余弦
	 * @param nums1
	 * @param nums2
	 * @return
	 */
	public static double cosine(final List<Integer> nums1, final List<Integer> nums2) {
		
		return dotProduct(nums1, nums2) / (module(nums1)*module(nums2));
	}
	
	/**
	 * 皮尔逊相关系数
	 * @param nums1
	 * @param nums2
	 * @return
	 */
	public static double corr(final List<Integer> nums1, final List<Integer> nums2) {
		
		double ret = cov(nums1, nums2);
		return ret / (Math.sqrt(cov(nums1, nums1)) * Math.sqrt(cov(nums2, nums2)));
	}
	
	
	/**
	 * 高斯滤波
	 * @param nums 待滤波信号数组
	 * @return
	 */
	public static List<Integer> gauss(List<Integer> nums) {
		
		List<Integer> ret = new ArrayList<>();
		int length = nums.size();
		//平均值
		int avg = mean(nums);
		/**
		 * 均方和
		 */
		int sumOfSqure = 0;
		for (int num : nums) sumOfSqure += Math.pow(num - avg, 2);
		/**
		 * 得到theta值
		 */
		int theta = (int) Math.sqrt(sumOfSqure/(length-1));
		/**
		 * 滤波
		 */
		int startPos = avg - theta;
		int endPos = avg + theta;
		for (int num : nums) {
			if (num >= startPos && num <= endPos) ret.add(num);
		}
		return ret;
	}
	
	

	/**
	 * 高斯滤波
	 * @param nums 待滤波信号数组
	 * @return
	 */
	public static int[] gauss(int[] nums) {
		
		List<Integer> ret = new ArrayList<>();
		int length = nums.length;
		//平均值
		int avg = mean(nums);
		/**
		 * 均方和
		 */
		int sumOfSqure = 0;
		for (int num : nums) sumOfSqure += Math.pow(num - avg, 2);
		/**
		 * 得到theta值
		 */
		int theta = (int) Math.sqrt(sumOfSqure/(length-1));
		/**
		 * 滤波
		 */
		int startPos = avg - theta;
		int endPos = avg + theta;
		for (int num : nums) {
			if (num >= startPos && num <= endPos) ret.add(num);
		}
		int [] result = new int[ret.size()];
		for (int i = 0; i < ret.size(); i++) result[i] = ret.get(i);
//		return ret.toArray(int);
		return result;
	}
	
	///################################################################################
	
	public static void testRemoveNminMmax() {
		
		int [] rssis = {2,1,3,4,6,7,9,5,8};
		int[] out = removeNminMmaxSorted(rssis,2,2);
		for (int i = 0; i < out.length; i++) {
			System.out.print(out[i] + " ");
		}
		System.out.println();
	}
	
//	public static void testRemoveNminMmaxNotSort() {
//		
//		int [] array = {1,1,1,2,2,3,3,4,6,5,4,7,7,7,8,8};
//		System.out.println(Arrays.toString(array));
//		int [] out = removeNminMmaxNotSort(array, 3,2);
//		System.out.println(Arrays.toString(out));
//	}
//	
//	public static void main(String[] args) {
//		
//		testRemoveNminMmaxNotSort();
//	}
//	
}
