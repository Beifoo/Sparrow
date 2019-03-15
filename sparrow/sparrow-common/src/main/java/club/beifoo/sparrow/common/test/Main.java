package club.beifoo.sparrow.common.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.util.concurrent.FutureCallback;

import club.beifoo.sparrow.common.core.Invoker;

public class Main {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Invoker invoker = new Invoker("Test", "WorkThread");
		//--
		invoker.execute(() -> {
			System.out.println("无返回值方法测试: " + Thread.currentThread().getName());
		});
		System.out.println("主方法-01" + Thread.currentThread().getName());
		//--
		invoker.submitWithCallback(() -> {
			Thread.sleep(1000L);
			System.out.println("有返回值方法测试 执行过程: " + Thread.currentThread().getName());
			return "我是callable的返回值";
		}, new FutureCallback<String>() {
			@Override
			public void onSuccess(@Nullable String result) {
				System.out.println("有返回值方法测试 [" + result + " ]: " + Thread.currentThread().getName());
			}

			@Override
			public void onFailure(Throwable t) {
				System.out.println("我是异常: " + Thread.currentThread().getName());
				t.printStackTrace();
			}
		});
		System.out.println("主方法-02" + Thread.currentThread().getName());
		Future<String> future = invoker.submit(() -> {
			Thread.sleep(1000L);
			return "我是callable的future返回值";
		});
		String result = future.get();
		System.out.println("有返回值阻塞方法测试  [" + result + " ]: " + Thread.currentThread().getName());
		System.out.println("主方法-03" + Thread.currentThread().getName());
		for (int i = 0; i < 10; i++) {
			invoker.execute(new Task(i));
		}
		System.out.println("主方法-04" + Thread.currentThread().getName());
		invoker.shutdown();
	}
}
