package r01f.core.fileexplorer.web;

import java.util.Collection;

import jakarta.servlet.http.HttpServletRequest;
import r01f.core.fileexplorer.FileExplorerVolume;
import r01f.core.fileexplorer.config.FileExplorerVolumeSpec;
import r01f.guids.OID;
import r01f.types.Path;

/**
 * given a business object oid (ie a contentOid or a serviceOid) 
 * returns the {@link FileExplorerVolume} with the folder(s) containing the file assets
 * @param <O>
 */
@FunctionalInterface
public interface FileExplorerBusinessObjToVolumes<O extends OID> {
	/**
	 * Resolves the {@link FileExplorerVolume} of the given business object oid
	 * @param <O>
	 * @param oid
	 * @param businessObjShortName business obj short name
	 * @param businessObjTargetPath 
	 * @param vols
	 * @param request
	 * @return
	 */
	public Collection<FileExplorerVolumeSpec> volumesFor(final O oid,
														 final String businessObjShortName,
														 final Path businessObjTargetPath,
														 final Collection<FileExplorerVolumeSpec> vols,
														 final HttpServletRequest request);
}
