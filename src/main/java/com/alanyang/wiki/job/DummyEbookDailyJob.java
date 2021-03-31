package com.alanyang.wiki.job;

import com.alanyang.wiki.service.EbookDummyService;
import com.alanyang.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DummyEbookDailyJob {

    private static final Logger LOG = LoggerFactory.getLogger(DummyEbookDailyJob.class);

    @Resource
    private EbookDummyService ebookDummyService;

    @Resource
    private SnowFlake snowFlake;

    /**
     * 自定义cron表达式跑批
     * 只有等上一次执行完成，下一次才会在下一个时间点执行，错过就错过
     */
//五分鐘一次
    @Scheduled(cron = "0 5 0/1 1/1 * ?")
    public void doDummy() {
        // 增加日志流水号
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("Generate today ebook dummy data");
        Long start = System.currentTimeMillis();
        ebookDummyService.genDummy();
        LOG.info("Generate today ebook dummy data end，Elapsed time：{}ms", System.currentTimeMillis() - start);
    }

}
