package r01f.core.batch;

import java.util.Date;

/**
 * Creates a {@link ItemFlowProcessingProgress} that encapsulates info about an item stream processing
 * given:
 * 		- the start moment
 * 		- the total items to be processed
 * 		- the current processed items count
 * 		- the currently processed item
 * @param <T>
 * @param <P>
 */
@FunctionalInterface
public interface ItemFlowProcessingProgressFactory<T,P extends ItemFlowProcessingProgress> {
	/**
	 * Creates a {@link ItemFlowProcessingProgress}
	 * @param startedAt
	 * @param totalItemsToBeProcessed
	 * @param currentProcessedItems
	 * @param justProcessedItems
	 * @return
	 */
	public P createFor(final Date startedAt,
					   final long totalItemsToBeProcessed,final long currentProcessedItems,
					   final T justProcessedItems);
}
