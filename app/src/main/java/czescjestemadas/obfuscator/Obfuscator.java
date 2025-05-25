package czescjestemadas.obfuscator;

import czescjestemadas.obfuscator.consumer.ClassConsumer;
import czescjestemadas.obfuscator.consumer.generator.*;
import czescjestemadas.obfuscator.consumer.transformer.ClassInsnTransform;
import czescjestemadas.obfuscator.consumer.transformer.ClassNameTransform;
import czescjestemadas.obfuscator.consumer.transformer.ClassPackageTransform;
import czescjestemadas.obfuscator.consumer.transformer.ClassSourceTransform;
import czescjestemadas.obfuscator.consumer.transformer.field.ClassFieldTransform;
import czescjestemadas.obfuscator.consumer.transformer.field.FieldInsnTransform;
import czescjestemadas.obfuscator.consumer.transformer.method.ClassMethodTransform;
import czescjestemadas.obfuscator.consumer.transformer.method.MethodInsnTransform;
import czescjestemadas.obfuscator.consumer.transformer.method.MethodLineNumberTransform;
import czescjestemadas.obfuscator.consumer.transformer.method.MethodLocalVarTransform;
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

		initGenerators();
		initTransformers();
	}

	private void initGenerators()
	{
		consumers.add(new ClassLoadGen());

		if (settings.getClassNameLength() > 0)
			consumers.add(new ClassNameGen());

		if (settings.getFieldNameLength() > 0)
			consumers.add(new ClassFieldGen());

		if (settings.getMethodNameLength() > 0)
		{
			consumers.add(new ClassMethodGen());
			consumers.add(new ClassMethodOverrideGen());
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

		if (settings.getFieldNameLength() > 0)
		{
			consumers.add(new ClassFieldTransform());
			consumers.add(new FieldInsnTransform());
		}

		if (settings.getMethodNameLength() > 0)
		{
			consumers.add(new ClassMethodTransform());
			consumers.add(new MethodInsnTransform());
		}

		if (settings.getPackageName() != null)
			consumers.add(new ClassPackageTransform());

		if (settings.getClassNameLength() > 0)
		{
			consumers.add(new ClassNameTransform());
			consumers.add(new ClassInsnTransform());
		}
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

		// run only generators if mappings only, otherwise run all
		runConsumers(nodes.values(), output == null ? ClassConsumer.generatorPredicate() : ClassConsumer.all());

		// abort if mappings only
		if (output == null)
			return;

		// re-add transformed .class from nodes to main entries
		for (final ClassNode node : nodes.values())
		{
			final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			node.accept(writer);

			entries.put(new JarEntry(node.name + ".class"), writer.toByteArray());
		}

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

	private void runConsumers(Collection<ClassNode> nodes, Predicate<ClassConsumer> filter)
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
