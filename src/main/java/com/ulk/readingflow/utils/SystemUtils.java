package com.ulk.readingflow.utils;

import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@NoArgsConstructor
public final class SystemUtils {

    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullProperties(source));
    }

    private static String[] getNullProperties(Object source) {

        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = src.getPropertyDescriptors();
        Set<String> emptyFields = new HashSet<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Object srcValue = src.getPropertyValue(propertyDescriptor.getName());
            if (null == srcValue) {
                emptyFields.add(propertyDescriptor.getName());
            }
        }

        String[] result = new String[emptyFields.size()];
        return emptyFields.toArray(result);

    }

    public static String joinStrings(List<String> strings) {
        int size = strings.size();
        if (size == 0) {
            return "";
        } else if (size == 1) {
            return strings.get(0);
        } else {
            return String.join(", ", strings.subList(0, size - 1)) + " e " + strings.get(size - 1);
        }
    }
}
