package io.chrisdima.utils.aws;

import io.chrisdima.alertmanager.enums.ContactType;
import io.chrisdima.alertmanager.objects.Alert;
import io.chrisdima.alertmanager.objects.Contact;
import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-dynamodb-enhanced.html
public class DynamoDB {
  public static void main(String[] args) {
    Alert alert = new Alert();
    alert.setId(java.util.UUID.randomUUID().toString());
    alert.setMessage("DB alert test");
    Contact contact = new Contact();
    contact.setType(ContactType.SMS);
    contact.setAddress("1234561234");
    alert.setContacts(List.of(contact));
    putAlert(alert);
  }
  public static void putAlert(Alert alert) {
    try (DynamoDbClient ddb = DynamoDbClient.builder().region(Region.US_EAST_1).build()) {

      DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb)
          .build();

      DynamoDbTable<Alert> custTable
          = enhancedClient.table("alert", TableSchema.fromBean(Alert.class));
      custTable.putItem(alert);
    } catch (DynamoDbException e) {
      e.printStackTrace();
    }
  }
}
