package com.xin.adaptor;


/**
 * 不存在的适配器
 * @author micro
 */
public class NullAdaptor extends AbstractAdaptor{
	private static NullAdaptor nullAdaptor = new NullAdaptor();

	private NullAdaptor(){}
	
	public static AbstractAdaptor getNullAdaptor()
	{
		return nullAdaptor;
	}
	
	@Override
	public AbstractAdaptorHandler getAdaptorHandlerInstance() {
		throw new UnsupportedOperationException("NullAdaptor can't be called");
	}
}
