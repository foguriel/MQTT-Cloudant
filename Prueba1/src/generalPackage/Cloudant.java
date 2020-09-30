package generalPackage;
import java.net.URL;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

public class Cloudant {
	
	public static CloudantClient connect() throws Exception {
		/*CloudantClient client = ClientBuilder.url(new URL("https://2eddbd51-59ea-4e2a-88c4-77b2fa3b56bd-bluemix.cloudantnosqldb.appdomain.cloud"))
			.iamApiKey("zmH8P6kAH5h2PUjpbb1620XUCDx8B8HniCUHvpBbUoGu")
            .build();*/
		CloudantClient client = ClientBuilder.url(new URL("https://3ae1d9ff-f0b3-43ed-b5b3-66595da5e0c3-bluemix:f04e8774bece2fb314e2b4b77281a7635c4abd0544e284086cdd7b79a95fb253@3ae1d9ff-f0b3-43ed-b5b3-66595da5e0c3-bluemix.cloudantnosqldb.appdomain.cloud"))
				.iamApiKey("fNPOdUqXkGlOvFIeSEE2cjw8iyhHWT4VAGap_dsaS8Th")
	            .build();
		//System.out.println("Conectado - " + client.getBaseUri());
		return client;
	}
	
	public static Database getDb(String dbName) throws Exception {
		Database db = connect().database(dbName, true);
		//System.out.println("Base de datos disponible - " + db.getDBUri());
		return db;
	}
	
}
