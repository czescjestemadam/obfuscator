package czescjestemadas.obfuscator.consumer.generator;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class ClassFieldGen implements ClassGenerator
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (Mappings.isSkipAnnotated(node.invisibleAnnotations))
			return false;

		for (final FieldNode field : node.fields)
		{
			if (!Mappings.isSkipAnnotated(field.invisibleAnnotations))
				mappings.generateMapping(Mappings.key(node, field), settings.getFieldNameLength());
		}

		return false;
	}
}
