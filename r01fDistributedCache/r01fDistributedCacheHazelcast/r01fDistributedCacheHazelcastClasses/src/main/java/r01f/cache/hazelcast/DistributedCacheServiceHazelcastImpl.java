package r01f.cache.hazelcast;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cache.DistributedCache;
import r01f.cache.DistributedCacheService;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;

/**
 * Distributed cache
 * Create:
 * <pre class='brush:java'>
 *		Config hzConfig = new Config();
 *		hzConfig.getNetworkConfig().setPort( 5900 )
 *		        .setPortAutoIncrement(false);
 *		DistributedCacheHazelcastConfig cacheCfg = DistributedCacheHazelcastConfig.createFrom(AppCode.forId("r01f"),AppComponent.forId("test"),
 *																				  	 		  hzConfig);
 *
 *		DistributedCacheService cacheSrvc = new DistributedCacheServiceHazelcastImpl(cacheCfg);
 * </pre>
 */
@Slf4j
@Accessors(prefix="_")
public class DistributedCacheServiceHazelcastImpl
  implements DistributedCacheService {

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Map<Class<? extends ModelObject>,DistributedCache<? extends OID,? extends ModelObject>> _caches = Maps.newLinkedHashMap();
	@Getter private final HazelcastInstance _hazelCastInstance;
	@Getter private final DistributedCacheHazelcastConfig _cfg;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	//@Inject
	public  DistributedCacheServiceHazelcastImpl(final DistributedCacheHazelcastConfig cfg) {
		_cfg = cfg;
		_hazelCastInstance = HazelcastManager.getOrCreateeHazelcastInstance(_cfg);
	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DistributedCacheService
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override	@SuppressWarnings("unchecked")
	public <O extends OID,M extends ModelObject & HasOID<O>> DistributedCache<O,M> getOrCreateCacheFor(final Class<M> modelObjType) {
		DistributedCache<? extends OID,? extends ModelObject> outCache = null;
		log.warn(" ... getOrCreateCacheFor :'{}'",modelObjType);
		if (_caches.get(modelObjType) != null) {			
			outCache = _caches.get(modelObjType);
			log.warn(".. got cache from cache map!");
		} else {
	    	 IMap<O,M> imap = _hazelCastInstance.getMap(modelObjType.getName());
	    	 log.warn(" ... create new cache entry map for  :'{}'",modelObjType);
	    	 imap.addEntryListener(new EntryAddedListener<O,M>() {
	    		 							@Override
										    public void entryAdded(final EntryEvent<O,M> event) {
										        // this will deserialize the new value and throw exception if format doesn't match
										    	// http://stackoverflow.com/questions/38912877/how-to-prevent-hazelcast-mapstore-to-put-into-imap-old-versions-of-objects
										        event.getValue();
										    }
	    	 						},
	    			 				true);	// true if EntryEvent should contain the value
			outCache = new DistributedHazelcastCacheForModelObject<>(modelObjType,
																		imap);
			_caches.put(modelObjType,outCache);
		}
		return (DistributedCache<O,M>)outCache;
	}
	@Override
	public <M extends ModelObject> boolean existsCacheFor(final Class<M> modelObjType) {
		return _caches.get(modelObjType) != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ServiceHandler
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void start() {
		// Do Nothing
	}
	@Override
	public void stop() {
		log.warn("######################################################################################");
		log.warn("Stopping Hazelcast");
		log.warn("######################################################################################");
		if (_hazelCastInstance != null) {
			_clearAll();
			synchronized(this) {
				if (_hazelCastInstance != null) _hazelCastInstance.shutdown();
			}
		}
	}
	private void _clearAll() {
		try {
			for (Iterator<DistributedCache<? extends OID,? extends ModelObject>> cacheIt = _caches.values().iterator(); cacheIt.hasNext(); ) {
				DistributedCache<? extends OID,? extends ModelObject> cache = cacheIt.next();
				cache.clear();
			}
			_caches.clear();
		} catch (Throwable t) {
			// nothing
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUGGABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();

		Set<HazelcastInstance> hazelcastInstances = Hazelcast.getAllHazelcastInstances();
		sb.append("Hazelcast Instances (/n");
		for (HazelcastInstance hzI : hazelcastInstances) {
			sb.append(hzI.getCluster()+ " > "+hzI.getName()+" ("+hzI.hashCode()+")"+"/n");
		}
		sb.append(")/n");
		sb.append("Cache Status (").append(_caches.size()).append(" caches created)\n");
		for (Iterator<DistributedCache<? extends OID,? extends ModelObject>> cacheIt = _caches.values().iterator(); cacheIt.hasNext(); ) {
			DistributedCache<? extends OID,? extends ModelObject> cache = cacheIt.next();
			sb.append(cache.debugInfo());
			if (cacheIt.hasNext()) sb.append("\n_____________________________________________\n");
		}
		return sb;
	}
}
