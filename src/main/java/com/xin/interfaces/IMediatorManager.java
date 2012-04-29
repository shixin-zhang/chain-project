package com.xin.interfaces;

import java.util.List;

import com.xin.mediator.AbstractMediator;

public interface IMediatorManager {

	/**
	 * 根据给定的判断是否存在指定的mediator
	 * @param mediator
	 * @return
	 */
	public boolean containMediator(String mediator);

	/**
	 * 根据给定的名字返回指定的实例
	 * @param mediatorName
	 * @return
	 */
	public AbstractMediator getMediatorInstance(String mediatorName);
	
	/**
	 * 添加中介器
	 * @param mediator
	 */
	public void addMediator(AbstractMediator mediator);
	
	/**
	 * 返回所有的中介器
	 * 返回的链表是不可修改的
	 * @return
	 */
	public List<AbstractMediator> getMediators();

	/**
	 * 移除指定的中介器
	 * @param mediator
	 */
	public void removeMediator(AbstractMediator mediator);
}
