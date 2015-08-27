package tank.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tank.api.IClassLoader;

/**
 * @author tank
 * @date:2015年1月20日 下午12:08:15
 * @description:
 * @version :0.1
 */

public class ClassLoaderFactory {
	private static Logger logger = LoggerFactory.getLogger(ClassLoaderFactory.class);

	public static IClassLoader createClassLoaer(Class cs, String jarPath) {
		return createClassLoaer(cs, jarPath, null);
	}

	public static IClassLoader createClassLoaer(Class cs, String jarPath, ClassLoader parent) {
		if (cs == CustomerJarUrlLoader.class) {
			if (parent != null) {
				return new CustomerJarUrlLoader(jarPath, parent);
			} else {
				return new CustomerJarUrlLoader(jarPath);
			}

		} else {
			logger.error("没有找到IClassLoader相关实现错误:{}", cs);
		}
		return null;
	}
}
