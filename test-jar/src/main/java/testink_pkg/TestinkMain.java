package testink_pkg;

public class TestinkMain
{
	public int num;
	public TestinkMain testinkMain;

	public TestinkMain()
	{
		this.num = 0;
	}

	public TestinkMain(int num)
	{
		this.num = num;
	}

	public int getNum()
	{
		return num;
	}

	public void setNum(int num)
	{
		this.num = num;
	}

	public static void printSample(TestinkMain sample)
	{
		System.out.println(sample.getNum());
	}

	public static void multiArg(int i, float f, Object o, TestinkMain t)
	{
	}

	public static TestinkMain ret()
	{
		return new TestinkMain();
	}

	public static void main(String[] args)
	{
		final TestinkMain sample = new TestinkMain();
		sample.setNum(10);
		printSample(sample);

		new UnobfuscatedSampleClass("jajoo").print();

		final Child child = new Child();
		child.forOverrid();

		final Base base = new Child();
		base.forOverrid();
	}
}
