package r01f.core.fileexplorer.config;

import r01f.filestore.api.FileStoreType;
import r01f.types.Path;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

public class FileExplorerStoreConfigForLocal
	 extends FileExplorerStoreConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerStoreConfigForLocal(final Path fsRootFullPath) {
		super(FileStoreType.LOCAL,
			  fsRootFullPath);
	}
	/**
	 * Loads the file store config from the properties file
	 * @param xmlProps
	 * @return
	 */
	public static FileExplorerStoreConfigForLocal from(final XMLPropertiesForAppComponent xmlProps,final String xPathPrefix) {
		Path fsRootPath = _fileExplorerRootFullPathFrom(xmlProps,xPathPrefix);
		FileExplorerStoreConfigForLocal outCfg = new FileExplorerStoreConfigForLocal(fsRootPath);
		return outCfg;
	}
}