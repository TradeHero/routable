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
  CHAR_SEQUENCE_ARRAY("CharSequenceArray")
  ;

  final String type;

  BundleType(String type) {
    this.type = type;
  }
}
