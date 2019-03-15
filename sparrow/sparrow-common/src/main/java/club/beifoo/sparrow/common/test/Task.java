package club.beifoo.sparrow.common.test;

public class Task implements Runnable {
	private int number;
	
	public Task(int number) {
		this.number = number;
	}
	@Override
	public void run() {
		try {
			Thread.sleep(1000L);
			System.out.println("批量无返回值方法测试: 任务 " + number + " " +  Thread.currentThread().getName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
