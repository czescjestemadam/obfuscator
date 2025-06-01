package czescjestemadas.obfuscator.consumer.transformer.field;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.transformer.ClassTransformer;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collections;
import java.util.Map;

public class FieldShuffleTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		if (node.fields.size() < 2)
			return false;

		Collections.shuffle(node.fields);
		Obfuscator.LOGGER.info("  Shuffled {} fields", node.name);

		return false;
	}
}
