package com.aiimage.mapper;

import com.aiimage.model.*;

import java.util.List;

public interface CodeMapper {

	List<CodeItem> getFullList(CodeItem param);

	List<CodeItem> getList(CodeParam param);

	Integer getListCount(CodeParam param);

	CodeItem detail(CodeParam param);

	int insertItem(CodeItem codeItem);

	int updateItem(CodeItem codeItem);

	int removeItem(CodeParam param);

	int checkItem(CodeItem codeItem);

	void setOrder(OrderParam param);
}
