package net.anotheria.anosite.decorator;

import java.util.ArrayList;
import java.util.List;

import net.anotheria.anodoc.data.Document;
import net.anotheria.anodoc.data.NoSuchDocumentException;
import net.anotheria.anodoc.data.NoSuchPropertyException;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.gen.aswebdata.data.Box;
import net.anotheria.anosite.gen.aswebdata.data.Pagex;
import net.anotheria.anosite.gen.aswebdata.service.ASWebDataServiceException;
import net.anotheria.anosite.gen.aswebdata.service.IASWebDataService;
import net.anotheria.asg.data.DataObject;
import net.anotheria.asg.util.decorators.IAttributeDecorator;
import net.anotheria.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * Decorator for linked attributes.
 * @author lrosenberg
 *
 */
public class AttributeDecorator implements IAttributeDecorator{
	
	/**
	 * Instance of webdataservice for retrieval of box definitions. 
	 */
	private static IASWebDataService service;

	static {
		try {
			service = MetaFactory.get(IASWebDataService.class);
		} catch (MetaFactoryException e) {
			Logger.getLogger(AttributeDecorator.class).fatal("IASWebDataService asg service init failure",e);
		}
	}
	
	@Override public String decorate(DataObject doc, String attributeName, String rule) {
		try{
			
			//tmp hack
			List<Object> links = null;
			try{
				links = ((Document)doc).getListProperty(attributeName).getListData();
			}catch(NoSuchPropertyException e){
				links = new ArrayList<Object>(0);
			}
			List<String> ids = new ArrayList<String>(links.size());
			String value = ""+links.size()+" [";
			String linksValue = "";
			for (Object l : links){
				if (linksValue.length()>0)
					linksValue+=", ";
				linksValue+=l;
				ids.add(""+l);
			}
			value = value + linksValue+"]";
			
			String href = "#";
			
			if (doc instanceof Pagex){
				href = "pagex"+StringUtils.capitalize(attributeName)+"Show?ownerId="+doc.getId()+"&pId="+doc.getId()+"&ts="+System.currentTimeMillis();
			}
			if (doc instanceof Box){
				href = "aswebdataBoxAttributesShow?ownerId="+doc.getId()+"&pId="+doc.getId()+"&ts="+System.currentTimeMillis();
			}
			
			StringBuilder title = new StringBuilder("");
			for (String id : ids){
				String name ;
				try{
					name = service.getAttribute(id).getName();
				}catch(NoSuchDocumentException e){
					name = "*DELETED*";
				}catch(ASWebDataServiceException e){
					name = "*ERR-"+e.getMessage()+"*";
				}
				if (title.length()>0)
					title.append(", ");
				title.append(name);
					
			}

			String stitle = title.length()>0 ? " title=\""+title.toString()+"\"" : "";
			
			String hrefTarget = href.length()>1 ? " target=\"_blank\"" : "";
			
			return "<a href=\""+href+"\""+hrefTarget+stitle+">"+value+"</a>";
				
		}catch(NoSuchPropertyException e){
			return "none";
		}
	}

}
