package com.xin.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.xin.adaptor.AbstractAdaptor;
import com.xin.adaptor.AbstractAdaptorHandler;
import com.xin.adaptor.NullAdaptor;
import com.xin.exception.NoneExistAdaptorOrMediatorException;
import com.xin.interfaces.AbstractHandler;
import com.xin.interfaces.IAdaptorManager;
import com.xin.interfaces.IMediatorManager;
import com.xin.mediator.AbstractMediator;
import com.xin.mediator.AbstractMediatorHandler;
import com.xin.metadata.ChainProcessors;
import com.xin.util.api.impl.FileProcessor;

/**
 * 整个系统中最重且最重要的类
 * @author Turing
 *
 */
public class ProcessManager {
	private static Logger log = Logger.getLogger(ProcessManager.class);
//	private DirectoryWatcher dw;
//	private Map<String,ProcessMeta> processes =  new HashMap<String, ProcessMeta>();
//	private List<ChainProcessors> sucessProcess = new ArrayList<ChainProcessors>(); 	
	//这个地方需要添加映射(流程名和流程的映射)
	private Map<String,Map<String,AbstractHandler>> process_Handlers = new HashMap<String, Map<String,AbstractHandler>>();
	
	private final String workDir ; //存放流程资源包的位置
	private final String runtimeDir ; //流程资源包解压后的工作位置
	
	private final IAdaptorManager adaptorManager; 
	private final IMediatorManager medaitorManager;
	
	public ProcessManager(IAdaptorManager adaptorManager,IMediatorManager medaitorManager,
			String workDir,String runtimeDir) {		
		this.adaptorManager = adaptorManager;
		this.medaitorManager = medaitorManager;
		this.workDir = workDir;
		this.runtimeDir = runtimeDir;
	}
	
	public void init()
	{
		deployConfigPackagesAsync();
	}
	
	/**
	 * 异步的部署流程
	 */
	private void deployConfigPackagesAsync()
	{
		//这是后话
	}
	
	//部署流程文件
	public boolean deployProcessFile(File newprocess)
	{
		//首先解压文件释放所有的资源文件
		//暂时通过名字获取目录(实现的时候需要添加工作目录)
		//以流程的名字新建文件夹，所有的资源文件全部放入文件夹中
		String fileName = newprocess.getName();
		String processName = fileName.substring(0, fileName.indexOf("."));
		String dir = runtimeDir + File.separator + processName; //先删除可能以前存在的文件夹
		
		FileProcessor.destoryDirectory(dir);
		FileProcessor.createDirectory(runtimeDir, processName); //在runtime中创建该样式的文件夹
		FileProcessor.unZip(newprocess.getAbsolutePath(), dir); //将所有的资源文件放入runtime对应的文件夹中
		
		File process = new File(dir);
		File []resourcesAndProcess = process.listFiles();
		
		File processFile = null;
		List<File> resources = new ArrayList<File>();
		for(File file : resourcesAndProcess)
		{
			if(file.getName().endsWith(".xml"))
			{
				processFile = file;
			}
			else
			{
				resources.add(file);
			}
		}
		
		if(processFile == null)  //要是压缩包中不存在流程文件的情况下就是错误的压缩包
		{
			log.info("error format process zip file");
			return false;
		}
		
		//部署流程
		return deployProcess(processFile, processName, resources);
	}
	
	private void unDeployProcess(String deletedProcess)
	{
		
	}
	
	/** 
	 * 根据流程xml文件部署流程
	 * @param processFile 流程文件
	 * @param processName 流程名
	 * @param resources 流程资源包中包含的资源
	 * @return 
	 */
	private boolean deployProcess(File processFile,String processName,List<File>resources)
	{
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new FileInputStream(processFile));
			
			if(isRightFormatProcessFile(document, resources))  //是否是正确的流程
			{
				//创建所有的Handler,包括适配器和中介器对象
				Map<String,AbstractAdaptor> adaptors = getProcessAdaptors(document);
				Map<String,AbstractMediator> mediators = getProcessMediators(document);
				
				//根据流程文件新建中介单元和处理单元(endpoints、mediators的数目),同时将资源的名字放置进来
				Map<String,AbstractHandler> handlers = getHanldersInProcess(document, adaptors, mediators, resources,processName);
				//对所有的处理单元进行初始化
				for(AbstractAdaptor adaptor : adaptors.values())
				{
					if(!adaptor.deployProcess(processName))
					{
						return false;
					}
				}
				
				for(AbstractMediator mediator : mediators.values())
				{
					if(!mediator.deployProcess(processName))
					{
						return false;
					}
				}
				/*for(AbstractHandler handler : handlers.values())
				{
					if(!handler.init())
					{
						return false;
					}
				}*/
				//添加processName 和 handlers的映射(貌似现在没用了,需要修改)
				process_Handlers.put(processName, handlers);
				
				return true;
			}
		} catch (FileNotFoundException e) {
			log.info("can't find the processfile");
			return false;
		} catch (DocumentException e) {
			log.info("error format xml processfile");
			return false;
		} catch (NoneExistAdaptorOrMediatorException e) {
			log.info("error process adaptor or mediator during deploy process");
			return false;
		}
		return false;
	}
	
	/**
	 * 判断流程文件是不是合法的
	 * 使用的资源文件重复非法,对应的适配器或者中介器尚未在系统中注册存在非法,每一个中介器或者适配器的下一站在流程中
	 * 其余就是合法的
	 * @param document 流程文件
	 * @param resources 资源
	 * @return
	 */
	boolean isRightFormatProcessFile(Document document,List<File> resources)
	{
		Element rootElement = document.getRootElement();
		List<Element> endpoints = rootElement.selectNodes("/EsbProcess/endpoints/endpoint");
		List<Element> mediators = rootElement.selectNodes("/EsbProcess/mediations/mediation");
		List<String> resourcesNames = new ArrayList<String>();
		for(File resource : resources)
		{
			resourcesNames.add(resource.getName());
		}
		
		return isRightEndpointFormat(endpoints, mediators, resourcesNames)
				&& isRightMediatorFormat(mediators, endpoints, resourcesNames);
	}
	
	/**
	 * 判断endpints节点是否正确(名字是否正确)
	 * false:名字不存在|名字对应的适配器尚未注册|出口节点的endpoints/mediators在流程文件中尚不存在|指定的服务包不存在
	 * endpoint的入口节点和出口节点的存在性
	 * @param endpoints 
	 * @param mediators
	 * @param resources
	 * @return
	 */
	private boolean isRightEndpointFormat(List<Element> endpoints,List<Element> mediators,List<String> resources)
	{
		for(Element endpoint:endpoints)
		{
			String endpointName = endpoint.attributeValue("name");
			if(endpointName == null || endpointName.equals(""))
			{
				log.info("error endpoint name");
				return false;
			}
			
			//判断给定的适配器是否存在
			String adaptorName = endpoint.attributeValue("adaptor");
			if(adaptorName == null)
			{
				log.info("error format endpoint xml file");
				return false;
			}
			
			if(!adaptorManager.containAdaptor(adaptorName))
			{
				log.info("have not register the adaptor " + adaptorName);
				return false;
			}
			
			//判断配置包是否存在
			String servicePackage = endpoint.attributeValue("service");
			if(servicePackage == null)
			{
				log.info("error format endpoint service xml file");
				return false;
			}
			if(!resources.contains(servicePackage))
			{
				log.info("error service resource name");
				return false;
			}
			
			//判断给定的入口节点的存在
			List<Element> inbounds = endpoint.selectNodes("inbounds/inbound");
			for(Element inbound : inbounds)
			{
				String sourceName = inbound.attributeValue("source");
				if(sourceName == null || sourceName.equals(""))
				{
					log.info("error format inbound name ");
					return false;
				}
				if(!elementContainIn(sourceName, endpoints)&&!elementContainIn(sourceName, mediators))
				{
					log.info("not exists inbound name " + sourceName);
					return false;
				}
			}
			
			//判断给定出口点的存在
			List<Element> outbounds = endpoint.selectNodes("outbounds/outbound");
			for(Element outbound : outbounds)
			{
				String nextName = outbound.attributeValue("next");
				if(nextName == null || nextName.equals(""))
				{
					log.info("error outbound name format");
					return false;
				}
				if(!elementContainIn(nextName, endpoints)&&!elementContainIn(nextName, mediators))
				{
					log.info("not exists outbounds name " + nextName);
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 判断给定的mediator节点是否正确
	 * 判断指定的中介器是否存在
	 * 存在资源包的情况下判断资源包是否合法
	 * 判断入口节点和出口节点在流程文件中是否是合法的
	 * @param meidators
	 * @param endpoints
	 * @param resources
	 * @return
	 */
	private boolean isRightMediatorFormat(List<Element> mediators,List<Element> endpoints,List<String> resources)
	{
		for(Element theMediator : mediators)
		{
			String mediatorName = theMediator.attributeValue("name");
			if(mediatorName == null || mediatorName.equals(""))
			{
				log.info("error mediator name");
				return false;
			}
			
			//判断给定的中介器是否存在
			String mediator = theMediator.attributeValue("mediator");
			if(mediator == null)
			{
				log.info("error mediator xml file format , should container mediator");
				return false;
			}
			if(!medaitorManager.containMediator(mediator))
			{
				log.info("have not register the mediator " + mediator);
				return false;
			}
			
			//中介器可以没有配置包文件,但是要是包含的话必须存在以属性service命名
			String resourceName = theMediator.attributeValue("service");
			if(!(resourceName == null))
			{
				if(resourceName.equals(""))
				{
					log.info("error mediator resource Name format");
					return false;
				}
				if(!resources.contains(resourceName))
				{
					log.info("not exists resource " + resourceName);
					return false;
				}
			}
			
			//判断给定入口节点的存在性
			List<Element> inbounds = theMediator.selectNodes("inbounds/inbound");
			for(Element inbound : inbounds)
			{
				String sourceName = inbound.attributeValue("source");
				if(sourceName == null || sourceName.equals(""))
				{
					log.info("error format inbound name ");
					return false;
				}
				if(!elementContainIn(sourceName, endpoints)&&!elementContainIn(sourceName, mediators))
				{
					log.info("not exists inbound name " + sourceName);
					return false;
				}
			}
			
			//判断给定出口点的存在
			List<Element> outbounds = theMediator.selectNodes("outbounds/outbound");
			for(Element outbound : outbounds)
			{
				String nextName = outbound.attributeValue("next");
				if(nextName == null || nextName.equals(""))
				{
					log.info("error outbound name format");
					return false;
				}
				if(!elementContainIn(nextName, endpoints)&&!elementContainIn(nextName, mediators))
				{
					log.info("not exists outbounds name " + nextName);
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 判断给定的名字是否在elements中
	 * 是返回True，否返回False
	 * @param name  某个element的名字
	 * @param elements  endpoints或者mediators的集合元素
	 * @return
	 */
	private boolean elementContainIn(String name,List<Element> elements)
	{
		for(Element element : elements)
		{
			String elementName = element.attributeValue("name");
			if(name.equals(elementName))
				return true;
		}
		return false;
	}
	
	/**
	 * 生成流程中的所有的适配器的实例
	 * 返回值是适配器名字和适配器实例的映射
	 * @param document 流程文件
	 * @return 适配器名字和适配器实例的映射
	 * @throws NoneExistMediatorException
	 */
	private Map<String,AbstractAdaptor> getProcessAdaptors(Document document) throws NoneExistAdaptorOrMediatorException
	{
		Element rootElement = document.getRootElement();
		List<Element> endpoints = rootElement.selectNodes("/EsbProcess/endpoints/endpoint");
		Map<String,AbstractAdaptor> adaptors = new HashMap<String,AbstractAdaptor>();	
		
		for(Element endpoint : endpoints)
		{
			String adaptorName = endpoint.attributeValue("adaptor");
			AbstractAdaptor adator = adaptorManager.getAdaptorInstance(adaptorName);
			if(adator == NullAdaptor.getNullAdaptor())
			{
				log.info("none exists adaptor " + adaptorName);
				throw new NoneExistAdaptorOrMediatorException("not exist adaptor " + adaptorName);
			}
			adaptors.put(adaptorName,adator);
		}
				
		return Collections.unmodifiableMap(adaptors);
	}
	
	/**
	 * 生成流程中所有的中介器的实例
	 * 返回值是中介器名字和中介器实例映射
	 * @param document 流程文件
	 * @return 中介器名字和中介器实例映射
	 * @throws NoneExistMediatorException 
	 */
	private Map<String,AbstractMediator> getProcessMediators(Document document) throws NoneExistAdaptorOrMediatorException
	{
		Element rootElement = document.getRootElement();
		List<Element> mediatorsElements = rootElement.selectNodes("/EsbProcess/mediations/mediation");
		Map<String,AbstractMediator> mediators = new HashMap<String,AbstractMediator>();
		
		for(Element mediatorElement : mediatorsElements)
		{
			String mediatorName = mediatorElement.attributeValue("mediator");
			AbstractMediator mediator = medaitorManager.getMediatorInstance(mediatorName);
			if(mediator == null)
			{
				log.info("none exist adaptor " + mediatorName);
				throw new NoneExistAdaptorOrMediatorException("not exist mediator " + mediatorName);
			}
			mediators.put(mediatorName,mediator);
		}	
		return Collections.unmodifiableMap(mediators);
	}
	
	/**
	 * 根据流程中的配置信息添加处理单元和资源
	 * 同时在函数中设置每个节点的出口处理单元和入口处理单元(入口的信息主要是为了考虑聚合器的使用)以及资源信息
	 * 以及发送的数据是不是同步的信息
	 * 设置每个handler所处在的流程的名字
	 * 返回值是每个单元的名字和处理单元的映射
	 * @param document 流程文件
	 * @param adaptors 适配器名字和适配器实例的映射
	 * @param mediators 中介器名字和中介器实例映射
	 * @param resources 返回流程中所有的处理单元和处理单元名字的映射
	 * @param processName 流程的名字
	 * @return
	 */
	private Map<String,AbstractHandler> getHanldersInProcess(final Document document,final Map<String,AbstractAdaptor> adaptors,
			final Map<String,AbstractMediator> mediators,final List<File> resources,final String processName)
	{
		Map<String,AbstractHandler> handlers = new HashMap<String, AbstractHandler>();
		Element rootElement = document.getRootElement();
//		String runtimeWorkDir = runtimeDir + File.separator + processName;
		
		//创建适配器器处理单元
		List<Element> endpoints = rootElement.selectNodes("/EsbProcess/endpoints/endpoint");
		for(Element endpoint : endpoints)
		{
			String adaptorName = endpoint.attributeValue("adaptor");
			String name = endpoint.attributeValue("name");
			boolean sendSync = "true".equals(endpoint.attributeValue("sendSync"));
			AbstractAdaptor adaptor = adaptors.get(adaptorName);	
			AbstractAdaptorHandler adaptorHandler = adaptor.getAdaptorHandlerInstance();
			adaptorHandler.setSendSync(sendSync);
			adaptorHandler.setProcessName(processName);
			adaptor.addAdaptorHandler(processName, adaptorHandler);
			handlers.put(name, adaptorHandler);
		}
		
		//创建中介器处理单元
		List<Element> mediatorsElements = rootElement.selectNodes("/EsbProcess/mediations/mediation");
		for(Element mediatorelement : mediatorsElements)
		{
			String mediatorName = mediatorelement.attributeValue("mediator");
			String name = mediatorelement.attributeValue("name");
			boolean sendSync = mediatorelement.attributeValue("sendsync").equals("true");
			AbstractMediator mediator = mediators.get(mediatorName);
			AbstractMediatorHandler mediatorHandler = mediator.getMediatorHandlerInstance();
			mediatorHandler.setSendSync(sendSync);
			mediatorHandler.setProcessName(processName);
			mediator.addMediatorHandler(processName, mediatorHandler);
			handlers.put(name, mediatorHandler);
		}
		
		//设置每个适配器处理单元的入口处理单元和出口单元和资源信息,以及serviceClass
		for(Element endpoint : endpoints)
		{
			String name = endpoint.attributeValue("name");
			String serviceClass = endpoint.attributeValue("class");
			AbstractHandler adaptor = handlers.get(name);
			
			List<Element> inbounds = endpoint.selectNodes("inbounds/inbound");
			for(Element inbound : inbounds)
			{
				String source = inbound.attributeValue("source");
				AbstractHandler inHandler = handlers.get(source);
				adaptor.addInboundHandler(inHandler);
			}
			List<Element> outbounds = endpoint.selectNodes("outbounds/outbound");	
			for(Element outbound : outbounds)
			{
				String next = outbound.attributeValue("next");
				AbstractHandler outHandler = handlers.get(next);
				adaptor.addOutboundHandler(outHandler);
			}
			
			//资源信息
			String resource  = endpoint.attributeValue("service");
			if(resource==null || resource.equals(""))
				continue;
			for(File f : resources)
			{
				if(f.getName().equals(resource))
				{
					adaptor.addResource(f);
				}
			}	
			
			//设置serviceClass
			adaptor.setServiceClass(serviceClass);	
		}
		
		//设置每个中介器处理单元的入口单元和出口单元以及使用的资源信息以及serviceClass
		for(Element mediatorelement : mediatorsElements)
		{
			String name = mediatorelement.attributeValue("name");
			String serviceClass = mediatorelement.attributeValue("class");
			AbstractHandler mediator = handlers.get(name);
			
			List<Element> inbounds = mediatorelement.selectNodes("inbounds/inbound");
			for(Element inbound : inbounds)
			{
				String source = inbound.attributeValue("source");
				AbstractHandler inHandler = handlers.get(source);
				mediator.addInboundHandler(inHandler);
			}
			List<Element> outbounds = mediatorelement.selectNodes("outbounds/outbound");
			for(Element outbound : outbounds)
			{
				String next = outbound.attributeValue("next");
				AbstractHandler outHandler = handlers.get(next);
				mediator.addOutboundHandler(outHandler);
			}
			
			//添加资源
			String resource  = mediatorelement.attributeValue("service");
			if(resource==null || resource.equals(""))
				continue;
			for(File f : resources)
			{
				if(f.getName().equals(resource))
				{
					mediator.addResource(f);
				}
			}
			
			//设置serviceClass
			mediator.setServiceClass(serviceClass);		
		}				
		return handlers;
	}
}












