package cn.edu.uestc.indoorlocation.dao;

import cn.edu.uestc.indoorlocation.dao.model.Print;

/**
 * 数据源接口
 * 数据源：
 *	 静态数据
 * 	 数据库数据 @see JdbcTemplateLocationDataSource
 * @author vincent
 *
 */
public interface LocationDataSource {

	/**
	 * 返回下一组指纹数据
	 * @return 坐标指纹数据
	 */
	Print next();
	
	/**
	 * 是否还存在下一组指纹数据
	 * @return
	 */
	boolean hasNext();
	
	/**
	 * 返回前一组指纹数据
	 * @return 坐标指纹数据
	 */
	Print prev();
}
