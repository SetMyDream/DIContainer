package main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {
    public List<Class<?>> scanClasses(String basePackage) throws Exception {
        List<Class<?>> componentClasses = new ArrayList<>();

        String basePath = basePackage.replace('.', '/');
        URL basePackageUrl = Thread.currentThread().getContextClassLoader().getResource(basePath);

        if (basePackageUrl != null) {
            File basePackageDirectory = new File(basePackageUrl.toURI());

            if (basePackageDirectory.isDirectory()) {
                scanDirectoryForComponentClasses(basePackageDirectory, basePackage, componentClasses);
            }
        }

        return componentClasses;
    }

    private void scanDirectoryForComponentClasses(File directory, String packageName, List<Class<?>> componentClasses) throws Exception {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String subPackageName = packageName + "." + file.getName();
                    scanDirectoryForComponentClasses(file, subPackageName, componentClasses);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    componentClasses.add(clazz);
                }
            }
        }
    }
}

