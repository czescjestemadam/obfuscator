package czescjestemadas.obfuscator.consumer.transformer.method;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.transformer.ClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class ClassMethodTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		for (final MethodNode method : node.methods)
		{
			final String fullName = Mappings.key(node, method);
			final String mappedName = mappings.getMapping(fullName);
			if (mappedName != null)
			{
				method.name = mappedName;
				Obfuscator.LOGGER.info("  {} -> {}", fullName, Mappings.key(node, mappedName));
			}

			for (final AbstractInsnNode instruction : method.instructions)
			{
				if (!(instruction instanceof MethodInsnNode methodInsnNode))
					continue;

				final String insnFullName = Mappings.key(methodInsnNode.owner, methodInsnNode.name);
				final String insnMappedName = mappings.getMapping(insnFullName);
				if (insnMappedName == null)
					continue;

				methodInsnNode.name = insnMappedName;
				Obfuscator.LOGGER.info("  INSN {} -> {}", insnFullName, Mappings.key(methodInsnNode.owner, insnMappedName));
			}
		}

		return false;
	}
}
