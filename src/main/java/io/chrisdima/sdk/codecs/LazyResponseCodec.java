package io.chrisdima.sdk.codecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chrisdima.sdk.pojos.LazyResponse;
import io.chrisdima.sdk.pojos.UppercaseResponse;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import java.io.IOException;

public class LazyResponseCodec implements MessageCodec<LazyResponse, LazyResponse> {

  @Override
  public void encodeToWire(Buffer buffer, LazyResponse lazyResponse){
    try {
      ObjectMapper mapper = new ObjectMapper();
      byte[] bytes = mapper.writeValueAsBytes(lazyResponse);
      buffer.appendInt(bytes.length);
      buffer.appendBytes(bytes);
    } catch (JsonProcessingException jpe){
      throw new IllegalStateException("Error encoding JSON", jpe);
    }
  }

  @Override
  public LazyResponse decodeFromWire(int position, Buffer buffer){
    int length = buffer.getInt(position);
    byte[] bytes = buffer.getBytes(position + 4, position + 4 + length);

    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(bytes, LazyResponse.class);
    }catch (IOException ioe){
      throw new IllegalStateException("Error decoding JSON", ioe);
    }
  }

  @Override
  public LazyResponse transform(LazyResponse lazyResponse) {
    return lazyResponse;
  }

  @Override
  public String name() {
    return "LazyResponseCodec";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}