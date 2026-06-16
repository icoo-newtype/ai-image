package com.aiimage.model;

import lombok.Data;

import java.util.List;

@Data
public class ListResult {
	private List<?> list;
	private Paging paging;

	public ListResult(List<?> list) {
		this.list = list;
		this.paging = new Paging(list.size());
	}

	public ListResult(List<?> list, int listCount) {
		this.list = list;
		this.paging = new Paging(listCount);
	}

	public ListResult(List<?> list, int listCount, ListParam listParam) {
		this.list = list;
		this.paging = new Paging(listCount, listParam);
	}

	@Data
	private static class Paging {
		final private int listCount;
		final private int pageCount;
		final private int pageNo;
		final private int perPage;

		private Paging(int listCount) {
			this.listCount = listCount;
			this.pageCount = 0;
			this.perPage = 0;
			this.pageNo = 1;
		}

		private Paging(int listCount, ListParam listParam) {
			this.listCount = listCount - listParam.getRecentOffset();
			this.perPage = listParam.getPerPage();
			this.pageNo = listParam.getPageNo();
			this.pageCount = perPage == 0 ? 1 : ((this.listCount - 1) / perPage) + 1;
		}
	}
}
