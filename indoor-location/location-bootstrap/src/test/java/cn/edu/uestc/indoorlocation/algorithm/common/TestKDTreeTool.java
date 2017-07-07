package cn.edu.uestc.indoorlocation.algorithm.common;

import org.junit.Test;

import de.biomedical_imaging.edu.wlu.cs.levy.CG.KDTree;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeyDuplicateException;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeyMissingException;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeySizeException;

public class TestKDTreeTool {

	@Test
	public void testAsUnmodifiableKDTree() throws KeySizeException, KeyDuplicateException, KeyMissingException {
		
		KDTree kd = new KDTree(2);
		kd.insert(new double[]{1,2},1);
		kd.insert(new double[]{2,2},2);
		KDTree tree = KDTreeTool.unmodifiableKDTree(kd);
		int val = (int) tree.nearest(new double[]{1,2});
		System.out.println(val);
		//delete
//		tree.delete(new double[]{1,2});
	}
	
}
