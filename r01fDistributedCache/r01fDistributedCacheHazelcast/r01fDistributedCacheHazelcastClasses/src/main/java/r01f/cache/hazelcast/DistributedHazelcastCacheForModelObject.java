package r01f.cache.hazelcast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.IMap;
import com.hazelcast.map.LocalMapStats;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.query.Predicates;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.cache.DistributedCache;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.model.ModelObject;
import r01f.util.types.collections.CollectionUtils;

/**
 * DistributedHazelcastCacheForModelObjectBase base class for Model objects
 */
@Slf4j
@Accessors(prefix="_")
     class DistributedHazelcastCacheForModelObject<O extends OID,M extends ModelObject & HasOID<O>>
implements DistributedCache<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
     @Getter private final Class<M> _modelObjectType;
     @Getter private final IMap<O,M> _imap;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
     public DistributedHazelcastCacheForModelObject(final Class<M> modelObjectType,
    		 									    final IMap<O,M> map) {
    	 _modelObjectType = modelObjectType;
    	 _imap = map;
     }
/////////////////////////////////////////////////////////////////////////////////////////
//	GET
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public M get(final O key) {
		if (log.isDebugEnabled()) log.debug("{}",this.debugInfo());
		M e = _imap.get(key);		
		return e;
	}
	@Override
	public Map<O, M> getAll(final Set<? extends O> keys) {
		return _imap.getAll(_keysAsSetOfOIDs(keys));
	}

	@Override
	public Map<O, M> getAll() {
		try {
			Map<O, M> result = new HashMap<>();
			for (Entry<O, M> entrada : _imap.entrySet()) {
				result.put(entrada.getKey(), entrada.getValue());
			}
			return result;
		} catch (HazelcastSerializationException hazex) {
			 _removeInvalidSerializedObjectFromMap();
		}
		// Try again
		return _imap.getAll(this.getKeySet());
	}
	@Override
	public Set<O> getKeySet() {
		return _imap.keySet();
	}
	@Override
	public boolean containsKey(final O key) {
		return _imap.containsKey(key);
	}
	@Override
	public <I extends OID> M getByIdField(final I modelObjectId) {
		Collection<M>  results = _imap.values(Predicates.equal("id",
															   modelObjectId));
		return CollectionUtils.pickOneAndOnlyElementOrNull(results);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PUT
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void put(final O key,final M value) {
		 //This method returns a clone of the previous value, not the original(identically equal) value previously put into the map. 
		 // .. this will be not used because it's confused ::> if no previous value exists ( creation), a null value will be return.
		_imap.put(key, value);
	}
	@Override
	public void put(final O key,final M value,
				    final long ttl,final TimeUnit timeunit ) {
		 //This method returns a clone of the previous value, not the original(identically equal) value previously put into the map. 
		 // .. this will be not used because it's confused ::> if no previous value exists ( creation), a null value will be return.
		 _imap.put(key,value,
				         ttl,timeunit);
	}
	@Override
	public M getAndPut(final O key,final M value) {		
		return _imap.putIfAbsent(key,value);
	}
	@Override
	public void putAll(final Map<? extends O,? extends M> map) {
		_imap.putAll(map);
	}
	@Override
	public boolean putIfAbsent(final O key,final M value) {
	  try {
		 _imap.putIfAbsent(key,value);
	  } catch (final Exception nulex) {
		 return false;
	  }
	  return true;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REPLACE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean replace(final O key,final M oldValue,final M newValue) {
		 return _imap.replace(key,oldValue,newValue);
	}
	@Override
	public boolean replace(final O key,final M value) {
		try {
			_imap.replace(key,value);
		} catch (final NullPointerException nulex ) {//- if the specified key is null.)
			return false;
		}
		return true;
	}
	@Override
	public M getAndReplace(final O key, final M value) {
		return _imap.replace(key,value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	REMOVE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean remove(final O key) {
		try {
			_imap.remove(key);
		} catch (final NullPointerException nulex) {//- if the specified key is null.)
			return false;
		}
		return true;
	}
	@Override
	public boolean remove(final O key,final M oldValue) {
		throw new UnsupportedOperationException(">>>>>>>> Not Implemented");
	}
	@Override
	public M getAndRemove(final O key) {
	  return _imap.remove(key);
	}
	@Override
	public void removeAll(final Set<? extends O> keys) {
		for (O id : keys ) {
			this.remove(id);
		}
	}
	@Override
	public void removeAll() {
		this.removeAll(this.getKeySet());
	}
	@Override
	public void clear() {
		_imap.clear();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isNullOrEmpty() {
		 return _imap == null
			 || _imap.size() < 1;
	}
	@Override
	public boolean hasElements() {
		return !this.isNullOrEmpty();
	}
    @Override
	public long size() {
		return _imap.size();
	} 
/////////////////////////////////////////////////////////////////////////////////////////
// OTHER OPERATIONS
/////////////////////////////////////////////////////////////////////////////////////////    
    @Override
    public long lastUpdateTime(final O key) {
		EntryView<O,M>  entryView  = _imap.getEntryView(key);
		return entryView.getLastUpdateTime();
	} 
    @Override
	public M touch(final O key) {
	   M lastValue = this.get(key);
	   return this.getAndReplace(key, lastValue);
	} 
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		LocalMapStats lms = _imap.getLocalMapStats();

		StringBuilder sb = new StringBuilder();
		sb.append(">>>>>>>>>>>  DEBUG : CACHE OF TYPE :: ").append(_imap.getName()).append("\n");
		sb.append("----------------------------------");
		sb.append(">> SIZE : ").append(lms.getOwnedEntryCount()).append(" (").append(lms.getOwnedEntryMemoryCost()).append(" bytes) ").append("\n");
		sb.append(">> HEAP COST : ").append(lms.getHeapCost()).append(" bytes ").append("\n");
		sb.append(">> OTHER INFO : ").append(lms).append("\n");
		sb.append(">> ELEMENTS : ").append(this.getKeySet().size()).append("\n");
		sb.append("----------------------------------");
		return sb;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	private Set<O> _keysAsSetOfOIDs(final Set<? extends O> keys) {
		Set<O> setID = keys.stream()
                           .map(key -> key)
                           .collect(Collectors.toSet());
		return setID;
	}
	private  void _removeInvalidSerializedObjectFromMap() {
		log.error("_______________________________________________________________________________________________");
		log.error("Warning! Detected some invalid class on HazelCast IMAP ({})",HazelcastSerializationException.class);
		log.error("_______________________________________________________________________________________________");

		Set<O> keys = this.getKeySet();
		 for (final O key : keys) {
			 log.debug(" Key : {}", key.asString());
			 try {
			      _imap.get(key);
			 } catch (final com.hazelcast.nio.serialization.HazelcastSerializationException ex) {
				 log.error("Catch it!  Try to remove this {}", key.asString());
				 // Removes the mapping for a key from this map if it is present (optional operation).
                 // Unlike remove(Object), this operation does not return the removed value, which avoids the serialization  of the returned value.
				 _imap.delete(key);
			 }
		 }
	}
}

