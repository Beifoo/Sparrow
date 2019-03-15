package club.beifoo.sparrow.common.core;

public class InvokeException extends Exception {
	private static final long serialVersionUID = 1L;	
	
	protected int code;
	public InvokeException() {
		
	}
	
	public InvokeException(int code) {
		super();
		this.code=code;
	}
	
	public InvokeException(int code,String msg){
		super(msg);
		this.code=code;
	}
	
	public InvokeException(int code,String msg,Throwable e){
		super(msg, e);
		this.code=code;
	}
	
	public InvokeException(int code,Throwable e){
		super(e);
		this.code=code;
	}

	public int getCode() {
		return code;
	}
}
