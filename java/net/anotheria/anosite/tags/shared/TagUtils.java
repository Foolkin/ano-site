package net.anotheria.anosite.tags.shared;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.util.ResponseUtils;

public class TagUtils {

	private static Logger log = Logger.getLogger(TagUtils.class);
	
	private static enum Scope{
		/**
		 * page scope.
		 */
		page(PageContext.PAGE_SCOPE),
		/**
		 * request scope.
		 */
		request(PageContext.REQUEST_SCOPE),
		/**
		 * session scope.
		 */
		session(PageContext.SESSION_SCOPE),
		/**
		 * application scope.
		 */
		application(PageContext.APPLICATION_SCOPE);
		/**
		 * Corresponding constant value in PageContext.
		 */
		private int pageContextScope;
		
		private Scope(int aPageContextScope){
			pageContextScope = aPageContextScope;
		}
		
		public int getPageContextScope(){
			return pageContextScope;
		}
		
		
		
	}
	
	public static int getScope(String scopeName) throws JspException {
		return Scope.valueOf(scopeName).getPageContextScope();
    }
	
	
	public static Object lookup(PageContext pageContext, String scopeName, String beanName, String propertyName, String subPropertyName) throws JspException {
		
		Object bean = lookup(pageContext, scopeName, beanName);
		if (bean == null) {
			return null;
		}
        
		if (propertyName == null) {
			return bean;
		}

		try {
			Object property = PropertyUtils.getProperty(bean, propertyName);
			
			if(subPropertyName == null) {
				return property;
			}
			
			try {
				return PropertyUtils.getProperty(property, subPropertyName);
			} catch (Exception e) {
				log.error(e,e);
				throw new JspException("Could not read " + beanName + "." + propertyName + "." + subPropertyName, e);
			} 
		} catch (Exception e) {
			log.error(e,e);
			throw new JspException("Could not read " + beanName + "." + propertyName, e);
		} 
	}
	
	public static Object lookup(PageContext pageContext, String scopeName, String aBeanName) throws JspException {
		String beanName = aBeanName;
		if(beanName == null) {
			beanName = "box";
		}
		
		if (scopeName == null) {
			Object bean = pageContext.findAttribute(beanName);
			if(bean == null && log.isDebugEnabled()) {
				log.debug("Did not find " + beanName + " in any scope.");
			}
			return bean;
		}

		Object bean = pageContext.getAttribute(beanName, getScope(scopeName));
		if(bean == null && log.isDebugEnabled()) {
			log.debug("Did not find " + beanName + " in scope " + scopeName);
		}
		return bean;
	}
	
	/**
	 * Puts an attribute into target scope.
	 * @param pageContext
	 * @param aScope
	 * @param anObjectName
	 * @param anBean
	 * @throws JspException
	 */
	public static void putAttribute(PageContext pageContext, String aScope, String anObjectName, Object anBean) throws JspException {
		if(aScope == null || aScope.length() == 0)
			aScope = "page";
		pageContext.setAttribute(anObjectName, anBean, getScope(aScope));
	}
	
	/**
	 * Writes a string to the page.
	 * @param pageContext
	 * @param s
	 * @throws JspException
	 */
	protected static void write(PageContext pageContext, String s) throws JspException{
		ResponseUtils.write(pageContext, s);		
	}
	
	/**
	 * Writes a string to the page and appends an empty line.
	 * @param pageContext
	 * @param s
	 * @throws JspException
	 */
	protected static void writeLn(PageContext pageContext, String s) throws JspException{
		write(pageContext, s+"\n");		
	}

	/**
	 * Quotes a string with double quotes.
	 * @param s
	 * @return
	 */
	protected static String quote(String s){
		return "\""+s+"\"";
	}

}

