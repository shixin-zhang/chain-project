package com.xin.mediator;


/**
 * 不存在的中介器
 * @author micro
 *
 */
public class NullMediator extends AbstractMediator {
	private static NullMediator nullMedaitor = new NullMediator();
	
	private NullMediator(){}
	
	public AbstractMediator getNullMedator()
	{
		return nullMedaitor;
	}

	@Override
	public AbstractMediatorHandler getMediatorHandlerInstance() {
		throw new UnsupportedOperationException("NullMedaitor can't be called");
	}
	
}
