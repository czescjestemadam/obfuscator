package czescjestemadas.obfuscator.consumer.generator;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class ClassNameGen implements ClassGenerator
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (!Mappings.isSkipAnnotated(node.invisibleAnnotations))
			mappings.generateClassMapping(node.name, settings.getClassNameLength());

		return false;
	}
}
