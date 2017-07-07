package cn.edu.uestc.indoorlocation.dao.datasource;

import cn.edu.uestc.indoorlocation.dao.LocationDataSource;
import cn.edu.uestc.indoorlocation.dao.model.Print;

public class StaticDataSource implements LocationDataSource{

	@Override
	public Print next() {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public Print prev() {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasNext() {

		throw new UnsupportedOperationException();
	}

}
