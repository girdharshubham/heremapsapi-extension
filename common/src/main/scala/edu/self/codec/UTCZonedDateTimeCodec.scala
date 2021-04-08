// Copyright (C) 2021-2022 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package edu.self.codec

import org.bson.codecs.configuration.CodecConfigurationException
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.{BsonReader, BsonType, BsonWriter}

import java.time.{Instant, ZoneOffset, ZonedDateTime}

object UTCZonedDateTimeCodec extends Codec[ZonedDateTime] {
  @throws(classOf[CodecConfigurationException])
  override def decode(reader: BsonReader, decoderContext: DecoderContext): ZonedDateTime = reader.getCurrentBsonType match {
    case BsonType.DATE_TIME =>
      Instant.ofEpochMilli(reader.readDateTime).atZone(ZoneOffset.UTC)

    case t =>
      throw new CodecConfigurationException(s"Could not decode into ZonedDateTime, expected DATE_TIME BsonType but got '$t'.")
  }

  @throws(classOf[CodecConfigurationException])
  override def encode(writer: BsonWriter, value: ZonedDateTime, encoderContext: EncoderContext): Unit = {
    try {
      writer.writeDateTime(value.withZoneSameInstant(ZoneOffset.UTC).toInstant.toEpochMilli)
    } catch {
      case e: ArithmeticException =>
        throw new CodecConfigurationException(
          s"Unsupported LocalDateTime value '$value' could not be converted to milliseconds: ${e.getMessage()}",
          e
        )
    }
  }

  override def getEncoderClass: Class[ZonedDateTime] = classOf[ZonedDateTime]
}
