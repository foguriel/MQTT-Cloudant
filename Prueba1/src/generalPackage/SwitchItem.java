package generalPackage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SwitchItem {
	
	private String _id;
	protected LocalDateTime fecha;
		
	public SwitchItem(LocalDateTime f) {
		this.fecha = f;
	}
	
	public SwitchItem(String id) {
		this._id = id;
		this.fecha = LocalDateTime.now();
	}
	
	public String getId() {
		return this._id;
	}
	
	public void setId(String id) {
		this._id = id;
	}
	
	public LocalDateTime getFecha() {
		return this.fecha;
	}
	
	public void setFecha(LocalDateTime f) {
		this.fecha = f;
	}
	
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = this.fecha.format(formatter);
        return formatDateTime;
	}
	
}
