package me.BerylliumOranges.main;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.BerylliumOranges.listeners.traits.ItemTrait;

public class DirectoryTools {

	/**
	 * Gets classes in package by name using Class.forName()
	 *
	 * @param pathtoPackage Ex: me/BerylliumOranges/event/segments
	 */
	public static ArrayList<Class<?>> getClasses(String pathToPackage) {
		ArrayList<Class<?>> classes = new ArrayList<>();
		CodeSource src = ItemTrait.class.getProtectionDomain().getCodeSource();
		if (src != null) {
			URL jar = src.getLocation();
			try {
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				while (true) {
					ZipEntry e = zip.getNextEntry();
					if (e == null)
						break;
					String name = e.getName();
					String packageNameAsPath = pathToPackage.replace('.', '/');
					if (name.startsWith(packageNameAsPath) && name.length() > packageNameAsPath.length()
							&& name.charAt(packageNameAsPath.length()) == '/'
							&& name.chars().filter(ch -> ch == '/').count() == packageNameAsPath.chars().filter(ch -> ch == '/').count()) {
						name = name.replace('/', '.').substring(0, name.length() - ".class".length());
						classes.add(Class.forName(name));
					}
				}
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return classes;
	}

	public static <T> ArrayList<Class<? extends T>> getClasses(String pathToPackage, Class<T> type) {
		ArrayList<Class<? extends T>> classes = new ArrayList<>();
		CodeSource src = type.getProtectionDomain().getCodeSource();
		if (src != null) {
			URL jar = src.getLocation();
			try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
				while (true) {
					ZipEntry e = zip.getNextEntry();
					if (e == null)
						break;
					String name = e.getName();
					String packageNameAsPath = pathToPackage.replace('.', '/');
					if (name.startsWith(packageNameAsPath) && name.endsWith(".class") && name.length() > packageNameAsPath.length() + 6
							&& name.charAt(packageNameAsPath.length()) == '/' && name.chars().filter(ch -> ch == '/')
									.count() == packageNameAsPath.chars().filter(ch -> ch == '/').count() + 1) {
						name = name.replace('/', '.').substring(0, name.length() - ".class".length());
						try {
							Class<?> cls = Class.forName(name);
							if (type.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
								// This will add only those classes that are assignable from the type and not
								// abstract
								classes.add(cls.asSubclass(type));
							}
						} catch (ClassNotFoundException | NoClassDefFoundError classNotFoundException) {
							// Handle the exception or print the stack trace as per your requirement
							classNotFoundException.printStackTrace();
						}
					}
				}
			} catch (IOException er) {
				er.printStackTrace();
			}
		}
		return classes;
	}

}