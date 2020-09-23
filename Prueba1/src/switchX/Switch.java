package switchX;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Switch {
	static int QOS = 0;
	static int switch_status = 0;
	static boolean finished = false;
	
	@SuppressWarnings("unchecked")
    public static void disableAccessWarnings() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);

            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
        }
    }
	
	public static int toggle () {
		if (switch_status == 0) {
			switch_status = 1;
		}else {
			switch_status = 0;
		}
		return switch_status;
	}
	
	public static void report_status(IMqttClient client) throws Exception  {
        if ( !client.isConnected()) {
            System.out.println("El cliente no esta conectado");
        }else {
	        
	        ObjectMapper mapper= new ObjectMapper();
	    	ObjectNode obj = mapper.createObjectNode();
	    	if (switch_status == 1) {
	    		obj.put("status", "true");
	    	}else {
	    		obj.put("status", "false");
	    	}
	    	byte[] payload = obj.toString().getBytes();
	        MqttMessage msg = new MqttMessage(payload);
	        msg.setQos(QOS);
	        client.publish("iot-2/evt/switch_status/fmt/json", msg);
        }
	}
	
	
	public static void main(String[] args) throws Exception {
		disableAccessWarnings();
		String publisherId = "d:6relw0:Switch:LIGHTSWITCH1";
		String IBMIoT = "tcp://6relw0.messaging.internetofthings.ibmcloud.com:1883";
		String user = "use-token-auth";
		String password = "PDyTR2020";
				
		IMqttClient publisher = new MqttClient(IBMIoT, publisherId);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		options.setUserName(user);
		options.setPassword(password.toCharArray());
		publisher.connect(options);
		
		if (publisher.isConnected()) {
			
			System.out.println("El switch se encuentra en línea. Finalice con q");
			
			publisher.setCallback(new MqttCallback() {
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					final String payload = new String(message.getPayload());
					//System.out.println(payload.toString());
					//System.out.println(topic);
	                if (payload.contains("\"toggle\"") ) {
	                	System.out.println("Se activó el interruptor, el estado es ahora " + (Integer.valueOf(toggle()).equals(1)  ? "ENCENDIDO" : "APAGADO"));
	                }else if(payload.contains("\"status\"")){
	                	System.out.println("Reportando el estado " + (Integer.valueOf(switch_status).equals(1)  ? "ENCENDIDO" : "APAGADO") );
	                	Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
	                        public void run() {
	                            try {
	                            	report_status(publisher);
	                            } catch (Exception e) {
	                                e.printStackTrace();
	                            }
	                        }
	                    });
	                }else {
	                	System.out.println("Se recibió un mensaje inválido.");
	                }
	            }

	            public void connectionLost(Throwable cause) {
	                System.out.println("Se perdió la conexión." + cause.getMessage());
	                
	            }

	            public void deliveryComplete(IMqttDeliveryToken token) {
	            }
				
			});
			
			publisher.subscribe("iot-2/cmd/switch_request/fmt/json", QOS);
			
			report_status(publisher);
            
			Scanner choose = new Scanner(System.in);
			String choice= null;
			while (!"q".equals(choice)) {
		        choice = choose.nextLine();
		    }
		    choose.close();

			publisher.unsubscribe("iot-2/cmd/switch_request/fmt/json");
			publisher.disconnect();
		}
		
		publisher.close();
		System.out.println("Sensor desconectado.");
	}

}
