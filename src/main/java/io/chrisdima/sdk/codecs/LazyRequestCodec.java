package io.chrisdima.sdk.codecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chrisdima.sdk.pojos.LazyRequest;
import io.chrisdima.sdk.pojos.UppercaseRequest;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import java.io.IOException;

public class LazyRequestCodec implements MessageCodec<LazyRequest, LazyRequest> {

  @Override
  public void encodeToWire(Buffer buffer, LazyRequest lazyRequest){
    try {
      ObjectMapper mapper = new ObjectMapper();
      byte[] bytes = mapper.writeValueAsBytes(lazyRequest);
      buffer.appendInt(bytes.length);
      buffer.appendBytes(bytes);
    } catch (JsonProcessingException jpe){
      throw new IllegalStateException("Error encoding JSON", jpe);
    }
  }

  @Override
  public LazyRequest decodeFromWire(int position, Buffer buffer){
    int length = buffer.getInt(position);
    byte[] bytes = buffer.getBytes(position + 4, position + 4 + length);

    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(bytes, LazyRequest.class);
    }catch (IOException ioe){
      throw new IllegalStateException("Error decoding JSON", ioe);
    }
  }

  @Override
  public LazyRequest transform(LazyRequest lazyRequest) {
    return lazyRequest;
  }

  @Override
  public String name() {
    return "LazyRequestCodec";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}