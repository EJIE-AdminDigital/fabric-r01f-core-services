package r01f.core.batch;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.patterns.ProgressSubscriber;
import r01f.securitycontext.SecurityContext;
import r01f.types.jobs.ItemsProcessingProgress;

@Slf4j
public abstract class ItemFlowProcessorBase<T> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final ExecutorService _executorService;

	private final OutputStream _outputStream;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR / BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public ItemFlowProcessorBase(final ExecutorService executorService,
								 final OutputStream outputStream) {
		_executorService = executorService;
		_outputStream = outputStream;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PUBLIC METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public void process(final SecurityContext securityContext,
						final long totalItemsToBeProcessed,final Flowable<T> items,
						final ProgressSubscriber<ItemsProcessingProgress> progressSubscriber) {

		try {

			// [1] - Init the file
			log.debug("...[items processor] > START");
			_init(securityContext,
				  _outputStream);

			// [2] - consume the flowable
			Instant startedAt = Instant.now();
			AtomicLong currItem = new AtomicLong(1);

			// See https://dzone.com/articles/rxjava-idiomatic-concurrency-flatmap-vs-parallel
			// Concurrency is achieved using:
			//		Flowable<Person> people = Flowable.fromIterable(col)
			//										  .flatMap(new Function<String,Flowable<String>() {
			//															@Override
			//															public Flowable<String> apply(final String item) throws Exception {
			//																return Flowable.just(item)
			//																			   .subscribeOn(Schedulers.io()
			//																			   // ... do anything in parallel;
			//														    }
			//												   })
			//										  .subscribe();
			// ... and since RxJava 2.0.5:
			//		Flowable<Person> people = Flowable.fromIterable(col)
	        //										  .parallel(10)
			//										  .runOn(Schedulers.io())
			//										  // ... do anything in parallel
			//										  // wait for all parallel task to complete
			//										  .sequential();
			// run in parallel
			log.debug("...[items processor] > run in parallel");
			items.parallel()
				 .runOn(Schedulers.from(_executorService))

				 // ... this will be run in parallel
				 .doOnNext(// item consumer
						   ItemThrowingConsumer.wrap(
								   // next
								   item -> {
									   	log.trace("...[export items processor] > next item");
										// a) process item
									  	//    BEWARE! synchronize while writing
								  		_processItem(securityContext,
								  				     item);

										// b) tell the subscriber about the progress
										long i = currItem.getAndIncrement();
										progressSubscriber.onNext(new ItemsProcessingProgress(startedAt,
																							  totalItemsToBeProcessed,
																							  i));
						 		   },
								   // error consumer
								   progressSubscriber::onError)
						   )
				 .doOnError(th -> log.error("\t... error during item processing: {}",
						   				   th.getMessage(),th))
				 // ... wait all rails to finish
				 .sequential()
				 // BEWARE!!! consume the Flowable in order to execute it
				 .blockingSubscribe();		// calling this method will block the caller thread until the upstream terminates normally or with an error


			// [3] - End the file
			log.debug("...[items processor] > END");
			_end(securityContext);

			// [99] - Close
			log.debug("...[items processor] > CLOSE");
			_close(securityContext);
		} catch (Throwable th) {
			log.error("Error processing item: {}",
					  th.getMessage(),th);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	protected void _init(final SecurityContext securityContext,
						 final OutputStream os) throws IOException {
		// nothing
	}
	@SuppressWarnings("unused")
	protected void _processItem(final SecurityContext securityContext,
							  	final T item) throws IOException {
		throw new UnsupportedOperationException("... maybe [processItem()] must be implemented!");
	}
	@SuppressWarnings("unused")
	protected void _end(final SecurityContext securityContext) throws IOException {
		// nothing
	}
	@SuppressWarnings("unused")
	protected void _close(final SecurityContext securityContext) {
		try {
			if (_outputStream != null) {
				_outputStream.flush();
				_outputStream.close();
			}
		} catch (IOException ioEx) {
			log.error("Error closing the stream: {}",
					  ioEx.getMessage(),ioEx);
		}
		log.debug("...[items processor] > stream CLOSED");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected abstract static class ItemThrowingConsumer<T>
	   		    		 implements io.reactivex.rxjava3.functions.Consumer<T> {

		private final Consumer<Throwable> _onErrorConsumer;	// added to the Consumer#accept method

		@Override
		public void accept(final T item) {
			try {
				this.throwingAccept(item);
			} catch (Throwable th) {
				log.error("Error building export file: {}",
						  th.getMessage(),th);
				_onErrorConsumer.accept(th);
			}
		}
		public abstract void throwingAccept(final T item) throws Throwable;

		public static <T> ItemThrowingConsumer<T> wrap(final io.reactivex.rxjava3.functions.Consumer<T> throwingConsummer,
													   final Consumer<Throwable> onErrorConsumer) {
			return new ItemThrowingConsumer<T>(onErrorConsumer) {
							@Override
							public void throwingAccept(final T item) throws Throwable {
								throwingConsummer.accept(item);
							}
				   };
		}
	}
}
