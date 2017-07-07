package cn.edu.uestc.indoorlocation.dao.datasource;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestJdbcTemplateLocationDataSource {

	
	public static BasicDataSource dataSource() {
		
		BasicDataSource source = new BasicDataSource();
		source.setDriverClassName("com.mysql.jdbc.Driver");
		source.setUrl("jdbc:mysql://localhost:3306/indoorlocation");
		source.setUsername("root");
		source.setPassword("rootvincent");
		source.setInitialSize(5);
		source.setMaxActive(10);
		return source;
	}
 	
	JdbcTemplateLocationDataSource source;
	
	@Before
	public void setUp() {
		source = new JdbcTemplateLocationDataSource(new JdbcTemplate(dataSource()));
	}
	
	@Test
	public void testNext() {
		long pre = System.currentTimeMillis();
		long start = pre;
		for (int i = 0; i < 100; i++) {
			while (source.hasNext()) {
				source.next();
//				System.out.println(++count);
			}
			System.out.print((System.currentTimeMillis() - pre) + " ");
			pre = System.currentTimeMillis();
		}
		long total = System.currentTimeMillis()-start;
		System.out.println();
		System.out.println("Total: " + total/100.0 + " milliseconds");
	}
}
