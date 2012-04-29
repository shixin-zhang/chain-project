package com.xin.metadata;


import java.util.Map;
import com.xin.interfaces.AbstractHandler;

/**
 * 
 * @author Turing
 *
 */
public class ChainProcessors {
	Map<String,AbstractHandler> processHandlers ;
	
	public void setProcessHandlers(Map<String, AbstractHandler> processHandlers) {
		this.processHandlers = processHandlers;
	}
	
	public Map<String, AbstractHandler> getProcessHandlers() {
		return processHandlers;
	}
}