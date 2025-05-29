package czescjestemadas.obfuscator.consumer.mapper;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class ClassFieldMap implements ClassMapper
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (Mappings.isSkipAnnotated(node.invisibleAnnotations))
			return false;

		for (final FieldNode field : node.fields)
		{
			if (Mappings.isSkipAnnotated(field.invisibleAnnotations))
				continue;

			final String fullName = Mappings.key(node, field);

			if (settings.getSkippedNames().contains(fullName))
				continue;

			mappings.generateMapping(fullName, settings.getFieldNameLength());
		}

		return false;
	}
}
