package czescjestemadas.obfuscator.consumer.transformer.field;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.transformer.ClassTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class ClassFieldTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		for (final FieldNode field : node.fields)
		{
			final String fullName = Mappings.key(node, field);
			final String mappedName = mappings.getMapping(fullName);
			if (mappedName == null)
				continue;

			field.name = mappedName;
			Obfuscator.LOGGER.info("  {} -> {}", fullName, Mappings.key(node, mappedName));
		}

		return false;
	}
}
