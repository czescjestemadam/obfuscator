package czescjestemadas.obfuscator.consumer.generator;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.api.SkipObfuscation;
import czescjestemadas.obfuscator.util.StrUtil;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class ClassLoadGen implements ClassGenerator
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		final String pkg = StrUtil.classPackage(node.name);
		final String packageName = SkipObfuscation.class.getPackageName();
		if (pkg.equals(packageName))
			return true;

		classes.put(node.name, node);
		return false;
	}
}
