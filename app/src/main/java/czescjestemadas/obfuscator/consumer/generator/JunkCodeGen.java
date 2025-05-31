package czescjestemadas.obfuscator.consumer.generator;

import czescjestemadas.obfuscator.Mappings;
import czescjestemadas.obfuscator.Obfuscator;
import czescjestemadas.obfuscator.ObfuscatorSettings;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JunkCodeGen implements ClassGenerator
{
	private static final Random RANDOM = new Random();

	private static final Type[] PRIMITIVE_TYPES = {
			Type.VOID_TYPE,
			Type.BOOLEAN_TYPE,
			Type.CHAR_TYPE,
			Type.BYTE_TYPE,
			Type.SHORT_TYPE,
			Type.INT_TYPE,
			Type.FLOAT_TYPE,
			Type.LONG_TYPE,
			Type.DOUBLE_TYPE
	};

	@Override
	public boolean run(ClassNode node, Map<String, ClassNode> classes, Mappings mappings, ObfuscatorSettings settings)
	{
		appendGeneratedElements(node, settings);

		return false;
	}

	@Override
	public List<ClassNode> generateNodes(ObfuscatorSettings settings)
	{
		final List<ClassNode> nodes = new ArrayList<>();

		final int nodeCount = RANDOM.nextInt(settings.getJunkCodeNodesMin(), settings.getJunkCodeNodesMax());
		for (int i = 0; i < nodeCount; i++)
		{
			final ClassNode node = new ClassNode();
			node.access = Opcodes.ACC_PUBLIC;
			node.name = settings.getPackageName() + '/' + Mappings.genName(settings.getClassNameLength());

			appendGeneratedElements(node, settings);

			nodes.add(node);
		}

		return nodes;
	}

	private static void appendGeneratedElements(ClassNode node, ObfuscatorSettings settings)
	{
		final int fieldCount = RANDOM.nextInt(settings.getJunkCodeFieldsMin(), settings.getJunkCodeFieldsMax());
		for (int i = 0; i < fieldCount; i++)
		{
			final FieldNode field = genField(Mappings.genName(settings.getFieldNameLength()));
			node.fields.add(RANDOM.nextInt(node.fields.size() + 1), field);
		}

		final int methodCount = RANDOM.nextInt(settings.getJunkCodeMethodsMin(), settings.getJunkCodeMethodsMax());
		for (int i = 0; i < methodCount; i++)
		{
			final MethodNode method = genMethod(Mappings.genName(settings.getMethodNameLength()));
			node.methods.add(RANDOM.nextInt(node.methods.size() + 1), method);
		}

		Obfuscator.LOGGER.info("  Generated {} fields, {} methods in {}", fieldCount, methodCount, node.name);
	}

	private static FieldNode genField(String name)
	{
		final int access = genAccessOpcode();

		final FieldNode field = new FieldNode(
				access,
				name,
				"I", // todo change
				null,
				RANDOM.nextInt() // todo change
		);

		return field;
	}

	private static MethodNode genMethod(String name)
	{
		int access = genAccessOpcode();

		final MethodNode method = new MethodNode(
				access,
				name,
				"()V",
				null,
				null
		);

		return method;
	}

	private static int genAccessOpcode()
	{
//		int ACC_PUBLIC = 0x0001; // class, field, method
//		int ACC_PRIVATE = 0x0002; // class, field, method
//		int ACC_PROTECTED = 0x0004; // class, field, method

		int access = Opcodes.ACC_PUBLIC << RANDOM.nextInt(3);

		if (RANDOM.nextBoolean())
			access |= Opcodes.ACC_STATIC;

		return access;
	}

	private static Type genSimpleType()
	{
		return null;
	}
}
