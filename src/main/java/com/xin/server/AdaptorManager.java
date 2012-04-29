package com.xin.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.xin.adaptor.AbstractAdaptor;
import com.xin.adaptor.AbstractAdaptorHandler;
import com.xin.adaptor.NullAdaptor;
import com.xin.interfaces.IAdaptorManager;

public class AdaptorManager implements IAdaptorManager{
	private final List<AbstractAdaptor> adaptors = new ArrayList<AbstractAdaptor>();
	
	public List<AbstractAdaptor> getAdapters() {
		return Collections.unmodifiableList(adaptors);
	}
	
	public void addAdaptors(AbstractAdaptor adaptor)
	{
		if(adaptor == null || adaptor == NullAdaptor.getNullAdaptor())
			return ;
		adaptors.add(adaptor);
	}
	
	/**
	 * 实例的对象只存在一个
	 * @param adaptor
	 */
	public void removeAdaptors(AbstractAdaptor adaptor)
	{
		adaptors.remove(adaptor);
	}

	/**
	 * 根据适配器的类型名查询适配器是否存在
	 * @param adaptorName
	 * @return
	 */
	public boolean containAdaptor(String adaptorName)
	{
		for(AbstractAdaptor adaptor : adaptors)
		{
			if(adaptor.getClass().getSimpleName().equals(adaptorName))
				return true;
		}
		return false;
	}
	/**
	 * 根据适配器的名字找寻适配器
	 * @param adaptorName
	 * @return
	 */
	public AbstractAdaptor getAdaptorInstance(String adaptorName)
	{
		for(AbstractAdaptor adaptor : adaptors)
		{
			if(adaptor.getClass().getSimpleName().equals(adaptorName))
				return adaptor;
		}
		
		return NullAdaptor.getNullAdaptor();
	}
	
}
