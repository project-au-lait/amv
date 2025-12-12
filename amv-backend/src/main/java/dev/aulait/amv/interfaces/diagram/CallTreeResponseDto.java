package dev.aulait.amv.interfaces.diagram;

import java.util.List;
import lombok.Data;

@Data
public class CallTreeResponseDto {
  private List<CallTreeDto> results;
  private int count;

  public CallTreeResponseDto(List<CallTreeDto> results, int count) {
    this.results = results;
    this.count = count;
  }
}
