package com.lumosity.utils;

import java.io.*;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

/**
 * 文件工具类
 *
 * @author xiaoleilu
 *
 */
public class FileKit {

	public static final String UTF_8 = "UTF-8";

	/**
	 * 读取文件内容
	 *
	 * @param file
	 *            文件
	 * @return 内容
	 * @throws IOException
	 */
	public static String readUtf8String(File file) throws IOException {
		return readString(file, UTF_8);
	}

	/**
	 * 读取文件内容
	 *
	 * @param path
	 *            文件路径
	 * @return 内容
	 * @throws IOException
	 */
	public static String readUtf8String(String path) throws IOException {
		return readString(path, UTF_8);
	}

	/**
	 * 读取文件内容
	 *
	 * @param path
	 *            文件路径
	 * @param charsetName
	 *            字符集
	 * @return 内容
	 * @throws IOException
	 */
	public static String readString(String path, String charsetName) throws IOException {
		return readString(file(path), charsetName);
	}

	/**
	 * 创建File对象，自动识别相对或绝对路径，相对路径将自动从ClassPath下寻找
	 *
	 * @param path
	 *            文件路径
	 * @return File
	 */
	public static File file(String path) {
		if (StringUtils.isBlank(path)) {
			throw new NullPointerException("File path is blank!");
		}
		return new File(getAbsolutePath(path));
	}

	/**
	 * 获取绝对路径，相对于classes的根目录<br>
	 * 如果给定就是绝对路径，则返回原路径，原路径把所有\替换为/
	 *
	 * @param path
	 *            相对路径
	 * @return 绝对路径
	 */
	public static String getAbsolutePath(String path) {
		if (path == null) {
			path = "";
		} else {
			path = normalize(path);

			if (path.startsWith("/") || path.matches("^[a-zA-Z]:/.*")) {
				// 给定的路径已经是绝对路径了
				return path;
			}
		}

		// 相对路径
		ClassLoader classLoader = getClassLoader();
		URL url = classLoader.getResource(path);
		String reultPath = url != null ? url.getPath() : getClassLoader() + path;
		// return StrKit.removePrefix(reultPath, PATH_FILE_PRE);
		return reultPath;
	}

	/**
	 * 获得class loader<br>
	 * 若当前线程class loader不存在，取当前类的class loader
	 *
	 * @return 类加载器
	 */
	public static ClassLoader getClassLoader() {
		ClassLoader classLoader = getContextClassLoader();
		if (classLoader == null) {
			classLoader = FileKit.class.getClassLoader();
		}
		return classLoader;
	}

	/**
	 * @return 当前线程的class loader
	 */
	public static ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * 修复路径<br>
	 * 1. 统一用 / <br>
	 * 2. 多个 / 转换为一个
	 *
	 * @param path
	 *            原路径
	 * @return 修复后的路径
	 */
	public static String normalize(String path) {
		return path.replaceAll("[/\\\\]{1,}", "/");
	}

	/**
	 * 读取文件内容
	 *
	 * @param file
	 *            文件
	 * @param charsetName
	 *            字符集
	 * @return 内容
	 * @throws IOException
	 */
	public static String readString(File file, String charsetName) throws IOException {
		return new String(readBytes(file), charsetName);
	}

	/**
	 * 读取文件所有数据<br>
	 * 文件的长度不能超过Integer.MAX_VALUE
	 *
	 * @param file
	 *            文件
	 * @return 字节码
	 * @throws IOException
	 */
	public static byte[] readBytes(File file) throws IOException {
		// check
		if (!file.exists()) {
			throw new FileNotFoundException("File not exist: " + file);
		}
		if (!file.isFile()) {
			throw new IOException("Not a file:" + file);
		}

		long len = file.length();
		if (len >= Integer.MAX_VALUE) {
			throw new IOException("File is larger then max array size");
		}

		byte[] bytes = new byte[(int) len];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			in.read(bytes);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return bytes;
	}
}
