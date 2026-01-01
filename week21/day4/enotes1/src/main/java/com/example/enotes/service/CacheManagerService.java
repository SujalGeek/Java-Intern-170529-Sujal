package com.example.enotes.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.cache.Cache;

public interface CacheManagerService {

	public Collection<String> getCache();
	
//	public Cache getCacheName(String cacheName);
	
	public void removeAllCache();
	
	public void removeCacheByName(List<String> cacheNames);
	
	public Map<String, Object> getCacheDetails(String cacheName);
}
