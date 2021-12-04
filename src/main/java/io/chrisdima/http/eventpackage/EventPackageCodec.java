package io.chrisdima.http.eventpackage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import java.io.IOException;

public class EventPackageCodec implements MessageCodec<EventPackage, EventPackage> {

  @Override
  public void encodeToWire(Buffer buffer, EventPackage eventPackage){
    try {
      ObjectMapper mapper = new ObjectMapper();
      byte[] bytes = mapper.writeValueAsBytes(eventPackage);
      buffer.appendInt(bytes.length);
      buffer.appendBytes(bytes);
    } catch (JsonProcessingException jpe){
      throw new IllegalStateException("Error encoding JSON", jpe);
    }
  }

  @Override
  public EventPackage decodeFromWire(int position, Buffer buffer){
    int length = buffer.getInt(position);
    byte[] bytes = buffer.getBytes(position + 4, position + 4 + length);

    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(bytes, EventPackage.class);
    }catch (IOException ioe){
      throw new IllegalStateException("Error decoding JSON", ioe);
    }
  }

  @Override
  public EventPackage transform(EventPackage eventPackage) {
    return eventPackage;
  }

  @Override
  public String name() {
    return "EventPackageCodec";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}