package czescjestemadas.obfuscator.consumer.mapper;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.api.SkipObfuscation;
import czescjestemadas.obfuscator.util.StrUtil;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class ClassLoadMap implements ClassMapper
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (StrUtil.classPackage(node.name).replace('/', '.').equals(SkipObfuscation.class.getPackageName()))
			return true;

		classes.put(node.name, node);
		return false;
	}
}
