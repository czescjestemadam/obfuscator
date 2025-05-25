package czescjestemadas.obfuscator.util;

public abstract class StrUtil
{
	///  / -> .
	private static String dotName(String desc)
	{
		return desc.replace('/', '.');
	}

	public static String classDesc(String desc)
	{
		return dotName(desc.substring(1, desc.length() - 1));
	}

	public static String classSimpleName(String name)
	{
		return name.substring(name.lastIndexOf('.') + 1);
	}

	public static String classPackage(String desc)
	{
		final String name = dotName(desc);
		return name.substring(0, name.lastIndexOf('.'));
	}
}
