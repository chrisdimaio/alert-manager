package io.chrisdima.services;

import static io.chrisdima.sdk.Constants.CONTENT_TYPE_PNG;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import io.chrisdima.sdk.Message;
import io.chrisdima.sdk.annotations.Address;
import io.chrisdima.sdk.base.BaseVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class QRCodeGenerator extends BaseVerticle {

  @Address("v1:generate-qr-code")
  public void generateQRCode(Message<JsonObject> message) {
    String data = message.body().getString("data");
    int width = 500;
    int height = 500;

    try {
      byte [] qrCode = generateQR(data, width, height);
      String base64Data = new String(Base64.getEncoder().encode(qrCode), StandardCharsets.UTF_8);
      JsonObject response = new JsonObject().put("base64", base64Data);
      message.reply(response, new DeliveryOptions()
          .addHeader("content_type", CONTENT_TYPE_PNG)
          .addHeader("is_base64", String.valueOf(true)));
    } catch (WriterException | IOException e) {
      e.printStackTrace();
      logger.error(e + "\n" + Arrays.toString(e.getStackTrace()));
      message.fail(500, e.getMessage());
    }
  }

  private byte [] generateQR(String data, int width, int height)
      throws IOException, WriterException {
    BitMatrix bitMatrix = new MultiFormatWriter().encode(
        new String(data.getBytes("UTF-8"), "UTF-8"),
        BarcodeFormat.QR_CODE, width, height);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

    return outputStream.toByteArray();
  }
}
