package temperature;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;


public class Temperature {
	
	static int QOS = 0;
	
	public static void main(String[] args) throws Exception {
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
			
			System.out.println("El sensor se encuentra en línea.");
			
			for (int i = 0; i < 10; i++ ){
				new TemperatureSensor(publisher, QOS).call();
				Thread.sleep(5000);
			}
			publisher.disconnect();
		}
		
		System.out.println("Sensor desconectado.");
	
		publisher.close();
	}

}
