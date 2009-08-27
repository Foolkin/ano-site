 package net.anotheria.anosite.content.variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.anotheria.anosite.content.variables.element.ContentElement;
import net.anotheria.anosite.content.variables.element.DynamicElement;
import net.anotheria.anosite.content.variables.element.StaticElement;
import net.anotheria.util.StringUtils;

/**
 * Utility for replacement of expressions with variables in texts. An expression consists of three parts:
 * {prefix:name:defaultValue}. The prefix tells the variable utility which processor to use, the name and default value are submitted to the processor in question.
 * @author lrosenberg
 */
public class VariablesUtility {
	/**
	 * The default value if no 'fallback' default value is specified.
	 */
	public static final String DEFAULT_VALUE  = "*UNSET*";
	/**
	 * Line delimiter for line parsing. The Variables utility only supports expression in a single line.
	 */
	public static final char LINE_DELIMITER = '\n';
	/**
	 * Word delimiter.
	 */
	public static final char WORD_DELIMITER = ' ';
	/**
	 * A tag (expression) starts with this character.
	 */
	public static final char TAG_START = '{';
	/**
	 * A tag (expression) ends with this character.
	 */
	public static final char TAG_END = '}';
	
	/**
	 * Contant for quoting.
	 */
	public static final String QUOTE = "\"";
	/**
	 * Escape char for special characters in the expressions.
	 */
	public static final char ESCAPE_CHAR = '\\';
	
	
	/**
	 * Map with preconfigured processors.
	 */
	private static final Map<String, VariablesProcessor> defaultProcessors = new HashMap<String, VariablesProcessor>();
	
	static{
		defaultProcessors.put(DefinitionPrefixes.PREFIX_CALENDAR, new CalendarProcessor());
		defaultProcessors.put(DefinitionPrefixes.PREFIX_CONSTANT, new ConstantsProcessor());
		defaultProcessors.put(DefinitionPrefixes.PREFIX_PARAMETER, new ParameterProcessor());
		VariablesProcessor p = new AttributeProcessor();
		defaultProcessors.put(DefinitionPrefixes.PREFIX_REQUEST_ATTRIBUTE, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_SESSION_ATTRIBUTE, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_CONTEXT_ATTRIBUTE, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_API_CALL_CONTEXT_ATTRIBUTE, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_API_SESSION_ATTRIBUTE, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_BOX_ATTRIBUTE, p);
		
		defaultProcessors.put(DefinitionPrefixes.PREFIX_IMAGE_LINK, new ImageLinkProcessor());
		defaultProcessors.put(DefinitionPrefixes.PREFIX_FILE_LINK, new FileLinkProcessor());
		defaultProcessors.put(DefinitionPrefixes.PREFIX_IMAGE, new ImageProcessor());
		
		p = new ConditionProcessor();
		defaultProcessors.put(DefinitionPrefixes.PREFIX_IF, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_IF_NOT, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_PRESENT, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_NOT_PRESENT, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_EQUALS, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_NOT_EQUALS, p);
		defaultProcessors.put(DefinitionPrefixes.PREFIX_IN_RANGE, p);
        //ANO_SITE3
        defaultProcessors.put(DefinitionPrefixes.PREFIX_GREATERTHEN,p);
        defaultProcessors.put(DefinitionPrefixes.PREFIX_GREATEREQUAL,p);
        defaultProcessors.put(DefinitionPrefixes.PREFIX_LESSTHAN,p);
        defaultProcessors.put(DefinitionPrefixes.PREFIX_LESSEQUAL,p);


        //adding TextResourceProcessor
        defaultProcessors.put(TextResourceProcessor.PREFIX, new TextResourceProcessor());

	}
	

	/**
	 * Adds a processor to this variable utility.
	 * @param prefix prefix for processor mapping.
	 * @param processor processor.
	 */
	public static void addProcessor(String prefix, VariablesProcessor processor){
		defaultProcessors.put(prefix, processor);
	}

	public static Map<String, VariablesProcessor> getDefaultProcessors(){
		 HashMap<String, VariablesProcessor> ret = new HashMap<String, VariablesProcessor>();
		 ret.putAll(defaultProcessors);
		 return ret;
	}
	
	/**
	 * Replaces variable expressions in the given string src with help of default processors.
	 * @param req
	 * @param src
	 * @return
	 */
	public static String replaceVariables(HttpServletRequest req, String src){
		return replaceVariables(req, src, defaultProcessors);
	}
	
	/**
	 * Replaces variable expressions in the given string src with the help of submitted processors. Useful for variable customization.
	 * @param req
	 * @param src
	 * @param processors
	 * @return
	 */
	public static String replaceVariables(HttpServletRequest req, String src, Map<String,VariablesProcessor> processors){
		if(src == null || src.length() == 0)
			return src;
		List<ContentElement> index = indexSource(src);
		return replaceVariables(req, index, processors);
	}
	
	public static String replaceVariables(HttpServletRequest req, List<ContentElement> index, Map<String,VariablesProcessor> processors){
		StringBuilder ret = new StringBuilder();
		for(ContentElement el: index)
			ret.append(replaceContentElement(req, el, processors));
		return ret.toString();
	}

	public static List<ContentElement> indexSource(String src){
		String myS = StringUtils.removeChar(src, '\r');
		List<String> stringIndex = StringUtils.indexSuperTags(myS, '{', '}');
		List<ContentElement> ret = new ArrayList<ContentElement>(stringIndex.size());
		for(String s: stringIndex)
			ret.add(createContentElementInDynamic(s, '{', '}'));
		return ret;
	}

	
	private static ContentElement createContentElementInDynamic(String elementText, char dynamicTagStart, char dynamicTagEnd){
		if(elementText.charAt(0) != dynamicTagStart)
			return new StaticElement(elementText);
		String varName = StringUtils.strip(elementText, 1, 1);
		char c = varName.charAt(0);
		if (c=='\t' || c==' ' || c=='\n' || c=='\r')
			return new StaticElement(elementText);
		
		if (varName!=null){
			if (varName.startsWith(QUOTE) && varName.endsWith(QUOTE))
				return new StaticElement(StringUtils.strip(varName, 1, 1));
		}
		
		List<String> tokens = StringUtils.tokenize(varName, '{', '}', ':');
		if (tokens.size() < 2)
			return new StaticElement("Wrong format \""+varName+"\" expected: {prefix:varname[:default value]}");
		String prefix = tokens.get(0);
		String var = tokens.get(1);
		String defaultValue = tokens.size()>2? tokens.get(2):"";
//		System.out.println("Var:" + var);
		List<ContentElement> varIndex = indexSource(var);
//		System.out.println("VarIndex size:" + varIndex.size());
//		System.out.println("DefValue:" + defaultValue);
		List<ContentElement> defValueIndex = indexSource(defaultValue);
//		System.out.println("DefValueIndex size:" + defValueIndex.size());
		return new DynamicElement(elementText, prefix, varIndex, defValueIndex);
	}
	
	private static String replaceContentElement(HttpServletRequest req, ContentElement contentElement, Map<String,VariablesProcessor> processors){
		
//		System.out.println("Replace element[dinamic:"+contentElement.isDynamic()+"]: " + contentElement.getElementText());
		if(!contentElement.isDynamic())
			return contentElement.getElementText();
			
		DynamicElement dynIndex = (DynamicElement) contentElement;
		
		String prefix = dynIndex.getPrefix();
//		System.out.println("Prefix: " + prefix);
		String var = replaceVariables(req,dynIndex.getVariableIndex(),processors);
//		System.out.println("Var: " + var);
		String defaultValue = replaceVariables(req,dynIndex.getDefValueIndex(),processors);
//		System.out.println("DefValue: " + defaultValue);
		VariablesProcessor processor = processors.get(prefix);
		if (processor==null)
			return dynIndex.getElementText();
		String ret = processor.replace(prefix, var, defaultValue, req);
//		System.out.println("RET: " + ret);
		return ret;
	}
}
