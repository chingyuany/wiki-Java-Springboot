package com.alanyang.wiki.job;

import com.alanyang.wiki.service.DocService;
import com.alanyang.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);
    @Resource
    private DocService docService;
    @Resource
    private SnowFlake snowFlake;
    /**
     * 每30秒執行 從第五秒開始  5,35,65..
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {
        // 增加日志流水号
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("update ebooks' doc number start");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("update ebooks' doc number end，cost：{}ms", System.currentTimeMillis() - start);
    }

}
