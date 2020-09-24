package controlPanel;

import java.util.Scanner;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import generalPackage.TemperatureItem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import com.cloudant.client.api.*;
import com.cloudant.client.api.model.Response;

public class ControlPanel {

	static int QOS = 0;
	static Double temperaturaX;
	static Database Cdb;
	
	@SuppressWarnings("unchecked")
    public static void disableAccessWarnings() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);

            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class<?> loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
        }
    }
	 
	 
	public static void msgToSwitch(IMqttClient p, String msgToSw) throws Exception {
        
        if ( !p.isConnected()) {
            System.out.println("El cliente no esta conectado");
        }else {
        
	        ObjectMapper mapper = new ObjectMapper();
	    	ObjectNode obj = mapper.createObjectNode();
	    	
			obj.put("request", msgToSw);
	    
	    	byte[] payload = obj.toString().getBytes();
	        MqttMessage msg = new MqttMessage(payload);
	        msg.setQos(QOS);
	        p.publish("iot-2/type/Switch/id/LIGHTSWITCH1/cmd/switch_request/fmt/json", msg);
		}
    }
	
	
	public static void main(String[] args) throws Exception {
		disableAccessWarnings();
		
		String publisherId = "a:6relw0:qcs1tfgehi";
		String IBMIoT = "tcp://6relw0.messaging.internetofthings.ibmcloud.com:1883";
		String user = "a-6relw0-qcs1tfgehi";
		String password = "oOHaAYApM8YXYKIObq";
		
		IMqttClient publisher = new MqttClient(IBMIoT, publisherId);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		options.setUserName(user);
		options.setPassword(password.toCharArray());
		publisher.connect(options);
		
		if (publisher.isConnected()) {
		
			publisher.setCallback(new MqttCallback() {
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					final String payload = new String(message.getPayload());
					//System.out.println(payload.toString());
					//System.out.println(topic);
					if (topic.contains("/temperature/")) {
						ObjectMapper mapper= new ObjectMapper();
				    	JsonNode obj = mapper.readTree( payload.toString() );
				    	temperaturaX = obj.get("Temperatura").asDouble();
	                }else if(topic.contains("/switch_status/")){
	                	ObjectMapper mapper= new ObjectMapper();
				    	JsonNode obj = mapper.readTree( payload.toString() );
				    	System.out.println("Reportando el estado: " + (String.valueOf(obj.get("status").asText()).equals("true") ? "ENCENDIDO" : "APAGADO"));
	                }else {
	                	System.out.println("Se recibió un mensaje inválido.");
	                }
	            }

	            public void connectionLost(Throwable cause) {
	                System.out.println("Se perdió la conexión. " + cause.getMessage());
 	            }

	            public void deliveryComplete(IMqttDeliveryToken token) {
	            }
			});
		
			publisher.subscribe("iot-2/type/Sensor/id/TEMPERATURE1/evt/temperature/fmt/json", QOS);
			publisher.subscribe("iot-2/type/Switch/id/LIGHTSWITCH1/evt/switch_status/fmt/json", QOS);

			System.out.println("Elija una opcion entre:");
			System.out.println(" 1) Consultar la temperatura");
			System.out.println(" 2) Prender/apagar la luz");
			System.out.println(" 3) Consultar el estado de la luz");
			System.out.println(" 4) Prueba de conexión");
			System.out.println(" q) Cerrar el panel de control");
						
			Scanner choose = new Scanner(System.in);
		    String choice= null;
		    while (!"q".equals(choice)) {
		        choice = choose.nextLine();
		        if ("1".equals(choice)) {
		        	System.out.println("La temperatura es " + temperaturaX.toString());
		            choice = null;
		        }
		        if ("2".equals(choice)) {
		            msgToSwitch(publisher, "toggle");
		        	System.out.println("Se modificó el estado de la luz.");
		            choice = null;
		        }
		        if ("3".equals(choice)) {
		        	msgToSwitch(publisher, "status");
		            choice = null;
		        }
		        if ("4".equals(choice)) {
		        	
		        	Cdb = getDb("db20");
		        	
		        	//TemperatureItem ti = new TemperatureItem(3);
		        	//Response response = Cdb.save(ti);
		        	
		        	 JsonObject json = new JsonObject();
		        	 json.addProperty("_id", "1:test-doc-id-2");
		        	 json.add("json-array", new JsonArray());
		        	 Response response = Cdb.save(json);
		        	 
		        	
		            choice = null;
		        }
		        
		    }
		    choose.close();
			
						
			publisher.unsubscribe("iot-2/type/Sensor/id/TEMPERATURE1/evt/temperature/fmt/json");
			publisher.unsubscribe("iot-2/type/Switch/id/LIGHTSWITCH1/evt/switch_status/fmt/json");
		}
		
		publisher.disconnect();
		publisher.close();

	}
	
	public static CloudantClient connect() throws Exception {
		CloudantClient client = ClientBuilder.url(new URL("https://2eddbd51-59ea-4e2a-88c4-77b2fa3b56bd-bluemix.cloudantnosqldb.appdomain.cloud"))
			.iamApiKey("zmH8P6kAH5h2PUjpbb1620XUCDx8B8HniCUHvpBbUoGu")
            .build();
		
		System.out.println("Conectado - " + client.getBaseUri());
		
		return client;
		
	}
	
	public static Database getDb(String dbName) throws Exception {
		
		Database db = connect().database(dbName, false);
		
		System.out.println("Base de datos disponible - " + db.getDBUri());
		
		return db;
		
	}
	

}
