package org.beable.common.rpc.core.serialize;

import org.beable.common.rpc.extension.SPI;

/**
 * @author qing.wu
 */
@SPI
public interface Serializer {

    byte[] serialize(Object object);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
