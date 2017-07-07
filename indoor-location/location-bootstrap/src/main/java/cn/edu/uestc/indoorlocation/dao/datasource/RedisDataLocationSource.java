package cn.edu.uestc.indoorlocation.dao.datasource;

import org.springframework.data.redis.core.RedisTemplate;

import cn.edu.uestc.indoorlocation.dao.LocationDataSource;
import cn.edu.uestc.indoorlocation.dao.model.Print;

public class RedisDataLocationSource implements LocationDataSource{

	private RedisTemplate redisTemplate; 
	
	public RedisDataLocationSource() {
		
	}
	
	@Override
	public Print next() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public Print prev() {
		throw new UnsupportedOperationException();
	}

}
