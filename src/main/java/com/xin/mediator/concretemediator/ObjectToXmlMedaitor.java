package com.xin.mediator.concretemediator;

import com.xin.mediator.AbstractMediator;
import com.xin.mediator.AbstractMediatorHandler;
import com.xin.mediator.concretehandler.ObjectToXmlMedaitorHandler;

public class ObjectToXmlMedaitor extends AbstractMediator {

	@Override
	public AbstractMediatorHandler getMediatorHandlerInstance() {
		return new ObjectToXmlMedaitorHandler();
	}

}
