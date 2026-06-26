package r01f.core.fileexplorer.command;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.compress.utils.Lists;

import com.google.common.base.Splitter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerConstants;
import r01f.core.fileexplorer.FileExplorerItemPathHash;
import r01f.core.fileexplorer.FileExplorerStorage;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseForSearch;
import r01f.core.fileexplorer.command.response.FileExplorerCommandResponseObject;
import r01f.core.fileexplorer.command.response.FileExplorerFileStoreItem;
import r01f.core.fileexplorer.config.FileExplorerVolumeSecuritySpec;
import r01f.file.FileProperties;
import r01f.mime.MimeType;
import r01f.mime.MimeTypes;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.ThrowingFunction;
import r01f.util.types.StringSplitter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * see: https://github.com/Studio-42/elFinder/wiki/Client-Server-API-2.1#search
 * 
 * Return a list of files and folders list, that match the search string. 
 * 
 * Arguments:
 * 		- cmd: search
 * 		- q: search string
 * 		- target: search target hash (optional)
 *		- mimes[]: Array of search target MIME-type (optional)
 * Response:
 *		- files: (Array) array of objects - files and folders list, that match the search string. Information about File/Directory			 
 */
public class FileExplorerCommandForSearch 
	 extends FileExplorerCommandBase 
  implements FileExplorerCommand {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public FileExplorerCommandForSearch(final FileExplorerStorage storage,
										final Marshaller marshaller) {
		super(storage,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public FileExplorerCommandResponseObject execute(final HttpServletRequest req) throws ServletException,
																						  IOException {
		// starting folder hash
		FileExplorerItemPathHash startingFolderPathHash = FileExplorerItemPathHash.from(req);
		
		// get the query
		String query = req.getParameter(FileExplorerConstants.REQ_PARAMETER_SEARCH_QUERY);
		String mimesStr = req.getParameter(FileExplorerConstants.REQ_PARAMETER_SEARCH_MIMES);
		
		Collection<MimeType> mimes = Strings.isNOTNullOrEmpty(mimesStr)
											? StringSplitter.using(Splitter.on(";"))
															.at(mimesStr)
															.stream()
															.map(String::trim)
															.map(ThrowingFunction.exceptionIgnoredAndReturnNull(aMimeStr -> MimeTypes.forName(aMimeStr)))
															.filter(Objects::nonNull)
															.toList()
											: null;

		// search
		FileExplorerCommandResponseObject cmdResponse = this.execute(startingFolderPathHash,
									   						  		 query,mimes);
		return cmdResponse;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EXPOSED FOR TESTING PURPOSES
/////////////////////////////////////////////////////////////////////////////////////////
	public FileExplorerCommandResponseForSearch execute(final FileExplorerItemPathHash startingFolderPathHash,
							  				 		 	final String query) throws IOException {
		return this.execute(startingFolderPathHash,
							query,Collections.emptyList());
	}
	public FileExplorerCommandResponseForSearch execute(final FileExplorerItemPathHash startingFolderPathHash,
							  				 		 	final String query,final Collection<MimeType> mimes) throws IOException {
		// search
		Collection<FileExplorerFileStoreItem> foundItems = Lists.newArrayList();
		for (FileExplorerVolume vol : _storage.getVolumes()) {
			// checks volume security
			FileExplorerVolumeSecuritySpec volSecurity = vol.getVolSpec()
															.getSecurity();
			// search only in volumes that are readable
			if (volSecurity != null 
			 && volSecurity.isReadable()) {
				Collection<FileProperties> files = vol.search(startingFolderPathHash.getRelativePathFromRoot(),
															  query,mimes);
				foundItems.addAll(FileExplorerFileStoreItem.itemBuilderForVolume(vol)
														   .forItems(files)
														   .build());
			}
		}
		
		// return result
		FileExplorerCommandResponseForSearch outResponse = new FileExplorerCommandResponseForSearch();
		outResponse.setFiles(CollectionUtils.hasData(foundItems) ? foundItems 
																 : List.of(new FileExplorerFileStoreItem()));	// kind of bug... the file list cannot be empty
		return outResponse;
	}
}
