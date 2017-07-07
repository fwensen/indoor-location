package cn.edu.uestc.indoorlocation.dao.datasource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import cn.edu.uestc.indoorlocation.dao.LocationDataSource;
import cn.edu.uestc.indoorlocation.dao.model.PlainFingerPrint;
import cn.edu.uestc.indoorlocation.dao.model.PlainRss;
import cn.edu.uestc.indoorlocation.dao.model.Point;
import cn.edu.uestc.indoorlocation.dao.model.Print;
import cn.edu.uestc.indoorlocation.dao.model.Rss;

/**
 * 数据库的数据源
 * 更好的实现方式是使用Iterable接口
 * @author vincent
 */

public class JdbcTemplateLocationDataSource implements LocationDataSource {

		private JdbcTemplate jdbcTemplate;
		
		private long N = 0;
		
		private long TOTAL_POINTS;
		
		private Print prev = null;
		
		/**
		 * 连续读提高效率
		 */
		private static final int FINGER_COUNT = 10;
		
		private List<PlainFingerPrint> seqPlainGingerPrint = null;
		
		private int startPos = 0;
		/**
		 * 缓存
		 */
		private static List<PlainFingerPrint> cache = new ArrayList<PlainFingerPrint>();
		
		//缓存长度
		private static final int CACHE_SIZE = 30;
		
		private static boolean init = false;
		
		public JdbcTemplateLocationDataSource(JdbcTemplate tmplate) {
			this.jdbcTemplate = tmplate;
			this.TOTAL_POINTS = this.count();
			//初始化后首先查询CACHE_SIZE条数据
			cache = queryNextNFingerprint(0, CACHE_SIZE);
//			System.out.println(cache);
		}
		
		/**
		 * 计算指纹数目(坐标点数目)
		 * @return
		 */
		@SuppressWarnings("deprecation")
		private long count() {
			return this.jdbcTemplate.queryForLong(SQLOperation.SELECT_COUNT_FINGERPRINT);
		}
		
		/**
		 * 使用方法是：首先使用hasNext()判断是否还有，然后才调用next()访问。
		 * 注意：当调用hasNext()返回false后，会恢复到原始状态
		 * 
		 */
		@Override
		public Print next() {
//			//取得下一个坐标
//			PlainFingerPrint print =  queryNextFingerprint();
//			//获得坐标点
//			Point point = new Point(print.position_x(), print.position_y(), print.position_z());
//			Print ret = new Print(point);
//			int ap_id = (int) print.ap_id();
//			//查询相关id的指纹信息
//			List<PlainRss> plainRss = findAllRssById(ap_id);
//			
//			for (PlainRss rss : plainRss) {
//				//数据库中存储的是mac1%rss1#mac2%rss2#mac3%rss3....mack%rssk#
//				String details = rss.details();
//				//解析该字符串
//				String[] macRss = details.split("#");
//				int len = macRss.length - 1;  //去掉最后的空格
//				List<Rss> rssis = new ArrayList<Rss>();
//				for (int i = 0; i < len; i++) {
//					String[] mr = macRss[i].trim().split("%");
//					rssis.add(new Rss(mr[0], Integer.parseInt(mr[1])));
//				}
//				ret.addPrint(rssis);
//			}
//			++N;
//			return ret;
			return cacheNext();
		}

		/**
		 * 连续缓存读
		 * @return
		 */
		private Print cacheNext() {
			
			//取得下一个坐标
			PlainFingerPrint print = null;
			
			//如果N小于CACHE_SIZE，则缓存中存有数据,这是可在缓存中查询数据
			if (this.N < CACHE_SIZE) {
				
				//取得下一个坐标
				print = cache.get((int)N);
			} else {
				
				if (this.N == CACHE_SIZE || startPos >= FINGER_COUNT - 1) {
					if (TOTAL_POINTS - N < FINGER_COUNT)
						seqPlainGingerPrint = queryNextNFingerprint((int)N, (int)(TOTAL_POINTS - N));
					else {
						seqPlainGingerPrint = queryNextNFingerprint((int)N, FINGER_COUNT);
					}
					startPos = 0;
				}
				
				print = seqPlainGingerPrint.get(startPos++);
//				print = queryNextFingerprint();
			}
			//获得坐标点
			Point point = new Point(print.position_x(), print.position_y(), print.position_z());
			Print ret = new Print(point);
			int ap_id = (int) print.ap_id();
			//查询相关id的指纹信息
			List<PlainRss> plainRss = findAllRssById(ap_id);
			
			for (PlainRss rss : plainRss) {
				//数据库中存储的是mac1%rss1#mac2%rss2#mac3%rss3....mack%rssk#
				String details = rss.details();
				//解析该字符串
				String[] macRss = details.split("#");
				int len = macRss.length;  //去掉最后的空格
				List<Rss> rssis = new ArrayList<Rss>();
				for (int i = 0; i < len; i++) {
					String[] mr = macRss[i].trim().split("%");
					rssis.add(new Rss(mr[0], Integer.parseInt(mr[1])));
				}
				ret.addPrint(rssis);
			}
			++N;
			return ret;
			
		}
		
		/**
		 * not implement
		 */
		@Override
		public Print prev() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * 当hasNext时不能再次调用它，再次调用回到了初始情况
		 */
		@Override
		public boolean hasNext() {
			boolean ret = N < TOTAL_POINTS;
			if (N >= TOTAL_POINTS) N = 0;
			return ret;
		}

		/**
		 * 查询下一条指纹坐标数据
		 * @return
		 */
		private PlainFingerPrint queryNextFingerprint() {
			return this.jdbcTemplate.queryForObject(SQLOperation.SELECT_SINGLEPRINT_BY_NUMBER,
					new FingerPrintRowMapper(), N);
		}
		
		/**
		 * 查找从start到start + count的指纹数据
		 * @param start
		 * @param count条
		 * @return
		 */
		private List<PlainFingerPrint> queryNextNFingerprint(int start, int count) {
			return this.jdbcTemplate.query(SQLOperation.SELECT_NPRINT_BY_NUMBER, new FingerPrintRowMapper(), start, count);
		}
		
		/**
		 * 根据id查询相关联的rss信息
		 * @param id
		 * @return
		 */
		private List<PlainRss> findAllRssById(int id) {
			return this.jdbcTemplate.query(SQLOperation.SELECT_RSSINFO_BY_APID, 
					new PlainRssRowMapper(), id);
		}
		
		/**
		 * Rss信息
		 * ap_id: 
		 * rss_detail: 这个对应的是mac，rss值组成的序列
		 * 数据库中存储的是mac1%rss1#mac2%rss2#mac3%rss3....mack%rssk#
		 * @author vincent
		 *
		 */
		private static final class PlainRssRowMapper implements RowMapper<PlainRss> {
			@Override
			public PlainRss mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				long ap_id = rs.getInt("ap_id");
				String details = rs.getString("rss_detail");
				return new PlainRss(ap_id, details);
			}
		}
		
		/**
		 * ap_id
		 * position_x：x坐标
		 * position_y：y坐标
		 * position_z：z坐标
		 * @author vincent
		 *
		 */
		private static final class FingerPrintRowMapper implements RowMapper<PlainFingerPrint> {
			@Override
			public PlainFingerPrint mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				long id = rs.getInt("ap_id");
				int x = rs.getInt("position_x");
				int y = rs.getInt("position_y");
				int z = rs.getInt("position_z");
				return new PlainFingerPrint(id, x, y, z);
			}
		}
}
