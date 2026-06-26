package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForError;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerDebug;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.core.fileexplorer.command.response.FileExplorerOptions;
import r01f.file.FileNameAndExtension;
import r01f.file.FileProperties;
import r01f.objectstreamer.Marshaller;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.util.types.Strings.StringIsContainedWrapper;
import r01f.util.types.collections.CollectionUtils;


/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1
 */
@Slf4j
abstract class FileExplorerCommandBase 
    implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final FileExplorerStorage _storage;
	protected final Marshaller _marshaller;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	FileExplorerCommandBase(final FileExplorerStorage storage,
							final Marshaller marshaller) {
		_storage = storage;
		_marshaller = marshaller;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXECUTE
///////////////////////////////////////////////////////////////////////////////////////// 
	@Override
	public void execute(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
																									IOException {
		try (PrintWriter writer = response.getWriter()) {
			// execute
			FileExplorerCommandResponseObject cmdResponse = this.execute(request);
			
			// set response
			response.setContentType(FileExplorerConstants.CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF_8);
			String json = _marshaller.forWriting()
									 .toJson(cmdResponse);
			writer.write(json);
			writer.flush();
		} catch (Throwable th) {
			// return the response
			log.error("Error executing [file explorer] command: ",
					  th.getMessage(),th);
			
			FileExplorerCommandResponseForError outResponse = new FileExplorerCommandResponseForError();
			outResponse.setError(th.getMessage());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OPTIONS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FileExplorerOptions getOptionsFor(final Path path) {
		return new FileExplorerOptions();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerDebug createDebugInfo() {
		return new FileExplorerDebug(_storage);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	UTIL
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Collection<FileExplorerFileStoreItem> _childFoldersOf(final Path folderRelPath,
																   		    final FileExplorerVolume vol) throws IOException {
		Collection<FileProperties> childItems = vol.listChildren(folderRelPath);
		return CollectionUtils.hasData(childItems)
					? childItems.stream()
								.filter(FileProperties::isFolder)
								.map(file -> FileExplorerFileStoreItem.itemBuilderForVolume(vol)
																	   .forItem(file)
																	   .build())
								.toList()
					: Collections.emptyList();
	}
	protected static boolean _toBoolean(final String str) {
		if (Strings.isNullOrEmpty(str)) return false;
		StringIsContainedWrapper strW = Strings.isContainedWrapper(str);
		return strW.containsAny("1","0") ? Integer.valueOf(str) == 1 ? true : false
										 : strW.containsAnyIgnoringCase("TRUE","FALSE") ? str.toUpperCase().equals("TRUE") ? true : false
							  						 									: false;
	}
	/**
	 * Computes the file name being aware of a possible name collision
	 * @param fileOrFolderName
	 * @param dstFolderChildItemsNames
	 * @param suffixWhenCollision
	 * @param iteration
	 * @return
	 */
	protected static String _fileNameBeingAwareOfNameCollision(final String fileOrFolderName,final boolean isFolder,
									   	 					   final Collection<String> dstFolderChildItemsNames,
									   	 					   final String suffixWhenCollision,final int iteration) {
		boolean nameCollision = CollectionUtils.hasData(dstFolderChildItemsNames)
									? dstFolderChildItemsNames.stream()
															  .anyMatch(name -> name.equals(fileOrFolderName))
									: false;
		if (!nameCollision) return fileOrFolderName;	// NO colission > EXIT!!!
		
		// if name collision rename to: name + suffix + iteration
		// ... but before that, remove the suffix + iteration part if present
		String namePatternStr = !isFolder ? "(.+)(" + suffixWhenCollision + "[0-9]+)(\\.?[^.]*)"	// beware if suffix is a regex operator
										  : "(.+)(" + suffixWhenCollision + "[0-9]+)";
		Matcher m = Pattern.compile(namePatternStr)	
						   .matcher(fileOrFolderName);
		String theFileOrFolderName = m.find() ? m.groupCount() == 3 ? m.group(1) + m.group(3)	// remove the suffixN part: has extension
																	: m.group(1) 				// remove the suffixN part: do not have extension
											  : fileOrFolderName;
		FileNameAndExtension nameAndExt = FileNameAndExtension.of(theFileOrFolderName);
		String newName = !isFolder && nameAndExt.hasExtension()
							? Strings.customized("{}{}{}.{}",
												 nameAndExt.getNameWithExtension(),
												 suffixWhenCollision,iteration,
												 nameAndExt.getExtension())
							: Strings.customized("{}{}{}",
												 nameAndExt.getNameWithExtension(),
												 suffixWhenCollision,iteration);
		// BEWARE recursion!!
		// check if the new name is valid
		String theNewName = _fileNameBeingAwareOfNameCollision(newName,isFolder,
										 					   dstFolderChildItemsNames, 
										 					   suffixWhenCollision,iteration + 1);
		return theNewName;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Collection<String> _itemsNames(final Collection<FileProperties> childItems) {
		return CollectionUtils.hasData(childItems)
			  		? childItems.stream()
  							 	.map(childItem -> childItem.getPath()
  									 					   .getLastPathElement())
  							 	.toList()
			  		: Collections.emptyList();
	}
}