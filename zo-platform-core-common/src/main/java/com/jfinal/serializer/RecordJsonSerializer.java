package com.jfinal.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * @author zhoump
 * @date 2026/9/14
 * @purpose
 */
@JsonComponent
public class RecordJsonSerializer extends JsonSerializer<Record> {

    @Override
    public void serialize(Record record, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(record.getColumns());
    }
}