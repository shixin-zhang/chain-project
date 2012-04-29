package com.xin.mediator;

import com.xin.interfaces.AbstractHandler;

/**
 * 抽象中介器处理单元(实例负责处理具体消息)
 * @author micro
 *
 */
public abstract class AbstractMediatorHandler extends AbstractHandler{
	
	@Override
	public void setServiceClass(String serviceClassName) {
		//一般的中介器不需要使用这个模块,除了自定义的中介器
	}
	
	/**
	 * 这个当中解压文件
	 */
	@Override
	public boolean init() {
		return super.init();
	}
	
	@Override
	public final void setsourceDestinationFlag(boolean isSourceDestination) {
		//只是给适配器用的
	}
	
	/**
	 * 任何覆盖该方法的子类需要先释放自己的子类,然后最后调用该方法
	 * 资源文件的删除由ProcessManager统一处理
	 *//*
	@Override
	public boolean shutdown() {
		//这样能释放资源
		inHandlers = null; 
		outHandlers = null;
		resource = null;
		return true;
	}*/
}
