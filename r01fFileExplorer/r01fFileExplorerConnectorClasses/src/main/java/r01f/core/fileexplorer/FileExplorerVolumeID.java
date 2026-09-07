package r01f.core.fileexplorer;

import lombok.Getter;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="volumeId")
public record FileExplorerVolumeID(@Getter Character id) 
   implements OIDTyped<Character> {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileExplorerVolumeID from(final Character ch) {
		return new FileExplorerVolumeID(ch);
	}
	public static FileExplorerVolumeID forId(final Character ch) {
		return new FileExplorerVolumeID(ch);
	}
	public static FileExplorerVolumeID valueOf(final String id) {
		return new FileExplorerVolumeID(id.charAt(0));
	}
	public static FileExplorerVolumeID fromString(final String id) {
		return new FileExplorerVolumeID(id.charAt(0));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String asString() {
		return this.toString();
	}
	@Override
	public String toString() {
		// BEWARE!! volumeId is like {char}_ (contains the underscore)
		return this.id.toString() + "_";
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileExplorerVolumeID firstVolumeId() {
		return FileExplorerVolumeID.forId('A');
	}
	public FileExplorerVolumeID nextVolumeId() {
		return FileExplorerVolumeID.nextVolumeIdOf(this);
	}
	public static FileExplorerVolumeID nextVolumeIdOf(final FileExplorerVolumeID volId) {
		Character ch = volId.getId();
		ch++;
		return FileExplorerVolumeID.forId(ch);
	}
}
