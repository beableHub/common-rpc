package org.beable.common.rpc.extension;

/**
 * @author qing.wu
 */
public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
