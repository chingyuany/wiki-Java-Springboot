package com.alanyang.wiki.job;

import com.alanyang.wiki.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);
    @Resource
    private DocService docService;

    /**
     * 每30秒執行 從第五秒開始  5,35,65..
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {
        LOG.info("update ebooks' doc number start");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("update ebooks' doc number end，cost：{}ms", System.currentTimeMillis() - start);
    }

}
