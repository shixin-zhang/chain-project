package com.xin.util.api.impl;

import javax.xml.transform.dom.DOMSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DOMWriter;
/**
 * Document 与 DOMSource 互相转化类
 */
public class DocSourceTransformer {
	
	/**
	 * 将DOMSource转化为Document
	 * @param source 要转化的DOMSource
	 * @return	转化后的Document
	 */
	public static Document toDocument(DOMSource source){
		DOMReader dreader = new DOMReader();
		return dreader.read((org.w3c.dom.Document) source.getNode());
	}
	
	/**
	 * 将Document转化为DOMSource
	 * @param doc	要转化的Document
	 * @return	转化后的DOMSource
	 */
	public static DOMSource toDOMSource(Document doc){
		DOMSource source = new DOMSource();
		DOMWriter dwriter = new DOMWriter();
		try {
			source.setNode(dwriter.write(doc));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return source;
	}
}
