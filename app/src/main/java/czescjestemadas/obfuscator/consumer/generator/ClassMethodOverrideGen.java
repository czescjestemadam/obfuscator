package czescjestemadas.obfuscator.consumer.generator;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class ClassMethodOverrideGen implements ClassGenerator
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (Mappings.isSkipAnnotated(node.invisibleAnnotations))
			return false;

		for (final MethodNode method : node.methods)
		{
			final String fullName = Mappings.key(node, method);
			final String superMethodFullName = Mappings.findSuperMethod(node, method, classes);
			if (superMethodFullName == null)
				continue;

			final String superMappedName = mappings.getMapping(superMethodFullName);

			if (superMappedName != null)
				mappings.setMapping(fullName, superMappedName);

			Obfuscator.LOGGER.info("  {} overrides {} -> {}", fullName, superMethodFullName, Mappings.key(node, superMappedName));
		}

		return false;
	}
}
