package org.beable.rpcframework.serialize;

import org.beable.rpcframework.common.extension.SPI;

/**
 * @author qing.wu
 */
@SPI
public interface Serializer {

    byte[] serialize(Object object);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
