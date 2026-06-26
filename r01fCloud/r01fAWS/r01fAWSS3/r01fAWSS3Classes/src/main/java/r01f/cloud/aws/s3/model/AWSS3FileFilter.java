package r01f.cloud.aws.s3.model;

import r01f.types.Path;

public interface AWSS3FileFilter {
	/**
	 * Accept only this filtered files
	 * @param path the path
	 * @return if is accepted
	 */
	public boolean accept(final Path path);
}
