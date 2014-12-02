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

  private final Map<TypeMirror, String> conversionMap = new LinkedHashMap<TypeMirror, String>();

  private final Elements elementUtils;
  private final Types typeUtils;

  public TypeToBundleMethodMap(Elements elementUtils, Types typeUtils) {
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;

    for (String type : DICTIONARY.keySet()) {
      put(getMirror(type), DICTIONARY.get(type));
    }
  }

  public String convert(TypeMirror fieldType) {
    for (TypeMirror candidate : conversionMap.keySet()) {
      if (typeUtils.isAssignable(fieldType, candidate)) {
        return conversionMap.get(candidate);
      }
    }
    return null;
  }

  private void put(TypeMirror type, String method) {
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

  private static final Map<String, String> DICTIONARY = new LinkedHashMap<String, String>() {{
    put("java.util.ArrayList<java.lang.Integer>", "IntegerArrayList");
    put("java.util.ArrayList<java.lang.String>", "StringArrayList");
    put("java.util.ArrayList<java.lang.CharSequence>", "CharSequenceArrayList");

    put("java.util.ArrayList<? extends android.os.Parcelable>", "ParcelableArrayList");
    put("android.util.SparseArray<? extends android.os.Parcelable>",
        "SparseParcelableArray");

    put("short", SHORT.type);
    put("short[]", SHORT_ARRAY.type);
    put("int", INT.type);
    put("int[]", INT_ARRAY.type);
    put("long", LONG.type);
    put("long[]", LONG_ARRAY.type);
    put("float", FLOAT.type);
    put("float[]", FLOAT_ARRAY.type);
    put("double", DOUBLE.type);
    put("double[]", DOUBLE_ARRAY.type);
    put("byte", BYTE.type);
    put("byte[]", BYTE_ARRAY.type);
    put("boolean", BOOLEAN.type);
    put("boolean[]", BOOLEAN_ARRAY.type);
    put("char", CHAR.type);
    put("char[]", CHAR_ARRAY.type);
    put("java.lang.String", STRING.type);
    put("java.lang.String[]", STRING_ARRAY.type);
    put("android.os.Bundle", "Bundle");

    put("java.lang.CharSequence", CHAR_SEQUENCE.type);
    put("java.lang.CharSequence[]", CHAR_SEQUENCE_ARRAY.type);
    put("android.os.Parcelable", "Parcelable");
    put("android.os.Parcelable[]", "ParcelableArray");
    put("java.io.Serializable", "Serializable");
  }};
}
