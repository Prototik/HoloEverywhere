package org.holoeverywhere.addon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
/**
 * Annotation for fast attaching all necessary addons.<br/>
 * May used on any class which support it.<br/>
 * By default it's {@link org.holoeverywhere.app.Application}, {@link org.holoeverywhere.app.Activity} and {@link org.holoeverywhere.app.Fragment}.<br/>
 * <br/>
 * Usage:<br/>
 * <pre>
 *     @Addons(AddonTabber.class, AddonSlider.class)
 *     public class MyActivity extends Activity {
 *         ...
 *     }
 * </pre>
 */
public @interface Addons {
    public Class<? extends IAddon>[] value() default {};
}
