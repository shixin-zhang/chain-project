package com.xin.mediator.concretemediator;

import com.xin.mediator.AbstractMediator;
import com.xin.mediator.AbstractMediatorHandler;
import com.xin.mediator.concretehandler.CustomerMediatorHandler;

public class CustomerMediator extends AbstractMediator {

	@Override
	public AbstractMediatorHandler getMediatorHandlerInstance() {
		return new CustomerMediatorHandler();
	}
}
