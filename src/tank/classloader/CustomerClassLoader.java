package tank.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;

/**
 * @author tank
 * @date:2014年12月30日 下午5:49:11
 * @description:动态加载.class文件
 * @version :0.1
 */
@Deprecated
public class CustomerClassLoader extends ClassLoader {

	private String basedir; // 需要该类加载器直接加载的类文件的基目录
	private HashSet dynaclazns; // 需要由该类加载器直接加载的类名

	public CustomerClassLoader(String basedir, String[] clazns) {
		super(null); // 指定父类加载器为 null
		this.basedir = basedir;
		dynaclazns = new HashSet();
		loadClassByMe(clazns);
	}

	private void loadClassByMe(String[] clazns) {
		for (int i = 0; i < clazns.length; i++) {
			try {
				loadDirectly(clazns[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dynaclazns.add(clazns[i]);
		}
	}

	private Class loadDirectly(String name) throws IOException {
		Class cls = null;
		StringBuffer sb = new StringBuffer(basedir);
		String classname = name.replace('.', File.separatorChar) + ".class";
		sb.append(File.separator + classname);
		File classF = new File(sb.toString());
		cls = instantiateClass(name, new FileInputStream(classF), classF.length());
		return cls;
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

}
