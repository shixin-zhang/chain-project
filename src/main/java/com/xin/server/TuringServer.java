package com.xin.server;

import com.xin.interfaces.IAdaptorManager;
import com.xin.interfaces.IMediatorManager;

public class TuringServer implements Runnable{
	private boolean state = true;
	
	public static void main(String[] args) {
		Thread thread = new Thread(new TuringServer());
//		thread.setDaemon(true);   //不能设置成后台进程
		thread.start();
		
		/**
		 * 作为测试先在这里手工进行类的创建,以及中介器和适配器的创建
		 */
		IAdaptorManager adaptorManager =  new AdaptorManager();
		//添加适配器
		
		IMediatorManager mediatorManager = new MediatorManager();
		//添加中介器
		String workDir = "";
		String runtimeDir = "";
		
		ProcessManager processManager  = new ProcessManager(adaptorManager, mediatorManager, workDir, runtimeDir);
		
		
		Runtime.getRuntime().addShutdownHook(new CleanUpResourceThread());
	}

	/**
	 * 进行初始化工作目录的工作
	 */
	public void run() {
		while(state)
		{
			
		}
	}
	
	public boolean shutDown()
	{
		state = false;
		return true;
	}
	
}

class CleanUpResourceThread extends Thread
{
	@Override
	public void run() {
		//需要清理的资源
		System.out.println("资源清理完毕");
	}
}
