package com.tradehero.route.internal;

enum BundleType {
  SHORT("Short"),
  SHORT_ARRAY("ShortArray"),
  INT("Int"),
  INT_ARRAY("IntArray"),
  LONG("Long"),
  LONG_ARRAY("LongArray"),
  FLOAT("Float"),
  FLOAT_ARRAY("FloatArray"),
  DOUBLE("Double"),
  DOUBLE_ARRAY("DoubleArray"),
  BYTE("Byte"),
  BYTE_ARRAY("ByteArray"),
  BOOLEAN("Boolean"),
  BOOLEAN_ARRAY("BooleanArray"),
  CHAR("Char"),
  CHAR_ARRAY("CharArray"),
  STRING("String"),
  STRING_ARRAY("StringArray"),
  CHAR_SEQUENCE("CharSequence"),
  CHAR_SEQUENCE_ARRAY("CharSequenceArray"),
  BUNDLE("Bundle"),
  PARCELABLE("Parcelable"),
  PARCELABLE_ARRAY("ParcelableArray"),
  SERIALIZABLE("Serializable"),
  INTEGER_ARRAYLIST("IntegerArrayList"),
  STRING_ARRAYLIST("StringArrayList"),
  CHAR_SEQUENCE_ARRAYLIST("CharSequenceArrayList"),
  PARCELABLE_ARRAYLIST("ParcelableArrayList"),
  SPARSE_PARCELABLE_ARRAY("SparseParcelableArray")
  ;

  final String type;

  BundleType(String type) {
    this.type = type;
  }
}
