package com.example.enotes.serviceImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.example.enotes.service.CacheManagerService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheManagerServiceImpl  implements CacheManagerService{

	@Autowired
	private CacheManager cacheManager;
	
	@Override
	public Collection<String> getCache() {
		Collection<String> cacheNames = cacheManager.getCacheNames();
		for(String cacheName: cacheNames)
		{
			Cache cache = cacheManager.getCache(cacheName);
			log.info("Cache Name={}",cache);
		}
		return cacheNames;
	}

//	@Override
//	public Cache getCacheName(String cacheName) {
//		Cache cache = cacheManager.getCache(cacheName);
//		log.info("Cache Name={}",cache);
//		return cache;
//	}

	@Override
	public void removeAllCache() {
		Collection<String> cacheNames = cacheManager.getCacheNames();
		for(String cacheName: cacheNames)
		{
			Cache cache = cacheManager.getCache(cacheName);
			log.info("Cache Name={}",cache);
			cache.clear();
		}
		
	}

	@Override
	public void removeCacheByName(List<String> cacheNames) {
		for(String cacheName: cacheNames)
		{
			Cache cache = cacheManager.getCache(cacheName);
			log.info("Cache Name={}",cache);
			cache.clear();
		}
	}
	
	// inside ServiceImpl
	@Override
	public Map<String, Object> getCacheDetails(String cacheName) {
	    Cache cache = cacheManager.getCache(cacheName);
	    if (cache != null) {
	        Map<String, Object> details = new HashMap<>();
	        details.put("name", cache.getName());
	        details.put("nativeCacheType", cache.getNativeCache().getClass().getName());
	        
	        // NEW CODE: Check if we can open the box and see the data
	        if (cache.getNativeCache() instanceof Map) {
	            Map<?, ?> nativeMap = (Map<?, ?>) cache.getNativeCache();
	            details.put("data", nativeMap); //  This puts the actual list in the JSON!
	        }
	        return details;
	    }
	    return null;
	}
}
