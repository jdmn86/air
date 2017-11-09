package pt.ipleiria.dei.iair.Utils;

import java.io.Serializable;
import java.util.HashMap;
/**
 * Created by kxtreme on 09-11-2017.
 */




public interface Converter<I extends Object, O extends Object> extends Serializable {

    O convert(I i, HashMap<String, Object> options) throws Exception;
}
