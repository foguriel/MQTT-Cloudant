package generalPackage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SwitchItemState extends SwitchItem {
	
	private boolean encendido;
	
	public SwitchItemState(LocalDateTime f) {
		super(f);
	}
	
	public SwitchItemState(String id, boolean e) {
		super(id);
		this.encendido = e;		
	}
		
	public SwitchItemState(LocalDateTime f, boolean e) {
		super(f);
		this.encendido = e;
	}
	
	public SwitchItemState(String id, LocalDateTime f, boolean e) {
		super(id);
		this.fecha = f;
		this.encendido = e;
	}
	
	public boolean getEncendido() {
		return this.encendido;
	}
	
	public void setEncendido(boolean e) {
		this.encendido = e;
	}
	
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = this.fecha.format(formatter);
        String enc = this.encendido ? "ENCENDIDO" : "APAGADO";

        return formatDateTime + " - " + enc;
	}
}
