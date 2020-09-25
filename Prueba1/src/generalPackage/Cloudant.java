package generalPackage;
import java.net.URL;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

public class Cloudant {
	
	public static CloudantClient connect() throws Exception {
		CloudantClient client = ClientBuilder.url(new URL("https://2eddbd51-59ea-4e2a-88c4-77b2fa3b56bd-bluemix.cloudantnosqldb.appdomain.cloud"))
			.iamApiKey("zmH8P6kAH5h2PUjpbb1620XUCDx8B8HniCUHvpBbUoGu")
            .build();
		//System.out.println("Conectado - " + client.getBaseUri());
		return client;
	}
	
	public static Database getDb(String dbName) throws Exception {
		Database db = connect().database(dbName, false);
		//System.out.println("Base de datos disponible - " + db.getDBUri());
		return db;
	}
	
}
