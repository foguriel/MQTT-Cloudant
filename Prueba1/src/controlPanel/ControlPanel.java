package controlPanel;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.cloudant.client.api.Database;
//import com.cloudant.client.api.model.Shard;
//import com.cloudant.client.api.query.JsonIndex;
//import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.QueryResult;
import com.cloudant.client.api.query.Selector;
import com.cloudant.client.api.query.Sort;

import static com.cloudant.client.api.query.Expression.lt;
import static com.cloudant.client.api.query.Expression.gt;
import static com.cloudant.client.api.query.Expression.eq;
import static com.cloudant.client.api.query.Operation.and;
import static com.cloudant.client.api.query.Operation.or;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import generalPackage.Cloudant;
import generalPackage.SwitchItem;
import generalPackage.SwitchItemState;
import generalPackage.TemperatureItem;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ControlPanel {

	static int QOS = 0;
	static Double temperaturaX = Double.valueOf(-1);
	static String tempCPPartition = "tempCP";
	static String switchCPartition = "switchChange";
	static String switchSPartition = "switchStatus";
	static String tempPartition = "tempSensor";
	static String tempSPartition = "tempSensor";
	
	static Database CdbL; 
	
	static ExecutorService taskExecutor = Executors.newCachedThreadPool();
	
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
		
		CdbL = Cloudant.getDb("db20");
		
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
				    	
				    	//ObjectMapper mapper= new ObjectMapper();
				    	//JsonNode obj = mapper.readTree( new String(msg.getPayload()).toString() );
				    	UUID uuid = UUID.randomUUID();
			    		TemperatureItem ti = new TemperatureItem(tempPartition + ":" + uuid.toString(), temperaturaX);
			    		//Response response = 
						CdbL.save(ti);
			    		//System.out.println("Registro de temperatura almacenado en la partición " + tempPartition);
			    		
	                }else if(topic.contains("/switch_status/")){
	                	ObjectMapper mapper= new ObjectMapper();
				    	JsonNode obj = mapper.readTree( payload.toString() );
				    	System.out.println("Reportando el estado: " + (String.valueOf(obj.get("status").asText()).equals("true") ? "ENCENDIDO" : "APAGADO"));
				    	saveSwitchState (obj);
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
			System.out.println(" 4) Listar las últimas 10 temperaturas registradas.");
			System.out.println(" 5) Listar los últimos 10 reportes de estado del switch.");
			System.out.println(" 6) Listar los últimos 10 cambios de estado del switch.");
			System.out.println(" q) Cerrar el panel de control");
				
			//Se ejecuta una sola vez.
			//CdbL.createIndex(JsonIndex.builder().asc("fecha").definition());
			
			Scanner choose = new Scanner(System.in);
		    String choice= null;
		    while (!"q".equals(choice)) {
		        choice = choose.nextLine();
		        if ("1".equals(choice)) {
		        	if (temperaturaX != -1) {
		        		System.out.println("La temperatura es " + temperaturaX.toString());
		        		UUID uuid = UUID.randomUUID();
		        		TemperatureItem ti = new TemperatureItem(tempCPPartition + ":" + uuid.toString(), temperaturaX);
		        		//Response response = 
	    				CdbL.save(ti);
		        		System.out.println("Registro almacenado correctamente en la partición: " + tempCPPartition);
		        	}else {
		        		System.out.println("Sensor desconectado...");
		        	}
		            choice = null;
		        }
		        if ("2".equals(choice)) {
		            msgToSwitch(publisher, "toggle");
		        	System.out.println("Se modificó el estado de la luz.");
	        		UUID uuid = UUID.randomUUID();
	        		SwitchItem ti = new SwitchItem(switchCPartition + ":" + uuid.toString());
	        		//Response response = 
    				CdbL.save(ti);
	        		System.out.println("Cambio de estado del switch almacenado en la partición " + switchCPartition);
		            choice = null;
		        }
		        if ("3".equals(choice)) {
		        	msgToSwitch(publisher, "status");
		            choice = null;
		        }
		        if ("4".equals(choice)) {
		        	Selector selector = gt("fecha.date.month", 1);
  		        	QueryResult<TemperatureItem> temps = CdbL.query(tempPartition,  
  		        											new QueryBuilder(selector).fields("fecha", "temperature")
  		        																	  .sort(Sort.desc("fecha"))
  		        																	  .limit(10)
  		        																	  .build(), 
  		        											TemperatureItem.class);
	        	 	
  		        	List<TemperatureItem> ts = temps.getDocs();
  		        	Collections.reverse(ts);
		        	for (TemperatureItem t : ts) {
		        		System.out.println(t);
		        	}
		            choice = null;
		        }
		        if ("5".equals(choice)) {
		        	Selector selector = gt("fecha.date.month", 1);
  		        	QueryResult<SwitchItemState> switchChanges = CdbL.query(switchSPartition,  
  		        											new QueryBuilder(selector).fields("fecha", "encendido")
  		        																	  .sort(Sort.desc("fecha"))
  		        																	  .limit(10)
  		        																	  .build(), 
  		        																	SwitchItemState.class);
	        	 	
  		        	List<SwitchItemState> sc = switchChanges.getDocs();
  		        	Collections.reverse(sc);
		        	for (SwitchItemState s : sc) {
		        		System.out.println(s);
		        	}
		            choice = null;
		        }
		        if ("6".equals(choice)) {
		        	Selector selector = gt("fecha.date.month", 1);
  		        	QueryResult<SwitchItem> switchChanges = CdbL.query(switchCPartition,  
  		        											new QueryBuilder(selector).fields("fecha", "encendido")
  		        																	  .sort(Sort.desc("fecha"))
  		        																	  .limit(10)
  		        																	  .build(), 
  		        																	SwitchItem.class);
	        	 	
  		        	List<SwitchItem> sc = switchChanges.getDocs();
  		        	Collections.reverse(sc);
		        	for (SwitchItem s : sc) {
		        		System.out.println(s);
		        	}
		            choice = null;
		        }
		        if ("q".equals(choice)) {
		        	taskExecutor.shutdown();
		        }
		    }
		    choose.close();
			
			publisher.unsubscribe("iot-2/type/Sensor/id/TEMPERATURE1/evt/temperature/fmt/json");
			publisher.unsubscribe("iot-2/type/Switch/id/LIGHTSWITCH1/evt/switch_status/fmt/json");
		}
		
		publisher.disconnect();
		publisher.close();
		
		System.out.println("Panel de control desconectado.");
	}
	
	public static void saveSwitchState(JsonNode obj) {
		taskExecutor.execute(new Runnable() {
			public void run() {
                try {
                	Database CdbTh = Cloudant.getDb("db20");
	        		UUID uuid = UUID.randomUUID();
	        		SwitchItemState ti = new SwitchItemState(switchSPartition + ":" + uuid.toString(), obj.get("status").asBoolean());
	        		//Response response = 
    				CdbTh.save(ti);
	        		System.out.println("Estado del switch almacenado en la partición " + switchSPartition);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
}
