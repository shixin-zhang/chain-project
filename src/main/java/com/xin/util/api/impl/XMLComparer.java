package com.xin.util.api.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;


/**
 *XML内容比较器
 */
public class XMLComparer {
	/**
	 *比较两个XML文档内容
	 *@param doc1	XML文档
	 *@param doc2	XML文档
	 *@return	如果相同返回True，否则返回False
	 */
	public static boolean compare(Document doc1, Document doc2){
		Element element1 = doc1.getRootElement();
		List<Leaf> elemList1 = new ArrayList<Leaf>();
		parseXML(element1, elemList1);
		
		Element element2 = doc2.getRootElement();
		List<Leaf> elemList2 = new ArrayList<Leaf>();
		parseXML(element2, elemList2);
		
		return elemList1.equals(elemList2);
	}
	
	/*
	 * 递归遍历XML元素
	 * 将信息储存在叶子节点中
	 */
	@SuppressWarnings("unchecked")
	private static void parseXML(Element element, List<Leaf> elemList){
		//遍历属性
		for(Iterator it = element.attributeIterator(); it.hasNext();){
			Attribute attr = (Attribute)it.next();
			String xpath = attr.getPath();
			String value = attr.getText();
			elemList.add(new Leaf(xpath, value));
		}
		List elements = element.elements();
		if (elements.size() == 0) {
			// 没有子元素
			String xpath = element.getPath();
			String value = element.getText();
			elemList.add(new Leaf(xpath, value));
		} else {
			// 有子元素,递归遍历
			for (Iterator it = elements.iterator(); it.hasNext();) {
				Element elem = (Element) it.next();
				parseXML(elem, elemList);
			}
		}
	}
}


