package com.xin.messge;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息接口
 */
public interface ICanonicalMsg extends Serializable{
	
	public static final int MSGTYPE_DOMSOURCE = 1;
	public static final int MSGTYPE_STREAMSOURCE = 2;
	public static final int MSGTYPE_SAXSOURCE = 3;

	public static final int STATUS_DONE = 2;
	public static final int STATUS_READY = 1;
	public static final int STATUS_FAULT = 3;
	
	/*
	 * the source of the message
	 */
	public void setSource(String source);
	
	/**
	 * set the mediation of the message
	 * @param mediation
	 */
	public void setMediation(String mediation);
	
	/**
	 * set the content of the message
	 * three types of content,there are domsouce,saxsource and streamsource
	 * @param content
	 */
	public void setContent(Object content);
	public void setEndPoint(String endpoint);
	public void setProperties(Map<String,Serializable> properties);
	public void setReplyTo(String replyto);
	public void setID(String id);
	public void setReplyID(String id);
	public void setReliable(boolean reliable);
	public void setSync(boolean sync);
	public void setType(int type);
	public void setStatus(int status);
	public void setDestination(String destination );
	
	public String getEndPoint();
	public String getReplyTo();
	public Map<String,Serializable> getProperties();
	public String getSource();
	public String getMediation();
	public Object getContent();
	public int getType();
	public String getID();
	public int getStatus();
	public boolean getReliable();
	public boolean getSync();
}
