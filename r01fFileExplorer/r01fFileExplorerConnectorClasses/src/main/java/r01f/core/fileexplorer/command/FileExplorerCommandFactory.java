package r01f.core.fileexplorer.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.enums.EnumWithCode;
import r01f.objectstreamer.Marshaller;


@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum FileExplorerCommandFactory 
 implements EnumWithCode<String,FileExplorerCommandFactory> {
	ARCHIVE_CREATE	("archive",		(storage,marshaller) -> new FileExplorerCommandForArchiveCreate(storage,marshaller)),
	ARCHIVE_EXTRACT	("extract",		(storage,marshaller) -> new FileExplorerCommandForArchiveExtract(storage,marshaller)),
	DIM				("dim",			(storage,marshaller) -> new FileExplorerCommandForDim(storage,marshaller)),
	DUPLICATE		("duplicate",	(storage,marshaller) -> new FileExplorerCommandForDuplicate(storage,marshaller)),
	FILE			("file",		(storage,marshaller) -> new FileExplorerCommandForFile(storage,marshaller)),
	INFO			("info",		(storage,marshaller) -> new FileExplorerCommandForInfo(storage,marshaller)),
	GET				("get",			(storage,marshaller) -> new FileExplorerCommandForGet(storage,marshaller)),
	LS				("ls",			(storage,marshaller) -> new FileExplorerCommandForLs(storage,marshaller)),
	MKDIR			("mkdir",		(storage,marshaller) -> new FileExplorerCommandForMkdir(storage,marshaller)),
	MKFILE			("mkfile",		(storage,marshaller) -> new FileExplorerCommandForMkfile(storage,marshaller)),
	OPEN			("open",		(storage,marshaller) -> new FileExplorerCommandForOpen(storage,marshaller)),
	PARENTS			("parents",		(storage,marshaller) -> new FileExplorerCommandForParents(storage,marshaller)),
	PASTE			("paste",		(storage,marshaller) -> new FileExplorerCommandForPaste(storage,marshaller)),
	PUT				("put",			(storage,marshaller) -> new FileExplorerCommandForPut(storage,marshaller)),
	RENAME			("rename",		(storage,marshaller) -> new FileExplorerCommandForRename(storage,marshaller)),
	RM				("rm",			(storage,marshaller) -> new FileExplorerCommandForRm(storage,marshaller)),
	SEARCH			("search",		(storage,marshaller) -> new FileExplorerCommandForSearch(storage,marshaller)),
	SIZE			("size",		(storage,marshaller) -> new FileExplorerCommandForSize(storage,marshaller)),
	TMB				("tmb",			(storage,marshaller) -> new FileExplorerCommandForTmb(storage,marshaller)),
	TMB_CREATE		("tmb-create",	(storage,marshaller) -> new FileExplorerCommandForTmbCreate(storage,marshaller)),
	TREE			("tree",		(storage,marshaller) -> new FileExplorerCommandForTree(storage,marshaller)),
	UPLOAD			("upload",		(storage,marshaller) -> new FileExplorerCommandForUpload(storage,marshaller));
	
	@Getter private final String _code;
	@Getter private final Class<String> _codeType = String.class;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface FileExplorerCommandFactoryFrom {
		public FileExplorerCommand createFrom(final FileExplorerStorage storage,final Marshaller marshaller);
	}
	@Getter private final FileExplorerCommandFactoryFrom _commandFactory;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings("unchecked")
	public <C extends FileExplorerCommand> C createCommandUsing(final FileExplorerStorage storage,final Marshaller marshaller) {
		return (C)_commandFactory.createFrom(storage,marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileExplorerCommandFactory fromCode(final String code) {
		return EnumWithCode.fromCode(code,FileExplorerCommandFactory.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + FileExplorerCommandFactory.class + " element with code=" + code));
	}
	public static FileExplorerCommandFactory from(final String code) {
		return FileExplorerCommandFactory.fromCode(code);
	}
	public static boolean isSupportedCommand(final String code) {
		return EnumWithCode.canBeFromCode(code,FileExplorerCommandFactory.class);
	}
	public static boolean isNOTSupportedCommand(final String code) {
		return !FileExplorerCommandFactory.isSupportedCommand(code);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileExplorerCommandFactoryStorageStep createCommandFor(final String commandStr) {
		FileExplorerCommandFactory cmdFact = FileExplorerCommandFactory.from(commandStr);
		return new FileExplorerCommandFactoryStorageStep(cmdFact);
	}
	@RequiredArgsConstructor
	public static class FileExplorerCommandFactoryStorageStep {
		private final FileExplorerCommandFactory _commandFactory;
		
		public <C extends FileExplorerCommand> C using(final FileExplorerStorage storage,final Marshaller marshaller) {
			return _commandFactory.createCommandUsing(storage,marshaller);
		}
	}
	
}
