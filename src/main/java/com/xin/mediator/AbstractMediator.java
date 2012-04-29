package com.xin.mediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xin.interfaces.AbstractProcessor;

/**
 * 抽象中介器
 * 现在貌似每个具体的中介器之需要生成具体的Handler实例
 * @author micro
 *
 */
public abstract class AbstractMediator implements AbstractProcessor {	
	/**
	 * 生成具体的处理单元实例
	 * @return
	 */
	public abstract AbstractMediatorHandler getMediatorHandlerInstance();
	
	/**
	 * 一条流程中可能会多次使用一个中介器(流程和中介处理单元的映射)
	 */
	private Map<String,List<AbstractMediatorHandler>> process_Handlers =new HashMap<String, List<AbstractMediatorHandler>>();
	
	public final void addMediatorHandler(String processName, AbstractMediatorHandler mediatorHandler)
	{
		List<AbstractMediatorHandler> handlers = process_Handlers.get(processName);
		if(handlers == null)
		{
			handlers = new ArrayList<AbstractMediatorHandler>();
			handlers.add(mediatorHandler);
			process_Handlers.put(processName, handlers);
		}
		else
		{
			handlers.add(mediatorHandler);
		}
	}
	
	public void remove(String processName)
	{
		
	}
	
	/**
	 * 部署流程
	 */
	public final boolean deployProcess(String processName) {
		List<AbstractMediatorHandler> handlers = process_Handlers.get(processName);
		if(handlers == null)
		{
			return true;
		}
		
		for(AbstractMediatorHandler mediatorHandler : handlers)
		{
			if(!mediatorHandler.init())
			{
				return false;
			}
		} 		
		return true;
	}
	
	/**
	 * 卸载流程
	 */
	public final boolean undeplyProcess(String processName) {
		List<AbstractMediatorHandler> handlers = process_Handlers.get(processName);
		if(handlers == null)
		{
			return true;
		}
		
		for(AbstractMediatorHandler mediatorHandler : handlers)
		{
			if(!mediatorHandler.shutdown())
			{
				return false;
			}
		}
		return true;
	}
	
}
