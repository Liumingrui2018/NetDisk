package com.lmr.netdisk.model;

import net.sf.json.util.PropertyFilter;

public class NetFilePropertyFilter implements PropertyFilter {

	@Override
	public boolean apply(Object source, String name, Object value) {
		// TODO Auto-generated method stub
		if(name.equals("realPath"))
			return true;
		else
			return false;
	}

}
