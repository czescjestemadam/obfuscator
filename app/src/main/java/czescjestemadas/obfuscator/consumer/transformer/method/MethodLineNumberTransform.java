package czescjestemadas.obfuscator.consumer.transformer.method;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.transformer.ClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;
import java.util.Map;

public class MethodLineNumberTransform implements ClassTransformer
{
	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		for (final MethodNode method : node.methods)
		{
			final ListIterator<AbstractInsnNode> it = method.instructions.iterator();
			while (it.hasNext())
			{
				if (it.next() instanceof LineNumberNode)
					it.remove();
			}
		}

		return false;
	}
}
