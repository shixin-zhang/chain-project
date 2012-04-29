package com.xin.interfaces;

import java.util.List;

import com.xin.adaptor.AbstractAdaptor;

public interface IAdaptorManager {
	
	/**
	 * 获得所有的适配器链表
	 * 注:目前实现中返回的是不可修改的链表
	 * @return
	 */
	public List<AbstractAdaptor> getAdapters();
	
	/**
	 * 添加适配器
	 * @param adaptor
	 */
	public void addAdaptors(AbstractAdaptor adaptor);
	
	/**
	 * 移除适配器
	 * @param adaptor
	 */
	public void removeAdaptors(AbstractAdaptor adaptor);

	/**
	 * 根据给定的名字判断给定的适配器是否存在
	 * @param adaptorName
	 * @return
	 */
	public boolean containAdaptor(String adaptorName);

	/**
	 * 根据给定的名字返回适配器实例(适配器的实例是唯一的)
	 * 若给定的不存在返回NullAdaptor
	 * @param adaptorName
	 * @return
	 */
	public AbstractAdaptor getAdaptorInstance(String adaptorName);
}
