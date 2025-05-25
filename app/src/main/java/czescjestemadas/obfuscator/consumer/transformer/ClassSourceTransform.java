package czescjestemadas.obfuscator.consumer.transformer;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class ClassSourceTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		node.sourceFile = null;
		node.sourceDebug = null;
		return false;
	}
}
