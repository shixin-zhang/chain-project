package com.xin.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.xin.interfaces.IMediatorManager;
import com.xin.mediator.AbstractMediator;

public class MediatorManager implements IMediatorManager{
	private final List<AbstractMediator> mediators = new ArrayList<AbstractMediator>();
	
	public void addMediator(AbstractMediator mediator)
	{
		mediators.add(mediator);
	}
	
	public void removeMediator(AbstractMediator mediator)
	{
		mediators.remove(mediator);
	}
	
	public List<AbstractMediator> getMediators() {
		return Collections.unmodifiableList(mediators);
	}
	
	/**
	 * 根据给定的名字判断是不是存在指定的mediator
	 * @param medaitorName
	 * @return
	 */
	public boolean containMediator(String medaitorName)
	{
		for(AbstractMediator mediator:mediators)
		{
			if(mediator.getClass().getSimpleName().equals(medaitorName))
				return true;
		}
		return false;
	}
	
	public AbstractMediator getMediatorInstance(String mediatorName)
	{
		for(AbstractMediator mediator : mediators)
		{
			if(mediator.getClass().getSimpleName().equals(mediatorName))
				return mediator;
		}
		return null;
	}

}
