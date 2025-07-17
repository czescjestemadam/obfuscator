package czescjestemadas.obfuscator.consumer.mapper;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.util.StrUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class ClassMethodMap implements ClassMapper
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (Mappings.isSkipAnnotated(node.invisibleAnnotations))
			return false;

		if (!StrUtil.startsWith(node.name, settings.getPackagePrefixes()))
			return false;

		for (final MethodNode method : node.methods)
		{
			if (Mappings.isNameIgnored(method.name) || Mappings.isSkipAnnotated(method.invisibleAnnotations))
				continue;

			final String fullName = Mappings.key(node, method);

			if (settings.getSkippedNames().contains(fullName))
				continue;

			mappings.generateMapping(fullName, settings.getMethodNameLength());
		}

		return false;
	}
}
