package czescjestemadas.obfuscator.consumer.transformer.field;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.transformer.ClassTransformer;
import org.objectweb.asm.tree.*;

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

		for (final MethodNode method : node.methods)
		{
			for (AbstractInsnNode instruction : method.instructions)
			{
				if (!(instruction instanceof FieldInsnNode fieldInsnNode))
					continue;

				final String fullName = Mappings.key(fieldInsnNode.owner, fieldInsnNode.name);
				final String mappedName = mappings.getMapping(fullName);
				if (mappedName == null)
					continue;

				fieldInsnNode.name = mappedName;
				Obfuscator.LOGGER.info("  INSN {} -> {}", fullName, Mappings.key(fieldInsnNode.owner, mappedName));
			}
		}

		return false;
	}
}
