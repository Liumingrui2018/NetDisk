package com.lmr.netdisk.dao;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import com.lmr.netdisk.model.NetFile;

public class UserFileDao {
	private ServletContext context;
	private String userName;
	
	public UserFileDao(ServletContext context) {
		this.context = context;
		this.userName = "";
	}
	public UserFileDao(ServletContext context,String userName) {
		this.context = context;
		this.userName = userName;
	}
	public void setUserName(String name) {
		this.userName = name;
	}
	public List<NetFile> listDir(Path dirPath) {
		List<NetFile> dirContent = new ArrayList<NetFile>();
		if(Files.isDirectory(dirPath)&&Files.exists(dirPath)) {
			try(Stream<Path> stream = Files.list(dirPath)) {
				Iterator<Path> iterator = stream.iterator();
				while(iterator.hasNext()) {
					Path path = iterator.next();
					NetFile file = new NetFile(path,context,userName);
					dirContent.add(file);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dirContent;
	}
	public void uploadFile() {
		//TODO 上传文件
	}
	public void deleteFile(Path file) {
		try {
			if(Files.exists(file, LinkOption.NOFOLLOW_LINKS)&&Files.isRegularFile(file,LinkOption.NOFOLLOW_LINKS))
				Files.delete(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void deleteFiles(List<Path> pathes) {
		for(Path path : pathes) {
			deleteFile(path);
		}
	}
	public void deleteDir(Path dir){
		if(!Files.exists(dir,LinkOption.NOFOLLOW_LINKS)||!Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS))
			return;
		final List<Path> filesToDelete = new ArrayList<Path>();//存储即将被删除的文件
		final List<Path> dirToDelete = new ArrayList<Path>();//存储即将被删除的目录
		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					filesToDelete.add(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					// TODO Auto-generated method stub
					filesToDelete.clear();
					dirToDelete.clear();
					return FileVisitResult.TERMINATE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					// TODO Auto-generated method stub
					dirToDelete.add(dir);
					return FileVisitResult.CONTINUE;
				}
				
			});
			deleteFiles(filesToDelete);
			//删除空目录
			for(Path emptyDir : dirToDelete) {
				Files.delete(emptyDir);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void createDir(Path dir) {
		try {
			Files.createDirectory(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<NetFile> queryByMimeHead(final String mimeHead,Path startDir) {
		List<NetFile> files = new ArrayList<NetFile>();
		try {
			Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					// TODO Auto-generated method stub
					String type = context.getMimeType(file.toString());
					if(type!=null&&type.startsWith(mimeHead)) {
						files.add(new NetFile(file,context,userName));
					}
					return FileVisitResult.CONTINUE;
				}
				
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return files;
	}
}
