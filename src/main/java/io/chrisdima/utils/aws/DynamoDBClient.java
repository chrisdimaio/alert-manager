package io.chrisdima.utils.aws;

import io.chrisdima.alertmanager.objects.Alert;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

//https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-dynamodb-enhanced.html
public class DynamoDBClient {
  private final Logger logger = LoggerFactory.getLogger( DynamoDBClient.class );

  private DynamoDbEnhancedClient client;

  public DynamoDBClient(Region region) {
    try {
      client = DynamoDbEnhancedClient.builder().dynamoDbClient(
          DynamoDbClient.builder().region(region).build()
      ).build();

    } catch (DynamoDbException e) {
      logger.error(e);
    }
  }

  public void putAlert(Alert alert) {
    DynamoDbTable<Alert> alertDynamoDbTable
        = client.table("alert", TableSchema.fromBean(Alert.class));
    alertDynamoDbTable.putItem(alert);
  }

  public Alert getAlert(String id) {
    DynamoDbTable<Alert> alertDynamoDbTable
        = client.table("alert", TableSchema.fromBean(Alert.class));
    return alertDynamoDbTable.getItem(Key.builder().partitionValue(id).build());
  }

  public static void main(String[] args) {

  }
//  public static void putAlert(Alert alert) {
//    try (DynamoDbClient ddb = DynamoDbClient.builder().region(Region.US_EAST_1).build()) {
//
//      DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb)
//          .build();
//
//      DynamoDbTable<Alert> custTable
//          = enhancedClient.table("alert", TableSchema.fromBean(Alert.class));
//      custTable.putItem(alert);
//    } catch (DynamoDbException e) {
//      e.printStackTrace();
//    }
//  }
}
