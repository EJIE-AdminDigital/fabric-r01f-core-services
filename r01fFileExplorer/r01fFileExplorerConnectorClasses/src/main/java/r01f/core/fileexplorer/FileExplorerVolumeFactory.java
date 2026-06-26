package r01f.core.fileexplorer;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import r01f.core.fileexplorer.config.FileExplorerConfig;
import r01f.core.fileexplorer.config.FileExplorerVolumeSpec;

@Slf4j
public enum FileExplorerVolumeFactory {
	LOCAL {
		@Override
		public FileExplorerVolume buildUsing(final FileExplorerVolumeID id,final FileExplorerVolumeSpec config,
										 	 final FileExplorerStoreMediator fsMediator) throws IOException {
			return new FileExplorerVolume(id,config,
									  	  fsMediator);
		}
	},
//	DROPBOX, 
//	GOOGLEDRIVE, 
//	ONEDRIVE, 
//	ICLOUD
	;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public abstract FileExplorerVolume buildUsing(final FileExplorerVolumeID id,final FileExplorerVolumeSpec config,
											  	  final FileExplorerStoreMediator fsMediator) throws IOException;	
/////////////////////////////////////////////////////////////////////////////////////////
//	INDIVIDUAL VOLUME FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileExplorerVolume createVolumeUsing(final FileExplorerVolumeID id,final FileExplorerVolumeSpec volCfg,
											 	   	   final FileExplorerStoreMediator fsMediator) throws IOException {
		if (volCfg == null) throw new IllegalArgumentException("[File Explorer] volume config cannot be null!");
		if (!volCfg.isValid()) throw new IllegalArgumentException("[File Explorer] volume config is NOT valid: " + volCfg.validate()
																												  		 .asNOKValidationResult()
																												  		 .getReason());
		// get the factory
		String normalizedSource = _normalizeSource(volCfg.getSource());
		FileExplorerVolumeFactory volFactory = _factoryFor(normalizedSource);
		
		// build an instance
		return volFactory.buildUsing(id,volCfg,
									 fsMediator);
	}
	private static FileExplorerVolumeFactory _factoryFor(final String source) {
		FileExplorerVolumeFactory outFactory = null;
		for (FileExplorerVolumeFactory vol : FileExplorerVolumeFactory.values()) {
			if (vol.name().equalsIgnoreCase(source)) {
				outFactory = vol;
				break;
			}
		}
		if (outFactory == null) throw new IllegalArgumentException("[File Explorer] volume (source=" + source + ") not supported! The supported volumes sources are: " + Arrays.deepToString(FileExplorerVolumeFactory.values()).toLowerCase());
		return outFactory;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VOLUME COLLECTION FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	public static Collection<FileExplorerVolume> createVolumesFor(final FileExplorerConfig config,
															  	  final FileExplorerStoreMediator fsMediator) {
		if (config.getVolumeCount() == 0) return Collections.emptyList();
		
		Collection<FileExplorerVolume> outVols = new ArrayList<>(config.getVolumeCount());

		// creates volumes
		FileExplorerVolumeID currVolId = FileExplorerVolumeID.firstVolumeId();
		for (FileExplorerVolumeSpec volCfg : config.getVolumes()) {
			try {
				// create new volume
				FileExplorerVolume vol = FileExplorerVolumeFactory.createVolumeUsing(currVolId,volCfg,
																	   		 		 fsMediator);
				outVols.add(vol);
				
				// prepare next volumeId character
				currVolId = currVolId.nextVolumeId();
			} catch (IOException ioException) {
				log.error("Error creating the volume for source={}: {}",
						  volCfg.getSource(),
						  ioException.getMessage(),ioException);
			}
		}
		return outVols;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static final String NOT_LETTER_REGEX = "[^\\p{L}]";
	private static final String WHITESPACE_REGEX = "[\\p{Z}]";
	private static final String NO_ASCII_CHAR_REGEX = "[^\\p{ASCII}]";
	private static final String EMPTY_STRING = "";
	
	private static String _normalizeSource(final String source) {
		String normalizedSource = Normalizer.normalize(source,Normalizer.Form.NFD);
		normalizedSource = normalizedSource.replaceAll(NOT_LETTER_REGEX,EMPTY_STRING);
		normalizedSource = normalizedSource.replaceAll(WHITESPACE_REGEX,EMPTY_STRING);
		normalizedSource = normalizedSource.replaceAll(NO_ASCII_CHAR_REGEX,EMPTY_STRING);
		normalizedSource = normalizedSource.trim().toUpperCase();
		return normalizedSource;
	}
}
