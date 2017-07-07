package cn.edu.uestc.indoorlocation.communication.message;

/**
 * 
 * 
 * @author vincent
 *
 */
public interface Serializer {

	/**
	 * 序列化，专用于Message的序列化
	 * @param obj
	 * @return
	 */
	 <T> byte[] serialize(T obj);
	
	/**
	 * 反序列化，同样专用于Message类的反序列化
	 * @param bytes
	 * @return
	 */
	 <T> T deserialize(byte[] bytes, Class<T> clazz);
	
}