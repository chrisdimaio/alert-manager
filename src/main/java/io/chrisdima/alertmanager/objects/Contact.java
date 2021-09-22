package io.chrisdima.alertmanager.objects;

import io.chrisdima.alertmanager.enums.ContactType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class Contact {
  private String address;
  private ContactType type;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public ContactType getType() {
    return type;
  }

  public void setType(ContactType type) {
    this.type = type;
  }
}