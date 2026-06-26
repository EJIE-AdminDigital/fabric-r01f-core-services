package r01f.brms.drools.sample;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(prefix="_")
public class OutboundSuggest {

    @Getter @Setter private String  _text;  

    public OutboundSuggest(final String text) {
       _text = text;
      
    }

}
