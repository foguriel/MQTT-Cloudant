package switchX;

import java.util.concurrent.Callable;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReportSwitchStatus implements Callable<Void> {
	
	public static final String TOPIC = "iot-2/evt/switch_status/fmt/jso";

    private IMqttClient client;
    private int switch_status;
    private int QOS = 0;

    public ReportSwitchStatus(IMqttClient client, int switch_status, int QOS) {
        this.client = client;
        this.switch_status = switch_status;
        this.QOS = QOS;
    }

    @Override
    public Void call() throws Exception {
        
        if ( !client.isConnected()) {
            System.out.println("El cliente no esta conectado");
            return null;
        }
        
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
        client.publish(TOPIC, msg);
     
        
        return null;        
    }

}
