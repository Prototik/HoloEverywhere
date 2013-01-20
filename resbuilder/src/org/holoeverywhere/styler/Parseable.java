
package org.holoeverywhere.styler;

public interface Parseable<T, Z extends Parseable<T, Z>> {
    public Z parse(T data);
}
