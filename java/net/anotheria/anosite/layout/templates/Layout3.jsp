<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="/tags/struts-bean" prefix="bean"
%><%@ taglib uri="/tags/struts-html" prefix="html"
%><%@ taglib uri="/tags/struts-logic" prefix="logic"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">

<!--  Version: Multiflex-3 Update-7 / Layout-3             -->
<!--  Date:    January 15, 2007                            -->
<!--  Author:  Wolfgang                                    -->
<!--  License: Fully open source without restrictions.     -->
<!--           Please keep footer credits with a link to   -->
<!--           Wolfgang (www.1-2-3-4.info). Thank you!     -->

<head>
<jsp:include page="MultiflexMetas.jsp"/>
</head>

<!-- Global IE fix to avoid layout crash when single word size wider than column width -->
<!--[if IE]><style type="text/css"> body {word-wrap: break-word;}</style><![endif]-->

<body>
  <!-- Main Page Container -->
  <div class="page-container">
 	<!-- START HEADER here -->
	<jsp:include page="MultiflexHeader.jsp"/>
  	<!-- END HEADER here -->
    <!-- B. MAIN -->
    <div class="main">
      	<!-- B.1 MAIN CONTENT -->
      	<div class="main-content">        
        	<!-- Pagetitle -->
	        <h1 class="pagetitle"><bean:write name="page" scope="request" property="title"/></h1>
        	<!-- Content unit - One column -->
			<!-- Rendering column 2 -->
			<bean:define id="__boxlist" toScope="request" name="page" property="column1"/>
			<jsp:include page="ListIterator.jsp" flush="false"/>
      	</div>
      	<!-- B.2 MAIN NAVIGATION -->
      	<div class="main-navigation">
			<!-- Rendering column 1 -->
			<bean:define id="__boxlist" toScope="request" name="page" property="column2"/>
			<jsp:include page="ListIterator.jsp" flush="false"/>
    	</div>
	</div>      
    <!-- C. FOOTER AREA -->      
    <div class="footer">
      <p>Copyright &copy; 2007 Your Company | All Rights Reserved</p>
      <p class="credits">Design by <a href="http://www.1-2-3-4.info/" title="Designer Homepage">Wolfgang</a>  | Modified by <a href="#" title="Modifyer Homepage">Your Name</a> | Powered by <a href="#" title="CMS Homepage">Your CMS</a> | <a href="http://validator.w3.org/check?uri=referer" title="Validate XHTML code">XHTML 1.0</a> | <a href="http://jigsaw.w3.org/css-validator/" title="Validate CSS code">CSS 2.0</a></p>
    </div>      
  </div> 

  <bean:define id="__boxlist" toScope="request" name="page" property="footerBoxes"/>
  <jsp:include page="ListIterator.jsp" flush="false"/>
</body>
</html>



