/*
 * Copyright (c) 2014 Karumien s.r.o.
 * 
 * The contractor, Karumien s.r.o., does not take any responsibility for defects
 * arising from unauthorized changes to the source code.
 */
package cz.i24.util.jasper;

import java.io.File;
import java.util.Arrays;

import net.sf.jasperreports.engine.util.FileResolver;


/**
 * Support class for JasperReport reading resources from classpath.
 *
 * @author <a href="miroslav.svoboda@karumien.com">Miroslav Svoboda</a>
 * @version 1.0
 * @since 28.04.2014 17:15:22
 */
public class ClassPathFileResolver implements FileResolver {

    private String[] classPath;

    /**
     * {@inheritDoc}
     */
    @Override
    public File resolveFile(String fileName) {
        return null;
    }

    public ClassPathFileResolver(String... classPath) {
        this.classPath = classPath;
    }

    public String[] getClassPath() {
        return this.classPath;
    }

    @Override
    public String toString() {
        return super.toString() + (this.classPath == null ? "" : " " + Arrays.toString(this.classPath));
    }

}
