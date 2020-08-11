package com.igteam.immersivegeology.client.guidebook.helper;

import java.util.HashMap;

public class IGGuideBookPageData {
	private String page_name;
	private int page_id;
	private String tooltip;
	private PageType page_type;
	private String page_desc;
	private PageTab page_tab;
	public HashMap<String, IGGuideLink> page_link_map = new HashMap<String, IGGuideLink>();;

	public IGGuideBookPageData() {};

	public PageTab getPage_tab() {
		return page_tab;
	}

	public void setPage_tab(PageTab page_tab) {
		this.page_tab = page_tab;
	}
	
	public String getPage_name() {
		return page_name;
	}

	public void setPage_name(String page_name) {
		this.page_name = page_name;
	}

	public int getPage_id() {
		return page_id;
	}

	public void setPage_id(int page_id) {
		this.page_id = page_id;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public PageType getPage_type() {
		return page_type;
	}

	public void setPage_type(PageType page_type) {
		this.page_type = page_type;
	}

	public String getPage_desc() {
		return page_desc;
	}

	public void setPage_desc(String page_desc) {
		this.page_desc = page_desc;
	}

}
