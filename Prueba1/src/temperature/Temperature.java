package temperature;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;


public class Temperature {

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
			
			
			for (int i = 0; i < 2; i++ ){
				
				TemperatureSensor ts = new TemperatureSensor(publisher);
				ts.call();
				
				Thread.sleep(5000);
			}

			publisher.disconnect();
			
		}
		
		System.out.println("papaya");
		
		
		publisher.close();

	}

}
