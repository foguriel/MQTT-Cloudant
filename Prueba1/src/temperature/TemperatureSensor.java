package temperature;

import java.util.Random;
import java.util.concurrent.Callable;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TemperatureSensor  implements Callable<Void> {
		
	    public static final String TOPIC = "iot-2/evt/temperature/fmt/json";

	    private IMqttClient client;
	    private Random rnd = new Random();
	    private int QOS = 0;
	    
	    public TemperatureSensor(IMqttClient client, int QOS) {
	        this.client = client;
	        this.QOS = QOS;
	    }

	    @Override
	    public Void call() throws Exception {
	        
	        if ( !client.isConnected()) {
	            System.out.println("El cliente no esta conectado");
	            return null;
	        }
	            
	        MqttMessage msg = readTemp();
	        msg.setQos(0);
	        msg.setRetained(true);
	        client.publish(TOPIC,msg);        
	        
	        return null;        
	    }
	    
	    private MqttMessage readTemp() {     
	    	ObjectMapper mapper= new ObjectMapper();
	    	ObjectNode obj = mapper.createObjectNode();
	    	obj.put("Temperatura", Double.valueOf(5 + rnd.nextDouble() * 20.0));
	    	byte[] payload = obj.toString().getBytes();
	        MqttMessage msg = new MqttMessage(payload); 
	        msg.setQos(QOS);
	        return msg;
	    }

}
