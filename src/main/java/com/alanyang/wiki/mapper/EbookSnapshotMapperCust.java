package com.alanyang.wiki.mapper;

import com.alanyang.wiki.resp.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCust {


    public void genSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}
