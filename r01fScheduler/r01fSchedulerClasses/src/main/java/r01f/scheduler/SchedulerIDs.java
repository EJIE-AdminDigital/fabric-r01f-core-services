package r01f.scheduler;

import org.quartz.Job;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDTyped;
import r01f.objectstreamer.annotations.MarshallType;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class SchedulerIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public interface IsSchedulerID 
			 extends OIDTyped<String> {
		// just a marker interface
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="schedulerId")
	public record SchedulerID(@Getter String id)
	   implements IsSchedulerID {

		public static SchedulerID from(final String id) {
			return new SchedulerID(id);
		}
		public static SchedulerID forId(final String id) {
			return new SchedulerID(id);
		}
		public static SchedulerID valueOf(final String id) {
			return new SchedulerID(id);
		}
		public static SchedulerID fromString(final String id) {
			return new SchedulerID(id);
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="schedulerJobId")
	public record SchedulerJobID(@Getter String id)
	   implements IsSchedulerID {
		   
		public static SchedulerJobID from(final String id) {
			return new SchedulerJobID(id);
		}
		public static SchedulerJobID forId(final String id) {
			return new SchedulerJobID(id);
		}
		public static SchedulerJobID valueOf(final String id) {
			return new SchedulerJobID(id);
		}
		public static SchedulerJobID fromString(final String id) {
			return new SchedulerJobID(id);
		}
		public static SchedulerJobID forJobType(final Class<? extends Job> jobType) {
			return SchedulerJobID.forId(jobType.getName());
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="schedulerTriggerId")
	public record SchedulerTriggerID(@Getter String id)
	   implements IsSchedulerID {
		   
		public static SchedulerTriggerID from(final String id) {
			return new SchedulerTriggerID(id);
		}
		public static SchedulerTriggerID forId(final String id) {
			return new SchedulerTriggerID(id);
		}
		public static SchedulerTriggerID valueOf(final String id) {
			return new SchedulerTriggerID(id);
		}
		public static SchedulerTriggerID fromString(final String id) {
			return new SchedulerTriggerID(id);
		}
		public static SchedulerTriggerID forJobType(final Class<? extends Job> jobType) {
			return SchedulerTriggerID.forId(jobType.getName());
		}
		
		@Override
		public String asString() {
			return this.id;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
}
