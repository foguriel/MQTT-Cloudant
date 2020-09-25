package generalPackage;

import java.time.LocalDateTime;

public class SwitchItemState extends SwitchItem {
	
	private boolean encendido;
	
	public SwitchItemState(LocalDateTime f) {
		super(f);
	}
	
	public SwitchItemState(String id, boolean e) {
		super(id);
		this.encendido = e;		
	}
		
	
	public boolean getEncendido() {
		return this.encendido;
	}
	
	public void setEncendido(boolean e) {
		this.encendido = e;
	}
}
