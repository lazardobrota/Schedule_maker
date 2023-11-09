package importexport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigMapping implements Comparable<ConfigMapping>{

    private int index;
    private String custom;
    private String original;

    public ConfigMapping(int index, String custom, String original) {
        this.index = index;
        this.custom = custom;
        this.original = original;
    }

    @Override
    public int compareTo(ConfigMapping that) {
        return this.index - that.getIndex();
    }
}
