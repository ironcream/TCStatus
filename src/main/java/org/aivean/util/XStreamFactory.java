package org.aivean.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import java.util.Date;
import java.util.Map;

public class XStreamFactory {

    private static final String DATE_FORMAT = "yyyyMMddHHmmss.SSSZ";
    private static final String[] ALT_DATE_FORMATS = {"yyyyMMdd'T'HHmmssZ"};

    public static XStream getXStream(Class... annotatedClasses) {
        XStream xstream = new XStream(new PureJavaReflectionProvider(), new XppDomDriver(new XmlFriendlyNameCoder
                ("_-", "_"))){
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    @Override
                    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                        boolean result;
                        if (definedIn == Object.class) {
                            //This is not compatible with implicit collections where item name is not defined
                            return false;
                        } else {
                            result = super.shouldSerializeMember(definedIn, fieldName);
                            return result;
                        }
                    }
                };
            }
        };


        if (annotatedClasses != null) {
            for (Class annotatedClass : annotatedClasses) {
                xstream.processAnnotations(annotatedClass);
            }
        }

        xstream.useAttributeFor(Integer.class);
        xstream.useAttributeFor(String.class);
        xstream.useAttributeFor(Boolean.class);
        xstream.useAttributeFor(Date.class);
        xstream.useAttributeFor(Long.class);

        xstream.registerConverter(new DateConverter(DATE_FORMAT, ALT_DATE_FORMATS, true));
        xstream.registerConverter(new MapConverter(xstream.getMapper()) {
            @SuppressWarnings("rawtypes")
            @Override
            public boolean canConvert(Class type) {
                return (type == Map.class);
            }
        });
        return xstream;
    }

}
