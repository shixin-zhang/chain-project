package com.xin.util.api.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
/**
 * 文件处理类
 */
public class FileProcessor {
	
	/**
	 * 创建多层目录
	 * @param directory	根目录
	 * @param subDirectory	所需创建的多层子目录
	 * @return	创建结果	
	 */
	public static boolean createDirectory(String directory, String subDirectory){
		File newDirectory = new File(directory + "/" + subDirectory);
		if(!newDirectory.exists()){
			String[] dir = subDirectory.replace('\\', '/').split("/");
			for (String d : dir){
				File subFile = new File(directory + "/"  + d);
				if ( !subFile.exists()){
					if(!subFile.mkdir()){
						//System.out.print(newDirectory.getAbsolutePath()+" can't be created !");
						return false;
					}
				}
				directory += "/"+ d;
			}
		}else{
			//System.out.println(newDirectory.getAbsolutePath()+" existes !");
			return false;
		}
		return true;
	}
	
	/**
	 * 创建单层目录
	 * @param directory 目录路径
	 * @return	创建结果
	 */
	public static boolean createDirectory(String directory){
		File newDirectory = new File(directory );
		if(!newDirectory.exists()){
			if(!newDirectory.mkdir()){
				System.out.print(newDirectory.getAbsolutePath()+" can't be created !");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 删除目录
	 * @param directory 目录路径
	 * @return	删除结果
	 */
	//这个删除文件的地方貌似可以优化(这样实现有时会产生不能删除文件的错误的情况发生)
	public static boolean destoryDirectory(String directory){
		File oldDirectory = new File(directory);
		if(oldDirectory.isDirectory() && oldDirectory.exists()){
			String[] entries = oldDirectory.list();
			if(entries != null){
				if(!emptyDirectory(directory)){  //这个打开的文件还没有关闭现在又删除一次会产生不能删除的错误的
					System.out.println(oldDirectory.getAbsolutePath()+" can't be empty!");
				}
			}
			if(oldDirectory.delete()){
				return true;
			}else{
				System.out.println(oldDirectory.getAbsolutePath()+" can't be deleted !");
				return false;
			}
		}else{
			//System.out.println(oldDirectory.getAbsolutePath()+" is not a directory or doesn't exist !");
			return false;
		}
	}
	
	/**
	 * 清空目录中的所有内容
	 * @param directory 目录路径
	 * @return	清空结果
	 */
	public static boolean emptyDirectory(String directory){
		File oldDirectory  = new File(directory);
		if(oldDirectory.isDirectory() && oldDirectory.exists()){
			String[] entries = oldDirectory.list();
			if(entries != null){
				for(String entry : entries){
					File ftemp = new File(directory +"/"+ entry);
					if(ftemp.isDirectory()){
						if(emptyDirectory(directory +"/"+ entry)){
							if(!ftemp.delete()){
								System.out.println(ftemp.getAbsolutePath()+" can't be deleted !");
								return false;
							}
						}else{
//							System.out.println(ftemp.canWrite() + " " + ftemp.canExecute());
							System.out.println(ftemp.getAbsolutePath()+" can't be empty !");
							return false;
						}							
					}else{
						if(!ftemp.delete()){
							System.out.println(ftemp.getAbsolutePath()+" can't be deleted !");
							return false;
						}
					}
				}
			}
			return true;
		}else{
			//System.out.println(oldDirectory.getAbsolutePath()+" is not a directory or doesn't exist !");
			return false;
		}
	}
	
	/**
	 * 创建文件
	 * @param file
	 * @return	创建结果
	 */
	public static boolean createFile(String file){
		File newFile = new File(file);
		if(newFile.isFile() && !(newFile.exists())){
			try {
				newFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}else{
			System.out.println(newFile.getAbsolutePath()+" exists or is not a file !");
			return false;
		}
	}
	
	/**
	 * 删除文件
	 * @param file
	 * @return	删除结果
	 */
	public static boolean destoryFile(String file){
		File oldFile = new File(file);
		if(oldFile.isFile() && oldFile.exists()){
			if(oldFile.delete()){
				return true;
			}else{
				System.out.println(oldFile.getAbsolutePath()+" can't be deleted !");
				return false;
			}
		}else{
			//System.out.println(oldFile.getAbsolutePath()+" is not a file or doesn't exist !");
			return false;
		}
	}
	
	/**
	 * 遍历目录，得到所有的文件
	 * @param directory
	 * @return	所有文件组
	 */
	public static File[] getFilesInside(String directory){
		File oldDirectory  = new File(directory);
		File[] files = null;
		if(oldDirectory.isDirectory() && oldDirectory.exists()){
			files = oldDirectory.listFiles();
		}
		return files;
	}
	
	/**
	 * 解压zip压缩文件
	 * @param zipFile	zip文件
	 * @param outputPath 解压目的地
	 */
	@SuppressWarnings("unchecked")
	public static void unZip(String zipFile, String outputPath){
		ZipFile filezip;
		try {
			filezip = new ZipFile(zipFile,"GBK");
			Enumeration e = filezip.getEntries();
			ZipEntry zipEntry = null;
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				if (zipEntry.isDirectory()) {
					String name = zipEntry.getName();
					name = name.substring(0,name.length() - 1);
					File f = new File(outputPath + File.separator + name);
					f.mkdir();
				} else {
					String fileName = zipEntry.getName();
					fileName = fileName.replace('\\', '/');
					if (fileName.indexOf("/") != -1) {
						String dir = outputPath + "/"+ fileName.substring(0,fileName.lastIndexOf("/"));
						File d = new File(dir);
						if(!d.exists()){
							createDirectory(outputPath,  fileName.substring(0,fileName.lastIndexOf("/")));
						}
					}

					File f = new File(outputPath + "/"+ zipEntry.getName());

					f.createNewFile();
					InputStream in = filezip.getInputStream(zipEntry);
					FileOutputStream out = new FileOutputStream(f);

					byte[] by = new byte[1024];
					int c;
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					out.close();
					in.close();
				}
			}
			filezip.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
		
	/**
	 * 将某个XML Document写入文件系统中
	 * @param document	XML文档
	 * @param outputPath 	写入目录
	 * @param fileName	文件名
	 */
	public static void writeXML(Document document, String outputPath, String fileName){
		try{
			
			OutputFormat format = OutputFormat.createPrettyPrint(); 
			format.setEncoding("UTF-8"); 
			format.setIndent(true); 
			format.setIndent("	"); 
			XMLWriter writer = new XMLWriter(new FileOutputStream(new File(outputPath + "/" + fileName)), format);
			writer.write(document);
			writer.close();
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void copyFile(File sourceFile,File targetFile) throws IOException{
		FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff=new BufferedInputStream(input);
 
        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff=new BufferedOutputStream(output);
        
        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();
        
        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
	}
}
