package testink_pkg;

public class TestinkMain
{
	public int num;

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
