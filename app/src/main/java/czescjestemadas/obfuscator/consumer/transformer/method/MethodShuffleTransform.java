package czescjestemadas.obfuscator.consumer.transformer.method;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.transformer.ClassTransformer;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collections;
import java.util.Map;

public class MethodShuffleTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (node.methods.size() > 1)
			return false;

		Collections.shuffle(node.methods);
		Obfuscator.LOGGER.info("  Shuffled {} methods", node.name);

		return false;
	}
}
