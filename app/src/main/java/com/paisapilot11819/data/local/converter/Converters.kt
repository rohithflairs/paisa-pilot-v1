package com.paisapilot11819.data.local.converter

import androidx.room.TypeConverters

/**
 * Minimal TypeConverters class for Room.
 *
 * Dates are stored as String (ISO "yyyy-MM-dd") and money amounts as Long (paise),
 * so no custom type conversions are needed at this time. This class serves as a
 * placeholder for the @TypeConverters annotation on the database and can be extended
 * if custom types are added in the future.
 */
@TypeConverters
class Converters
