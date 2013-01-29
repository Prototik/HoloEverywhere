
package org.holoeverywhere.resbuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.holoeverywhere.resbuilder.FileProcesser.ProcessResult;
import org.json.JSONObject;

public abstract class TypeProcesser {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public static @interface Type {
        public String name() default "";

        /**
         * Name for block which TypeProcesser will be process
         */
        public String value();
    }

    private final String type, name;

    public TypeProcesser() {
        Class<? extends TypeProcesser> clazz = getClass();
        if (!clazz.isAnnotationPresent(Type.class)) {
            throw new IllegalStateException("TypeProcesser should be present Type annotation");
        }
        Type type = clazz.getAnnotation(Type.class);
        String name = type.name();
        if (name.length() == 0) {
            name = clazz.getSimpleName();
            if (name.startsWith("Type") && name.length() > 4) {
                name = name.substring(4);
            }
        }
        this.name = name;
        this.type = type.value();
    }

    public final String getName() {
        return name;
    }

    public final String getType() {
        return type;
    }

    public abstract ProcessResult process(BuildMojo mojo, JSONObject json);
}
