package com.xin.adaptor;

import com.xin.interfaces.AbstractHandler;

/**
 * 抽象适配器处理单元(实例负责处理具体的消息)
 * @author micro
 * 异步的处理过程是这样的：外部发起请求,请求向下发送,若是异步的情况下存在返回值,则源端当收到内部的调用的时候这个时候就是最终的结果
 * 同步就很简单源端发起请求之后最终肯定能返回一个消息msg
 *
 */

/**
 * 异步的过程是这样的,若接收到requestMessageASync(requestMsg)首先判断sourceDestinationFlag标志,若是true,就将这个请求返回给外部的请求者
 * 若是false,就遍历outHandlers(异步的情况下可能有很多的下家)
 */

/**
 * 同步的过程就比较简单了,接收到请求若没有下家了就证明到达了终点,处理完消息就将值返回(同步的情况下需要考虑到线程安全?)
 */

public abstract class AbstractAdaptorHandler extends AbstractHandler {
	
	/**
	 * 在流程中判断是不是源处理端
	 * 这个主要是用于适配器处理单元若是源端则在接收到返回的数据时候不用再向outHandlers发送数据(貌似现在主要还是用在源适配器端)
	 */
	private boolean sourceDestinationFlag =true;
	
	@Override
	public void setServiceClass(String serviceClassName) {
		//同样一般的函数不需使用,保留的接口		
		//目前还没想到适配器使用这个地方
	}
	
	/**
	 * 设置这个标志的目的是为了防止在异步的情况下,要是存在环的情况下消息没有落地的时候
	 */
	@Override
	public void setsourceDestinationFlag(boolean isSourceDestination) {
		this.sourceDestinationFlag = isSourceDestination; 
	}
	
}
 