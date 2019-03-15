package club.beifoo.sparrow.common.core;

public class Task implements Runnable {
	private int number;
	
	public Task(int number) {
		this.number = number;
	}
	@Override
	public void run() {
		try {
			Thread.sleep(1000L);
			System.out.println("任务 " + number);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
