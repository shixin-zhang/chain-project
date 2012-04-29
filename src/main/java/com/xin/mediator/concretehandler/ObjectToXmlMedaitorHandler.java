package com.xin.mediator.concretehandler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.xin.interfaces.AbstractHandler;
import com.xin.mediator.AbstractMediatorHandler;
import com.xin.mediator.concretemediator.ObjectToXmlMedaitor;
import com.xin.messge.ICanonicalMsg;

/**
 * 这种的使用方式若有用户自定义的类型的话需要放置在jar中
 * @author Turing
 *
 */
public class ObjectToXmlMedaitorHandler extends AbstractMediatorHandler {

	private static Logger log = Logger.getLogger(ObjectToXmlMedaitorHandler.class);
	private Map<String,String> className_alias = new HashMap<String,String>();
	private Map<Class,String> alias_class = new HashMap<Class, String>();
	
	private File xmlfile = null;
	private String jarfile = null;
	private String encoding = null;
	@Override
	public boolean init() {
		super.init();
		SAXReader reader = new SAXReader();
		File file[] =  new File(workDir).listFiles();
		for(File f : file)
		{
			if(f.getName().endsWith(".xml"))
			{
				xmlfile = f;
			}
			else if(f.getName().endsWith(".jar"))
			{
				jarfile = f.getName();
			}
		}
		if(xmlfile == null)
		{
			log.info("can't find the xml configure file");
			return false;
		}
		
		try {
			Document document = reader.read(xmlfile);
			Element  rootElement =  document.getRootElement();
			encoding = rootElement.attributeValue("encoding");
			List<Element> classes = rootElement.selectNodes("/ObjectToXmlMediator/class");
			for(Element alias : classes)
			{
				String className = alias.element("name").getTextTrim();
				String aliasName = alias.element("alias").getTextTrim();
				className_alias.put(className, aliasName);
			}
			URLClassLoader classLoader = null;
			if(xmlfile != null)
			{
				URL url = new URL("file:" + workDir + File.separator + jarfile);
				classLoader = new URLClassLoader(new URL[]{url});
			}
			for(String key : className_alias.keySet())
			{
				try {
					Class clazz = Thread.currentThread().getContextClassLoader().loadClass(key);
					alias_class.put(clazz , className_alias.get(key));
				} catch (ClassNotFoundException e) {
					if(xmlfile != null)
					{
						Class clazz = classLoader.loadClass(key);
						alias_class.put(clazz , className_alias.get(key));
					}
					else
					{
						log.info("can not load the class " + key);
						return false;
					}
				}
			}
			return true;
		} catch (MalformedURLException e) {
			log.info("failed to create classloader");
			log.debug(e, e);
			return false;
		} catch (DocumentException e) {
			log.info("error format xml configure file");
			log.debug(e, e);
			return false;
		} catch (ClassNotFoundException e) {
			log.info("can not load class " );
			log.debug(e, e);
			return false;
		}
		
	}
	
	/**
	 * 目前没什么资源清理的
	 */
	@Override
	public boolean shutdown() {
		className_alias = null;
		alias_class = null;
		return true;
	}

	@Override
	public String getProcessorName() {
		return ObjectToXmlMedaitor.class.getSimpleName();
	}

	@Override
	public ICanonicalMsg requestMessageSync(ICanonicalMsg msg) {
		XStream xStream = new XStream(new DomDriver(encoding));
		for(Class  clazz : alias_class.keySet())
		{
			xStream.alias(alias_class.get(clazz), clazz);
		}
		Object payload = msg.getContent();
		String xml = xStream.toXML(payload);
		msg.setContent(xml);
		return outHandlers.get(0).requestMessageSync(msg);
	}

	@Override
	public void requestMessageASync(ICanonicalMsg msg) {
		XStream xStream = new XStream(new DomDriver(encoding));
		for(Class  clazz : alias_class.keySet())
		{
			xStream.alias(alias_class.get(clazz), clazz);
		}
		Object payload = msg.getContent();
		String xml = xStream.toXML(payload);
		msg.setContent(xml);
		for(AbstractHandler handler : outHandlers)
		{
			handler.requestMessageASync(msg);
		}
	}

}

/*
  
*/
 
















