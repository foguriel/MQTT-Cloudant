package temperature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Temperature {
	
	static int QOS = 0;
	private static Random rnd = new Random();
    
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
	
	public static void report_temp(IMqttClient client) throws Exception {
        if ( !client.isConnected()) {
            System.out.println("El cliente no esta conectado");
        }else {
	        MqttMessage msg = readTemp();
	        msg.setQos(0);
	        msg.setRetained(true);
	        client.publish("iot-2/evt/temperature/fmt/json",msg);      
	        
	        
        }        
    }
    
    private static MqttMessage readTemp() {     
    	ObjectMapper mapper= new ObjectMapper();
    	ObjectNode obj = mapper.createObjectNode();
    	obj.put("Temperatura", Double.valueOf(5 + rnd.nextDouble() * 20.0));
    	System.out.println("Reportando temperatura: " + obj.get("Temperatura"));
    	byte[] payload = obj.toString().getBytes();
        MqttMessage msg = new MqttMessage(payload); 
        msg.setQos(QOS);
        return msg;
    }
	
	public static void main(String[] args) throws Exception {
		disableAccessWarnings();
		String publisherId = "d:6relw0:Sensor:TEMPERATURE1";
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
			
			System.out.println("El sensor se encuentra en línea. Finalice con q");
			
			publisher.setCallback(new MqttCallback() {
				public void messageArrived(String topic, MqttMessage message) throws Exception {
	            }

	            public void connectionLost(Throwable cause) {
	                System.out.println("Se perdió la conexión." + cause.getMessage());
	            }

	            public void deliveryComplete(IMqttDeliveryToken token) {
	            }
				
			});
			
			Runnable r = new ReportThread(publisher);
			Thread t = new Thread(r);
			t.start();
			
			Scanner choose = new Scanner(System.in);
			String choice= null;
			while (!"q".equals(choice)) {
		        choice = choose.nextLine();
		    }
		    choose.close();
		    
		    ((ReportThread) r).terminate();
		    t.join();
	    
			publisher.disconnect();
		}
		
		System.out.println("Sensor desconectado.");
	
		publisher.close();
	}
	
	static public class ReportThread implements Runnable {
		IMqttClient publisher;
		private volatile boolean running = true;

	    public void terminate() {
	        running = false;
	    }
		public ReportThread (IMqttClient p) {
			this.publisher = p;
		}
		
	    @Override
	    public void run() {
	        	while (running) {
	        		try {
						report_temp(publisher);
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
	        	}
	        }
	    }
	

}
