package r01f.cache;

import r01f.config.ContainsConfigData;
import r01f.guids.CommonOIDs.IsAppCode;
import r01f.guids.CommonOIDs.IsAppComponent;

public interface DistributedCacheConfig
         extends ContainsConfigData {

   public IsAppComponent getAppComponent();

   public IsAppCode getAppCode();

   public <C extends DistributedCacheConfig > C as(final Class<C> type);

   public CharSequence debugInfo();

}
