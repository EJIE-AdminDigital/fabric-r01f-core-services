package r01f.core.fileexplorer.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.util.types.collections.Lists;

/**
 * File explorer config is splitted in TWO data structures:
 * 		- file explorer: the file-explorer component behavior
 * 		- file store: the access to the underlying storage
 * This object models the [file-explorer] part
 * 
 * The config is something like:
 * <pre class='brush:xml'>
 *		<file-explorer>
 *			<thumbnail>
 *				<width>80</width>
 *			</thumbnail>
 *			<volumes>
 *				<volume>
 *					<source>r01fs</source>
 *					<alias>/</alias>
 *					<path>/</path>
 *					<language>BASQUE</language>
 *					<security locked="false" readable="true" writable="true">
 *						<read-only-files>
 *							<file-pattern>.*\.txt</file-pattern>
 *						</read-only-files>
 *					</security>
 *				</volume>
 *			</volumes>
 *		</file-explorer>
 * </pre>
 */
@MarshallType(as="file-explorer")
@Accessors(prefix="_")
public class FileExplorerConfig 
  implements Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="thumbnail")
	@Getter @Setter protected FileExplorerThumbnailSpec _thumbnail;
	
	@MarshallField(as="volumes",
				   whenXml = @MarshallFieldAsXml(collectionElementName = "volume"))
	@Getter @Setter protected Collection<FileExplorerVolumeSpec> _volumes;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileExplorerConfig defaultConfig() {
		// thumb
		FileExplorerThumbnailSpec thumb = new FileExplorerThumbnailSpec();
		thumb.setWidth(80);
		
		// volumes
		FileExplorerVolumeSpec volumeCfg = new FileExplorerVolumeSpec();
		volumeCfg.setSource("r01fs");
		volumeCfg.setAlias("/");
		volumeCfg.setPath(Path.from("/"));
		volumeCfg.setSecurity(FileExplorerVolumeSecuritySpec.builder()
															.readable(true)
															.writable(true)
															.locked(false)
														.build());
		// create
		FileExplorerConfig outCfg = new FileExplorerConfig();
		outCfg.setThumbnail(thumb);
		outCfg.setVolumes(Lists.newArrayList(volumeCfg));
		return outCfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getThumbnailWidth() {
		int thumbnailWidth = 80; //default value
		if (this.getThumbnail() != null) {
			thumbnailWidth = this.getThumbnail()
								 .getWidth();
		}
		return thumbnailWidth;
	}
	public Collection<FileExplorerVolumeSpec> getVolumeList() {
		return Collections.unmodifiableList(this.getVolumes() != null ? (List<FileExplorerVolumeSpec>)this.getVolumes() 
																	  : Lists.newArrayList());
	}
	public int getVolumeCount() {
		return _volumes != null ? _volumes.size() : 0;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		for (FileExplorerVolumeSpec volume : this.getVolumes()) {
			sb.append(volume.debugInfo());
			sb.append("\n");
		}
		sb.append(Strings.customized("Thumbnail Width: {}",this.getThumbnailWidth()));
		return sb;
	}
}
