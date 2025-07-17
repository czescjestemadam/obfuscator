package czescjestemadas.obfuscator;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ObfuscatorSettings
{
	// remove
	@Builder.Default
	private final boolean removeDebugInfo = true;
	private final boolean fieldFinalRemove;

	// values
	private final boolean inline;
	private final boolean strings;
	private final boolean numbers;
	private final boolean booleans;

	// names
	@Builder.Default
	private final int namesCharLimit = Character.MAX_CODE_POINT;

	@Builder.Default
	private final String packageName = "czescjestemadas";
	@Builder.Default
	private final int classNameLength = 24;
	@Builder.Default
	private final int fieldNameLength = 16;
	@Builder.Default
	private final int methodNameLength = 32;

	@Singular
	private final List<String> skippedNames;
	@Singular
	private final List<String> packagePrefixes;


	// code
	@Builder.Default
	private final boolean fieldShuffle = true;
	@Builder.Default
	private final boolean methodShuffle = true;

	private final boolean junkCodeGen;
	@Builder.Default
	private final int junkCodeNodesMin = 32;
	@Builder.Default
	private final int junkCodeNodesMax = 64;
	@Builder.Default
	private final int junkCodeFieldsMin = 8;
	@Builder.Default
	private final int junkCodeFieldsMax = 16;
	@Builder.Default
	private final int junkCodeMethodsMin = 16;
	@Builder.Default
	private final int junkCodeMethodsMax = 24;

	private final String signature;
}
