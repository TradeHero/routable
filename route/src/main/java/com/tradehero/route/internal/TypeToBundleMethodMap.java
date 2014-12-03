package com.tradehero.route.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.tradehero.route.internal.BundleType.*;

final class TypeToBundleMethodMap {

  private final Map<TypeMirror, BundleType> conversionMap = new LinkedHashMap<TypeMirror, BundleType>();

  private final Elements elementUtils;
  private final Types typeUtils;

  public TypeToBundleMethodMap(Elements elementUtils, Types typeUtils) {
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;

    for (String type : DICTIONARY.keySet()) {
      put(getMirror(type), DICTIONARY.get(type));
    }
  }

  public BundleType convert(TypeMirror fieldType) {
    for (TypeMirror candidate : conversionMap.keySet()) {
      if (typeUtils.isAssignable(fieldType, candidate)) {
        return conversionMap.get(candidate);
      }
    }
    return null;
  }

  private void put(TypeMirror type, BundleType method) {
    conversionMap.put(type, method);
  }

  private TypeMirror getMirror(String type) {
    if (isCollection(type)) {
      return getCollectionMirror(type);
    }
    if (isArray(type)) {
      return getArrayMirror(type);
    }
    if (isPrimitive(type)) {
      return getPrimitiveMirror(type);
    }
    return getSimpleMirror(type);
  }

  private boolean isCollection(String type) {
    return type.indexOf('<') != -1;
  }

  private TypeMirror getCollectionMirror(String type) {
    String collectionType = type.substring(0, type.indexOf('<'));
    String elementType = type.substring(type.indexOf('<') + 1, type.length() - 1);

    TypeElement collectionTypeElement = elementUtils.getTypeElement(collectionType);

    TypeMirror elementTypeMirror = getElementMirror(elementType);

    return typeUtils.getDeclaredType(collectionTypeElement, elementTypeMirror);
  }

  private TypeMirror getElementMirror(String elementType) {
    if (isWildCard(elementType)) {
      TypeElement typeElement = elementUtils.getTypeElement(wildCard(elementType));
      return typeUtils.getWildcardType(typeElement.asType(), null);
    }
    return elementUtils.getTypeElement(elementType).asType();
  }

  private boolean isWildCard(String elementType) {
    return elementType.startsWith("?");
  }

  private String wildCard(String elementType) {
    return elementType.substring("? extends ".length());
  }

  private boolean isArray(String type) {
    return type.endsWith("[]");
  }

  private ArrayType getArrayMirror(String type) {
    return typeUtils.getArrayType(getMirror(type.substring(0, type.length() - 2)));
  }

  private boolean isPrimitive(String type) {
    return type.indexOf('.') == -1;
  }

  private PrimitiveType getPrimitiveMirror(String type) {
    return typeUtils.getPrimitiveType(TypeKind.valueOf(type.toUpperCase()));
  }

  private TypeMirror getSimpleMirror(String type) {
    return elementUtils.getTypeElement(type).asType();
  }

  private static final Map<String, BundleType> DICTIONARY = new LinkedHashMap<String, BundleType>() {{
    put("short", SHORT);
    put("short[]", SHORT_ARRAY);
    put("int", INT);
    put("int[]", INT_ARRAY);
    put("long", LONG);
    put("long[]", LONG_ARRAY);
    put("float", FLOAT);
    put("float[]", FLOAT_ARRAY);
    put("double", DOUBLE);
    put("double[]", DOUBLE_ARRAY);
    put("byte", BYTE);
    put("byte[]", BYTE_ARRAY);
    put("boolean", BOOLEAN);
    put("boolean[]", BOOLEAN_ARRAY);
    put("char", CHAR);
    put("char[]", CHAR_ARRAY);
    put("java.lang.String", STRING);
    put("java.lang.String[]", STRING_ARRAY);
    put("java.lang.CharSequence", CHAR_SEQUENCE);
    put("java.lang.CharSequence[]", CHAR_SEQUENCE_ARRAY);

    // url supported?
    put("android.os.Bundle", BUNDLE);
    put("android.os.Parcelable", PARCELABLE);
    put("android.os.Parcelable[]", PARCELABLE_ARRAY);
    put("java.io.Serializable", SERIALIZABLE);
    put("java.util.ArrayList<java.lang.Integer>", INTEGER_ARRAYLIST);
    put("java.util.ArrayList<java.lang.String>", STRING_ARRAYLIST);
    put("java.util.ArrayList<java.lang.CharSequence>", CHAR_SEQUENCE_ARRAYLIST);
    put("java.util.ArrayList<? extends android.os.Parcelable>", PARCELABLE_ARRAYLIST);
    put("android.util.SparseArray<? extends android.os.Parcelable>", SPARSE_PARCELABLE_ARRAY);
  }};
}
