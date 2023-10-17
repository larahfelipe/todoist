package com.larahfelipe.todoist.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {

  private Utils() {
    throw new IllegalStateException("Class not meant to be instantiated");
  }

  public static String[] getNullObjectKeys(Object sourceObj) {
    final BeanWrapper src = new BeanWrapperImpl(sourceObj);

    PropertyDescriptor[] allPropertiesDescriptors = src.getPropertyDescriptors();

    Set<String> emptyKeys = new HashSet<>();

    for (PropertyDescriptor propertyDescriptor : allPropertiesDescriptors) {
      Object srcValue = src.getPropertyValue(propertyDescriptor.getName());

      if (srcValue == null) {
        emptyKeys.add(propertyDescriptor.getName());
      }
    }

    String[] emptyKeysResult = new String[emptyKeys.size()];

    return emptyKeys.toArray(emptyKeysResult);
  }

  public static void copyNonNullObjectKeys(Object sourceObj, Object targetObj) {
    BeanUtils.copyProperties(sourceObj, targetObj, getNullObjectKeys(sourceObj));
  }

}
