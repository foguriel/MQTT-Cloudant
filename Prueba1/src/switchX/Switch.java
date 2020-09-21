package switchX;

import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Switch {
	static int QOS = 0;
	static int switch_status = 0;
	static int finished = 0;
		
	public static int toggle () {
		if (switch_status == 0) {
			switch_status = 1;
		}else {
			switch_status = 0;
		}
		return switch_status;
	}
	
	public static void main(String[] args) throws Exception {
		
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
			
			System.out.println("El switch se encuentra en línea.");
			
			publisher.subscribe("iot-2/cmd/switch_request/fmt/json", new IMqttMessageListener() {
				public void messageArrived (final String topic, final MqttMessage message) throws Exception {
	                final String payload = new String(message.getPayload());

	                if (payload.contains("\"toggle\"") ) {
	                	System.out.println("Se activó el interruptor, el estado es ahora " + Integer.valueOf(toggle()).toString());
	                }else if(payload.contains("\"status\"")){
	                	System.out.println("Reportando el estado " + Integer.valueOf(switch_status).toString());
	                	Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
	                        public void run() {
	                            try {
	                                new ReportSwitchStatus(publisher, switch_status, QOS).call();
	                            } catch (Exception e) {
	                                e.printStackTrace();
	                            }
	                        }
	                    });
	                }else {
	                	System.out.println("Se recibió un mensaje inválido.");
	                }
	            }
	        });
			
			
			new ReportSwitchStatus(publisher, switch_status, QOS).call();
            
            
			for (int i = 0; i < 5; i++ ){
				//TemperatureSensor ts = new TemperatureSensor(publisher);
				//ts.call();
				Thread.sleep(1000);
			}
			
			
			
			publisher.unsubscribe("iot-2/cmd/switch_request/fmt/json");
			publisher.disconnect();
		}
		
		System.out.println("Sensor desconectado.");
		publisher.close();

	}

}
