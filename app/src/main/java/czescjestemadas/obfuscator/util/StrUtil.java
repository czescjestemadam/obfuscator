package czescjestemadas.obfuscator.util;

public abstract class StrUtil
{
	public static String classSimpleName(String name)
	{
		return name.substring(name.lastIndexOf('.') + 1);
	}

	public static String classPackage(String name)
	{
		return name.substring(0, name.lastIndexOf('/'));
	}
}
