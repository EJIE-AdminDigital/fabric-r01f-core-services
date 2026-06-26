package r01f.core.fileexplorer;

import r01f.guids.OIDBaseMutable;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="volumeId")
public class FileExplorerVolumeID 
	 extends OIDBaseMutable<Character> {

	private static final long serialVersionUID = -4666561604587463857L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerVolumeID(final Character ch) {
		super(ch);
	}
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
	public String toString() {
		// BEWARE!! volumeId is like {char}_ (contains the underscore)
		return super.toString() + "_";
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
