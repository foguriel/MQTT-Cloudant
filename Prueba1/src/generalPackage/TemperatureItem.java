package generalPackage;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TemperatureItem  {
	private String _id;
	private LocalDateTime fecha;
	private double temperature;
	
	public TemperatureItem(double t) {
		this.fecha = LocalDateTime.now();
		this.temperature = t;
	}
	
	public TemperatureItem(LocalDateTime f, double t) {
		this.fecha = f;
		this.temperature = t;
	}
	
	public TemperatureItem(String id, double t) {
		this._id = id;
		this.fecha = LocalDateTime.now();
		this.temperature = t;
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
	
	public double getTemperature() {
		return this.temperature;
	}
	
	public void setTemperature(double t) {
		this.temperature = t;
	}
	
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = this.fecha.format(formatter);
        String temp = new DecimalFormat("#.0#").format(this.temperature);

        return formatDateTime + " - Temp: " + temp + "º";
	}
	
}
