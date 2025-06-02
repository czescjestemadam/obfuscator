package czescjestemadas.obfuscator;

import czescjestemadas.obfuscator.consumer.ClassConsumer;
import czescjestemadas.obfuscator.consumer.generator.*;
import czescjestemadas.obfuscator.consumer.mapper.*;
import czescjestemadas.obfuscator.consumer.transformer.*;
import czescjestemadas.obfuscator.consumer.transformer.field.ClassFieldTransform;
import czescjestemadas.obfuscator.consumer.transformer.field.FieldFinalRemoveTransform;
import czescjestemadas.obfuscator.consumer.transformer.field.FieldShuffleTransform;
import czescjestemadas.obfuscator.consumer.transformer.method.ClassMethodTransform;
import czescjestemadas.obfuscator.consumer.transformer.method.MethodLineNumberTransform;
import czescjestemadas.obfuscator.consumer.transformer.method.MethodLocalVarTransform;
import czescjestemadas.obfuscator.consumer.transformer.method.MethodShuffleTransform;
import czescjestemadas.obfuscator.consumer.transformer.value.ValueBoolTransform;
import czescjestemadas.obfuscator.consumer.transformer.value.ValueInlineTransform;
import czescjestemadas.obfuscator.consumer.transformer.value.ValueNumberTransform;
import czescjestemadas.obfuscator.consumer.transformer.value.ValueStringTransform;
import czescjestemadas.obfuscator.util.JarUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Obfuscator
{
	public static final Logger LOGGER = LoggerFactory.getLogger(Obfuscator.class);
	
	private final Map<String, ClassNode> classes = new HashMap<>();
	private final Mappings mappings = new Mappings();

	private final ObfuscatorSettings settings;

	private final List<ClassConsumer> consumers = new ArrayList<>();

	public Obfuscator(ObfuscatorSettings settings)
	{
		this.settings = settings;

		initMappers();
		initTransformers();
		initGenerators();
	}

	private void initMappers()
	{
		consumers.add(new ClassLoadMap());

		if (settings.getClassNameLength() > 0)
			consumers.add(new ClassNameMap());

		if (settings.getFieldNameLength() > 0)
			consumers.add(new ClassFieldMap());

		if (settings.getMethodNameLength() > 0)
		{
			consumers.add(new ClassMethodMap());
			consumers.add(new ClassMethodOverrideMap());
		}
	}

	private void initTransformers()
	{
		if (settings.isRemoveDebugInfo())
		{
			consumers.add(new ClassSourceTransform());
			consumers.add(new MethodLineNumberTransform());
			consumers.add(new MethodLocalVarTransform());
		}

		if (settings.isFieldFinalRemove())
			consumers.add(new FieldFinalRemoveTransform());

		if (settings.isInline())
			consumers.add(new ValueInlineTransform());

		if (settings.isStrings())
			consumers.add(new ValueStringTransform());

		if (settings.isNumbers())
			consumers.add(new ValueNumberTransform());

		if (settings.isBooleans())
			consumers.add(new ValueBoolTransform());

		if (settings.getFieldNameLength() > 0)
			consumers.add(new ClassFieldTransform());

		if (settings.getMethodNameLength() > 0)
			consumers.add(new ClassMethodTransform());

		if (settings.getClassNameLength() > 0)
			consumers.add(new ClassNameTransform());

		if (settings.getPackageName() != null)
			consumers.add(new ClassPackageTransform());

		if (settings.isFieldShuffle())
			consumers.add(new FieldShuffleTransform());

		if (settings.isMethodShuffle())
			consumers.add(new MethodShuffleTransform());
	}

	private void initGenerators()
	{
		if (settings.isJunkCodeGen())
			consumers.add(new JunkCodeGen());

		if (settings.getSignature() != null)
			consumers.add(new SignatureGen());
	}

	/**
	 * Generates mappings and obfuscates file when {@code outputFile != null}
	 * @param file input file
	 * @param outputFile output file path, when {@code null} generates mappings only
	 */
	public void obfuscate(File file, Path outputFile)
	{
		if (!file.exists())
		{
			LOGGER.error("File does not exist: {}", file.getAbsolutePath());
			return;
		}

		if (file.isDirectory())
		{
			LOGGER.error("File is a directory: {}", file);
			return;
		}

		LOGGER.info("\n");
		if (outputFile == null)
			LOGGER.info("== Generating mappings from {} ==", file);
		else
			LOGGER.info("== Starting obfuscation on {} -> {} ==", file, outputFile);

		final String filename = file.getName();

		if (filename.endsWith(".jar"))
		{
			if (outputFile != null)
				outputFile.getParent().toFile().mkdirs();

			try (final JarFile jar = new JarFile(file);
				 final JarOutputStream output = outputFile != null ? new JarOutputStream(Files.newOutputStream(outputFile)) : null)
			{
				obfuscateJar(jar, output);
			}
			catch (IOException e)
			{
				LOGGER.error("Failed to read jar file: {}", file.getAbsolutePath(), e);
			}
		}
		else
			LOGGER.warn("Unsupported file type, skipping");
	}

	private void obfuscateJar(JarFile jar, JarOutputStream output) throws IOException
	{
		final Map<JarEntry, byte[]> entries = JarUtil.read(jar);

		final Map<JarEntry, ClassNode> nodes = new HashMap<>();

		// add .class to nodes
		for (final Map.Entry<JarEntry, byte[]> entry : entries.entrySet())
		{
			final JarEntry jarEntry = entry.getKey();
			final byte[] bytes = entry.getValue();

			if (!jarEntry.getName().endsWith(".class"))
				continue;

			final ClassNode node = new ClassNode();
			final ClassReader reader = new ClassReader(bytes);
			reader.accept(node, 0);

			nodes.put(jarEntry, node);
		}

		// clear .class from main entries
		for (final JarEntry entry : nodes.keySet())
			entries.remove(entry);

		final List<ClassNode> extraNodes = new ArrayList<>();

		// run only generators if mappings only, otherwise run all
		runConsumers(nodes.values(), extraNodes, output == null ? ClassConsumer.mapperPredicate() : ClassConsumer.all());

		// abort if mappings only
		if (output == null)
			return;

		final Consumer<ClassNode> nodeCollector = node -> {
			final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			node.accept(writer);

			entries.put(new JarEntry(node.name + ".class"), writer.toByteArray());
		};

		// re-add transformed .class from nodes to main entries
		nodes.values().forEach(nodeCollector);

		// add generated extra nodes to main entries
		extraNodes.forEach(nodeCollector);

		// write everything back to .jar
		for (final Map.Entry<JarEntry, byte[]> entry : entries.entrySet())
		{
			final JarEntry jarEntry = entry.getKey();
			final byte[] bytes = entry.getValue();

			if (jarEntry.getName().equals("plugin.yml"))
				JarUtil.writeEntry(output, jarEntry, obfuscatePluginYml(new String(bytes)).getBytes());
			else
				JarUtil.writeEntry(output, jarEntry, bytes);
		}
	}

	private void runConsumers(Collection<ClassNode> nodes, List<ClassNode> extraNodes, Predicate<ClassConsumer> filter)
	{
		final Set<ClassNode> ignored = new HashSet<>();

		for (final ClassConsumer consumer : consumers)
		{
			LOGGER.info("Running {}...", consumer.getClass().getSimpleName());

			if (!filter.test(consumer))
				continue;

			for (final ClassNode node : nodes)
			{
				if (ignored.contains(node))
					continue;

				final boolean ignore = consumer.run(node, classes, mappings, settings);
				if (ignore)
				{
					if (ignored.add(node))
						LOGGER.info("Ignoring class {}", node.name);
				}
			}

			if (consumer instanceof ClassGenerator classGenerator)
				extraNodes.addAll(classGenerator.generateNodes(settings));
		}
	}

	private String obfuscatePluginYml(String str)
	{
		final String[] lines = str.split("\\n");

		for (int i = 0; i < lines.length; i++)
		{
			if (lines[i].startsWith("main: "))
			{
				final String mainClass = lines[i].substring(6);
				final String mappedMainClass = mappings.getClassMapping(mainClass);
				if (mappedMainClass == null)
					break;

				lines[i] = "main: " + mappedMainClass;
				break;
			}

			LOGGER.info("  {}", lines[i]);
		}

		return String.join("\n", lines);
	}
}
