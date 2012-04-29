package com.xin.interfaces;

public abstract class AbstractCustomerTransformer {
	/*
	 * 用户只要覆盖该方法实现自己的逻辑就行
	 * 在使用的时候用户需要将自己使用的资源文件打包,并且配置文件中给出全类名
	 */
	public abstract Object customerHandlerMessage(Object payload);
	
	/*
	 * 这种方式下可能会用到,但是暂时还没用到
	 * public abstract Object customerHandlerMessage(Object payload ,String encoding);
	 */
	
	/**
	 * 用户若是在卸载流程的过程中要是有需要释放的资源的话就进行释放
	 * 不删除的话有什么恶劣的后果不保证运行的正确性
	 * @return
	 */
	public abstract boolean shutdown();
}
