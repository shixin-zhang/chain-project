package com.xin.util.api;

import java.io.File;
/**
 *文件监听接口，用于实现文件监控 
 */
public interface FileListener {
	
	/**
	 *新添文件
	 *@param newFile  新添的文件 
	 */
	public void onAdd(File newFile) ;

	/**
	 *文件被更新
	 *@param changedFile  更新的文件
	 */
	public void onChange(File changedFile) ;

	/**
	 *文件被删除
	 *@param deletedFileName	删除的文件名
	 */
	public void onDelete(String deletedFileName) ;
}
