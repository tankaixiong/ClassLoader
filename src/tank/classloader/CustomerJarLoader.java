package tank.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tank
 * @date:2014年12月30日 下午5:49:11
 * @description:动态加载jar文件
 * @version :0.1
 */
@Deprecated
public class CustomerJarLoader extends ClassLoader {

	private Logger logger = LoggerFactory.getLogger(CustomerJarLoader.class);

	private HashSet<String> dynaclazns; // 需要由该类加载器直接加载的类名

	public CustomerJarLoader(String jarPath) {
		super(null); // 指定父类加载器为 null
		dynaclazns = new HashSet();

		loadClassByMe(jarPath);
	}

	private void loadClassByMe(String jarPath) {

		try {
			JarFile jarFile = new JarFile(jarPath);
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();

				if (jarEntry.getName().endsWith(".class")) {

					String jarClassName = jarEntry.getName().substring(0, jarEntry.getName().lastIndexOf("."));
					String className = jarClassName.replace('/', '.');

					InputStream jfin = jarFile.getInputStream(jarEntry);

					logger.debug("加载类:{}", className);
					dynaclazns.add(className);

					instantiateClass(className, jfin, jfin.available());
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("{}", e);
		}
	}

	private Class instantiateClass(String name, InputStream fin, long len) throws IOException {
		byte[] raw = new byte[(int) len];
		fin.read(raw);
		fin.close();
		return defineClass(name, raw, 0, raw.length);
	}

	protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class cls = null;
		cls = findLoadedClass(name);
		if (!this.dynaclazns.contains(name) && cls == null)
			cls = getSystemClassLoader().loadClass(name);
		if (cls == null)
			throw new ClassNotFoundException(name);
		if (resolve)
			resolveClass(cls);
		return cls;
	}

	/**
	 * 得到所有动态加载类的类名
	 * 
	 * @return
	 */
	public HashSet<String> getDynamicClass() {
		return dynaclazns;
	}

}
