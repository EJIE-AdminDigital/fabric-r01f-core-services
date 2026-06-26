package r01f.core.fileexplorer.command.response;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.debug.Debuggable;
import r01f.file.FileProperties;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Path;
import r01f.util.types.Objects;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * (see https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#information-about-filedirectory)
 * 
 * The information about files and directories is like:
 *		{
 *			"volumeid": "l1_",			  	// Only root folder (String) Volume id. It can include to options.
 *			"hash"    : "l0_SW1hZ2Vz",		// Required		 	(String) hash of current file/dir path 
 *			"phash"   : "l0_Lw",			// Required		 	(String) hash of parent directory
 *
 *			"isroot"  : 0,				    // 1 = root / 0 or not present = not root
 *			"rootRev" : "",
 *
 *			"type"	  : "",					// group = volume group
 *
 *        	"url": null,					//
 *
 *			"name"    : "Images",			// Required		 	(String) name of file/dir.
 *			"mime"    : "directory",		// Required		 	(String) mime type
 *			"ts"	  : 1334163643,		   	// Required		 	(Number) file modification time in unix timestamp
 *			"size"    : 12345,				//				 	(Number) file size in bytes
 *
 *			"dirs"    : 1,					// Only folders  	(Number) Marks if directory has child directories inside it. 
 *											//								0 (or not set) - no
 *											//								1 			   - yes. Do not need to calculate amount.
 *			"locked"  : 0,					//				 	(Number) is file locked. If locked that object cannot be deleted, renamed or moved
 *			"read"    : 1,					//				 	(Number) is readable
 *			"write"   : 1,					//				 	(Number) is writable
 *
 *			"isowner" : true,				//				 	(Bool)   has ownership
 *
 *			"csscls"  : "custom-icon",		//				 	(String) CSS class name for holder icon. It can include to options.
 *			"tmb"	  : 'bac0d52ca495.png'	// Only images   	(String) Thumbnail file name, if file do not have thumbnail yet, but it can be generated then it must have value "1"
 *			"dim"	  : "640x480",			// Only images   	(String) image dimensions. Optionally.
 *
 *			"alias"   : "files/images",	   	// Only symlinks 	(String) Symlink target path.
 *			"thash"   : "l1_c2NhbnMy",		// Only symlinks 	(String) Symlink target hash.
 *
 *			"netkey"  : "",				   	//				 	(String) Netmount volume unique key, Required for netmount volume. It can include to options.
 *
 *        	"options": {
 *				see @FileExplorerOptions
 *        	}
 *		}			 
 */
@MarshallType(as="fileStoreItem")
@Slf4j
@Accessors(prefix="_")
public class FileExplorerFileStoreItem 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
////////// Volume & hashes
	@MarshallField(as="volumeid")
	@Getter @Setter private String _volumeid;
	
	@MarshallField(as="hash")
	@Getter @Setter private String _hash;
	
	@MarshallField(as="phash")
	@Getter @Setter private String _phash;
	
	@MarshallField(as="isroot")
	@Getter @Setter private int _isroot;
	
	@MarshallField(as="rootRev")
	@Getter @Setter private String _rootRev;
	
	@MarshallField(as="dirs")
	@Getter @Setter private int _dirs;
	
////////// Metadata	
	@MarshallField(as="name")
	@Getter @Setter private String _name;
	
	@MarshallField(as="size")
	@Getter @Setter private long _size;
	
	@MarshallField(as="ts")
	@Getter @Setter private long _ts;
	
	@MarshallField(as="mime")
	@Getter @Setter private String _mime;
	
////////// Security
	@MarshallField(as="locked")
	@Getter @Setter private int _locked;
	
	@MarshallField(as="read")
	@Getter @Setter private int _read;
	
	@MarshallField(as="write")
	@Getter @Setter private int _write;
	
////////// Presentation
	@MarshallField(as="csscls")
	@Getter @Setter private String _csscls;
	
	@MarshallField(as="tmb")
	@Getter @Setter private String _tmb = "0";	// thumbnails can be generated
	
	@MarshallField(as="dim")
	@Getter @Setter private String _dim;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerItemPathHash getPathHash() {
		return FileExplorerItemPathHash.from(_hash);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerFileStoreItem() {
		// default no-args constructor
	}
	private FileExplorerFileStoreItem(final FileExplorerVolume vol,
								   	  final FileProperties file) {
		// volume (just for root dir)
		_volumeid = vol.isRoot(file.getPath()) ? vol.getId().asString()	
											   : null;
		// hashes
		_hash = vol.itemPathHashOf(file.getPath())
				   .asString();
		if (!vol.isRoot(file.getPath())) {
			Path parentFolder = file.getPath()
									.getParentFolderPath();
			Path parentFolderRelPathFromRoot = parentFolder.remainingPathFrom(vol.getRootPath());
			_phash = FileExplorerItemPathHash.from(vol.getId(),parentFolderRelPathFromRoot)
											 .asString();
		}
		
		// is root
		_isroot = vol.isRoot(file.getPath()) ? 1 : 0;
		
		// has child
		try {
			_dirs = file.isFolder() ? vol.hasChildFolder(file.getPath()) ? 1 	// the folder has child folders. Do not need to calculate amount
																		 : 0	// the folder DOES NOT has child folders
									: 0;	// it's not a folder
		} catch (IOException ioEx) {
			log.error("[Elfinder]: error trying to guess if folder at {} has child folders: {}",
					  file.getPath(),
					  ioEx.getMessage(),ioEx);
		}
		
		// metadata
		_name = vol.isRoot(file.getPath()) ? vol.getAlias()
										   : file.getPath()
										   		 .getLastPathElement();
		_size = file.getSize();
		_ts = file.getModificationTimeStamp();
		
		MimeType mime = null;
		try {
			mime = vol.getMimeType(file);
			_mime = mime.asString();
		} catch (IOException ioEx) {
			log.error("[Elfinder]: error trying to guess the MimeType of file at {}: {}",
					  file.getPath(),
					  ioEx.getMessage(),ioEx);
		}
		
		// security
		boolean readOnlyFile = vol.isReadOnlyFile(file.getPath());
		
		_locked = vol.isLocked() 
			   || readOnlyFile ? 1 : 0;
		_read = vol.isReadable() ? 1 : 0;
		_write = vol.isWritable()
			 && !readOnlyFile ? 1 : 0;
		
		// thumbnail
		if (file.isFile() 
		 && mime != null && MimeTypes.wrap(mime)
		 							 .isImage()) {
			_tmb = "1";
		} else {
			_tmb = "0";
		}
	}
	public static FileExplorerStoreItemBuilderFileStep itemBuilderForVolume(final FileExplorerVolume vol) {
		return new FileExplorerStoreItemBuilderFileStep(vol);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FileExplorerStoreItemBuilderFileStep {
		private final FileExplorerVolume _vol;
		
		public FileExplorerStoreItemBuilderBuildStep forItem(final FileProperties item) {
			FileExplorerFileStoreItem feItem = new FileExplorerFileStoreItem(_vol,
																	 	     item);
			return new FileExplorerStoreItemBuilderBuildStep(feItem);
		}
		public FileExplorerStoreItemsBuilderBuildStep forItems(final Collection<FileProperties> files) {
			Collection<FileExplorerFileStoreItem> feItems =	CollectionUtils.hasData(files) 
																? files.stream()
																	   .map(item -> new FileExplorerFileStoreItem(_vol,
																	 		 							    	  item))
																	   .toList()
																: Collections.emptyList();
			return new FileExplorerStoreItemsBuilderBuildStep(feItems);
		}
	}
////////// One item
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FileExplorerStoreItemBuilderBuildStep {
		private final FileExplorerFileStoreItem _item;
		
		public FileExplorerFileStoreItem build() {
			return _item;
		}
	}
////////// Multiple items
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class FileExplorerStoreItemsBuilderBuildStep {
		private final Collection<FileExplorerFileStoreItem> _items;
		
		public Collection<FileExplorerFileStoreItem> build() {
			return _items;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		
		if (!(obj instanceof FileExplorerFileStoreItem)) return false;
		FileExplorerFileStoreItem other = (FileExplorerFileStoreItem)obj;
		
		boolean pathHashEqs = Objects.areEqual(this.getHash(),other.getHash(),
											   String::equals);
		return pathHashEqs;
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.getHash());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(Strings.customized("volume id={} hash={} parentHash={} size={} lastModified={} name={} mimeType={} [locked={} / readable={} / writable={}]",
									 _volumeid,_hash,_phash,_size,_ts,_name,_mime,
									 _locked,_read,_write));
		return sb;
	}	
}
