package com.xin.util.api.impl;
/**
 * 叶子节点
 * 用来储存元素的文本内容或属性值
 */
public class Leaf {
	private String xpath;//节点的Xpath路径
	private String value;//节点的文本或属性值
	
	public Leaf(String xpath, String value){
		this.xpath = xpath;
		this.value = value;
	}
	
	/**
	 * 设置Xpath路径值
	 * @param xpath	Xpath路径值
	 */
	public void setXpath(String xpath){
		this.xpath = xpath;
	}
	
	/**
	 * 获得Xpath路径值
	 * @return	Xpath路径值
	 */
	public String getXpath(){
		return this.xpath;
	}
	
	/**
	 * 设置文本或属性值
	 * @param value	文本或属性值
	 */
	public void setValue(String value){
		this.value = value;
	}
	
	/**
	 * 获得文本或属性值
	 * @return	文本或属性值
	 */
	public String getValue(){
		return this.value;
	}
	
	/**
	 * 覆改equals方法
	 *比较两节点内容是否相同
	 */
	public boolean equals(Object obj){
		
		boolean result1 = false;
		boolean result2 = false;
		if(obj instanceof  Leaf){
			Leaf leaf = (Leaf)obj;
			if(this.xpath != null){
				result1 = this.xpath.equals(leaf.getXpath());
			}else{
				result1 = leaf.getXpath() == null;
			}
			
			if(this.value != null)
				result2 = this.value.equals(leaf.getValue());
			else
				result2 =  leaf.getValue() == null;
	
			return result1 & result2;
		}else{
			return false;
		}
	}
	
	/**
	 * 覆盖的hashCode方法
	 *使得节点数组间也可以比较
	 */
	public int hashCode(){
		return 0;
	}

}