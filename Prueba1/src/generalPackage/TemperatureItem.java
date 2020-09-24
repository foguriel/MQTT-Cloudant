package generalPackage;

import java.time.LocalDateTime;

public class TemperatureItem  {
	//private LocalDateTime fecha;
	private double temperature;
	
	public TemperatureItem(double t) {
		//this.fecha = LocalDateTime.now();
		this.temperature = t;
	}
	
	/*public TemperatureItem(LocalDateTime f, double t) {
		this.fecha = f;
		this.temperature = t;
	}
	
	public LocalDateTime getFecha() {
		return this.fecha;
	}
	
	public void setFecha(LocalDateTime f) {
		this.fecha = f;
	}*/
	
	public double getTemperature() {
		return this.temperature;
	}
	
	public void setTemperature(double t) {
		this.temperature = t;
	}
	
}
