package tank.classloader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tank.api.IClassLoader;

/**
 * @author tank
 * @date:2015年1月20日 上午10:07:45
 * @description:
 * @version :0.1
 */

public class CustomerJarUrlLoader implements IClassLoader {

	private Logger logger = LoggerFactory.getLogger(CustomerJarUrlLoader.class);

	private URLClassLoader classLoader;

	public CustomerJarUrlLoader(String filePath) {

		// filePath 是jar的绝对路径
		URL url;
		try {
			url = new URL("file:" + filePath);
			// 里面是一个url的数组，可以同时加载多个
			classLoader = new URLClassLoader(new URL[] { url });

		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error("{}", e);
		}

	}
	public CustomerJarUrlLoader(String filePath,ClassLoader parent) {

		// filePath 是jar的绝对路径
		URL url;
		try {
			url = new URL("file:" + filePath);
			// 里面是一个url的数组，可以同时加载多个
			classLoader = new URLClassLoader(new URL[] { url },parent);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error("{}", e);
		}

	}

	@Override
	public Class<?> loadClass(String packageClassName) {
		Class myclass = null;
		try {
			myclass = classLoader.loadClass(packageClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("{}", e);
		}

		return myclass;
	}

	/**
	 * 
	 * @param urlClassLoader
	 * @param jarPath
	 */
	public void addJar(String jarPath) {

		try {
			File file = new File(jarPath);
			URL url = file.toURI().toURL();

			Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			boolean accessible = method.isAccessible();
			try {
				if (accessible == false) {
					method.setAccessible(true);
				}
				// 将当前类路径加入到类加载器中
				method.invoke(classLoader, url);
			} finally {
				method.setAccessible(accessible);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
