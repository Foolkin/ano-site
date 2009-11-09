<%@ page
	contentType="text/html;charset=UTF-8" session="true"
%><%@ taglib uri="/tags/struts-bean" prefix="bean"
%><%@ taglib uri="/tags/struts-html" prefix="html"
%><%@ taglib uri="/tags/struts-logic" prefix="logic"
%><%@ page isELIgnored ="false" %>

<div id="filelist">
	<logic:iterate id="image" name="asImageResources" type="net.anotheria.anosite.content.bean.ImageResourceBean" >
	<div style="width: 100px;" class="file image thumbnail" id="img_${image.id}">
				<div class="wrap">
				<a style="height: 100px;" class="pic" rel="file" title="${image.title}" href="#action">
					<div class="mid"><div class="mid2"><img class="thumbnailimage" style="width: 68px; height: 90px;" alt="${image.link}" src="${image.link}"/></div></div></a>
				</div>
				<div class="details">
					<div class="wrap2">
						<div title="${image.name}" style="width: 74px;" class="name">${image.name}</div>
					</div>
				</div>
	</div>
	</logic:iterate>
</div>

<bean:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"/>
<logic:notEqual name="box" property="content" value="">
	<bean:write name="box" property="content" filter="false"/>
</logic:notEqual>
<bean:define id="__boxlist" toScope="request" name="box" property="subboxes"/>
<jsp:include page="../templates/ListIterator.jsp" flush="false"/>