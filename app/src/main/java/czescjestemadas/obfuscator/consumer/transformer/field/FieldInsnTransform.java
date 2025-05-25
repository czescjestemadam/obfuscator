package czescjestemadas.obfuscator.consumer.transformer.field;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.transformer.ClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class FieldInsnTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
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
				Obfuscator.LOGGER.info("  {} -> {}", fullName, Mappings.key(fieldInsnNode.owner, mappedName));
			}
		}

		return false;
	}
}
