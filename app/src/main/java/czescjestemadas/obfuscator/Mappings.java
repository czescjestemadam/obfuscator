package czescjestemadas.obfuscator;

import czescjestemadas.obfuscator.api.SkipObfuscation;
import czescjestemadas.obfuscator.util.StrUtil;
import lombok.ToString;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

@ToString
public class Mappings
{
	private static int[] NAME_CHARS;
	private static final Random RANDOM = new Random();

	private final Map<String, String> names = new HashMap<>();
	private final Map<String, String> classes = new HashMap<>();

	public void setMapping(String name, String mapping)
	{
		names.put(name, mapping);
	}

	public void generateMapping(String name, int newNameLength)
	{
		String newName;
		do
		{
			newName = genName(newNameLength);
		}
		while (names.containsValue(newName));

		names.put(name, newName);
	}

	public void generateClassMapping(String className, int newNameLength)
	{
		String newName;
		do
		{
			newName = genName(newNameLength);
		}
		while (classes.containsValue(newName));

		classes.put(className, newName);
	}

	public String getMapping(String name)
	{
		return names.get(name);
	}

	public String getClassMapping(String name)
	{
		return classes.get(name);
	}

	public Type getClassMapping(Type type)
	{
		final String className = type.getClassName().replace('.', '/');

		final String mappedSimpleName = classes.get(className);
		if (mappedSimpleName == null)
			return null;

		final String packageName = StrUtil.classPackage(className);
		return Type.getObjectType(packageName + "/" + mappedSimpleName);
	}

	public Type mapMethodDesc(Type type)
	{
		final Type returnType = type.getReturnType();
		final Type mappedReturnType = getClassMapping(returnType);

		final Type[] argumentTypes = type.getArgumentTypes();
		for (int i = 0; i < argumentTypes.length; i++)
		{
			final Type mappedArgumentType = getClassMapping(argumentTypes[i]);
			if (mappedArgumentType != null)
				argumentTypes[i] = mappedArgumentType;
		}

		final Type mappedType = Type.getMethodType(mappedReturnType != null ? mappedReturnType : returnType, argumentTypes);

		return type.equals(mappedType) ? null : mappedType;
	}

	public boolean containsMappedClass(String mapped)
	{
		return classes.containsValue(mapped);
	}


	public static String key(ClassNode node, FieldNode field)
	{
		return key(node, field.name);
	}

	public static String key(ClassNode node, MethodNode method)
	{
		return key(node, method.name);
	}

	public static String key(ClassNode node, String elementName)
	{
		return key(node.name, elementName);
	}

	public static String key(String className, String elementName)
	{
		return className + '#' + elementName;
	}

	public static String findSuperMethod(ClassNode node, MethodNode method, Map<String, ClassNode> classes)
	{
		final ClassNode superClassNode = classes.get(node.superName);
		if (superClassNode != null)
		{
			for (final MethodNode methodNode : superClassNode.methods)
			{
				if (methodNode.name.equals(method.name))
					return key(superClassNode, method);
			}
		}

		for (final String anInterface : node.interfaces)
		{
			final ClassNode interfaceNode = classes.get(anInterface);
			if (interfaceNode == null)
				continue;

			for (final MethodNode methodNode : interfaceNode.methods)
			{
				if (methodNode.name.equals(method.name))
					return key(interfaceNode, method);
			}
		}

		return null;
	}

	public static String repackage(String pkg, String className)
	{
		final String classSimpleName = StrUtil.classSimpleName(className);
		return pkg + "/" + classSimpleName;
	}

	public static Type repackageMethod(String pkg, Type type)
	{
		final Type returnType = type.getReturnType();
		final String returnTypeName = returnType.getClassName().replace('.', '/');
		final Type mappedReturnType = Type.getType(pkg + '/' + StrUtil.classSimpleName(returnTypeName));

		final Type[] argumentTypes = type.getArgumentTypes();
		for (int i = 0; i < argumentTypes.length; i++)
		{
			final String argumentTypeName = argumentTypes[i].getClassName().replace('.', '/');
			argumentTypes[i] = Type.getType(pkg + '/' + argumentTypeName);
		}

		final Type mappedType = Type.getMethodType(mappedReturnType != null ? mappedReturnType : returnType, argumentTypes);

		return type.equals(mappedType) ? null : mappedType;
	}

	public static boolean isNameIgnored(String name)
	{
		return name.equals("<init>") || name.equals("<clinit>") || name.equals("main") || name.equals("this");
	}

	public static boolean isSkipAnnotated(List<AnnotationNode> annotations)
	{
		if (annotations == null)
			return false;

		for (final AnnotationNode annotation : annotations)
		{
			if (annotation.desc.substring(1, annotation.desc.length() - 1)
					.replace('/', '.')
					.equals(SkipObfuscation.class.getName()))
				return true;
		}

		return false;
	}

	public static String genName(int len)
	{
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++)
			sb.append((char)NAME_CHARS[RANDOM.nextInt(NAME_CHARS.length)]);

		return sb.toString();
	}

	public static int genChars(int limit)
	{
		final IntStream.Builder chars = IntStream.builder();

		for (int i = Character.MIN_CODE_POINT; i <= limit; i++)
		{
			if (Character.isJavaIdentifierStart(i) && !Character.isSurrogate((char)i))
				chars.accept(i);
		}

		NAME_CHARS = chars.build().toArray();
		return NAME_CHARS.length;
	}
}
