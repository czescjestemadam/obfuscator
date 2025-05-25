package testink_pkg;

import czescjestemadas.obfuscator.api.SkipObfuscation;

public class UnobfuscatedSampleClass
{
	@SkipObfuscation
	private final String str;

	public UnobfuscatedSampleClass(String str)
	{
		this.str = str;
	}

	@SkipObfuscation
	public void print()
	{
		System.out.println(str);
	}
}
