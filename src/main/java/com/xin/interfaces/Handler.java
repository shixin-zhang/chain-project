package com.xin.interfaces;

import com.xin.messge.ICanonicalMsg;

public interface Handler {
	
	/**
	 * 同步的方式
	 * @param msg
	 * @return 同步的情况下就返回处理完毕的消息
	 */
	public ICanonicalMsg requestMessageSync(ICanonicalMsg msg);
	
	/**
	 * 异步的方式
	 * @param msg
	 */
	public void requestMessageASync(ICanonicalMsg msg);
	
}
