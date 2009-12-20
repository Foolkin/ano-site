package net.anotheria.anosite.api.util.paging;

public class PagingLink extends PagingElement{
	
	private String pagingParameter;
	private String caption;
	
	public PagingLink(String aCaption, int pageNumber){
		caption = aCaption;
		pagingParameter = ""+pageNumber;
	}

	@Override
	public String getPagingParameter() {
		return pagingParameter;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isLinked() {
		return true;
	}

	@Override
	public boolean isSeparator() {
		return false;
	}
	
	@Override
	public String getCaption(){
		return caption;
	}
	
	@Override
	public String toString(){
		return "caption:"+caption+", pagingParameter:"+pagingParameter;
	}
}
