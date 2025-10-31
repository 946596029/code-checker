// Generated from MdLexer.g4 by ANTLR 4.13.2
 package org.example.code.checker.parser.markdown.parser; 
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MdLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		HEADER=1, INDENT=2, TAB=3, BLANK=4, SPACE_TOK=5, DASH=6, STAR=7, PLUS=8, 
		NEWLINE=9, TEXT=10;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"HEADER", "INDENT", "TAB", "BLANK", "SPACE_TOK", "DASH", "STAR", "PLUS", 
			"NEWLINE", "TEXT", "DIGITS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, "'-'", "'*'", "'+'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "HEADER", "INDENT", "TAB", "BLANK", "SPACE_TOK", "DASH", "STAR", 
			"PLUS", "NEWLINE", "TEXT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MdLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MdLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\nl\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0003\u00011\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0003\u0002>\b\u0002\u0001\u0002\u0001\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0003\u0003K\b\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0003\b_\b\b\u0001\b\u0001"+
		"\b\u0001\t\u0004\td\b\t\u000b\t\f\te\u0001\n\u0004\ni\b\n\u000b\n\f\n"+
		"j\u0000\u0000\u000b\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t"+
		"\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u0000\u0001\u0000"+
		"\u0002\u0002\u0000\n\n\r\r\u0001\u000009p\u0000\u0001\u0001\u0000\u0000"+
		"\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000"+
		"\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000"+
		"\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000"+
		"\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000"+
		"\u0013\u0001\u0000\u0000\u0000\u0001\u0017\u0001\u0000\u0000\u0000\u0003"+
		"#\u0001\u0000\u0000\u0000\u00056\u0001\u0000\u0000\u0000\u0007A\u0001"+
		"\u0000\u0000\u0000\tN\u0001\u0000\u0000\u0000\u000bW\u0001\u0000\u0000"+
		"\u0000\rY\u0001\u0000\u0000\u0000\u000f[\u0001\u0000\u0000\u0000\u0011"+
		"^\u0001\u0000\u0000\u0000\u0013c\u0001\u0000\u0000\u0000\u0015h\u0001"+
		"\u0000\u0000\u0000\u0017\u0018\u0005<\u0000\u0000\u0018\u0019\u0005h\u0000"+
		"\u0000\u0019\u001a\u0005e\u0000\u0000\u001a\u001b\u0005a\u0000\u0000\u001b"+
		"\u001c\u0005d\u0000\u0000\u001c\u001d\u0005e\u0000\u0000\u001d\u001e\u0005"+
		"r\u0000\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f \u0005:\u0000"+
		"\u0000 !\u0003\u0015\n\u0000!\"\u0005>\u0000\u0000\"\u0002\u0001\u0000"+
		"\u0000\u0000#0\u0005<\u0000\u0000$%\u0005I\u0000\u0000%&\u0005N\u0000"+
		"\u0000&\'\u0005D\u0000\u0000\'(\u0005E\u0000\u0000()\u0005N\u0000\u0000"+
		")1\u0005T\u0000\u0000*+\u0005i\u0000\u0000+,\u0005n\u0000\u0000,-\u0005"+
		"d\u0000\u0000-.\u0005e\u0000\u0000./\u0005n\u0000\u0000/1\u0005t\u0000"+
		"\u00000$\u0001\u0000\u0000\u00000*\u0001\u0000\u0000\u000012\u0001\u0000"+
		"\u0000\u000023\u0005:\u0000\u000034\u0003\u0015\n\u000045\u0005>\u0000"+
		"\u00005\u0004\u0001\u0000\u0000\u00006=\u0005<\u0000\u000078\u0005T\u0000"+
		"\u000089\u0005A\u0000\u00009>\u0005B\u0000\u0000:;\u0005t\u0000\u0000"+
		";<\u0005a\u0000\u0000<>\u0005b\u0000\u0000=7\u0001\u0000\u0000\u0000="+
		":\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?@\u0005>\u0000\u0000"+
		"@\u0006\u0001\u0000\u0000\u0000AB\u0005<\u0000\u0000BC\u0005b\u0000\u0000"+
		"CD\u0005l\u0000\u0000DE\u0005a\u0000\u0000EF\u0005n\u0000\u0000FG\u0005"+
		"k\u0000\u0000GJ\u0001\u0000\u0000\u0000HI\u0005:\u0000\u0000IK\u0003\u0015"+
		"\n\u0000JH\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000KL\u0001\u0000"+
		"\u0000\u0000LM\u0005>\u0000\u0000M\b\u0001\u0000\u0000\u0000NO\u0005<"+
		"\u0000\u0000OP\u0005s\u0000\u0000PQ\u0005p\u0000\u0000QR\u0005a\u0000"+
		"\u0000RS\u0005c\u0000\u0000ST\u0005e\u0000\u0000TU\u0001\u0000\u0000\u0000"+
		"UV\u0005>\u0000\u0000V\n\u0001\u0000\u0000\u0000WX\u0005-\u0000\u0000"+
		"X\f\u0001\u0000\u0000\u0000YZ\u0005*\u0000\u0000Z\u000e\u0001\u0000\u0000"+
		"\u0000[\\\u0005+\u0000\u0000\\\u0010\u0001\u0000\u0000\u0000]_\u0005\r"+
		"\u0000\u0000^]\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_`\u0001"+
		"\u0000\u0000\u0000`a\u0005\n\u0000\u0000a\u0012\u0001\u0000\u0000\u0000"+
		"bd\b\u0000\u0000\u0000cb\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000"+
		"ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000f\u0014\u0001\u0000"+
		"\u0000\u0000gi\u0007\u0001\u0000\u0000hg\u0001\u0000\u0000\u0000ij\u0001"+
		"\u0000\u0000\u0000jh\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000"+
		"k\u0016\u0001\u0000\u0000\u0000\u0007\u00000=J^ej\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}