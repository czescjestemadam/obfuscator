package czescjestemadas.obfuscator.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

@Slf4j
public abstract class JarUtil
{
	public static Map<JarEntry, byte[]> read(JarFile jar)
	{
		final Map<JarEntry, byte[]> entries = new HashMap<>();

		final Enumeration<JarEntry> jarEntryEnumeration = jar.entries();
		while (jarEntryEnumeration.hasMoreElements())
		{
			final JarEntry entry = jarEntryEnumeration.nextElement();

			if (entry.isDirectory())
				continue;

			try (final InputStream inputStream = jar.getInputStream(entry))
			{
				entries.put(entry, inputStream.readAllBytes());
			}
			catch (IOException e)
			{
				log.error("Cannot get input stream from jar entry {}", entry, e);
			}
		}

		return entries;
	}

	public static void writeEntry(JarOutputStream output, JarEntry entry, byte[] bytes)
	{
		try
		{
			output.putNextEntry(entry);
			output.write(bytes);
			output.closeEntry();
		}
		catch (IOException e)
		{
			log.error("Failed to write entry: {}", entry, e);
		}
	}
}
