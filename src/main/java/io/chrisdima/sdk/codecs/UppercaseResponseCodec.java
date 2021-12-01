package io.chrisdima.sdk.codecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chrisdima.sdk.pojos.UppercaseRequest;
import io.chrisdima.sdk.pojos.UppercaseResponse;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import java.io.IOException;

public class UppercaseResponseCodec implements MessageCodec<UppercaseResponse, UppercaseResponse> {

  @Override
  public void encodeToWire(Buffer buffer, UppercaseResponse uppercaseResponse){
    try {
      ObjectMapper mapper = new ObjectMapper();
      byte[] bytes = mapper.writeValueAsBytes(uppercaseResponse);
      buffer.appendInt(bytes.length);
      buffer.appendBytes(bytes);
    } catch (JsonProcessingException jpe){
      throw new IllegalStateException("Error encoding JSON", jpe);
    }
  }

  @Override
  public UppercaseResponse decodeFromWire(int position, Buffer buffer){
    int length = buffer.getInt(position);
    byte[] bytes = buffer.getBytes(position + 4, position + 4 + length);

    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(bytes, UppercaseResponse.class);
    }catch (IOException ioe){
      throw new IllegalStateException("Error decoding JSON", ioe);
    }
  }

  @Override
  public UppercaseResponse transform(UppercaseResponse uppercaseResponse) {
    return uppercaseResponse;
  }

  @Override
  public String name() {
    return "UppercaseResponseCodec";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}