package czescjestemadas.obfuscator.consumer.transformer.field;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.transformer.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Map;

public class FieldFinalRemoveTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (settings.getSkippedNames().contains(node.name))
			return false;

		for (FieldNode field : node.fields)
		{
			if ((field.access & Opcodes.ACC_FINAL) <= 0)
				continue;

			field.access &= ~Opcodes.ACC_FINAL;
			Obfuscator.LOGGER.info("  Removed final from {}", Mappings.key(node, field));
		}

		return false;
	}
}
