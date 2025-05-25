package czescjestemadas.obfuscator.consumer;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import czescjestemadas.obfuscator.consumer.generator.ClassGenerator;
import czescjestemadas.obfuscator.consumer.transformer.ClassTransformer;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;
import java.util.function.Predicate;

public interface ClassConsumer
{
	/**
	 * @return {@code true} to ignore {@code node}
	 */
	boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings);


	static Predicate<ClassConsumer> all()
	{
		return cc -> true;
	}

	static Predicate<ClassConsumer> generatorPredicate()
	{
		return cc -> cc instanceof ClassGenerator;
	}

	static Predicate<ClassConsumer> transformerPredicate()
	{
		return cc -> cc instanceof ClassTransformer;
	}
}
