package rangeClasses;

public class Range {
  private int start, end;

  public Range(int s, int e) {
    start = s < e ? s : e;
    end = s < e ? e : s;
  }

  public int sumUp() {
    int ans = 0;
    for (int i=start; i<end; i++) {
      ans += i;
    }

    return ans;
  }

  public int sumUpFast() {
    return (start+end-1)*(end-start)/2;
  }
  
}
