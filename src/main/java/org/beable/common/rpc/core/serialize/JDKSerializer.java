package org.beable.common.rpc.core.serialize;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author qing.wu
 */
@Slf4j
public class JDKSerializer implements Serializer{


    @Override
    public byte[] serialize(Object object) {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(object);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("serialize error",e);
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream in = new ByteArrayInputStream(bytes)){
            ObjectInputStream ois = new ObjectInputStream(in);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("deserialize error",e);
        }
        return null;
    }
}
