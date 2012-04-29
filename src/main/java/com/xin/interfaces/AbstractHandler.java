package com.xin.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.xin.messge.ICanonicalMsg;
import com.xin.util.api.impl.FileProcessor;

public abstract class AbstractHandler implements Handler {

	protected List<AbstractHandler> inHandlers = new ArrayList<AbstractHandler>();
	protected List<AbstractHandler> outHandlers = new ArrayList<AbstractHandler>();
	protected File resource ;
	/**
	 * 工作目录的具体就是在runtime目录下processName下具体resource同名的文件夹中
	 */
	protected String workDir ; //要是有资源的话该目录就存在有用,否则没有价值(该目录主要就是防止配置信息的地方)
	protected boolean sendSync;
	
	private String processName ;
	
	public abstract ICanonicalMsg requestMessageSync(ICanonicalMsg msg) ;

	public abstract void requestMessageASync(ICanonicalMsg msg) ;
	
	public final void addResource(File resource) {
		this.resource = resource;
	}
	
	public final void addInboundHandler(final AbstractHandler inhandler)
	{
		inHandlers.add(inhandler);
	}
	
	public final void addOutboundHandler(final AbstractHandler outhandler)
	{
		outHandlers.add(outhandler);
	}
	
	/**
	 * 设置每个工作单元的工作目录
	 * @param workdir
	 */
	public final void setWorkDir(String workdir)
	{
		this.workDir = workdir;
	}
	
	/**
	 * 流程上传的时候进行初始化
	 * 父类中的init的函数的功能就是解压缩各种资源文件
	 * @return 成功返回true
	 */
	public boolean init()
	{
		if(resource == null)
		{
			return true;
		}
		else
		{
			//所有的资源包全部是.zip文件
			String resourceFileName = resource.getName();
			String absolutpathName = resource.getAbsolutePath();
			String resourceDir = absolutpathName.substring(0,absolutpathName.lastIndexOf(resourceFileName)); //资源包所在的目录

			workDir = resourceDir + File.separator + resourceFileName;
			FileProcessor.destoryDirectory(workDir);//防止残存文件
			FileProcessor.createDirectory(resourceDir, resourceFileName); //以资源的名字创建子文件夹
			FileProcessor.unZip(resource.getAbsolutePath(), workDir);
			return true;			
		}
	}
	
	/**
	 * 流程卸载的时候调用,进行清理资源
	 * 注意:每个Handler可能会有runtime文件夹,但是这里不处理由processName在卸载流程的时候最后一次性删除文件夹统一处理
	 * @return 资源清理成功返回true,否则返回false
	 */
	public abstract boolean shutdown();
	
	/*
	 * 设置是不是源端，主要是用于在异步消息消息传输的时候使用的，用于适配器的,中介器不需要使用
	 * @param isSourceDestination
	 */
	public abstract void setsourceDestinationFlag(boolean isSourceDestination);
	
	/**
	 * 对于自定义的转换器可能需要有自己的转换服务函数
	 * @param serviceClassName
	 */
	public abstract void setServiceClass(String serviceClassName);
	
	/**
	 * 每个子类需要设置Handler所在的中介器或者适配器的名字
	 * @return
	 */
	public abstract String getProcessorName();
	
	/**
	 * 是否是同步的发送
	 * @param sendSync
	 */
	public final void setSendSync(boolean sendSync) {
		this.sendSync = sendSync;
	}
	
	public final boolean isSendSync()
	{
		return sendSync;
	}
	
	public final void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public final String getProcessName() {
		return processName;
	}
}
















