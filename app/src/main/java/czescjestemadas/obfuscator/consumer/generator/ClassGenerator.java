package czescjestemadas.obfuscator.consumer.generator;

import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.ClassConsumer;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public interface ClassGenerator extends ClassConsumer
{
	default List<ClassNode> generateNodes(ObfuscatorSettings settings)
	{
		return List.of();
	}
}
