package com.xin.adaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xin.interfaces.AbstractProcessor;

/**
 * 抽象适配器
 * @author micro
 *
 */
public abstract class AbstractAdaptor implements AbstractProcessor{

	/**
	 * 生成适配器处理单元实例
	 * @return
	 */
	public abstract AbstractAdaptorHandler getAdaptorHandlerInstance();
	
	//一条流程中可能使用多次一个适配器(流程名和适配器处理单元的映射)
	private Map<String,List<AbstractAdaptorHandler>> process_Handlers = new HashMap<String, List<AbstractAdaptorHandler>>();
	
	public void addAdaptorHandler(String processName , AbstractAdaptorHandler adaptorHandler)
	{
		List<AbstractAdaptorHandler> handlers = process_Handlers.get(processName);
		if(handlers == null)
		{
			handlers = new ArrayList<AbstractAdaptorHandler>();
			handlers.add(adaptorHandler);
			process_Handlers.put(processName, handlers);
		}
		else
		{
			handlers.add(adaptorHandler);
		}
	}
	
	public void remove(String processName)
	{
		
	}
	
	/**
	 * 部署流程
	 */
	public final boolean deployProcess(String processName)
	{
		List<AbstractAdaptorHandler> handlers = process_Handlers.get(processName);
		if(handlers == null)
		{
			return true;
		}
		for(AbstractAdaptorHandler adaptorHandler : handlers)
		{
			if(!adaptorHandler.init())
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 卸载流程
	 */
	public final boolean undeplyProcess(String processName)
	{
		List<AbstractAdaptorHandler> handlers = process_Handlers.get(processName);
		if(handlers == null)
		{
			return true;
		}
		for(AbstractAdaptorHandler adaptorHandler : handlers)
		{
			if(!adaptorHandler.shutdown())
			{
				return false;
			}
		}
		return true;
	}
}
