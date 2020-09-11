package com.yang.mapper;

import com.yang.pojo.SearchRecords;
import com.yang.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

    List<String> getHotRecords();
}