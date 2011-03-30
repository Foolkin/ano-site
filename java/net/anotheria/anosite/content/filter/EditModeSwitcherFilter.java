package net.anotheria.anosite.content.filter;

import net.anotheria.anosite.gen.shared.service.AnoDocConfigurator;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static net.anotheria.anosite.util.AnositeConstants.*;

/**
 * This filter is used to switch the on-site edit mode on and off. To perform this task it checks whether the switch mode parameter is present.
 * @author another
 *
 */
public class EditModeSwitcherFilter implements Filter{

	@Override public void destroy() {
		
	}

	@Override public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException {
		if (!(sreq instanceof HttpServletRequest))
			return;
		HttpServletRequest req = (HttpServletRequest)sreq;
		req.setCharacterEncoding(AnoDocConfigurator.getEncoding());
		
		String p = req.getParameter(PARAM_SWITCH_MODE);
		if (p!=null && p.length()>0){
			if (p.equals(PARAM_VALUE_EDIT_MODE))
				req.getSession().setAttribute(SA_EDIT_MODE_FLAG, Boolean.TRUE);
			if (p.equals(PARAM_VALUE_VIEW_MODE))
				req.getSession().removeAttribute(SA_EDIT_MODE_FLAG);
		}
		
		chain.doFilter(sreq, sres);
	}

	@Override public void init(FilterConfig arg0) throws ServletException {
		
	}

}
