package controlPanel;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;



public class ControlPanel {

	
	
	
	
	public static void main(String[] args) throws MqttException {
		
		String publisherId = "a:6relw0:qcs1tfgehi";
		
		String IBMIoT = "tcp://6relw0.messaging.internetofthings.ibmcloud.com:1883";

		
		IMqttClient publisher = new MqttClient(IBMIoT, publisherId);
		
		
		
		System.out.println("papaya");
		publisher.disconnect();
		publisher.close();

	}

}
