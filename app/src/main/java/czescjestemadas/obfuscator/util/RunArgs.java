package czescjestemadas.obfuscator.util;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@ToString
public class RunArgs
{
	private final Set<String> flags = new HashSet<>();
	private final Map<String, String> args = new HashMap<>();

	public RunArgs(String[]	mainArgs)
	{

	}

	public boolean hasFlag(String name)
	{
		return flags.contains(name);
	}

	public String getArg(String name)
	{
		return args.get(name);
	}
}
