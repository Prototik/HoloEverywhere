
package org.holoeverywhere.builder;

public interface Parseable<T, Z extends Parseable<T, Z>> {
    public Z parse(T data);
}
