package com.xin.util.api.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.xin.util.api.FileListener;

/**
 * 目录监控类，基于线程
 */
public class DirectoryWatcher implements Runnable {

	private ArrayList<DirectorySnapshot> directorys = new ArrayList<DirectorySnapshot>();//需要监控的目录列表

	private Collection<FileListener> listeners = new LinkedList<FileListener>();//注册的文件监听者
	
	/*是否从现在开始监控
	 * false意味着将监控开始前目录中存在的文件作为新添文件考虑
	 * true则表示不考虑将先前存在的文件
	 */
	private boolean watchSinceNow = false;
		
	private boolean active = false;

	private int interval = -1;//扫描间隔，毫秒

	private Thread runner;
	
	/*
	 * 构造函数
	 * @param directoryPaths	所有监控的目录列表
	 * @param intervalSeconds	扫描间隔 
	 * @param watchSinceNow 是否从现在开始监控
	 */
	public DirectoryWatcher(String[] directoryPaths, int intervalSeconds, boolean watchSinceNow)
			throws IllegalArgumentException {

		for(String directoryPath : directoryPaths){
			
			File theDirectory = new File(directoryPath);
	
			if (theDirectory != null && !theDirectory.isDirectory()) {
	
				String message = "The path "
						+ directoryPath
						+ " does not represent a valid directory.";
				throw new IllegalArgumentException(message);
	
			}
			
			DirectorySnapshot d = new DirectorySnapshot();
			d.setDirectory(directoryPath);
			directorys.add(d);
		}
		
		this.interval = intervalSeconds * 1000;
		this.watchSinceNow = watchSinceNow;

	}

	/**
	 * 创建、启动线程，开始监控
	 */
	public void start(String threadName) {
		
		/*
		 * 当需要从现在开始监控时，
		 * 记录当前目录状态作为参照物
		 */
		if(watchSinceNow){
			for(DirectorySnapshot d: directorys){
				d.takeSnapshot();
			}
		}
		active = true;

		if (runner == null && interval > 0) {
			runner = new Thread(this,threadName);
			runner.start();
		}

	}

	/**
	 * 停止监控
	 */
	public void stop() {
		active = false;
	}

	public void run() {

		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		while (active) {
			try {
				doScan();//扫描
				Thread.sleep(interval);//在指定时间内休眠
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 扫描目录并响应变化
	 */
	private void doScan() {

		for(DirectorySnapshot d : directorys){
			Map<String, Long> prevFiles = d.getPrevFiles();//先前状态
			Map<String, Long> currentFiles = d.getCurrentFiles();//当前状态
			d.takeSnapshot();//更新当前目录状态
			Iterator<String> currentIt = currentFiles.keySet().iterator();
			//处理当前目录中的每个文件
			while (currentIt.hasNext()) {
				String fileName = currentIt.next();
				Long lastModified = currentFiles.get(fileName);

				if (!prevFiles.containsKey(fileName)) //新增
					fileAdded(new File(fileName));
				else if (prevFiles.containsKey(fileName)) {//更新
					Long prevModified = prevFiles.get(fileName);
					if (prevModified.compareTo(lastModified) != 0) 
						fileChanged(new File(fileName));
	
				}
			}
			//通过先前和当前的状态对比，发现删除的文件
			Iterator<String> prevIt = prevFiles.keySet().iterator();
			while (prevIt.hasNext()) {
				String fileName = (String) prevIt.next();
				if (!currentFiles.containsKey(fileName)) 
					fileDeleted(fileName);
			}
		}
	}

	/**
	 * 删除所有的文件监听者
	*/
	public void removeAllListeners() {
		listeners.clear();
	}

	/**
	 * 添加新的文件监听者
	 * @param listener		文件监听者
	 */
	public void addListener(FileListener listener) {
		listeners.add(listener);
	}

	/**
	 * 调用所有文件监听者中的新添文件处理函数
	 * @param newFile	新添的文件
	 */
	private void fileAdded(File newFile) {

		Iterator<FileListener> listIt = listeners.iterator();

		while (listIt.hasNext()) {
			listIt.next().onAdd(newFile);
		}
	}

	/**
	 * 调用所有文件监听者中的文件更新处理函数
	 * @param changedFile		被更新的文件
	 */
	private void fileChanged(File changedFile) {

		Iterator<FileListener> listIt = listeners.iterator();

		while (listIt.hasNext()) {
			listIt.next().onChange(changedFile);
		}
	}

	/**
	 * 调用所有文件监听者中的文件删除处理函数
	 * @param deletedFile		被删除的文件名
	 */
	private void fileDeleted(String deletedFile) {
		
		Iterator<FileListener> listIt = listeners.iterator();

		while (listIt.hasNext()) {
			 listIt.next().onDelete(deletedFile);
		}
	}
	
	/*
	 * 目录快照类，用来更新目录状态
	 */
	class DirectorySnapshot{
		
		private String directory = null;//所需扫描的目录
		
		private Map<String, Long> prevFiles = new HashMap<String, Long>();//先前状态

		private Map<String, Long> currentFiles = new HashMap<String, Long>();//当前状态
		
		DirectorySnapshot(){
		}

		public void setDirectory(String directory) {
			this.directory = directory;
		}

		public String getDirectory() {
			return directory;
		}

		public void setPrevFiles(Map<String, Long> prevFiles) {
			this.prevFiles = prevFiles;
		}

		public Map<String, Long> getPrevFiles() {
			return prevFiles;
		}

		public void setCurrentFiles(Map<String, Long> currentFiles) {
			this.currentFiles = currentFiles;
		}

		public Map<String, Long> getCurrentFiles() {
			return currentFiles;
		}
		
		//通过遍历，更新目录的先前和当前状态
		public void takeSnapshot() {

			prevFiles.clear();
			prevFiles.putAll(currentFiles);

			currentFiles.clear();

			File theDirectory = new File(directory);
			File[] children = theDirectory.listFiles();

			for (int i = 0; i < children.length; i++) {
				File file = children[i];
				currentFiles.put(file.getAbsolutePath(), new Long(file
						.lastModified()));
			}
		}
	}
}
