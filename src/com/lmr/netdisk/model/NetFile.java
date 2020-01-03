package com.lmr.netdisk.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import javax.servlet.ServletContext;
/**
 * �������ڷ������ͻ��˴����й��ļ���Ŀ¼�������Ϣ
 * @author lmr
 *
 */
public class NetFile {
	
	private Path realPath;
	private BasicFileAttributes fileAttributes;
	private ServletContext context;
	
	private String userName;
	
	public NetFile(Path path,ServletContext context,String userName) {
		this.realPath = path;
		this.context = context;
		this.userName = userName;
		try {
			fileAttributes = Files.readAttributes(path, BasicFileAttributes.class,LinkOption.NOFOLLOW_LINKS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//Bean����
	public String getRealPath() {
		return realPath.toString();
	}
	public String getVirtualPath() {
		Path userRoot = Paths.get(context.getInitParameter("filesRoot"), userName);
		Path virtualPath = userRoot.relativize(realPath);
		return virtualPath.toString();
	}
	public String getName() {
		return realPath.getFileName().toString();
	}
	public void setName(String name) {
		realPath = realPath.getParent().resolve(name);
	}
	public String getType() {
		String type = null;
		if (Files.isDirectory(realPath)) {
			type = "directory";
		}else if(Files.isRegularFile(realPath)) {
			type = "file";
		}
		return type;
	}
	public String getMime() {
		String fileName = realPath.getFileName().toString();
		return context.getMimeType(fileName);
	}
	/**
	 * 
	 * @return �ļ����ֽ���
	 */
	public long getSize() {
		return fileAttributes.size();
	}
	
}
