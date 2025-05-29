package czescjestemadas.obfuscator.consumer.mapper;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class ClassNameMap implements ClassMapper
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (Mappings.isSkipAnnotated(node.invisibleAnnotations))
			return false;

		if (settings.getSkippedNames().contains(node.name))
			return false;

		mappings.generateClassMapping(node.name, settings.getClassNameLength());

		return false;
	}
}
