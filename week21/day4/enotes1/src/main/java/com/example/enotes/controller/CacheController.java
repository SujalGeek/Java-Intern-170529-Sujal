package com.example.enotes.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.enotes.endpoint.CacheEndpoint;
import com.example.enotes.service.CacheManagerService;
import com.example.enotes.util.CommonUtil;

@RestController
public class CacheController implements CacheEndpoint{

	@Autowired
	private CacheManagerService cacheService;
	
	@Override
	public ResponseEntity<?> getAllCache() {
		Collection<String> cache = cacheService.getCache();
		return CommonUtil.createBuildResponse(cache, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getCache(String cache_name) {
		Map<String, Object> cacheDetails = cacheService.getCacheDetails(cache_name);
	    
	    if (cacheDetails == null) {
	        return CommonUtil.createBuildResponseMessage("Cache not found", HttpStatus.NOT_FOUND);
	    }
	    return CommonUtil.createBuildResponse(cacheDetails, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> removeAllCache() {
		cacheService.removeAllCache();
		return CommonUtil.createBuildResponseMessage("Remove all Cache", HttpStatus.OK);
	}

}
