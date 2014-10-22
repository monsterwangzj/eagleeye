package com.chengyi.eagleeye.patrol;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.beans.factory.annotation.Autowired;

public class CacheDaoImpl {
	@Autowired
	private Cache simpleCache;
	
	@Autowired
	private CacheManager ehcacheManager;
	
	public void init() {
		System.out.println(simpleCache);
		System.out.println("size:" + simpleCache.getSize());

		String key = "aaa";
		Element value = simpleCache.get(key);
		if (value == null) {
			Element element = new Element(key, "111=" + System.currentTimeMillis());
			simpleCache.put(element);
			simpleCache.flush();
			
			value = simpleCache.get(key);
			System.out.println("now value:" + value + ", " + value.getObjectValue());
		} else {
			System.out.println(value.getObjectValue());
		}
//		ehcacheManager.shutdown();
//		
//		System.out.println(simpleCache);
//		System.out.println("size:" + simpleCache.getSize());
//		System.out.println("simpleCache.get(key);:" + simpleCache.get(key) );
	}

}
