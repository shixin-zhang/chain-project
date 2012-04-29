package com.xin.mediator.concretehandler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.WeakishReference.HardReference;

import com.xin.exception.HandlerMessageErrorException;
import com.xin.interfaces.AbstractCustomerTransformer;
import com.xin.interfaces.AbstractHandler;
import com.xin.mediator.AbstractMediatorHandler;
import com.xin.mediator.concretemediator.CustomerMediator;
import com.xin.messge.ICanonicalMsg;

/**
 * 用户只要实现customerHandlerMessage,给定所在的服务class的全名即可(访问资源该怎么办???)
 * 使用这个类的场景就是用户将需要的类资源防止在jar中,其余就没了
 * @author Turing
 *
 */
public class CustomerMediatorHandler extends AbstractMediatorHandler {
	
	private static Logger log = Logger.getLogger(CustomerMediatorHandler.class);
	//serviceClass的全名称
	private String serviceClassName ;
	private AbstractCustomerTransformer customerMediatorHandler;
	
	/**
	 * 初始化的时候先解压resource压缩包中的文件
	 * 对于用户打包的文件有特殊的规定
	 */
	@Override
	public boolean init() {
		super.init();  //父类中只是解压文件,几乎不会出现出错
		try {
			File[] infiles = new File(workDir).listFiles();
			String jarfilename = null;
			for(File f : infiles)
			{
				if(f.getName().endsWith(".jar"))
				{
					jarfilename = f.getName();
					break;
				}
			}
			
			if(jarfilename == null)
			{
				log.info("CustomerMedator Need jar file resource");
				return false;
			}
			
			URL url = new URL("file:" + workDir + File.separator + jarfilename); //file:E:\java_study\SimpleWs\bin\**.jar
			URLClassLoader classLoader =  new URLClassLoader(new URL[]{url});
			Class clazz = classLoader.loadClass(serviceClassName);
			customerMediatorHandler = (AbstractCustomerTransformer) clazz.newInstance();
			return true;
		} catch (MalformedURLException e) {
			log.info("error url format");
			log.debug(e, e);
			return false;
		} catch (ClassNotFoundException e) {
			log.info("error load the serviceClass " + serviceClassName);
			log.debug(e, e);
			return false;
		} catch (InstantiationException e) {
			log.info("can't instance the service class");
			log.debug(e, e);
			return false;
		} catch (IllegalAccessException e) {
			log.info("can't access the serviceClass contructor class");
			log.debug(e, e);
			return false;
		}
	}
	
	@Override
	public void setServiceClass(String serviceClassName) {
		super.setServiceClass(serviceClassName);
		this.serviceClassName = serviceClassName ;
	}
	
	@Override
	public final void requestMessageASync(ICanonicalMsg msg) {	
		//捕获异常,防止线程死掉
		Object result = null;
		try{
			result = customerMediatorHandler.customerHandlerMessage(msg.getContent());
			msg.setContent(result);
			for(AbstractHandler out : outHandlers)
			{
				out.requestMessageASync(msg);
			}
		}catch (Exception e) { //此时职责链终止
			log.info("error handle message in CustomerMediatorHandler in process " + getProcessName());
			log.debug(e,e);
			//需要自定义Server自己的异常类型将e包装进去
			Exception exception = new HandlerMessageErrorException(e);
			msg.setContent(exception);
			return ;
		}
		
	}
	
	/**
	 * 同步的情况下只能有一个入口和一个出口,reason慢慢想吧
	 */
	@Override
	public final ICanonicalMsg requestMessageSync(ICanonicalMsg msg) {
		Object result = null;
		try{
			result = customerMediatorHandler.customerHandlerMessage(msg.getContent());
			msg.setContent(result);
			return outHandlers.get(0).requestMessageSync(msg);
		}catch (Exception e) {
			log.info("error handle message in CustomerMediatorHandler in process " + getProcessName());
			log.debug(e,e);
			//需要自定义Server自己的异常类型将e包装进去
			Exception exception = new HandlerMessageErrorException(e);
			msg.setContent(exception);
			return msg; //直接将包含异常的信息返回
		}
		
	}
	
	@Override
	public boolean shutdown() {
		// TODO Auto-generated method stub
		try{
			return customerMediatorHandler.shutdown();
		}finally{
			//super.shutdown(); //释放占用的资源
			inHandlers = null;
			outHandlers = null;
			resource = null;
		}
	}

	/**
	 * 返回所在的中介器的名字
	 */
	@Override
	public final String getProcessorName() {
		return CustomerMediator.class.getSimpleName();
	}
}


















