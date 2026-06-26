package r01f.brms.drools.sample;


import lombok.Getter;
import lombok.Setter;

public class InboundInput {

    @Getter @Setter  private String text;
  

    public InboundInput(final String text) {
        this.text = text;
      
    }

}
