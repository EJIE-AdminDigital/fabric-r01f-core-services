package r01f.core.fileexplorer.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.filestore.api.FileStoreType;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
abstract class FileExplorerStoreConfigBase
	implements FileExplorerStoreConfig {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final FileStoreType _type;
	@Getter protected final Path _fsRootFullPath;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerStoreConfigBase(final FileStoreType type,
								   	   final Path fsRootFullPath) {
		_type = type;
		_fsRootFullPath = fsRootFullPath;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Path _fileExplorerRootFullPathFrom(final XMLPropertiesForAppComponent xmlProps,
											   			final String xpathPrefix) {
		String xpath = Strings.isNOTNullOrEmpty(xpathPrefix) 
							? Path.from(xpathPrefix,"fileStore/rootPath")
								  .asAbsoluteString()
							: "/fileStore/rootPath";
		Path cmsRootPath = xmlProps.propertyAt(xpath)
									 .asPath(Path.from("c:/develop/temp_dev/plateaweb/cms"));
		return cmsRootPath;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("impl={} data root full path={}",
								  _type,
								  _fsRootFullPath);
	}
}
