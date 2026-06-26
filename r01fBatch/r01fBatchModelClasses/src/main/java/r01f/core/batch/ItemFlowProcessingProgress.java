package r01f.core.batch;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

/**
 * Encapsulates info about an item flow processing progress 
 */
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public abstract class ItemFlowProcessingProgress 
  		   implements Serializable {

	private static final long serialVersionUID = -663847341609211484L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="startedAt",dateFormat=@MarshallDateFormat(use=DateFormat.ISO8601),
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _startedAt;
	
	@MarshallField(as="totalItemsToBeProcessed",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private long _totalItemsToBeProcessed;
	
	@MarshallField(as="currentlyProcessedItems",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private long _currentlyProcessedItems;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasFinished() {
		return _currentlyProcessedItems == _totalItemsToBeProcessed;
	}
	public float getProgrees() {
		return _totalItemsToBeProcessed > 0 ? (float)_currentlyProcessedItems / (float)_totalItemsToBeProcessed 
											: 0;
	}
}
