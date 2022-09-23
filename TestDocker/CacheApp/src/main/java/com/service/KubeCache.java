package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service

public class KubeCache {

        private CacheManager cacheManager;

        private Cache quoteIdCache;

    private static final String QUOTE_ID_CACHE_STRING = "quoteIdCache";
    @Autowired
    public KubeCache(CacheManager cacheManager) {

        this.cacheManager = cacheManager;
    }

    @PostConstruct
    private void initCacheManager() {
        try {

            quoteIdCache = this.cacheManager.getCache(QUOTE_ID_CACHE_STRING);

            if (quoteIdCache == null) {
                //log.error("[FalconCache][FalconCache] QuoteIdCache not configured");
            }


        } catch (Exception ex) {
            //log.error("[FalconCache][FalconCache] Some problem in starting falcon cache..." + ex.getMessage());
            //ex.printStackTrace();
        }
    }
}
